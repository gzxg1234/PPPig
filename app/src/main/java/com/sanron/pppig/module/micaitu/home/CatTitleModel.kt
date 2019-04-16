package com.sanron.pppig.module.micaitu.home

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import android.support.annotation.DrawableRes
import com.sanron.pppig.base.BaseViewModel

/**
 * Author:sanron
 * Time:2019/4/15
 * Description:
 */
class CatTitleModel(application: Application) : BaseViewModel(application) {


    val title = MutableLiveData<String>()
    val icon = MutableLiveData<Int>()


    fun setVariable(title:String?,@DrawableRes icon:Int){
        this.title.value = title
        this.icon.value = icon
    }
}
