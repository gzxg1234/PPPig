package com.sanron.datafetch_interface.video.bean

/**
 * Author:sanron
 * Time:2019/2/20
 * Description:
 */
class HomeCat {

    var name: String? = null

    var items: List<VideoItem>? = null

    var type: Int? = null


    companion object {
        const val MOVIE = 1
        const val TV = 2
        const val ANIM = 3
        const val VARIETY = 4
    }
}
