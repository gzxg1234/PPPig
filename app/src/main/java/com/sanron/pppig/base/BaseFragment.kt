package com.sanron.pppig.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import com.sanron.pppig.common.LoadingDlg
import com.sanron.pppig.util.showToast

/**
 * Author:sanron
 * Time:2019/2/20
 * Description:
 */
abstract class BaseFragment<T : ViewDataBinding, M : BaseViewModel> : androidx.fragment.app.Fragment() {

    private var mDataBinding: T? = null

    private var mViewModel: M? = null

    val loadingDialog: LoadingDlg by lazy {
        LoadingDlg(context!!)
    }

    protected var dataBinding: T
        private set(value) {}
        get() {
            return mDataBinding!!
        }
    protected var viewModel: M
        private set(value) {}
        get() {
            return mViewModel!!
        }

    protected abstract fun getLayout(): Int

    protected abstract fun createViewModel(): M?

    protected abstract fun initView()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mViewModel?.toastMsg?.observe(this@BaseFragment, Observer {
            showToast(it)
        })
        mViewModel?.rxShowLoading?.observe(this, Observer { b ->
            if (b != null) {
                loadingDialog.setOnCancelListener {
                    b.dispose()
                }
                loadingDialog.show()
            } else {
                loadingDialog.dismiss()
            }
        })
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (mDataBinding == null) {
            mDataBinding = DataBindingUtil.inflate(inflater, getLayout(), container, false)
            mDataBinding?.lifecycleOwner = this
            mViewModel = createViewModel()
            initView()
        }
        return mDataBinding?.root
    }

}
