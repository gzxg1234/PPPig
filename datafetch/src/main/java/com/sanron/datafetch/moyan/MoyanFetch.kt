package com.sanron.datafetch.moyan

import com.sanron.datafetch.BuildConfig
import com.sanron.datafetch.SourceManagerImpl
import com.sanron.datafetch.WebHelper
import com.sanron.datafetch.exception.ParseException
import com.sanron.datafetch.kkkkmao.KmaoApi
import com.sanron.datafetch_interface.DataFetch
import com.sanron.datafetch_interface.bean.*
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.util.*

/**
 * Author:sanron
 * Time:2019/5/8
 * Description:
 */
class MoyanFetch : DataFetch {

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

    private val mRetrofit: Retrofit by lazy {
        return@lazy Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createAsync())
                .client(SourceManagerImpl.okHttpClient)
                .validateEagerly(BuildConfig.DEBUG)
                .baseUrl(MoyanApi.BASE_URL)
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
        return getService(MoyanApi::class.java)
                .home()
                .map { s ->
                    MoyanParser.instance.parseHome(s.string())
                }
    }

    override fun getTopMovie(): Observable<PageData<VideoItem>> {
        return getService(KmaoApi::class.java)
                .topMovie()
                .map { s -> MoyanParser.instance.parseTopMovie(s.string()) }
    }

    override fun getVideoDetail(path: String): Observable<VideoDetail> {
        return getService(MoyanApi::class.java)
                .html(path)
                .map { responseBody -> MoyanParser.instance.parseVideoDetail(responseBody.string()) }
    }

    override fun getVideoSource(url: String): Observable<List<String>> {
        return Observable.create(ObservableOnSubscribe<String> { emitter ->
            val cancellable = MoyanVideoUrlHelper.getVideoPageUrl(SourceManagerImpl.context, MoyanApi.BASE_URL + url, null, object : WebHelper.Callback {
                override fun success(result: String) {
                    emitter.onNext(result)
                    emitter.onComplete()
                }

                override fun error(msg: String) {
                    emitter.tryOnError(ParseException("解析失败"))
                }
            })
            emitter.setCancellable {
                cancellable.cancel()
            }
        }).flatMap { videoPageUrl ->
            return@flatMap Observable.create(ObservableOnSubscribe<List<String>> { emitter ->
                val cancellable = MoyanVideoUrlHelper.getVideoSource(SourceManagerImpl.context, videoPageUrl, null, object : WebHelper.Callback {
                    override fun success(result: String) {
                        emitter.onNext(listOf(result))
                        emitter.onComplete()
                    }

                    override fun error(msg: String) {
                        emitter.tryOnError(ParseException("解析失败"))
                    }
                })
                emitter.setCancellable {
                    cancellable.cancel()
                }
            })
        }
    }

    override fun getVideoListFilter(type: Int): Map<String, List<FilterItem>> {
        if (type == TYPE_MOVIE) {
            return MoyanFilter.moveListFilter()
        } else if (type == TYPE_TV) {
            return MoyanFilter.tvListFilter()
        } else if (type == TYPE_VARIETY) {
            return MoyanFilter.varietyListFilter()
        } else if (type == TYPE_ANIM) {
            return MoyanFilter.animListFilter()
        }
        return mapOf()
    }

    private fun getTvList(params: Map<String, FilterItem>, page: Int): Observable<PageData<VideoItem>> {
        return getService(KmaoApi::class.java)
                .tvList(params["类型"]?.value ?: "", params["状态"]?.value
                        ?: "", params["国家"]?.value ?: "", params["年代"]?.value ?: "", page)
                .map { s -> MoyanParser.instance.parseVideoList(s.string()) }
    }

    private fun getMovieList(params: Map<String, FilterItem>, page: Int): Observable<PageData<VideoItem>> {
        return getService(KmaoApi::class.java)
                .movieList(params["类型"]?.value ?: "movie", params["国家"]?.value
                        ?: "", params["年代"]?.value ?: "", page)
                .map { s -> MoyanParser.instance.parseVideoList(s.string()) }
    }

    private fun getVarietyList(params: Map<String, FilterItem>, page: Int): Observable<PageData<VideoItem>> {
        return getService(KmaoApi::class.java)
                .varietyList(params["类型"]?.value ?: "", params["状态"]?.value
                        ?: "", params["国家"]?.value ?: "", params["年代"]?.value ?: "", page)
                .map { s -> MoyanParser.instance.parseVideoList(s.string()) }
    }

    private fun getAnimList(params: Map<String, FilterItem>, page: Int): Observable<PageData<VideoItem>> {
        return getService(KmaoApi::class.java)
                .animList(params["类型"]?.value ?: "", params["状态"]?.value
                        ?: "", params["国家"]?.value ?: "", params["年代"]?.value ?: "", page)
                .map { s -> MoyanParser.instance.parseVideoList(s.string()) }
    }

    override fun getVideoListTypes(): List<VideoListType> {
        return TYPES
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
