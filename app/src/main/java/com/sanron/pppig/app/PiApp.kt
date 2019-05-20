package com.sanron.pppig.app

import android.app.Application
import android.content.Context
import android.view.View
import android.webkit.WebView
import androidx.multidex.MultiDex
import com.blankj.utilcode.util.ProcessUtils
import com.facebook.cache.disk.DiskCacheConfig
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.imagepipeline.core.ImagePipelineConfig
import com.sanron.pppig.BuildConfig
import com.sanron.pppig.R
import com.sanron.pppig.base.state.LoadState
import com.sanron.pppig.module.play.fixexo.PigExoPlayerManager
import com.sanron.pppig.widget.loadlayout.LoadLayout
import com.sanron.pppig.widget.loadlayout.ViewCreator
import com.shuyu.gsyvideoplayer.player.IjkPlayerManager
import com.shuyu.gsyvideoplayer.player.PlayerFactory
import com.tencent.bugly.crashreport.CrashReport
import com.tencent.bugly.crashreport.CrashReport.UserStrategy
import tv.danmaku.ijk.media.player.IjkMediaPlayer


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
        initBugly()
        initGsyPlay()
        initLoadSir()
        WebView.setWebContentsDebuggingEnabled(BuildConfig.DEBUG)
    }

    private fun initLoadSir() {
        LoadLayout.initializer = {
            addState(LoadState.ERROR, object : ViewCreator.SimpleViewCreator(R.layout.state_error_layout) {
                override fun onViewCreated(view: View) {
                    super.onViewCreated(view)
                    view.setOnClickListener {
                        onReloadListener?.invoke()
                    }
                }
            })
            addState(LoadState.LOADING, ViewCreator.SimpleViewCreator(R.layout.state_loading_layout))
        }
    }

    private fun initGsyPlay() {
        PlayerFactory.setPlayManager(PigExoPlayerManager::class.java)
        IjkPlayerManager.setLogLevel(if (BuildConfig.DEBUG) IjkMediaPlayer.IJK_LOG_INFO else IjkMediaPlayer.IJK_LOG_SILENT)
    }

    private fun initBugly() {
        val context = applicationContext
        val packageName = context.packageName
        val processName = ProcessUtils.getCurrentProcessName()
        val strategy = UserStrategy(context)
        strategy.isUploadProcess = processName == null || processName == packageName
        CrashReport.initCrashReport(this, BuildConfig.BUGLY_ID, BuildConfig.DEBUG, strategy)
    }

    private fun initFresco() {
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
