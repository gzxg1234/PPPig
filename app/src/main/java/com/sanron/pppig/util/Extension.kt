package com.sanron.pppig.util

import android.animation.Animator
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.OnLifecycleEvent
import android.content.Context
import android.databinding.Observable
import android.databinding.ObservableField
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.animation.Animation
import com.sanron.pppig.BR
import me.jessyan.autosize.utils.AutoSizeUtils

/**
 *Author:sanron
 *Time:2019/4/9
 *Description:
 */
fun Context.dp2px(dp: Float) = AutoSizeUtils.dp2px(this, dp)

/**
 * 绑定lifecycle，自动取消
 */
fun Animation.bindLifecycle(lifecycleOwner: LifecycleOwner) {
    lifecycleOwner.lifecycle.addObserver(object : LifecycleObserver {
        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        fun destroy() {
            this@bindLifecycle.cancel()
        }
    })
}

fun Animator.bindLifecycle(lifecycleOwner: LifecycleOwner) {
    lifecycleOwner.lifecycle.addObserver(object : LifecycleObserver {
        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        fun destroy() {
            this@bindLifecycle.cancel()
        }
    })
}

fun <A,B,C> AsyncTask<A,B,C>.bindLifecycle(lifecycleOwner: LifecycleOwner) {
    lifecycleOwner.lifecycle.addObserver(object : LifecycleObserver {
        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        fun destroy() {
            this@bindLifecycle.cancel(false)
        }
    })
}

val Context.inflater: LayoutInflater
    get() = LayoutInflater.from(this)

fun Observable.addOnPropertyChangedCallback(callback: (sender: Observable?, id: Int) -> Unit) {
    this.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
        override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
            callback(sender, propertyId)
        }
    })
}

fun <T> ObservableField<T>.addOnChangeCallback(callback: (value: T?) -> Unit) {
    this.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
        override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
            if (propertyId == BR._all) {
                callback(this@addOnChangeCallback.get())
            }
        }
    })
}