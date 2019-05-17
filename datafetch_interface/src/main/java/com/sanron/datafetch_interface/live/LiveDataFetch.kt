package com.sanron.datafetch_interface.live

import com.sanron.datafetch_interface.live.bean.LiveCat
import com.sanron.datafetch_interface.live.bean.LiveItem
import com.sanron.datafetch_interface.video.VideoSource
import com.sanron.datafetch_interface.video.bean.PlayLine
import io.reactivex.Observable

/**
 *Author:sanron
 *Time:2019/5/16
 *Description:
 */
interface LiveDataFetch {

    /**
     * 获取直播分类
     */
    fun getLiveCats():Observable<List<LiveCat>>

    /**
     * 获取分类下直播频道
     */
    fun getCatItems(liveCat: LiveCat):Observable<List<LiveItem>>

    /**
     * 获取频道播放线路
     */
    fun getPlayLineList(item:LiveItem):Observable<List<PlayLine>>

    /**
     * 获取直播源地址
     */
    fun getLiveSourceUrl(item:PlayLine.Item):Observable<List<String>>
}