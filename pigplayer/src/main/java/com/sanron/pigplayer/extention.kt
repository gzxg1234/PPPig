package com.sanron.pigplayer

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
