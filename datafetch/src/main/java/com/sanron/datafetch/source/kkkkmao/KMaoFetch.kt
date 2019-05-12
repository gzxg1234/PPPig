package com.sanron.datafetch.source.kkkkmao

import com.sanron.datafetch.SourceManagerImpl
import com.sanron.datafetch.WebHelper
import com.sanron.datafetch.exception.ParseException
import com.sanron.datafetch.source.flifli.FliFilter
import com.sanron.datafetch_interface.DataFetch
import com.sanron.datafetch_interface.bean.*
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import org.jsoup.Jsoup
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.io.IOException
import java.util.*

/**
 * Author:sanron
 * Time:2019/5/8
 * Description:
 */
class KMaoFetch : DataFetch {
    companion object {
        const val TYPE_MOVIE = 1
        const val TYPE_TV = 2
        const val TYPE_VARIETY = 3
        const val TYPE_ANIM = 4
        val TYPES = listOf(
                VideoListType("电影", TYPE_MOVIE),
                VideoListType("电视剧", TYPE_TV),
                VideoListType("综艺", TYPE_VARIETY),
                VideoListType("动漫", TYPE_ANIM)
        )
    }

    override fun getVideoListTypes(): List<VideoListType> {
        return TYPES
    }

    private val mRetrofit: Retrofit by lazy {
        return@lazy Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createAsync())
                .client(SourceManagerImpl.okHttpClient)
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

    override fun getHomeData(): Observable<Home> {
        return getService(KmaoApi::class.java)
                .home()
                .map { s ->
                    val data = KKMaoParser.instance.parseHome(s.string())
                    return@map data
                }
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

    override fun getVideoSource(url: String): Observable<List<String>> {
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
                        WebHelper.getHtml(SourceManagerImpl.context, sourceUrl, header, object : WebHelper.Callback {
                            override fun success(result: String) {
                                val doc = Jsoup.parse(result)
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

    override fun getVideoListFilter(type: Int): Map<String, List<FilterItem>> {
        if (type == TYPE_MOVIE) {
            return FliFilter.moveListFilter()
        } else if (type == TYPE_TV) {
            return FliFilter.tvListFilter()
        } else if (type == TYPE_VARIETY) {
            return FliFilter.varietyListFilter()
        } else if (type == TYPE_ANIM) {
            return FliFilter.animListFilter()
        }
        return mapOf()
    }

    private fun getTvList(params: Map<String, FilterItem>, page: Int): Observable<PageData<VideoItem>> {
        return getService(KmaoApi::class.java)
                .tvList(params["类型"]?.value ?: "", params["状态"]?.value
                        ?: "", params["国家"]?.value ?: "", params["年代"]?.value ?: "", page)
                .map { s -> KKMaoParser.instance.parseVideoList(s.string()) }
    }

    private fun getMovieList(params: Map<String, FilterItem>, page: Int): Observable<PageData<VideoItem>> {
        return getService(KmaoApi::class.java)
                .movieList(params["类型"]?.value ?: "movie", params["国家"]?.value
                        ?: "", params["年代"]?.value ?: "", page)
                .map { s -> KKMaoParser.instance.parseVideoList(s.string()) }
    }

    private fun getVarietyList(params: Map<String, FilterItem>, page: Int): Observable<PageData<VideoItem>> {
        return getService(KmaoApi::class.java)
                .varietyList(params["类型"]?.value ?: "", params["状态"]?.value
                        ?: "", params["国家"]?.value ?: "", params["年代"]?.value ?: "", page)
                .map { s -> KKMaoParser.instance.parseVideoList(s.string()) }
    }

    private fun getAnimList(params: Map<String, FilterItem>, page: Int): Observable<PageData<VideoItem>> {
        return getService(KmaoApi::class.java)
                .animList(params["类型"]?.value ?: "", params["状态"]?.value
                        ?: "", params["国家"]?.value ?: "", params["年代"]?.value ?: "", page)
                .map { s -> KKMaoParser.instance.parseVideoList(s.string()) }
    }

    override fun getVideoList(type: Int, param: Map<String, FilterItem>, page: Int): Observable<PageData<VideoItem>> {
        if (type == TYPE_MOVIE) {
            return getMovieList(param, page)
        } else if (type == TYPE_TV) {
            return getTvList(param, page)
        } else if (type == TYPE_VARIETY) {
            return getVarietyList(param, page)
        } else if (type == TYPE_ANIM) {
            return getAnimList(param, page)
        }
        return Observable.empty()
    }
}
