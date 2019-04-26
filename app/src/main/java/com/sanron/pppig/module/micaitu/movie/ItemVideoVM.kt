package com.sanron.pppig.module.micaitu.movie

import android.app.Application
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import com.sanron.pppig.base.BaseViewModel
import com.sanron.pppig.data.bean.micaitu.VideoItem

/**
 * Author:sanron
 * Time:2019/4/15
 * Description:
 */
class ItemVideoVM(application: Application) : BaseViewModel(application) {

    val item = MutableLiveData<VideoItem>()
    val scoreGone: LiveData<Boolean> = Transformations.map(item) {
        return@map it?.score.isNullOrEmpty()
    }

}
