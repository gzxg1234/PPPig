package com.sanron.pppig.module.home

import android.arch.lifecycle.ViewModelProviders
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.sanron.pppig.R
import com.sanron.pppig.base.LazyFragment
import com.sanron.pppig.databinding.FragmentMainBinding
import com.sanron.pppig.module.micaitu.home.HomeFragment
import com.sanron.pppig.module.micaitu.movie.MovieFragment

/**
 * Author:sanron
 * Time:2019/2/20
 * Description:
 */
class MainFragment : LazyFragment<FragmentMainBinding, MainFragViewModel>() {


    override fun createViewModel(): MainFragViewModel {
        return ViewModelProviders.of(this).get(MainFragViewModel::class.java)
    }

    override fun getLayout(): Int {
        return R.layout.fragment_main
    }

    override fun initView() {
        binding!!.viewPager.adapter = PageAdapter(childFragmentManager)
        binding!!.tabLayout.setViewPager(binding!!.viewPager)
    }

    override fun initData() {
        viewModel!!.getData()
    }

    private class PageAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        companion object {
            val TITLES = arrayOf("首页", "电影")
        }

        override fun getItem(i: Int): Fragment {
            return when (i) {
                0 -> HomeFragment()
                else -> MovieFragment()
            }
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return TITLES[position]
        }

        override fun getCount(): Int {
            return TITLES.size
        }
    }
}
