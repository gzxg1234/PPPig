package com.sanron.pppig.widget

import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.drawable.Drawable

/**
 *Author:sanron
 *Time:2019/4/17
 *Description:
 */
class SimpleSizeDrawable(var width: Int = 0, var height: Int = 0) : Drawable() {

    override fun getIntrinsicWidth(): Int {
        return width
    }

    override fun getIntrinsicHeight(): Int {
        return height
    }

    override fun draw(canvas: Canvas) {
    }

    override fun setAlpha(alpha: Int) {
    }

    override fun getOpacity(): Int {
        return 0
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
    }

}