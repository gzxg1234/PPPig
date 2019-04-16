package com.sanron.pppig.module.home

import android.app.Application
import com.sanron.pppig.base.BaseObserver
import com.sanron.pppig.base.BaseViewModel
import com.sanron.pppig.data.Repo
import com.sanron.pppig.data.bean.micaitu.Home
import io.reactivex.android.schedulers.AndroidSchedulers

/**
 * Author:sanron
 * Time:2019/2/20
 * Description:
 */
class MainFragViewModel(application: Application) : BaseViewModel(application) {



    fun getData() {
        Repo.getMicaituHome()
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
