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

    protected var mActivity: BaseActivity<T, M>? = null

    abstract val layout: Int

    protected var mDataBinding: T? = null

    protected var mViewModel: M? = null

    abstract fun createViewModel(): M

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivity = this
        mDataBinding = DataBindingUtil.setContentView(this, layout)
        mViewModel = createViewModel()
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
