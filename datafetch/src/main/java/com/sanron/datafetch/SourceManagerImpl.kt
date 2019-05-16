package com.sanron.datafetch

import android.annotation.SuppressLint
import android.content.Context
import com.sanron.datafetch.livesource.haoqu.HaoquFetch
import com.sanron.datafetch.videosource.kkkkmao.KMaoDataFetch
import com.sanron.datafetch.videosource.moyan.MoyanDataFetch
import com.sanron.datafetch.videosource.nianlun.NianlunDataFetch
import com.sanron.datafetch_interface.SourceManager
import com.sanron.datafetch_interface.live.LiveSource
import com.sanron.datafetch_interface.video.VideoSource
import okhttp3.OkHttpClient

/**
 * @author chenrong
 * @date 2019/5/11
 */
class SourceManagerImpl : SourceManager {

    override fun getVersion(): Int = 1

    override fun setHttpClient(okHttpClient: OkHttpClient) {
        SourceManagerImpl.okHttpClient = okHttpClient
    }

    override fun initContext(context: Context) {
        SourceManagerImpl.context = context.applicationContext
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        internal lateinit var context: Context
        internal lateinit var okHttpClient: OkHttpClient

        var SOURCE_LIST = listOf(
                VideoSource("kkkkmao", "快看影视", KMaoDataFetch()),
                VideoSource("moyan", "陌颜影视", MoyanDataFetch()),
                VideoSource("nianlun", "年轮影视", NianlunDataFetch())
        )
        var LIVE_LIST = listOf(
                LiveSource("haoqu", "好趣", HaoquFetch())
        )
    }

    override fun getLiveSourceList(): List<LiveSource> = LIVE_LIST

    override fun getVideoSourceList(): List<VideoSource> = SOURCE_LIST
}
