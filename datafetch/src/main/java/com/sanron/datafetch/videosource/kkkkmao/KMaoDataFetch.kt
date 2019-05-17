package com.sanron.datafetch.videosource.kkkkmao

import com.sanron.datafetch.MediaSearch
import com.sanron.datafetch.SourceManagerImpl
import com.sanron.datafetch_interface.video.VideoDataFetch
import com.sanron.datafetch_interface.video.bean.*
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.io.IOException

/**
 * Author:sanron
 * Time:2019/5/8
 * Description:
 */
class KMaoDataFetch : VideoDataFetch {

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
    private val api: KmaoApi by lazy {
        mRetrofit.create(KmaoApi::class.java)
    }

    override fun getHomeData(): Observable<Home> {
        return api
                .home()
                .map { s ->
                    val data = KKMaoParser.parseHome(s.string())
                    return@map data
                }
    }

    override fun getTopMovie(): Observable<PageData<VideoItem>> {
        return api
                .topMovie()
                .map { s -> KKMaoParser.parseTopMovie(s.string()) }
    }

    override fun getVideoDetail(path: String): Observable<VideoDetail> {
        return api
                .html(path)
                .map { responseBody -> KKMaoParser.parseVideoDetail(responseBody.string()) }
    }

    override fun getVideoPlayPageUrl(item: PlayLine.Item): Observable<String> {
        return api
                .html(item["link"] ?: "")
                .map { responseBody ->
                    return@map KKMaoParser.parsePlayPageUrl(responseBody.string())
                }
    }

    override fun getVideoSource(item: PlayLine.Item): Observable<List<String>> {
        val link: String? = item["link"]
        return getVideoPlayPageUrl(item)
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap { playPageUrl ->
                    val referer = "http://m.kkkkmao.com/$link"
                    val header = mutableMapOf<String, String>()
                    header["Referer"] = referer
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
        return api
                .tvList(params["类型"]?.value ?: "", params["状态"]?.value
                        ?: "", params["地区"]?.value ?: "", params["年代"]?.value ?: "", page)
                .map { s -> KKMaoParser.parseVideoList(s.string()) }
    }

    private fun getMovieList(params: Map<String, FilterItem>, page: Int): Observable<PageData<VideoItem>> {
        return api
                .movieList(params["类型"]?.value ?: "movie", params["地区"]?.value
                        ?: "", params["年代"]?.value ?: "", page)
                .map { s -> KKMaoParser.parseVideoList(s.string()) }
    }

    private fun getVarietyList(params: Map<String, FilterItem>, page: Int): Observable<PageData<VideoItem>> {
        return api
                .varietyList(params["类型"]?.value ?: "", params["状态"]?.value
                        ?: "", params["地区"]?.value ?: "", params["年代"]?.value ?: "", page)
                .map { s -> KKMaoParser.parseVideoList(s.string()) }
    }

    private fun getAnimList(params: Map<String, FilterItem>, page: Int): Observable<PageData<VideoItem>> {
        return api
                .animList(params["类型"]?.value ?: "", params["状态"]?.value
                        ?: "", params["地区"]?.value ?: "", params["年代"]?.value ?: "", page)
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
        return api
                .search(word, page)
                .map { responseBody -> KKMaoParser.parseSearchResult(responseBody.string()) }
    }
}
