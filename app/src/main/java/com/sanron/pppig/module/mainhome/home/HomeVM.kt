package com.sanron.pppig.module.mainhome.home

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import com.sanron.datafetch_interface.bean.Home
import com.sanron.datafetch_interface.bean.VideoItem
import com.sanron.pppig.base.BaseObserver
import com.sanron.pppig.base.BaseViewModel
import com.sanron.pppig.common.MsgFactory
import com.sanron.pppig.data.Repo
import com.sanron.pppig.util.SingleLiveEvent
import com.sanron.pppig.util.main

/**
 * Author:sanron
 * Time:2019/2/21
 * Description:
 */
class HomeVM(application: Application) : BaseViewModel(application) {


    val homeData = MutableLiveData<Home>()
    val refresh = MutableLiveData<Boolean>()
    val toVideoDetail = SingleLiveEvent<VideoItem>()

    init {
        refresh.value = false
    }

    fun onRefresh() {
        loadData()
    }


    fun loadData() {
        refresh.value = true
        Repo.getHomeData()
                .main()
                .compose(addDisposable())
                .subscribe(object : BaseObserver<Home>() {
                    override fun onNext(home: Home) {
                        super.onNext(home)
                        homeData.value = home
                    }

                    override fun onError(e: Throwable) {
                        super.onError(e)
                        refresh.postValue(false)
                        toastMsg.value = MsgFactory.get(e)
                    }

                    override fun onComplete() {
                        super.onComplete()
                        refresh.postValue(false)
                    }
                })
    }
}