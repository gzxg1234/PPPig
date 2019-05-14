package com.sanron.datafetch.source.nianlun

import com.sanron.datafetch.MediaSearch
import com.sanron.datafetch.SourceManagerImpl
import com.sanron.datafetch.WebHelper
import com.sanron.datafetch_interface.DataFetch
import com.sanron.datafetch_interface.bean.*
import com.sanron.datafetch_interface.exception.ParseException
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.net.URLEncoder
import java.util.*

/**
 * Author:sanron
 * Time:2019/5/8
 * Description:
 */
class NianlunFetch : DataFetch {
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
                .baseUrl(NianlunApi.BASE_URL)
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
        return getService(NianlunApi::class.java)
                .home()
                .map { s ->
                    val data = NianlunParser.parseHome(s.string())
                    return@map data
                }
    }

    override fun getTopMovie(): Observable<PageData<VideoItem>> {
        return getService(NianlunApi::class.java)
                .topMovie()
                .map { s -> NianlunParser.parseTopMovie(s.string()) }
    }

    override fun getVideoDetail(path: String): Observable<VideoDetail> {
        return getService(NianlunApi::class.java)
                .html(path)
                .map { responseBody -> NianlunParser.parseVideoDetail(responseBody.string()) }
    }

    override fun getVideoListFilter(type: Int): Map<String, List<FilterItem>> {
        if (type == TYPE_MOVIE) {
            return NianlunFilter.moveListFilter()
        } else if (type == TYPE_TV) {
            return NianlunFilter.tvListFilter()
        } else if (type == TYPE_VARIETY) {
            return NianlunFilter.varietyListFilter()
        } else if (type == TYPE_ANIM) {
            return NianlunFilter.animListFilter()
        }
        return mapOf()
    }


    private fun getMovieList(params: Map<String, FilterItem>, page: Int): Observable<PageData<VideoItem>> {
//        "https://www.moyantv.com/index.php/vod/show/area/大陆/class/喜剧/id/1/year/2018.html"
        val path = StringBuilder("/vodshow/1")
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
        path.append("/page/").append(page)
        params["年代"]?.value?.let {
            if (it.isNotEmpty()) {
                path.append("/year/").append(URLEncoder.encode(it, "utf-8"))
            }
        }
        path.append(".html")
        return getService(NianlunApi::class.java)
                .html(path.toString())
                .map { s -> NianlunParser.parseVideoList(s.string()) }
    }

    private fun getTvList(params: Map<String, FilterItem>, page: Int): Observable<PageData<VideoItem>> {
//        "https://www.moyantv.com/index.php/vod/show/area/大陆/class/喜剧/id/1/year/2018.html"
        val path = StringBuilder("/vodshow/2")
        params["地区"]?.value?.let {
            if (it.isNotEmpty()) {
                path.append("/area/").append(URLEncoder.encode(it, "utf-8"))
            }
        }
        path.append("/page/").append(page)
        params["年代"]?.value?.let {
            if (it.isNotEmpty()) {
                path.append("/year/").append(URLEncoder.encode(it, "utf-8"))
            }
        }
        path.append(".html")
        return getService(NianlunApi::class.java)
                .html(path.toString())
                .map { s -> NianlunParser.parseVideoList(s.string()) }
    }


    private fun getVarietyList(params: Map<String, FilterItem>, page: Int): Observable<PageData<VideoItem>> {
//        "https://www.moyantv.com/index.php/vod/show/area/大陆/class/喜剧/id/1/year/2018.html"
        val path = StringBuilder("/vodshow/3")
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
        path.append("/page/").append(page)
        params["年代"]?.value?.let {
            if (it.isNotEmpty()) {
                path.append("/year/").append(URLEncoder.encode(it, "utf-8"))
            }
        }
        path.append(".html")
        return getService(NianlunApi::class.java)
                .html(path.toString())
                .map { s -> NianlunParser.parseVideoList(s.string()) }
    }

    private fun getAnimList(params: Map<String, FilterItem>, page: Int): Observable<PageData<VideoItem>> {
//        "https://www.moyantv.com/index.php/vod/show/area/大陆/class/喜剧/id/1/year/2018.html"
        val path = StringBuilder("/vodshow/4")
        params["地区"]?.value?.let {
            if (it.isNotEmpty()) {
                path.append("/area/").append(URLEncoder.encode(it, "utf-8"))
            }
        }
        path.append("/page/").append(page)
        params["年代"]?.value?.let {
            if (it.isNotEmpty()) {
                path.append("/year/").append(URLEncoder.encode(it, "utf-8"))
            }
        }
        path.append(".html")
        return getService(NianlunApi::class.java)
                .html(path.toString())
                .map { s -> NianlunParser.parseVideoList(s.string()) }
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


    override fun getVideoPlayPageUrl(videoPageUrl: String): Observable<String> {
        return Observable.create { emitter ->
            val cancellable = NianlunVideoUrlHelper.getVideoPageUrl(SourceManagerImpl.context, NianlunApi.BASE_URL + videoPageUrl, null, object : WebHelper.Callback {
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
        }
    }

    override fun getVideoSource(videoPageUrl: String): Observable<List<String>> {
        return Observable.create(ObservableOnSubscribe<JSONObject> { emitter ->
            val cancellable = NianlunVideoUrlHelper.getVideoPageUrl2(SourceManagerImpl.context, NianlunApi.BASE_URL + videoPageUrl, null, object : WebHelper.Callback {
                override fun success(result: String) {
                    var json: JSONObject? = null
                    try {
                        json = JSONObject(result)
                    } catch (e: JSONException) {
                    }
                    if (json == null) {
                        emitter.tryOnError(ParseException("解析失败"))
                    } else {
                        emitter.onNext(json)
                        emitter.onComplete()
                    }
                }

                override fun error(msg: String) {
                    emitter.tryOnError(ParseException("解析失败"))
                }
            })
            emitter.setCancellable {
                cancellable.cancel()
            }
        }).flatMap { jsonObj ->
            val isSource = jsonObj.optBoolean("isSource")
            val url = jsonObj.optString("url")
            if (isSource) {
                return@flatMap Observable.just(listOf(url))
            }
            return@flatMap Observable.create(ObservableOnSubscribe<List<String>> { emitter ->
                val cancellable = MediaSearch.search(SourceManagerImpl.context,
                        url, null, 1, object : MediaSearch.Callback {
                    override fun success(result: List<String>) {
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
            })
//            return@flatMap Observable.create(ObservableOnSubscribe<List<String>> { emitter ->
//                val cancellable = NianlunVideoUrlHelper.getVideoSource(SourceManagerImpl.context, url, null, object : WebHelper.Callback {
//                    override fun success(result: String) {
//                        emitter.onNext(listOf(result))
//                        emitter.onComplete()
//                    }
//
//                    override fun error(msg: String) {
//                        emitter.tryOnError(ParseException("解析失败"))
//                    }
//                })
//                emitter.setCancellable {
//                    cancellable.cancel()
//                }
//            })
        }.subscribeOn(AndroidSchedulers.mainThread())
    }

    override fun getSearchResult(word: String, page: Int): Observable<PageData<VideoItem>> {
        return getService(NianlunApi::class.java)
                .search(word, page)
                .map { responseBody -> NianlunParser.parseSearchResult(responseBody.string()) }
    }

}
