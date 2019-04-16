package com.sanron.pppig.app

import android.app.Application
import android.content.Context
import android.support.multidex.MultiDex
import com.facebook.cache.disk.DiskCacheConfig

import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.imagepipeline.core.ImagePipelineConfig


/**
 * Author:sanron
 * Time:2019/2/20
 * Description:
 */
class PiApp : Application() {

    companion object {
        lateinit var sInstance: PiApp
    }


    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    override fun onCreate() {
        super.onCreate()
        sInstance = this
        initFresco()
    }

    fun initFresco() {
        val MB = 1024 * 1024L
        val config = ImagePipelineConfig.newBuilder(this)
                .setDownsampleEnabled(true)
                .setMainDiskCacheConfig(DiskCacheConfig.newBuilder(this).apply {
                    setMaxCacheSize(100 * MB)
                    setMaxCacheSizeOnLowDiskSpace(50 * MB)
                    setMaxCacheSizeOnVeryLowDiskSpace(20 * MB)
                }.build())
                .setSmallImageDiskCacheConfig(DiskCacheConfig.newBuilder(this).apply {
                    setMaxCacheSize(50 * MB)
                    setMaxCacheSizeOnLowDiskSpace(30 * MB)
                    setMaxCacheSizeOnVeryLowDiskSpace(10 * MB)
                }.build())
                .build()
        Fresco.initialize(this, config)
    }
}
