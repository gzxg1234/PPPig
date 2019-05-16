package com.sanron.datafetch_interface.video.bean

import java.io.Serializable

/**
 *Author:sanron
 *Time:2019/5/7
 *Description:
 */
class PlaySource : Serializable{
    var name: String? = null
    var items: MutableList<Item>? = null

    class Item : Serializable {
        var name: String? = null
        var link: String? = null
    }
}
