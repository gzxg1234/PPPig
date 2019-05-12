package com.sanron.datafetch

import android.annotation.SuppressLint
import android.content.Context
import com.sanron.datafetch.kkkkmao.KMaoFetch
import com.sanron.datafetch.moyan.MoyanFetch
import com.sanron.datafetch_interface.Source
import com.sanron.datafetch_interface.SourceManager

/**
 * @author chenrong
 * @date 2019/5/11
 */
class SourceManagerImpl : SourceManager {
    override fun init(context: Context) {
        SourceManagerImpl.context = context.applicationContext
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
        var SOURCE_LIST = listOf(
                Source("看看猫", KMaoFetch()),
                Source("陌颜影视", MoyanFetch())
        )
    }


    override fun getSourceList(): List<Source> = SOURCE_LIST
}
