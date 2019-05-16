package com.sanron.datafetch_interface.live.bean

import io.reactivex.Observable

/**
 *Author:sanron
 *Time:2019/5/16
 *Description:
 */
class LiveCat {

    var name: String? = ""

    var link:String?=""

    var items: Observable<List<LiveItem>>? = null
}