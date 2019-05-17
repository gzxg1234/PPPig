package com.sanron.datafetch_interface.video.bean

import com.sanron.datafetch_interface.BaseBean

/**
 *Author:sanron
 *Time:2019/4/16
 *Description:
 */
class PageData<T>: BaseBean() {

    var data: MutableList<T>? = null

    var hasMore = false
}