package com.sanron.pppig.base

import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.v7.app.AppCompatActivity

/**
 * Author:sanron
 * Time:2019/1/14
 * Description:
 */
abstract class BaseActivity<T : ViewDataBinding, M : BaseViewModel> : AppCompatActivity() {

    protected lateinit var activity: BaseActivity<T, M>

    abstract val layout: Int

    private var mDataBinding: T? = null

    private var mViewModel: M? = null

    abstract fun createViewModel(): M

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity = this
        mDataBinding = DataBindingUtil.setContentView(this, layout)
        mViewModel = createViewModel()
    }

    var dataBinding: T
        private set(value) {}
        get() {
            return mDataBinding!!
        }
    var viewModel: M
        private set(value) {}
        get() {
            return mViewModel!!
        }
}
