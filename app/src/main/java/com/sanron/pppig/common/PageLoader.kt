package com.sanron.pppig.common

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.OnLifecycleEvent
import androidx.recyclerview.widget.DiffUtil
import com.chad.library.adapter.base.BaseQuickAdapter
import com.sanron.datafetch_interface.video.bean.PageData
import com.sanron.pppig.base.BaseObserver
import com.sanron.pppig.base.state.LoadState
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

    val refreshing = SingleLiveEvent<Boolean>()

    val loadState = SingleLiveEvent<Int>()

    val loadMoreState = MutableLiveData<Int>()

    var diffResult = SingleLiveEvent<DiffUtil.DiffResult>()

    var diffCallback: DiffUtil.Callback? = null

    var page: Int = 0

    var disposable: Disposable? = null

    var scrollToTop = SingleLiveEvent<Void>()


    /**
     * 刷新数据
     */
    fun refresh() {
        loadData(true)
    }

    /**
     * 重置数据状态
     */
    fun reset() {
        loadState.value = LoadState.LOADING
        listData.value?.clear()
        refreshing.value = false
    }

    internal fun loadMore() {
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
                        if (refresh) {
                            refreshing.value = true
                            if (loadState.value != LoadState.SUCCESS) {
                                loadState.value = LoadState.LOADING
                            }
                        }
                    }

                    override fun onNext(t: PageData<T>) {
                        super.onNext(t)
                        if (loadState.value != LoadState.SUCCESS) {
                            loadState.value = LoadState.SUCCESS
                        }
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
                        if (loadState.value != LoadState.SUCCESS) {
                            loadState.value = LoadState.ERROR
                        }
                        refreshing.value = false
                        showToast(MsgFactory.get(e))
                    }
                })
    }

}
