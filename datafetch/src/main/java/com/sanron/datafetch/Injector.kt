package com.sanron.datafetch


import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Author:sanron
 * Time:2018/7/24
 * Description:
 */


object Injector {

    private val sInstanceHolder = HashMap<Class<*>, Any>()


    fun provideOkHttpClient(): OkHttpClient {
        return get(OkHttpClient::class.java) ?: OkHttpClient.Builder().apply {
            connectTimeout(20000, TimeUnit.MILLISECONDS)
            writeTimeout(30000, TimeUnit.MILLISECONDS)
            readTimeout(30000, TimeUnit.MILLISECONDS)
            hostnameVerifier { hostname, session -> true }
            //添加日志拦截
            if (BuildConfig.DEBUG) {
                val interceptor = HttpLoggingInterceptor()
                interceptor.level = HttpLoggingInterceptor.Level.BODY
                addInterceptor(interceptor)
            }
            this@Injector.put(this)
        }.build()
    }

    private fun put(obj: Any) {
        sInstanceHolder[obj.javaClass] = obj
    }

    private fun <T> get(clazz: Class<T>): T? {
        return sInstanceHolder[clazz] as T?
    }
}

