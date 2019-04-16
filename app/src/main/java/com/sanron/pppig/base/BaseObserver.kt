package com.sanron.pppig.base

import io.reactivex.Observer
import io.reactivex.disposables.Disposable

/**
 * Author:sanron
 * Time:2019/1/14
 * Description:
 */
open class BaseObserver<T> : Observer<T> {

    override fun onSubscribe(d: Disposable) {

    }

    override fun onNext(t: T) {

    }

    override fun onError(e: Throwable) {

    }

    override fun onComplete() {

    }
}
