package com.sanron.pppig.base

import androidx.lifecycle.Observer
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.sanron.pppig.util.runInMainIdle
import com.sanron.pppig.util.showToast

/**
 * Author:sanron
 * Time:2019/1/14
 * Description:
 */
abstract class BaseActivity<T : ViewDataBinding, M : BaseViewModel> : AppCompatActivity() {

    protected lateinit var activity: BaseActivity<T, M>

    private var mDataBinding: T? = null

    private var mViewModel: M? = null

    protected abstract fun getLayout(): Int

    abstract fun createViewModel(): M?

    //加载数据
    open fun initData() {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity = this
        mDataBinding = DataBindingUtil.setContentView(this, getLayout())
        mViewModel = createViewModel()
        mViewModel?.toastMsg?.observe(this, Observer {
            showToast(it)
        })
        runInMainIdle(this) {
            initData()
        }
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
