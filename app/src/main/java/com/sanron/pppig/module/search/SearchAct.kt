package com.sanron.pppig.module.search

import android.os.Bundle
import com.sanron.pppig.R
import com.sanron.pppig.base.BaseActivity
import com.sanron.pppig.base.BaseViewModel
import com.sanron.pppig.databinding.ActivitySearchBinding

/**
 *
 * @author chenrong
 * @date 2019/5/11
 */
class SearchAct : BaseActivity<ActivitySearchBinding, BaseViewModel>() {
    override fun getLayout(): Int {
        return R.layout.activity_search
    }

    override fun createViewModel(): BaseViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
}