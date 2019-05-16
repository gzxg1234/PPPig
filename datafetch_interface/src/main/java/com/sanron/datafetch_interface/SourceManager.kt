package com.sanron.datafetch_interface

import android.content.Context
import com.sanron.datafetch_interface.live.LiveSource
import com.sanron.datafetch_interface.video.VideoSource
import okhttp3.OkHttpClient

/**
 *
 * @author chenrong
 * @date 2019/5/11
 */
interface SourceManager {

    /**
     * 版本
     */
    fun getVersion(): Int

    /**
     * 设置Context
     */
    fun initContext(context: Context)

    /**
     * 获取视频数据资源
     */
    fun getVideoSourceList(): List<VideoSource>

    /**
     * 直播数据资源
     */
    fun getLiveSourceList():List<LiveSource>

    /**
     * 设置HttpClient
     */
    fun setHttpClient(okHttpClient: OkHttpClient)

}