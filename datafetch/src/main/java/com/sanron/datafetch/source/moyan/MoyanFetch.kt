package com.sanron.datafetch.source.moyan

import com.sanron.datafetch.BuildConfig
import com.sanron.datafetch.SourceManagerImpl
import com.sanron.datafetch.WebHelper
import com.sanron.datafetch_interface.DataFetch
import com.sanron.datafetch_interface.bean.*
import com.sanron.datafetch_interface.exception.ParseException
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.net.URLEncoder
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
                    MoyanParser.parseHome(s.string())
                }
    }

    override fun getTopMovie(): Observable<PageData<VideoItem>> {
        return getService(MoyanApi::class.java)
                .topMovie()
                .map { s -> MoyanParser.parseTopMovie(s.string()) }
    }

    override fun getVideoDetail(path: String): Observable<VideoDetail> {
        return getService(MoyanApi::class.java)
                .html(path)
                .map { responseBody -> MoyanParser.parseVideoDetail(responseBody.string()) }
    }

    override fun getVideoPlayPageUrl(videoPageUrl: String): Observable<String> {
        return Observable.create(ObservableOnSubscribe<String> { emitter ->
            val cancellable = MoyanVideoUrlHelper.getVideoPageUrl(SourceManagerImpl.context, MoyanApi.BASE_URL + videoPageUrl, null, object : WebHelper.Callback {
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
        }).subscribeOn(AndroidSchedulers.mainThread())
    }

    override fun getVideoSource(videoPageUrl: String): Observable<List<String>> {
        return getVideoPlayPageUrl(videoPageUrl)
                .flatMap { videoPlayPageUrl ->
                    return@flatMap Observable.create(ObservableOnSubscribe<List<String>> { emitter ->
                        val cancellable = MoyanVideoUrlHelper.getVideoSource(SourceManagerImpl.context, videoPlayPageUrl, null, object : WebHelper.Callback {
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

    private fun getMovieList(params: Map<String, FilterItem>, page: Int): Observable<PageData<VideoItem>> {
//        "https://www.moyantv.com/index.php/vod/show/area/大陆/class/喜剧/id/1/year/2018.html"
        val path = StringBuilder("/index.php/vod/show")
        params["地区"]?.value?.let {
            if (it.isNotEmpty()) {
                path.append("/area/").append(URLEncoder.encode(it, "utf-8"))
            }
        }
        params["类型"]?.value?.let {
            if (it.isNotEmpty()) {
                path.append("/class/").append(URLEncoder.encode(it, "utf-8"))
            }
        }
        path.append("/id/1").append("/page/").append(page)
        params["年代"]?.value?.let {
            if (it.isNotEmpty()) {
                path.append("/year/").append(URLEncoder.encode(it, "utf-8"))
            }
        }
        path.append(".html")
        return getService(MoyanApi::class.java)
                .html(path.toString())
                .map { s -> MoyanParser.parseVideoList(s.string()) }
    }

    private fun getTvList(params: Map<String, FilterItem>, page: Int): Observable<PageData<VideoItem>> {
//        "https://www.moyantv.com/index.php/vod/show/area/大陆/class/喜剧/id/1/year/2018.html"
        val path = StringBuilder("/index.php/vod/show")
        params["地区"]?.value?.let {
            if (it.isNotEmpty()) {
                path.append("/area/").append(URLEncoder.encode(it, "utf-8"))
            }
        }
        params["类型"]?.value?.let {
            if (it.isNotEmpty()) {
                path.append("/class/").append(URLEncoder.encode(it, "utf-8"))
            }
        }
        path.append("/id/2").append("/page/").append(page)
        params["年代"]?.value?.let {
            if (it.isNotEmpty()) {
                path.append("/year/").append(URLEncoder.encode(it, "utf-8"))
            }
        }
        path.append(".html")
        return getService(MoyanApi::class.java)
                .html(path.toString())
                .map { s -> MoyanParser.parseVideoList(s.string()) }
    }


    private fun getVarietyList(params: Map<String, FilterItem>, page: Int): Observable<PageData<VideoItem>> {
//        "https://www.moyantv.com/index.php/vod/show/area/大陆/class/喜剧/id/1/year/2018.html"
        val path = StringBuilder("/index.php/vod/show")
        params["地区"]?.value?.let {
            if (it.isNotEmpty()) {
                path.append("/area/").append(URLEncoder.encode(it, "utf-8"))
            }
        }
        params["类型"]?.value?.let {
            if (it.isNotEmpty()) {
                path.append("/class/").append(URLEncoder.encode(it, "utf-8"))
            }
        }
        path.append("/id/3").append("/page/").append(page)
        params["年代"]?.value?.let {
            if (it.isNotEmpty()) {
                path.append("/year/").append(URLEncoder.encode(it, "utf-8"))
            }
        }
        path.append(".html")
        return getService(MoyanApi::class.java)
                .html(path.toString())
                .map { s -> MoyanParser.parseVideoList(s.string()) }
    }

    private fun getAnimList(params: Map<String, FilterItem>, page: Int): Observable<PageData<VideoItem>> {
//        "https://www.moyantv.com/index.php/vod/show/area/大陆/class/喜剧/id/1/year/2018.html"
        val path = StringBuilder("/index.php/vod/show")
        params["地区"]?.value?.let {
            if (it.isNotEmpty()) {
                path.append("/area/").append(URLEncoder.encode(it, "utf-8"))
            }
        }
        params["类型"]?.value?.let {
            if (it.isNotEmpty()) {
                path.append("/class/").append(URLEncoder.encode(it, "utf-8"))
            }
        }
        path.append("/id/4").append("/page/").append(page)
        params["年代"]?.value?.let {
            if (it.isNotEmpty()) {
                path.append("/year/").append(URLEncoder.encode(it, "utf-8"))
            }
        }
        path.append(".html")
        return getService(MoyanApi::class.java)
                .html(path.toString())
                .map { s -> MoyanParser.parseVideoList(s.string()) }
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

    override fun getSearchResult(word: String, page: Int): Observable<PageData<VideoItem>> {
        return getService(MoyanApi::class.java)
                .search(word, page)
                .map { responseBody -> MoyanParser.parseSearchResult(responseBody.string()) }
    }
}
