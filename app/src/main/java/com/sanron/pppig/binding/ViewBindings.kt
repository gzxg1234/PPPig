package com.sanron.pppig.binding

import android.databinding.*
import android.view.View
import android.widget.ImageView
import com.facebook.drawee.view.SimpleDraweeView
import com.sanron.pppig.widget.PiRefreshLayout
import com.scwang.smartrefresh.layout.constant.RefreshState
import com.scwang.smartrefresh.layout.listener.OnRefreshListener
import com.tmall.ultraviewpager.UltraViewPager

/**
 * Author:sanron
 * Time:2019/2/21
 * Description:
 */

@BindingAdapter(value = ["android:autoScroll"])
fun setAutoScroll(pager: UltraViewPager, time: Int) {
    if (time > 0) {
        pager.setAutoScroll(time)
    } else {
        pager.disableAutoScroll()
    }
}

@BindingAdapter(value = ["android:imgRes"])
fun setImageRes(view: ImageView, resId: Int) {
    view.setImageResource(resId)
}

@BindingAdapter(value = ["android:url"])
fun setFrescoUrl(sdv: SimpleDraweeView, url: String?) {
    sdv.setImageURI(url)
}

@BindingAdapter(value = ["android:gone"])
fun setGone(view: View, gone: Boolean) {
    view.visibility = if (gone) View.GONE else View.VISIBLE
}

