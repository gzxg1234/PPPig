package com.sanron.datafetch_interface.video

import com.sanron.datafetch_interface.video.bean.*
import io.reactivex.Observable


/**
 *Author:sanron
 *Time:2019/5/8
 *Description:
 * 视频数据获取
 */
interface VideoDataFetch {
    /**
     * 获取首页数据
     */
    fun getHomeData(): Observable<Home>

    /**
     * 获取top电影
     */
    fun getTopMovie(): Observable<PageData<VideoItem>>

    /**
     * 获取视频详情
     */
    fun getVideoDetail(path: String): Observable<VideoDetail>

    /**
     * 获取视频播放页url
     */
    fun getVideoPlayPageUrl(videoPageUrl: String): Observable<String>

    /**
     * 获取视频播放源
     */
    fun getVideoSource(videoPageUrl: String): Observable<List<String>>

    /**
     * 获取视频列表分类
     */
    fun getVideoListTypes(): List<VideoListType>

    /**
     * 获取视频列表
     */
    fun getVideoList(type: Int, param: Map<String, FilterItem>, page: Int): Observable<PageData<VideoItem>>

    /**
     * 获取视频列表过滤条件
     */
    fun getVideoListFilter(type: Int): Map<String, List<FilterItem>>

    /**
     * 获取搜索结果列表
     */
    fun getSearchResult(word: String, page: Int): Observable<PageData<VideoItem>>
}