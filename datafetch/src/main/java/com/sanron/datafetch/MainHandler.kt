package com.sanron.datafetch

import android.os.Handler
import android.os.Looper

/**
 *
 * @author chenrong
 * @date 2019/5/12
 */
object MainHandler : Handler(Looper.getMainLooper())

fun runOnUiThread(run: () -> Unit) {
    if (Thread.currentThread() == Looper.getMainLooper().thread) {
        run()
    } else {
        MainHandler.post {
            run()
        }
    }
}