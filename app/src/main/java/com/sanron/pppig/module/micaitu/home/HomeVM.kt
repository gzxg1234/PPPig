package com.sanron.pppig.module.micaitu.home

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import com.sanron.pppig.base.BaseObserver
import com.sanron.pppig.base.BaseViewModel
import com.sanron.pppig.data.Repo
import com.sanron.pppig.data.bean.micaitu.Home
import com.sanron.pppig.data.bean.micaitu.VideoItem
import com.sanron.pppig.util.SingleLiveEvent
import com.sanron.pppig.util.main
import io.reactivex.android.schedulers.AndroidSchedulers

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
        Repo.getMicaituHome()
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
                    }

                    override fun onComplete() {
                        super.onComplete()
                        refresh.postValue(false)
                    }
                })
    }
}
