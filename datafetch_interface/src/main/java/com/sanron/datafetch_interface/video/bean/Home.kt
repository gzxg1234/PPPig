package com.sanron.datafetch_interface.video.bean

import com.sanron.datafetch_interface.BaseBean

/**
 * Author:sanron
 * Time:2019/2/20
 * Description:
 */
class Home: BaseBean() {

    var banner: List<Banner>? = null

    var categories: MutableList<HomeCat> = mutableListOf()
}
