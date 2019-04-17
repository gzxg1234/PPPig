package com.sanron.pppig.binding

import android.databinding.BindingAdapter
import android.support.v7.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter

/**
 *Author:sanron
 *Time:2019/4/17
 *Description:
 */

object CommonList {
    val STATE_FAIL = 0
    val STATE_COMPLETE = 1
    val STATE_END = 2

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
