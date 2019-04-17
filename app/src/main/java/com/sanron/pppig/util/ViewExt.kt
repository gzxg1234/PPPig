package com.sanron.pppig.util

import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.view.ViewGroup
import com.dinuscxj.itemdecoration.LinearDividerItemDecoration
import com.facebook.drawee.backends.pipeline.Fresco
import com.sanron.pppig.widget.GridDividerItemDecoration
import com.sanron.pppig.widget.SimpleSizeDrawable

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

/**
 * 设置间距
 */
fun RecyclerView.gap(hGap: Int = 0, vGap: Int = 0) {
    if (layoutManager is GridLayoutManager) {
        val ori = if ((layoutManager as GridLayoutManager).orientation == GridLayoutManager.HORIZONTAL)
            GridDividerItemDecoration.GRID_DIVIDER_HORIZONTAL
        else
            GridDividerItemDecoration.GRID_DIVIDER_VERTICAL
        addItemDecoration(GridDividerItemDecoration(context, ori).apply {
            setHorizontalDivider(SimpleSizeDrawable(width = hGap))
            setVerticalDivider(SimpleSizeDrawable( height = vGap))
        })
    } else if (layoutManager is LinearLayoutManager) {
        val ori = if ((layoutManager as LinearLayoutManager).orientation == GridLayoutManager.HORIZONTAL)
            LinearDividerItemDecoration.LINEAR_DIVIDER_HORIZONTAL
        else
            LinearDividerItemDecoration.LINEAR_DIVIDER_VERTICAL
        addItemDecoration(LinearDividerItemDecoration(context, ori).apply {
            setDivider(SimpleSizeDrawable(width = hGap, height = hGap))
        })
    } else {
        Log.w("RecyclerView", "不支持其他布局")
    }
}
