package com.sanron.pppig.module.live

import android.databinding.ViewDataBinding
import com.sanron.pppig.R
import com.sanron.pppig.base.BaseViewModel
import com.sanron.pppig.base.LazyFragment

/**
 * Author:sanron
 * Time:2019/2/21
 * Description:
 */
class LiveFragment : LazyFragment<ViewDataBinding, BaseViewModel>() {
    override fun initData() {

    }

    override fun createViewModel(): BaseViewModel? {
        return null
    }

    override fun getLayout(): Int {
        return R.layout.layout_tab
    }

    override fun initView() {

    }
}
