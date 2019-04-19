package com.sanron.pppig.module.micaitu.movie

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import com.sanron.pppig.base.BaseViewModel
import com.sanron.pppig.data.bean.micaitu.VideoItem

/**
 *Author:sanron
 *Time:2019/4/16
 *Description:
 */
class ItemMovieViewModel(application: Application) : BaseViewModel(application) {

    var item = MutableLiveData<VideoItem>()

}