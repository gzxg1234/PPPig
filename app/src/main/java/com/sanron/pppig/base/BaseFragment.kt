package com.sanron.pppig.base

import android.arch.lifecycle.Observer
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sanron.pppig.util.showToast

/**
 * Author:sanron
 * Time:2019/2/20
 * Description:
 */
abstract class BaseFragment<T : ViewDataBinding, M : BaseViewModel> : Fragment() {

    protected var binding: T? = null

    protected var viewModel: M? = null


    protected abstract fun getLayout(): Int

    protected abstract fun createViewModel(): M?

    protected abstract fun initView()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel?.apply {
            toastCmd.observe(this@BaseFragment, Observer {
                showToast(it!!)
            })
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (binding == null) {
            binding = DataBindingUtil.inflate(inflater, getLayout(), container, false)
            viewModel = createViewModel()
            initView()
        }
        return binding?.root
    }

}
