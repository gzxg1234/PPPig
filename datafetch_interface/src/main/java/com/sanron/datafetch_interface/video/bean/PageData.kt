package com.sanron.datafetch_interface.video.bean

/**
 *Author:sanron
 *Time:2019/4/16
 *Description:
 */
class PageData<T> {

    var data: MutableList<T>? = null

    var hasMore = false
}