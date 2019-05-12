package com.sanron.pppig.module.play

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import com.sanron.datafetch_interface.bean.PlaySource
import com.sanron.pppig.base.BaseObserver
import com.sanron.pppig.base.BaseViewModel
import com.sanron.pppig.common.MsgFactory
import com.sanron.pppig.data.Repo
import com.sanron.pppig.util.main

/**
 * Author:sanron
 * Time:2019/5/8
 * Description:
 */
class PlayerVM(application: Application) : BaseViewModel(application) {


    var playItem: PlaySource.Item? = null

    var loading = MutableLiveData<Boolean>()

    var videoSourceList = MutableLiveData<List<String>>()


    fun loadData() {
        Repo.getVideoSource(playItem?.link ?: "")
                .main()
                .compose(addDisposable())
                .doOnSubscribe {
                    loading.value = true
                }
                .doFinally {
                    loading.value = false
                }
                .subscribe(object : BaseObserver<List<String>>() {
                    override fun onNext(t: List<String>) {
                        videoSourceList.value = t
                        super.onNext(t)
                    }

                    override fun onError(e: Throwable) {
                        super.onError(e)
                        toastMsg.value = MsgFactory.get(e)
                    }
                })
    }
}