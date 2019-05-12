package com.sanron.datafetch

import android.annotation.SuppressLint
import android.content.Context
import com.sanron.datafetch.source.flifli.FliFetch
import com.sanron.datafetch.source.moyan.MoyanFetch
import com.sanron.datafetch_interface.Source
import com.sanron.datafetch_interface.SourceManager
import okhttp3.OkHttpClient

/**
 * @author chenrong
 * @date 2019/5/11
 */
class SourceManagerImpl : SourceManager {

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
                Source("看看猫", FliFetch()),
                Source("陌颜影视", MoyanFetch())
        )
    }


    override fun getSourceList(): List<Source> = SOURCE_LIST
}
