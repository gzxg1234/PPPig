package com.sanron.pppig.base

import android.arch.lifecycle.Observer
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sanron.pppig.util.CLog
import com.sanron.pppig.util.showToast

/**
 * Author:sanron
 * Time:2019/2/20
 * Description:
 */
abstract class BaseFragment<T : ViewDataBinding, M : BaseViewModel> : Fragment() {

    private var mDataBinding: T? = null

    private var mViewModel: M? = null

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

    protected abstract fun getLayout(): Int

    protected abstract fun createViewModel(): M?

    protected abstract fun initView()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mViewModel?.apply {
            toastCmd.observe(this@BaseFragment, Observer {
                showToast(it!!)
            })
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (mDataBinding == null) {
            mDataBinding = DataBindingUtil.inflate(inflater, getLayout(), container, false)
            mViewModel = createViewModel()
            initView()
        }
        return mDataBinding?.root
    }

}
