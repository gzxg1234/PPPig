package com.sanron.pppig.module.mainhome

import android.app.Application
import com.sanron.datafetch_interface.bean.Home
import com.sanron.pppig.base.BaseObserver
import com.sanron.pppig.base.BaseViewModel
import com.sanron.pppig.data.Repo
import io.reactivex.android.schedulers.AndroidSchedulers

/**
 * Author:sanron
 * Time:2019/2/20
 * Description:
 */
class MainFragViewModel(application: Application) : BaseViewModel(application) {


    fun getData() {
        Repo.getHomeData()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : BaseObserver<Home>() {
                    override fun onNext(home: Home) {
                        super.onNext(home)
                    }

                    override fun onError(e: Throwable) {
                        super.onError(e)
                    }
                })
    }

}
