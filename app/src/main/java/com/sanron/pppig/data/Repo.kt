package com.sanron.pppig.data

import com.sanron.datafetch_interface.video.VideoDataFetch
import com.sanron.datafetch_interface.video.bean.*
import io.reactivex.Observable

/**
 * Author:sanron
 * Time:2019/2/20
 * Description:
 */
object Repo : VideoDataFetch{

    lateinit var sVideoDataFetch: VideoDataFetch

    override fun getHomeData(): Observable<Home> = sVideoDataFetch.getHomeData()

    override fun getTopMovie(): Observable<PageData<VideoItem>> = sVideoDataFetch.getTopMovie()

    override fun getVideoDetail(path: String): Observable<VideoDetail> = sVideoDataFetch.getVideoDetail(path)

    override fun getVideoPlayPageUrl(item:PlayLine.Item): Observable<String> = sVideoDataFetch.getVideoPlayPageUrl(item)

    override fun getVideoSource(item:PlayLine.Item) = sVideoDataFetch.getVideoSource(item)

    override fun getVideoListTypes(): List<VideoListType> = sVideoDataFetch.getVideoListTypes()

    override fun getVideoList(type: Int, param: Map<String, FilterItem>, page: Int): Observable<PageData<VideoItem>> = sVideoDataFetch.getVideoList(type, param, page)

    override fun getVideoListFilter(type: Int): Map<String, List<FilterItem>> = sVideoDataFetch.getVideoListFilter(type)

    override fun getSearchResult(word: String, page: Int): Observable<PageData<VideoItem>> = sVideoDataFetch.getSearchResult(word, page)
}

