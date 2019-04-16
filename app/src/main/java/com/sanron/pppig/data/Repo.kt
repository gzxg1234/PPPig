package com.sanron.pppig.data

import com.google.gson.GsonBuilder
import com.sanron.pppig.BuildConfig
import com.sanron.pppig.data.api.MicaituApi
import com.sanron.pppig.data.bean.micaitu.Home
import com.sanron.pppig.data.bean.micaitu.ListData
import com.sanron.pppig.data.bean.micaitu.VideoItem
import com.sanron.pppig.data.parser.KKMaoParser
import io.reactivex.Observable
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

/**
 * Author:sanron
 * Time:2019/2/20
 * Description:
 */
object Repo {

    private val mRetrofit: Retrofit by lazy {
        val gsonBuilder = GsonBuilder()
        gsonBuilder.setDateFormat("yyyy-MM-dd HH:mm:ss")
        return@lazy Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createAsync())
                .addConverterFactory(GsonConverterFactory.create(gsonBuilder.create()))
                .client(createOkHttp())
                .validateEagerly(BuildConfig.DEBUG)
                .baseUrl("https://www.baidu.com")
                .build()
    }

    private val mApis = HashMap<Class<*>, Any>()


    private fun <T> getService(clazz: Class<T>): T {
        var t = mApis[clazz] as T?
        if (t == null) {
            t = mRetrofit.create(clazz)
            mApis[clazz] = t!! as Any
        }
        return t
    }

    private fun createOkHttp(): OkHttpClient {
        val builder = Injector.provideOkHttpClient()
                .newBuilder()
        return builder.build()
    }

    fun getMicaituHome(): Observable<Home> {
        return getService(MicaituApi::class.java)
                .home()
                .map { s -> KKMaoParser.instance.parseHome(s.string()) }
    }

    fun getTopMovie(): Observable<ListData<VideoItem>> {
        return getService(MicaituApi::class.java)
                .topMovie()
                .map { s -> KKMaoParser.instance.parseTopMovie(s.string()) }
    }

    fun getAll(type: String?, country: String?, year: String?, page: Int): Observable<ListData<VideoItem>> {
        return getService(MicaituApi::class.java)
                .all(type ?: "movie", page, year ?: "", country ?: "")
                .map { s -> KKMaoParser.instance.parseMovieList(s.string()) }
    }
}
