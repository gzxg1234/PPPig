package com.sanron.pppig.binding

import androidx.lifecycle.Observer
import androidx.databinding.*
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.sanron.pppig.common.PageLoader

/**
 *Author:sanron
 *Time:2019/4/17
 *Description:
 */

@InverseBindingMethods(InverseBindingMethod(type = androidx.swiperefreshlayout.widget.SwipeRefreshLayout::class, attribute = "android:refreshing"))
object SwipeRefreshAdapter {
    @JvmStatic
    @InverseBindingAdapter(attribute = "android:refreshing", event = "android:refreshingAttrChanged")
    fun getRefreshing(view: androidx.swiperefreshlayout.widget.SwipeRefreshLayout): Boolean {
        return view.isRefreshing
    }

    @JvmStatic
    @BindingAdapter(value = ["android:onRefresh", "android:refreshingAttrChanged"], requireAll = false)
    fun setOnRefreshListener(view: androidx.swiperefreshlayout.widget.SwipeRefreshLayout,
                             onRefreshListener: androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener?,
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
    fun setRefreshState(refreshLayout: androidx.swiperefreshlayout.widget.SwipeRefreshLayout, refresh: Boolean) {
        if (!refreshLayout.isAttachedToWindow && refresh) {
            refreshLayout.postDelayed({
                refreshLayout.isRefreshing = refresh
            }, 100)
            return
        }
        refreshLayout.isRefreshing = refresh
    }
}