package com.sanron.pppig.data

import android.annotation.SuppressLint
import com.sanron.pppig.app.PiApp

/**
 *Author:sanron
 *Time:2019/5/15
 *Description:
 */
@SuppressLint("StaticFieldLeak")
object AppPref {

    var autoPlayNext by AppSP("autoPlayNext", true)

    class AppSP<T>(name: String, default: T) : Preference<T>(PiApp.sInstance, "APP_CONFIG", name, default)
}