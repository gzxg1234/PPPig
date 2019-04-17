package com.sanron.pppig.util

import android.widget.Toast
import com.sanron.pppig.app.PiApp

/**
 *Author:sanron
 *Time:2019/4/17
 *Description:
 */
var toast: Toast? = null

fun showToast(msg: CharSequence, duration: Int = Toast.LENGTH_SHORT) {
    toast?.apply {
        cancel()
    }
    toast = Toast.makeText(PiApp.sInstance, msg, duration)
    toast!!.show()
}

