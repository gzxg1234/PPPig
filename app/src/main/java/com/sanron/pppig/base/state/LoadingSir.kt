package com.sanron.pppig.base.state

import com.kingja.loadsir.callback.Callback
import com.sanron.pppig.R

/**
 *Author:sanron
 *Time:2019/5/16
 *Description:
 */
class LoadingSir : Callback() {

    override fun onCreateView(): Int = R.layout.state_loading_layout

}