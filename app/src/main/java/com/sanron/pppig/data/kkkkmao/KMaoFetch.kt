package com.sanron.pppig.data.kkkkmao

import com.google.gson.GsonBuilder
import com.sanron.pppig.BuildConfig
import com.sanron.pppig.data.DataFetch
import com.sanron.pppig.data.Injector
import com.sanron.pppig.data.WebPageHelper
import com.sanron.pppig.data.bean.micaitu.Home
import com.sanron.pppig.data.bean.micaitu.PageData
import com.sanron.pppig.data.bean.micaitu.VideoDetail
import com.sanron.pppig.data.bean.micaitu.VideoItem
import com.sanron.pppig.data.exception.ParseException
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import okhttp3.OkHttpClient
import org.jsoup.Jsoup
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.*

/**
 * Author:sanron
 * Time:2019/5/8
 * Description:
 */
class KMaoFetch : DataFetch {

    private val mRetrofit: Retrofit by lazy {
        val gsonBuilder = GsonBuilder()
        gsonBuilder.setDateFormat("yyyy-MM-dd HH:mm:ss")
        return@lazy Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createAsync())
                .addConverterFactory(GsonConverterFactory.create(gsonBuilder.create()))
                .client(createOkHttp())
                .validateEagerly(BuildConfig.DEBUG)
                .baseUrl("https://m.kkkkmao.com")
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

    override fun getMicaituHome(): Observable<Home> {
        return getService(KmaoApi::class.java)
                .home()
                .map { s -> KKMaoParser.instance.parseHome(s.string()) }
    }

    override fun getTopMovie(): Observable<PageData<VideoItem>> {
        return getService(KmaoApi::class.java)
                .topMovie()
                .map { s -> KKMaoParser.instance.parseTopMovie(s.string()) }
    }

    override fun getVideoDetail(path: String): Observable<VideoDetail> {
        return getService(KmaoApi::class.java)
                .html(path)
                .map { responseBody -> KKMaoParser.instance.parseVideoDetail(responseBody.string()) }
    }

    override fun getVideoSource(url: String, webPageHelper: WebPageHelper): Observable<List<String>> {
        return getService(KmaoApi::class.java)
                .html(url)
                .flatMap { responseBody ->
                    val sourceUrl = KKMaoParser.instance.parsePlayPageUrl(responseBody.string())
                            ?: throw ParseException("解析url失败")
                    val referer = "http://m.kkkkmao.com/$url"
                    val header = mutableMapOf<String, String>()
                    header["Referer"] = referer
                    Observable.create(ObservableOnSubscribe<String> { })
                    return@flatMap Observable.create(ObservableOnSubscribe<List<String>> {
                        webPageHelper.getHtml(sourceUrl, header, object : WebPageHelper.Callback {
                            override fun success(html: String) {
                                val doc = Jsoup.parse(html)
                                val list = mutableListOf<String>()
                                doc.select("video").forEach {
                                    list.add(it.attr("src"))
                                }
                                it.onNext(list)
                                it.onComplete()
                            }

                            override fun error(msg: String) {
                                it.onError(IOException(msg))
                            }
                        })
                    })
                }
    }

    override fun getAll(type: String?, country: String?, year: String?, page: Int): Observable<PageData<VideoItem>> {
        return getService(KmaoApi::class.java)
                .all(type ?: "movie", page, year ?: "", country ?: "")
                .map { s -> KKMaoParser.instance.parseMovieList(s.string()) }
    }
}
