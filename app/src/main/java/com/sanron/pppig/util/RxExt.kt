package com.sanron.pppig.util

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers

/**
 *Author:sanron
 *Time:2019/4/16
 *Description:
 */

fun <T> Observable<T>.main(): Observable<T> {
    return observeOn(AndroidSchedulers.mainThread())
}