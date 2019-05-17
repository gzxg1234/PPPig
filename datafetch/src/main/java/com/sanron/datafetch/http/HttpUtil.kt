package com.sanron.datafetch.http

import com.sanron.datafetch.BuildConfig
import com.sanron.datafetch.SourceManagerImpl
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory

/**
 *
 * @author chenrong
 * @date 2019/5/18
 */
object HttpUtil {

    private val mRetrofit: Retrofit by lazy {
        return@lazy Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createAsync())
                .client(SourceManagerImpl.okHttpClient)
                .validateEagerly(BuildConfig.DEBUG)
                .baseUrl("http:/localhost")
                .build()
    }

    val api: CommonApi by lazy {
        mRetrofit.create(CommonApi::class.java)
    }

}