package com.sanron.pppig.base

import android.arch.lifecycle.Observer
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.os.Looper
import android.os.MessageQueue
import android.support.v7.app.AppCompatActivity
import com.sanron.pppig.util.showToast

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

    private val idleHandler = MessageQueue.IdleHandler {
        initData()
        false
    }

    abstract fun createViewModel(): M

    //加载数据
    open fun initData() {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity = this
        mDataBinding = DataBindingUtil.setContentView(this, layout)
        mViewModel = createViewModel()
        mViewModel?.toastMsg?.observe(this, Observer {
            showToast(it)
        })
        Looper.myQueue().addIdleHandler(idleHandler)
    }

    override fun onDestroy() {
        Looper.myQueue().removeIdleHandler(idleHandler)
        super.onDestroy()
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
