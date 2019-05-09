package com.sanron.pppig.data

import com.sanron.pppig.data.bean.micaitu.Home
import com.sanron.pppig.data.bean.micaitu.PageData
import com.sanron.pppig.data.bean.micaitu.VideoDetail
import com.sanron.pppig.data.bean.micaitu.VideoItem
import io.reactivex.Observable

/**
 *Author:sanron
 *Time:2019/5/8
 *Description:
 */
interface DataFetch {

    fun getMicaituHome(): Observable<Home>

    fun getTopMovie(): Observable<PageData<VideoItem>>

    fun getVideoDetail(path: String): Observable<VideoDetail>

    fun getVideoSource(url: String, webPageHelper: WebPageHelper): Observable<List<String>>

    fun getAll(type: String?, country: String?, year: String?, page: Int): Observable<PageData<VideoItem>>
}