package com.sanron.datafetch_interface

import android.content.Context
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
     * 获取视频源
     */
    fun getSourceList(): List<Source>

    /**
     * 设置HttpClient
     */
    fun setHttpClient(okHttpClient: OkHttpClient)

}