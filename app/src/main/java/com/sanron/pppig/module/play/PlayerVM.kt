package com.sanron.pppig.module.play

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.sanron.datafetch_interface.Source
import com.sanron.datafetch_interface.bean.PlaySource
import com.sanron.pppig.base.BaseObserver
import com.sanron.pppig.base.BaseViewModel
import com.sanron.pppig.common.MsgFactory
import com.sanron.pppig.data.FetchManager
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


    lateinit var source: Source

    fun setSource(sourceId: String) {
        source = FetchManager.getSourceById(sourceId)!!
    }

    fun loadData() {
        source.fetch.getVideoSource(playItem?.link ?: "")
                .main()
                .compose(autoDispose("getVideoSourceUrl"))
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
