package com.sanron.datafetch.videosource.kkkkmao

import com.sanron.datafetch.MediaSearch
import com.sanron.datafetch.SourceManagerImpl
import com.sanron.datafetch.http.HttpUtil
import com.sanron.datafetch.http.HttpUtil.api
import com.sanron.datafetch.urlEncode
import com.sanron.datafetch_interface.video.VideoDataFetch
import com.sanron.datafetch_interface.video.bean.*
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import java.io.IOException

/**
 * Author:sanron
 * Time:2019/5/8
 * Description:
 */
class KMaoDataFetch : VideoDataFetch {


    companion object {
        const val BASE_URL = "http://m.kkkkmao.com"
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


    override fun getHomeData(): Observable<Home> {
        return HttpUtil.api
                .url(BASE_URL)
                .map { s ->
                    val data = KKMaoParser.parseHome(s.string())
                    return@map data
                }
    }

    override fun getTopMovie(): Observable<PageData<VideoItem>> {
        return api
                .url("$BASE_URL/top_mov.html")
                .map { s -> KKMaoParser.parseTopMovie(s.string()) }
    }

    override fun getVideoDetail(url: String): Observable<VideoDetail> {
        return api
                .url(url)
                .map { responseBody -> KKMaoParser.parseVideoDetail(responseBody.string()) }
    }

    override fun getVideoPlayPageUrl(item: PlayLine.Item): Observable<String> {
        return api
                .url(item["link"] ?: "")
                .map { responseBody ->
                    return@map KKMaoParser.parsePlayPageUrl(responseBody.string())
                }
    }

    override fun getVideoSource(item: PlayLine.Item): Observable<List<String>> {
        val link: String? = item["link"]
        return getVideoPlayPageUrl(item)
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap { playPageUrl ->
                    val header = mutableMapOf<String, String>()
                    header["Referer"] = link ?: ""
                    return@flatMap Observable.create(ObservableOnSubscribe<List<String>> {
                        val task = MediaSearch.search(SourceManagerImpl.context,
                                playPageUrl, header, 1, object : MediaSearch.Callback {
                            override fun success(result: List<String>) {
                                it.onNext(result)
                                it.onComplete()
                            }

                            override fun error(msg: String) {
                                it.onError(IOException(msg))
                            }
                        })
                        it.setCancellable {
                            task.cancel()
                        }
                    })
                }
    }

    override fun getVideoListFilter(type: Int): Map<String, List<FilterItem>> {
        if (type == TYPE_MOVIE) {
            return KMaoFilter.moveListFilter()
        } else if (type == TYPE_TV) {
            return KMaoFilter.tvListFilter()
        } else if (type == TYPE_VARIETY) {
            return KMaoFilter.varietyListFilter()
        } else if (type == TYPE_ANIM) {
            return KMaoFilter.animListFilter()
        }
        return mapOf()
    }

    private fun getTvList(params: Map<String, FilterItem>, page: Int): Observable<PageData<VideoItem>> {
        val url = "$BASE_URL/tv/index" +
                "_$page}" +
                "_${params["类型"]?.value.urlEncode()}" +
                "_${params["状态"]?.value.urlEncode()}" +
                "_${params["年代"]?.value.urlEncode()}" +
                "___${params["地区"]?.value.urlEncode()}_1.html"
        return api
                .url(url)
                .map { s -> KKMaoParser.parseVideoList(s.string()) }
    }

    private fun getMovieList(params: Map<String, FilterItem>, page: Int): Observable<PageData<VideoItem>> {
        val url = "$BASE_URL/movie/index" +
                "_$page}" +
                "_${params["类型"]?.value.urlEncode()}" +
                "__${params["年代"]?.value.urlEncode()}" +
                "___${params["地区"]?.value.urlEncode()}_1.html"
        return api
                .url(url)
                .map { s -> KKMaoParser.parseVideoList(s.string()) }
    }

    private fun getVarietyList(params: Map<String, FilterItem>, page: Int): Observable<PageData<VideoItem>> {
//        /Arts/index_{page}_{type}_{end}_{year}___{country}_1.html
        val url = "$BASE_URL/Arts/index" +
                "_$page}" +
                "_${params["类型"]?.value.urlEncode()}" +
                "_${params["状态"]?.value.urlEncode()}" +
                "_${params["年代"]?.value.urlEncode()}" +
                "___${params["地区"]?.value.urlEncode()}_1.html"
        return api
                .url(url)
                .map { s -> KKMaoParser.parseVideoList(s.string()) }
    }

    private fun getAnimList(params: Map<String, FilterItem>, page: Int): Observable<PageData<VideoItem>> {
        val url = "$BASE_URL/Animation/index" +
                "_$page" +
                "_${params["类型"]?.value.urlEncode()}" +
                "_${params["状态"]?.value.urlEncode()}" +
                "_${params["年代"]?.value.urlEncode()}" +
                "___${params["地区"]?.value.urlEncode()}_1.html"
        return api
                .url(url)
                .map { s -> KKMaoParser.parseVideoList(s.string()) }
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
//        /vod-search-wd-{word}-p-{page}
        val url = "$BASE_URL/vod-search" +
                "-wd-${word.urlEncode()}" +
                "-p-$page" +
                ".html"
        return api
                .url(url)
                .map { responseBody -> KKMaoParser.parseSearchResult(responseBody.string()) }
    }
}
