package com.sanron.datafetch

import android.net.Uri
import java.net.URLEncoder

/**
 *Author:sanron
 *Time:2019/5/17
 *Description:
 */

fun String?.completeUrl(host: String): String {
    return when {
        this == null -> ""
        Uri.parse(this).host.isNullOrEmpty() -> host + this
        else -> this
    }
}

fun String?.urlEncode(): String? {
    return if (this == null) {
        ""
    } else {
        URLEncoder.encode(this, "utf-8")
    }
}
