package com.sanron.datafetch_interface.live

import com.sanron.datafetch_interface.live.bean.LiveCat
import io.reactivex.Observable

/**
 *Author:sanron
 *Time:2019/5/16
 *Description:
 */
interface LiveDataFetch {

    fun getLiveCats():Observable<List<LiveCat>>
}