package com.sanron.pppig.module.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.bottomnavigation.LabelVisibilityMode
import com.sanron.lib.StatusBarHelper
import com.sanron.pppig.R
import com.sanron.pppig.base.BaseActivity
import com.sanron.pppig.databinding.ActivityMainBinding
import com.sanron.pppig.module.live.LiveFragment
import com.sanron.pppig.module.mainhome.MainFragment
import com.sanron.pppig.widget.BaseFragmentPageAdapter

/**
 * Author:sanron
 * Time:2019/2/20
 * Description:
 */
class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>() {

    override fun getLayout(): Int = R.layout.activity_main

    override fun createViewModel(): MainViewModel {
        return ViewModelProviders.of(this).get(MainViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
    }

    private fun initView() {
        StatusBarHelper.with(this)
                .setStatusBarColor(0)
                .setLayoutBelowStatusBar(true)
        dataBinding.apply {
            viewPager.adapter = HomePageAdapter(supportFragmentManager)
            bottomNavigationBar.labelVisibilityMode = LabelVisibilityMode.LABEL_VISIBILITY_LABELED
            bottomNavigationBar.setupWithViewPager(dataBinding.viewPager)
        }
    }

    private class HomePageAdapter(fm: FragmentManager) : BaseFragmentPageAdapter(fm) {

        override fun getItem(i: Int): Fragment {
            if (i == 0) {
                return MainFragment()
            } else if (i == 1) {
                return LiveFragment()
            } else if (i == 2) {
                return Fragment()
            }
            return Fragment()
        }

        override fun getCount(): Int {
            return 3
        }
    }
}
