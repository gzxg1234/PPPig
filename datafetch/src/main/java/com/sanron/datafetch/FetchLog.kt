package com.sanron.datafetch

import android.util.Log


/**
 * Author:sanron
 * Time:2018/7/16
 * Description:
 */
object FetchLog {

    var DEBUG = true

    fun d(tag: String, msg: String) {
        if (DEBUG) {
            Log.d(tag, msg)
        }
    }
}
