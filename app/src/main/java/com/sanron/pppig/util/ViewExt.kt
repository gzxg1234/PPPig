package com.sanron.pppig.util

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
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

inline fun ViewGroup.childForEach(action: (index: Int, v: View) -> Unit) {
    for (i in 0 until this.childCount) {
        action(i, this.getChildAt(i))
    }
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
            setVerticalDivider(SimpleSizeDrawable(height = vGap))
        })
    } else if (layoutManager is androidx.recyclerview.widget.LinearLayoutManager) {
        val ori = if ((layoutManager as androidx.recyclerview.widget.LinearLayoutManager).orientation == GridLayoutManager.HORIZONTAL)
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

/**
 * EditText绑定清除按钮
 */
fun EditText.bindClear(view: View) {
    view.setOnClickListener {
        this.setText("")
    }
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            if (s?.length ?: 0 == 0) {
                view.visibility = View.GONE
            } else {
                view.visibility = View.VISIBLE
            }
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }
    })
}

