//package com.sanron.pppig.binding
//
//import android.databinding.*
//import com.sanron.pppig.widget.PiRefreshLayout
//import com.scwang.smartrefresh.layout.constant.RefreshState
//import com.scwang.smartrefresh.layout.listener.OnRefreshListener
//
///**
// *Author:sanron
// *Time:2019/4/17
// *Description:
// */
//
//
//@InverseBindingMethods(InverseBindingMethod(type = PiRefreshLayout::class, attribute = "android:refreshing"))
//object SmartRefreshAdapter {
//    @JvmStatic
//    @InverseBindingAdapter(attribute = "android:refreshing", event = "android:refreshingAttrChanged")
//    fun getRefreshing(view: PiRefreshLayout): Boolean {
//        return view.state == RefreshState.Refreshing
//    }
//
//    @JvmStatic
//    @BindingAdapter(value = ["android:onRefresh", "android:refreshingAttrChanged"], requireAll = false)
//    fun setOnRefreshListener(view: PiRefreshLayout,
//                             onRefreshListener: OnRefreshListener?,
//                             refreshingAttrChanged: InverseBindingListener?) {
//
//        if (refreshingAttrChanged == null) {
//            view.setOnRefreshListener(onRefreshListener)
//        } else {
//            view.setOnRefreshListener {
//                refreshingAttrChanged.onChange()
//                onRefreshListener?.onRefresh(view)
//            }
//        }
//    }
//
//    @JvmStatic
//    @BindingAdapter("android:refreshing")
//    fun setRefreshState(refreshLayout: PiRefreshLayout, refresh: Boolean) {
//        if (refresh) {
//            refreshLayout.autoRefresh()
//        } else {
//            refreshLayout.finishRefresh()
//        }
//    }
//}