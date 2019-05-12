package com.sanron.pppig.binding

import android.arch.lifecycle.Observer
import android.databinding.*
import android.support.v4.widget.SwipeRefreshLayout
import com.sanron.pppig.common.PageLoader

/**
 *Author:sanron
 *Time:2019/4/17
 *Description:
 */

@BindingAdapter(value = ["android:pageLoader"], requireAll = false)
fun bindPageLoader(refreshLayout: SwipeRefreshLayout, pageLoader: PageLoader<*>) {
    refreshLayout.setOnRefreshListener {
        pageLoader.refreshing.value = true
        pageLoader.refresh()
    }
    pageLoader.refreshing.observe(pageLoader.lifecycleOwner!!, Observer {
        if (!refreshLayout.isAttachedToWindow && it == true) {
            refreshLayout.postDelayed({
                refreshLayout.isRefreshing = true
            }, 100)
            return@Observer
        }
        refreshLayout.isRefreshing = it ?: false
    })
}

@InverseBindingMethods(InverseBindingMethod(type = SwipeRefreshLayout::class, attribute = "android:refreshing"))
object SwipeRefreshAdapter {
    @JvmStatic
    @InverseBindingAdapter(attribute = "android:refreshing", event = "android:refreshingAttrChanged")
    fun getRefreshing(view: SwipeRefreshLayout): Boolean {
        return view.isRefreshing
    }

    @JvmStatic
    @BindingAdapter(value = ["android:onRefresh", "android:refreshingAttrChanged"], requireAll = false)
    fun setOnRefreshListener(view: SwipeRefreshLayout,
                             onRefreshListener: SwipeRefreshLayout.OnRefreshListener?,
                             refreshingAttrChanged: InverseBindingListener?) {

        if (refreshingAttrChanged == null) {
            view.setOnRefreshListener(onRefreshListener)
        } else {
            view.setOnRefreshListener {
                refreshingAttrChanged.onChange()
                onRefreshListener?.onRefresh()
            }
        }
    }

    @JvmStatic
    @BindingAdapter("android:refreshing")
    fun setRefreshState(refreshLayout: SwipeRefreshLayout, refresh: Boolean) {
        if (!refreshLayout.isAttachedToWindow && refresh) {
            refreshLayout.postDelayed({
                refreshLayout.isRefreshing = refresh
            }, 100)
            return
        }
        refreshLayout.isRefreshing = refresh
    }
}