package com.sanron.pppig.module.micaitu.home

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import com.sanron.pppig.base.BaseViewModel

/**
 * Author:sanron
 * Time:2019/4/15
 * Description:
 */
class ItemVideoViewModel(application: Application) : BaseViewModel(application) {

    val name = MutableLiveData<String>()
    val label = MutableLiveData<String>()
    val score = MutableLiveData<String>()
    val img = MutableLiveData<String>()
    val scoreGone = Transformations.map(score){
        return@map it.isNullOrEmpty()
    }

}
