package com.sanron.datafetch_interface.video.bean

import com.sanron.datafetch_interface.BaseBean

/**
 *Author:sanron
 *Time:2019/5/7
 *Description:
 * 播放线路
 */
class PlayLine : BaseBean() {
    var name: String? = null
    var items: MutableList<Item>? = null

    open class Item : BaseBean() {
        var name: String? = null
    }
}
