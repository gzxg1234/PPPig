package com.sanron.pppig.util

import android.content.Context
import android.databinding.Observable
import android.databinding.ObservableField
import android.view.LayoutInflater
import com.sanron.pppig.BR
import me.jessyan.autosize.utils.AutoSizeUtils

/**
 *Author:sanron
 *Time:2019/4/9
 *Description:
 */
fun Context.dp2px(dp: Float) = AutoSizeUtils.dp2px(this, dp)

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