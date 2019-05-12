package com.sanron.datafetch_interface

import com.sanron.datafetch_interface.bean.*
import io.reactivex.Observable


/**
 *Author:sanron
 *Time:2019/5/8
 *Description:
 */
interface DataFetch {


    fun getHomeData(): Observable<Home>

    fun getTopMovie(): Observable<PageData<VideoItem>>

    fun getVideoDetail(path: String): Observable<VideoDetail>

    fun getVideoSource(url: String): Observable<List<String>>

    fun getVideoListTypes(): List<VideoListType>

    fun getVideoList(type: Int, param: Map<String, FilterItem>, page: Int): Observable<PageData<VideoItem>>

    fun getVideoListFilter(type: Int): Map<String, List<FilterItem>>
}