package com.sanron.datafetch.livesource.haoqu

import com.sanron.datafetch.BuildConfig
import com.sanron.datafetch.SourceManagerImpl
import com.sanron.datafetch.videosource.moyan.MoyanApi
import com.sanron.datafetch_interface.live.LiveDataFetch
import com.sanron.datafetch_interface.live.bean.LiveCat
import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.nio.charset.Charset

/**
 *Author:sanron
 *Time:2019/5/16
 *Description:
 */
class HaoquFetch : LiveDataFetch {

    private val mRetrofit: Retrofit by lazy {
        return@lazy Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createAsync())
                .client(SourceManagerImpl.okHttpClient)
                .validateEagerly(BuildConfig.DEBUG)
                .baseUrl(MoyanApi.BASE_URL)
                .build()
    }

    private val api: HaoquApi by lazy {
        mRetrofit.create(HaoquApi::class.java)
    }


    override fun getLiveCats(): Observable<List<LiveCat>> {
        return api.html("zhibo")
                .map { it ->
                    val cats = HaoquParser.parserCat(String(it.bytes(), Charset.forName("gb2312")))
                    cats.forEach {
                        it.items = api.html(it.link ?: "")
                                .map {
                                    return@map HaoquParser.parseIem(String(it.bytes(), Charset.forName("gb2312")))
                                }
                    }
                    return@map cats
                }
    }
}