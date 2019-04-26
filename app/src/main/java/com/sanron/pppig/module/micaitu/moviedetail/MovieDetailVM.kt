package com.sanron.pppig.module.micaitu.moviedetail

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import android.text.TextUtils
import com.sanron.pppig.base.BaseObserver
import com.sanron.pppig.base.BaseViewModel
import com.sanron.pppig.data.Repo
import com.sanron.pppig.data.bean.micaitu.VideoDetail
import com.sanron.pppig.util.main
import io.reactivex.disposables.Disposable

/**
 *Author:sanron
 *Time:2019/4/24
 *Description:
 */
class MovieDetailVM(application: Application) : BaseViewModel(application) {

    val fav = MutableLiveData<Boolean>()

    val image = MutableLiveData<String>()
    val title = MutableLiveData<String>()
    val infoList = MutableLiveData<List<String>>()

    var url: String? = null


    fun loadData() {
        Repo.getVideoDetail(url ?: "")
                .main()
                .subscribe(object : BaseObserver<VideoDetail>() {
                    override fun onSubscribe(d: Disposable) {
                        super.onSubscribe(d)
                    }

                    override fun onNext(t: VideoDetail) {
                        super.onNext(t)
                        title.value = t.title
                        image.value = t.image
                        infoList.value = t.infoList
                    }

                    override fun onError(e: Throwable) {
                        super.onError(e)
                    }

                    override fun onComplete() {
                        super.onComplete()
                    }
                })
    }
}