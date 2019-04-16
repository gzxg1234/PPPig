package com.sanron.pppig.base

import android.databinding.ObservableField

/**
 * Author:sanron
 * Time:2019/1/14
 * Description:
 */
class CommandField : ObservableField<Boolean>(true) {

    private var flag = true

    fun exec() {
        set(!flag)
        flag = !flag
    }
}
