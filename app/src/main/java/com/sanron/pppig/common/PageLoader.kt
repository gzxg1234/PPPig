package com.sanron.pppig.common

import android.arch.lifecycle.*
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.sanron.datafetch_interface.bean.PageData
import com.sanron.pppig.base.BaseObserver
import com.sanron.pppig.binding.RecyclerViewAdapter
import com.sanron.pppig.util.SingleLiveEvent
import com.sanron.pppig.util.main
import com.sanron.pppig.util.showToast
import io.reactivex.Observable
import io.reactivex.disposables.Disposable

/**
 *Author:sanron
 *Time:2019/4/19
 *Description:通用列表加载类
 */
class PageLoader<T>(private val fetch: (page: Int) -> Observable<PageData<T>>) : LifecycleObserver {

    var adapter: BaseQuickAdapter<T, *>? = null

    val listData = MutableLiveData<MutableList<T>>().apply {
        value = mutableListOf()
    }

    val refreshing = MutableLiveData<Boolean>().apply {
        value = false
    }

    val loadMoreState = MutableLiveData<Int>().apply {
        value = RecyclerViewAdapter.STATE_COMPLETE
    }

    var diffResult = SingleLiveEvent<DiffUtil.DiffResult>()

    var diffCallback: DiffUtil.Callback? = null

    var page: Int = 0

    var disposable: Disposable? = null

    var scrollToTop = SingleLiveEvent<Void>()

    fun refresh() {
        refreshing.value = true
        loadData(true)
    }

    fun loadMore() {
        loadData(false)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun destroy() {
        disposable?.dispose()
    }

    private fun loadData(refresh: Boolean) {
        val reqPage = if (refresh) 1 else page + 1
        fetch.invoke(reqPage)!!
                .main()
                .subscribe(object : BaseObserver<PageData<T>>() {
                    override fun onSubscribe(d: Disposable) {
                        super.onSubscribe(d)
                        disposable?.dispose()
                        disposable = d
                    }

                    override fun onNext(t: PageData<T>) {
                        super.onNext(t)
                        if (refresh) {
                            listData.value?.clear()
                        }
                        t.data?.apply {
                            listData.value?.addAll(this)
                        }
                        listData.value = listData.value

                        diffCallback?.let {
                            diffResult.value = DiffUtil.calculateDiff(diffCallback!!)
                        }
                        page = reqPage
                        refreshing.value = false
                        loadMoreState.value = if (t.hasMore) RecyclerViewAdapter.STATE_COMPLETE else RecyclerViewAdapter.STATE_END
                        if (refresh) {
                            scrollToTop.call()
                        }
                    }

                    override fun onError(e: Throwable) {
                        super.onError(e)
                        if (!refresh) {
                            loadMoreState.value = RecyclerViewAdapter.STATE_FAIL
                        }
                        refreshing.value = false
                        showToast(MsgFactory.get(e))
                    }
                })
    }

}


fun PageLoader<*>.bindRecyclerView(lifecycleOwner: LifecycleOwner, recyclerView: RecyclerView) {
    if (recyclerView.adapter == null || recyclerView.adapter !is BaseQuickAdapter<*, *> || lifecycleOwner == null) {
        return
    }
    (recyclerView.adapter as BaseQuickAdapter<Any?, *>).let { adapter ->
        adapter.setNewData(this.listData.value)
        if (this.diffCallback == null) {
            (this as PageLoader<Any>).listData.observe(lifecycleOwner, Observer {
                adapter.notifyDataSetChanged()
            })
        } else {
            this.diffResult.observe(lifecycleOwner, Observer {
                it?.dispatchUpdatesTo(adapter)
            })
        }
        this.scrollToTop.observe(lifecycleOwner, Observer {
            recyclerView.scrollToPosition(0)
        })
        this.loadMoreState.observe(lifecycleOwner, Observer {
            when (it) {
                RecyclerViewAdapter.STATE_FAIL -> adapter.loadMoreFail()
                RecyclerViewAdapter.STATE_END -> adapter.loadMoreEnd()
                else -> {
                    adapter.loadMoreComplete()
                }
            }
        })
        adapter.setOnLoadMoreListener({
            this.loadMore()
        }, recyclerView)
    }
}

fun PageLoader<*>.bindRefreshLayout(lifecycleOwner: LifecycleOwner, refreshLayout: SwipeRefreshLayout) {
    refreshLayout.setOnRefreshListener {
        this.refreshing.value = true
        this.refresh()
    }
    this.refreshing.observe(lifecycleOwner, Observer {
        if (!refreshLayout.isAttachedToWindow && it == true) {
            refreshLayout.postDelayed({
                refreshLayout.isRefreshing = true
            }, 100)
            return@Observer
        }
        refreshLayout.isRefreshing = it ?: false
    })
}