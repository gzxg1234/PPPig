package com.sanron.datafetch_interface

import android.content.Context
import okhttp3.OkHttpClient

/**
 *
 * @author chenrong
 * @date 2019/5/11
 */
interface SourceManager {

    fun initContext(context: Context)

    fun getSourceList(): List<Source>

    fun setHttpClient(okHttpClient:OkHttpClient)

}