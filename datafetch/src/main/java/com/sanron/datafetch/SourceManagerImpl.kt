package com.sanron.datafetch

import android.annotation.SuppressLint
import android.content.Context
import com.sanron.datafetch.source.kkkkmao.KMaoFetch
import com.sanron.datafetch.source.moyan.MoyanFetch
import com.sanron.datafetch.source.nianlun.NianlunFetch
import com.sanron.datafetch_interface.Source
import com.sanron.datafetch_interface.SourceManager
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
                Source("kkkkmao", "快看影视", KMaoFetch()),
                Source("moyan", "陌颜影视", MoyanFetch()),
                Source("nianlun", "年轮影视", NianlunFetch())
        )
    }

    override fun getSourceList(): List<Source> = SOURCE_LIST
}
