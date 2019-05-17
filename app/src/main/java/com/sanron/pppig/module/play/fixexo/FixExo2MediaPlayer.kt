package com.sanron.pppig.module.play.fixexo

import android.content.Context
import android.net.Uri
import com.google.android.exoplayer2.source.hls.HlsDataSourceFactory
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import tv.danmaku.ijk.media.exo2.IjkExo2MediaPlayer
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Proxy

/**
 *Author:sanron
 *Time:2019/5/17
 *Description:
 */
class FixExo2MediaPlayer(context: Context?) : IjkExo2MediaPlayer(context) {

    override fun setDataSource(context: Context?, uri: Uri?) {
        super.setDataSource(context, uri)
        hookMediaSource()
    }

    fun hookMediaSource() {
        try {
            if (mMediaSource is HlsMediaSource) {
                val field1 = HlsMediaSource::class.java.getDeclaredField("dataSourceFactory")
                field1.isAccessible = true
                val originDataSourceFactory = field1.get(mMediaSource) as HlsDataSourceFactory
                field1.set(mMediaSource, Proxy.newProxyInstance(HlsDataSourceFactory::class.java.classLoader,
                        arrayOf(HlsDataSourceFactory::class.java), InvocationHandler { proxy, method, args ->
                    method.invoke(originDataSourceFactory,args[0])?.let { dataSource ->
                        dataSource::class.java.getDeclaredField("allowCrossProtocolRedirects").apply {
                            isAccessible = true
                            set(dataSource, true)
                        }
                        return@InvocationHandler dataSource
                    }
                    return@InvocationHandler null
                }))
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }
}