package com.sanron.pppig.widget

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet

/**
 * Author:sanron
 * Time:2019/5/13
 * Description:
 * 限制最大高度RecyclerView
 */
class LimitHeightRecyclerView : RecyclerView {


    var maxHeight = -1
        set(value) {
            field = value
            requestLayout()
        }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle)

    override fun onMeasure(widthSpec: Int, heightSpec: Int) {
        super.onMeasure(widthSpec, heightSpec)
        if (maxHeight >= 0) {
            if (measuredHeight > maxHeight) {
                setMeasuredDimension(measuredWidth, maxHeight)
            }
        }
    }
}
