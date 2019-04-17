package com.sanron.pppig.binding

import android.databinding.*
import android.support.v4.widget.SwipeRefreshLayout

/**
 *Author:sanron
 *Time:2019/4/17
 *Description:
 */


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