package com.sanron.pppig.util

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.facebook.drawee.backends.pipeline.Fresco

/**
 *Author:sanron
 *Time:2019/4/15
 *Description:
 */
fun View.setPadding(paddingLeft: Int? = null, paddingTop: Int? = null, paddingRight: Int? = null, paddingBottom: Int? = null) {
    this.setPadding(paddingLeft ?: this.paddingLeft,
            paddingTop ?: this.paddingTop,
            paddingRight ?: this.paddingRight,
            paddingBottom ?: this.paddingBottom)
}

fun View.setMargin(left: Int? = null, top: Int? = null, right: Int? = null, bottom: Int? = null) {
    this.layoutParams?.apply {
        if (this@apply is ViewGroup.MarginLayoutParams) {
            left?.run { leftMargin = left }
            top?.run { topMargin = top }
            right?.run { rightMargin = right }
            bottom?.run { bottomMargin = bottom }
        }
    }
}

fun RecyclerView.pauseFrescoOnScroll() {
    addOnScrollListener(object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                Fresco.getImagePipeline().resume()
            } else {
                Fresco.getImagePipeline().pause()
            }
        }
    })
}
