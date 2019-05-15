package com.sanron.pppig.util

import androidx.annotation.Size

/**
 *Author:sanron
 *Time:2019/4/26
 *Description:
 */
fun <T : Comparable<T>> min(@Size(min = 1) vararg v: T): T {
    var min = v[0]
    v.forEach {
        if (it < min) {
            min = it
        }
    }
    return min
}

fun <T : Comparable<T>> T.limit(min: T? = null, max: T? = null): T {
    var i = this
    min?.let {
        if (i < min) {
            i = min
        }
    }
    max?.let {
        if (i > max) {
            i = max
        }
    }
    return i
}
