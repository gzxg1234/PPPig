package com.sanron.pppig.module.micaitu.movie

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import com.sanron.pppig.base.BaseViewModel

/**
 *Author:sanron
 *Time:2019/4/16
 *Description:
 */
class ItemMovieViewModel(application: Application) : BaseViewModel(application){

    val name = MutableLiveData<String>()
    val label = MutableLiveData<String>()
    val img = MutableLiveData<String>()

}