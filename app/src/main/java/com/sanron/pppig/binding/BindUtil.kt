package com.sanron.pppig.binding

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.chad.library.adapter.base.BaseQuickAdapter
import com.sanron.pppig.common.PageLoader
import com.sanron.pppig.widget.loadlayout.LoadLayout

/**
 *Author:sanron
 *Time:2019/5/20
 *Description:
 */
fun <R : LiveData<Int>> LoadLayout.bindStateValue(lifecycleOwner: LifecycleOwner, state: R) {
    state.observe(lifecycleOwner, Observer {
        it?.let {
            showState(it)
        }
    })
}

fun LoadLayout.bindPageLoader(lifecycleOwner: LifecycleOwner, pageLoader: PageLoader<*>) {
    setOnReloadListener {
        pageLoader.refresh()
    }
    bindStateValue(lifecycleOwner, pageLoader.loadState)
}

fun <T> RecyclerView.bindPageLoader(lifecycleOwner: LifecycleOwner, pageLoader: PageLoader<T>) {
    if (adapter == null || adapter !is BaseQuickAdapter<*, *>) {
        return
    }
    (adapter as BaseQuickAdapter<T, *>).let { adapter ->
        adapter.setNewData(pageLoader.listData.value)
        if (pageLoader.diffCallback == null) {
            pageLoader.listData.observe(lifecycleOwner, Observer {
                adapter.notifyDataSetChanged()
            })
        } else {
            pageLoader.diffResult.observe(lifecycleOwner, Observer {
                it?.dispatchUpdatesTo(adapter)
            })
        }
        pageLoader.scrollToTop.observe(lifecycleOwner, Observer {
            this.scrollToPosition(0)
        })
        pageLoader.loadMoreState.observe(lifecycleOwner, Observer {
            when (it) {
                RecyclerViewAdapter.STATE_FAIL -> adapter.loadMoreFail()
                RecyclerViewAdapter.STATE_END -> adapter.loadMoreEnd()
                else -> {
                    adapter.loadMoreComplete()
                }
            }
        })
        adapter.setOnLoadMoreListener({
            pageLoader.loadMore()
        }, this)
    }
}

fun <T> SwipeRefreshLayout.bindPageLoader(lifecycleOwner: LifecycleOwner, pageLoader: PageLoader<T>) {
    setOnRefreshListener {
        pageLoader.refresh()
    }
    pageLoader.refreshing.observe(lifecycleOwner, Observer {
        if (!isAttachedToWindow && it == true) {
            postDelayed({
                isRefreshing = true
            }, 100)
            return@Observer
        }
        isRefreshing = it ?: false
    })
}