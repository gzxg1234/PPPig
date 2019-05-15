package com.sanron.pppig.binding

import androidx.lifecycle.Observer
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.sanron.pppig.common.PageLoader

/**
 *Author:sanron
 *Time:2019/4/17
 *Description:
 */


@BindingAdapter(value = ["android:adapterData"])
fun setData(recyclerView: androidx.recyclerview.widget.RecyclerView, data: List<*>) {
    val adapter = recyclerView.adapter
    if (adapter is BaseQuickAdapter<*, *>) {
        if (adapter.data == data) {
            adapter.notifyDataSetChanged()
        } else {
            (adapter as BaseQuickAdapter<Any, *>).setNewData(data)
        }
    }
}

@BindingAdapter(value = ["android:diffResult"])
fun setDiffResult(recyclerView: androidx.recyclerview.widget.RecyclerView, diffResult: DiffUtil.DiffResult) {
    recyclerView.adapter?.apply {
        diffResult.dispatchUpdatesTo(this)
    }
}

@BindingAdapter(value = ["android:loadMoreState", "android:loadMoreEnable", "android:onLoadMore"], requireAll = false)
fun bindBaseAdapter(recyclerView: androidx.recyclerview.widget.RecyclerView, state: Int?, loadMoreEnable: Boolean?, loadMoreListener: BaseQuickAdapter.RequestLoadMoreListener?) {
    if (recyclerView.adapter == null || recyclerView.adapter !is BaseQuickAdapter<*, *>) {
        return
    }
    (recyclerView.adapter as BaseQuickAdapter<*, *>).apply {
        state?.let {
            when (state) {
                RecyclerViewAdapter.STATE_FAIL -> loadMoreFail()
                RecyclerViewAdapter.STATE_END -> loadMoreEnd()
                else -> {
                    loadMoreComplete()
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

object RecyclerViewAdapter {
    val STATE_FAIL = 0
    val STATE_COMPLETE = 1
    val STATE_END = 2

}
