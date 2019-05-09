package com.sanron.pppig.util

import android.graphics.Color

/**
 *Author:sanron
 *Time:2019/4/26
 *Description:
 */
object ColorUtil {
    const val GREY_THRESHOLD = 100

    fun rgb(r: Int, g: Int, b: Int): Int {
        return (r shl 16) or (g shl 8) or b
    }

    fun argb(a: Int, r: Int, g: Int, b: Int): Int {
        return (a shl 24) or rgb(r, g, b)
    }

    /**
     * 颜色转换成灰度值
     *
     * @param rgb 颜色
     * @return　灰度值
     */
    fun toGrey(rgb: Int): Int {
        val b = rgb and 0xFF
        val g = rgb shr 8 and 0xFF
        val r = rgb shr 16 and 0xFF
        return (r * 0.299 + g * 0.587 + b * 0.114).toInt()
    }

    fun covertToDark(color: Int): Int {
        val hsv = FloatArray(3)
        Color.colorToHSV(color,hsv)

        if(hsv[2]>0.6f){
            hsv[2] = 0.6f
        }
        return Color.HSVToColor(hsv)
    }
}