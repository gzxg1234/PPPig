package com.sanron.pppig.module.main

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.design.bottomnavigation.LabelVisibilityMode
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.sanron.lib.StatusBarHelper
import com.sanron.pppig.R
import com.sanron.pppig.base.BaseActivity
import com.sanron.pppig.databinding.ActivityMainBinding
import com.sanron.pppig.module.live.LiveFragment
import com.sanron.pppig.module.mainhome.MainFragment

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

    private class HomePageAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(i: Int): Fragment {
            if (i == 0) {
                return MainFragment()
            } else if (i == 1) {
                return LiveFragment()
            } else if (i == 2) {
                return LiveFragment()
            }
            return LiveFragment()
        }

        override fun getCount(): Int {
            return 3
        }
    }
}
