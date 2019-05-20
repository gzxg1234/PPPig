package com.sanron.pppig.util

import android.animation.Animator
import android.content.Context
import android.os.AsyncTask
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.animation.Animation
import android.view.inputmethod.InputMethodManager
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.databinding.Observable
import androidx.databinding.ObservableField
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.sanron.pppig.BR
import me.jessyan.autosize.utils.AutoSizeUtils

/**
 *Author:sanron
 *Time:2019/4/9
 *Description:
 */
fun Context.dp2px(dp: Float) = AutoSizeUtils.dp2px(this, dp)

fun Context.getColorCompat(@ColorRes id: Int): Int {
    return ContextCompat.getColor(this, id)
}

val MainHandler by lazy {
    Handler(Looper.getMainLooper())
}

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

fun <A, B, C> AsyncTask<A, B, C>.bindLifecycle(lifecycleOwner: LifecycleOwner) {
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

fun Context.hideInput(view: View) {
    val im = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    im.hideSoftInputFromWindow(view.windowToken, 0)
}