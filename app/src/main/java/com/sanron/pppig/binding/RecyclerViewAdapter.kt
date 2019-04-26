package com.sanron.pppig.binding

import android.arch.lifecycle.Observer
import android.databinding.BindingAdapter
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.sanron.pppig.common.PageLoader

/**
 *Author:sanron
 *Time:2019/4/17
 *Description:
 */


@Suppress("UNCHECKED_CAST")
@BindingAdapter(value = ["android:pageLoader"], requireAll = false)
fun bindPageLoader(recyclerView: RecyclerView, pageLoader: PageLoader<*>) {
    if (recyclerView.adapter == null || recyclerView.adapter !is BaseQuickAdapter<*, *> || pageLoader.lifecycleOwner == null) {
        return
    }
    (recyclerView.adapter as BaseQuickAdapter<Any?, *>).apply {
        setNewData(pageLoader.listData.value)
        if (pageLoader.diffCallback == null) {
            (pageLoader as PageLoader<Any>).listData.observe(pageLoader.lifecycleOwner!!, Observer {
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
                RecyclerViewAdapter.STATE_FAIL -> loadMoreFail()
                RecyclerViewAdapter.STATE_END -> loadMoreEnd()
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

@BindingAdapter(value = ["android:adapterData"])
fun setData(recyclerView: RecyclerView, data: List<*>) {
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
fun setDiffResult(recyclerView: RecyclerView, diffResult: DiffUtil.DiffResult) {
    recyclerView.adapter?.apply {
        diffResult.dispatchUpdatesTo(this)
    }
}

@BindingAdapter(value = ["android:loadMoreState", "android:loadMoreEnable", "android:onLoadMore"], requireAll = false)
fun bindBaseAdapter(recyclerView: RecyclerView, state: Int?, loadMoreEnable: Boolean?, loadMoreListener: BaseQuickAdapter.RequestLoadMoreListener?) {
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

object RecyclerViewAdapter {
    val STATE_FAIL = 0
    val STATE_COMPLETE = 1
    val STATE_END = 2

}
