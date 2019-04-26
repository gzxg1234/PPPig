package com.sanron.pppig.util

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
        var a: Int = color shr 24 and 0xFF
        var r = color ushr 16 and 0xFF
        var g = color ushr 8 and 0xFF
        var b = color and 0xFF

        val grey = toGrey(color)
        if (grey > GREY_THRESHOLD) {
            var v: Int = (grey - GREY_THRESHOLD / (0.299 + 0.587 + 0.114)).toInt()
            //防止减到负数
            v = min(v, r, g, b)
            r -= v
            g -= v
            b -= v
        }
        return argb(a, r, g, b)
    }
}