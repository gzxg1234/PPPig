package com.sanron.pppig.base

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import com.sanron.pppig.app.PiApp
import io.reactivex.ObservableTransformer
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

/**
 * Author:sanron
 * Time:2019/1/14
 * Description:
 */
open class BaseViewModel(application: Application) : AndroidViewModel(application) {

    private val mCompositeDisposable = CompositeDisposable()

    fun addDisposable(disposable: Disposable) {
        if (!mCompositeDisposable.isDisposed) {
            mCompositeDisposable.add(disposable)
        } else {
            disposable.dispose()
        }
    }

    fun <T> addDisposable(): ObservableTransformer<T, T> {
        return ObservableTransformer { upstream -> upstream.doOnSubscribe { disposable -> addDisposable(disposable) } }
    }

    override fun onCleared() {
        mCompositeDisposable.clear()
        super.onCleared()
    }
}
