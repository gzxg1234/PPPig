package com.sanron.pppig.binding

import android.arch.lifecycle.Observer
import android.databinding.BindingAdapter
import android.support.v7.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.sanron.pppig.common.PageLoader

/**
 *Author:sanron
 *Time:2019/4/17
 *Description:
 */

object RecyclerViewAdapter {
    val STATE_FAIL = 0
    val STATE_COMPLETE = 1
    val STATE_END = 2


    @Suppress("UNCHECKED_CAST")
    @JvmStatic
    @BindingAdapter(value = ["android:pageLoader"], requireAll = false)
    fun bindPageLoader(recyclerView: RecyclerView, pageLoader: PageLoader<*>) {
        if (recyclerView.adapter == null || recyclerView.adapter !is BaseQuickAdapter<*, *>) {
            return
        }
        (recyclerView.adapter as BaseQuickAdapter<Any?, *>).apply {
            setNewData(pageLoader.data.value)
            if (pageLoader.diffCallback == null) {
                (pageLoader as PageLoader<Any>).data.observe(pageLoader.lifecycleOwner!!, Observer {
                    notifyDataSetChanged()
                })
            } else {
                pageLoader.diffResult.observe(pageLoader.lifecycleOwner!!, Observer {
                    it?.dispatchUpdatesTo(this)
                })
            }
            pageLoader.scrollToTop.observe(pageLoader.lifecycleOwner!!, Observer {
                recyclerView.scrollToPosition(0)
            })
            pageLoader.loadMoreState.observe(pageLoader.lifecycleOwner!!, Observer {
                when (it) {
                    STATE_FAIL -> loadMoreFail()
                    STATE_END -> loadMoreEnd()
                    else -> {
                        loadMoreComplete()
                        disableLoadMoreIfNotFullPage()
                    }
                }
            })
            setOnLoadMoreListener({
                pageLoader.loadMore()
            }, recyclerView)
        }
    }


    @JvmStatic
    @BindingAdapter(value = ["android:loadMoreState", "android:loadMoreEnable", "android:onLoadMore"], requireAll = false)
    fun bindBaseAdapter(recyclerView: RecyclerView, state: Int?, loadMoreEnable: Boolean?, loadMoreListener: BaseQuickAdapter.RequestLoadMoreListener?) {
        if (recyclerView.adapter == null || recyclerView.adapter !is BaseQuickAdapter<*, *>) {
            return
        }
        (recyclerView.adapter as BaseQuickAdapter<*, *>).apply {
            state?.let {
                when (state) {
                    STATE_FAIL -> loadMoreFail()
                    STATE_END -> loadMoreEnd()
                    else -> {
                        loadMoreComplete()
                        disableLoadMoreIfNotFullPage()
                    }
                }
            }
            loadMoreEnable?.let {
                setEnableLoadMore(loadMoreEnable)
            }
            loadMoreListener?.let {
                setOnLoadMoreListener(loadMoreListener, recyclerView)
            }
        }
    }
}
