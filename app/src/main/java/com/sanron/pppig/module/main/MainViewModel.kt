package com.sanron.pppig.module.main

import android.app.Application
import com.sanron.pppig.base.BaseViewModel
import kotlin.properties.Delegates
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Author:sanron
 * Time:2019/2/20
 * Description:
 */
fun <T> test(): DelegateTest<T> {
    return DelegateTest()
}

class DelegateTest<T> : ReadWriteProperty<Any, T> {


    override fun getValue(thisRef: Any, property: KProperty<*>): T {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {

        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}

class MainViewModel(application: Application) : BaseViewModel(application) {

    val s: Int by Delegates.notNull<Int>()
}
