package com.sanron.pppig.data.bean.micaitu

/**
 *Author:sanron
 *Time:2019/5/7
 *Description:
 */
class PlaySource() {
    var name: String? = null
    var items: MutableList<Item>? = null

    class Item {
        var name: String? = null
        var link: String? = null
    }
}
