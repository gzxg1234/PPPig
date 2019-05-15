package com.sanron.pppig.module.mainhome.videolist

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.sanron.datafetch_interface.bean.VideoItem
import com.sanron.pppig.base.BaseViewModel

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
