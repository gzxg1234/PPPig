package com.sanron.pppig.module.micaitu.videodetail

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import com.sanron.pppig.base.BaseObserver
import com.sanron.pppig.base.BaseViewModel
import com.sanron.pppig.common.MsgFactory
import com.sanron.pppig.data.Repo
import com.sanron.pppig.data.bean.micaitu.PlaySource
import com.sanron.pppig.data.bean.micaitu.VideoDetail
import com.sanron.pppig.util.SingleLiveEvent
import com.sanron.pppig.util.main
import io.reactivex.disposables.Disposable

/**
 *Author:sanron
 *Time:2019/4/24
 *Description:
 */
class VideoDetailVM(application: Application) : BaseViewModel(application) {

    val fav = MutableLiveData<Boolean>()

    val image = MutableLiveData<String>()
    val title = MutableLiveData<String>()
    val infoList = MutableLiveData<List<String>>()
    val intro = MutableLiveData<String>()

    val playSourceList = MutableLiveData<List<PlaySource>>()

    var url: String? = null

    var loading = SingleLiveEvent<Boolean>()

    init {
        loading.value = false
    }

    fun loadData() {
        Repo.getVideoDetail(url ?: "")
                .main()
                .compose(addDisposable())
                .subscribe(object : BaseObserver<VideoDetail>() {
                    override fun onSubscribe(d: Disposable) {
                        super.onSubscribe(d)
                        loading.value = true
                    }

                    override fun onNext(t: VideoDetail) {
                        super.onNext(t)
                        infoList.value = t.infoList
                        image.value = t.image
                        title.value = t.title
                        playSourceList.value = t.source
                        intro.value = t.intro
                        loading.value = false
                    }

                    override fun onError(e: Throwable) {
                        super.onError(e)
                        loading.value = false
                        toastMsg.value = MsgFactory.get(e)
                    }
                })
    }
}