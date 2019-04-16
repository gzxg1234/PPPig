package com.sanron.pppig.util

import android.util.Log

import com.sanron.pppig.BuildConfig


/**
 * Author:sanron
 * Time:2018/7/16
 * Description:
 */
object CLog {

    private val DEBUG = BuildConfig.DEBUG

    fun d(tag: String, msg: String) {
        if (DEBUG) {
            Log.d(tag, msg)
        }
    }

    fun i(tag: String, msg: String) {
        if (DEBUG) {
            Log.i(tag, msg)
        }
    }

    fun w(tag: String, msg: String) {
        if (DEBUG) {
            Log.w(tag, msg)
        }
    }

    fun e(tag: String, msg: String) {
        if (DEBUG) {
            Log.e(tag, msg)
        }
    }

    fun e(tag: String, msg: String, e: Throwable) {
        if (DEBUG) {
            Log.e(tag, msg, e)
        }
    }
}
