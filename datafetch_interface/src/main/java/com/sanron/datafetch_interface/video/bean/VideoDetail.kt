package com.sanron.datafetch_interface.video.bean

import com.sanron.datafetch_interface.BaseBean

/**
 * Author:sanron
 * Time:2019/4/24
 * Description:
 */
class VideoDetail: BaseBean() {

    var title: String? = null

    var image: String? = null

    var infoList: MutableList<String>? = null

    var mLine: MutableList<PlayLine>? = null

    var intro: String? = null
}
