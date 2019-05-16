package com.sanron.pppig.base.state

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.kingja.loadsir.callback.SuccessCallback
import com.kingja.loadsir.core.LoadService

/**
 *Author:sanron
 *Time:2019/5/16
 *Description:
 */
fun <T> LoadService<T>.bindStateValue(lifecycleOwner: LifecycleOwner, state: LiveData<Int>) {
    state.observe(lifecycleOwner, Observer {
        when (it) {
            State.LOADING -> this.showCallback(LoadingSir::class.java)
            State.ERROR -> this.showCallback(ErrorSir::class.java)
            State.SUCCESS -> this.showCallback(SuccessCallback::class.java)
        }
    })
}

object State {
    const val LOADING = 0
    const val ERROR = 1
    const val SUCCESS = 2
}