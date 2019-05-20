package com.sanron.pppig.module.mainhome.home

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.sanron.datafetch_interface.video.bean.Home
import com.sanron.datafetch_interface.video.bean.VideoItem
import com.sanron.pppig.base.BaseObserver
import com.sanron.pppig.base.BaseViewModel
import com.sanron.pppig.base.state.LoadState
import com.sanron.pppig.common.MsgFactory
import com.sanron.pppig.data.Repo
import com.sanron.pppig.util.SingleLiveEvent
import com.sanron.pppig.util.main
import io.reactivex.disposables.Disposable

/**
 * Author:sanron
 * Time:2019/2/21
 * Description:
 */
class HomeVM(application: Application) : BaseViewModel(application) {

    val loadState = SingleLiveEvent<Int>()
    val homeData = MutableLiveData<Home>()
    val refresh = SingleLiveEvent<Boolean>()
    val toVideoDetail = SingleLiveEvent<VideoItem>()

    init {
        refresh.value = false
    }

    fun refresh() {
        Repo.getHomeData()
                .main()
                .compose(autoDispose())
                .subscribe(object : BaseObserver<Home>() {
                    override fun onSubscribe(d: Disposable) {
                        super.onSubscribe(d)
                        refresh.value = true
                        if (loadState.value != LoadState.SUCCESS) {
                            loadState.value = LoadState.LOADING
                        }
                    }

                    override fun onNext(home: Home) {
                        super.onNext(home)
                        homeData.value = home
                        if (loadState.value != LoadState.SUCCESS) {
                            loadState.value = LoadState.SUCCESS
                        }
                    }

                    override fun onError(e: Throwable) {
                        super.onError(e)
                        refresh.postValue(false)
                        toastMsg.value = MsgFactory.get(e)
                        if (loadState.value != LoadState.SUCCESS) {
                            loadState.value = LoadState.ERROR
                        }
                    }

                    override fun onComplete() {
                        super.onComplete()
                        refresh.postValue(false)
                    }
                })
    }
}
