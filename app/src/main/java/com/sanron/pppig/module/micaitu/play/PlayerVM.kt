package com.sanron.pppig.module.micaitu.play

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import com.sanron.pppig.base.BaseObserver
import com.sanron.pppig.base.BaseViewModel
import com.sanron.pppig.common.MsgFactory
import com.sanron.pppig.data.Repo
import com.sanron.pppig.data.WebPageHelper
import com.sanron.pppig.util.main
import com.sanron.pppig.util.showToast
import io.reactivex.disposables.Disposable

/**
 * Author:sanron
 * Time:2019/5/8
 * Description:
 */
class PlayerVM(application: Application) : BaseViewModel(application) {

    var url = ""

    var loading = MutableLiveData<Boolean>()

    var videoSourceList = MutableLiveData<List<String>>()

    val webPageHelper: WebPageHelper by lazy {
        WebPageHelper(application)
    }

    override fun onCleared() {
        super.onCleared()
        webPageHelper.destroy()
    }

    fun loadData() {
        Repo.getVideoSource(url, webPageHelper)
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
