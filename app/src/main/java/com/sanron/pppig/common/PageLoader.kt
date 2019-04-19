package com.sanron.pppig.common

import android.arch.lifecycle.*
import android.support.v7.util.DiffUtil
import com.chad.library.adapter.base.BaseQuickAdapter
import com.sanron.pppig.base.BaseObserver
import com.sanron.pppig.binding.RecyclerViewAdapter
import com.sanron.pppig.data.bean.micaitu.PageData
import com.sanron.pppig.util.SingleLiveEvent
import com.sanron.pppig.util.main
import io.reactivex.Observable
import io.reactivex.disposables.Disposable

/**
 *Author:sanron
 *Time:2019/4/19
 *Description:
 */
class PageLoader<T> : LifecycleObserver {


    var fetch: ((page: Int) -> Observable<PageData<T>>)? = null

    var adapter: BaseQuickAdapter<T, *>? = null

    val data = MutableLiveData<MutableList<T>>().apply {
        value = mutableListOf()
    }

    val refreshing = MutableLiveData<Boolean>().apply {
        value = false
    }

    val loadMoreState = MutableLiveData<Int>().apply {
        value = RecyclerViewAdapter.STATE_COMPLETE
    }

    var lifecycleOwner: LifecycleOwner? = null
        set(value) {
            value?.lifecycle?.addObserver(this)
            field = value
        }


    var diffResult = SingleLiveEvent<DiffUtil.DiffResult>()

    var diffCallback: DiffUtil.Callback? = null

    var page: Int = 0

    var disposable: Disposable? = null

    var scrollToTop = SingleLiveEvent<Void>()

    fun refresh() {
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
        fetch?.invoke(reqPage)!!
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
                            data.value?.clear()
                        }
                        t.data?.apply {
                            data.value?.addAll(this)
                        }
                        data.value = data.value

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
                    }
                })
    }

}