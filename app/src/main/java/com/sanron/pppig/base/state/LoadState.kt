package com.sanron.pppig.base.state

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.sanron.pppig.widget.loadlayout.LoadLayout

/**
 *Author:sanron
 *Time:2019/5/16
 *Description:
 */

object LoadState {
    const val LOADING = 0
    const val ERROR = 1
    const val SUCCESS = LoadLayout.STATE_CONTENT
}