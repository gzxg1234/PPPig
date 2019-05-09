package com.sanron.pppig.module.home

import android.arch.lifecycle.ViewModelProviders
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.util.SparseArray
import com.flyco.tablayout.listener.OnTabSelectListener
import com.sanron.lib.StatusBarHelper
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

    private lateinit var pageAdapter: PageAdapter

    override fun createViewModel(): MainFragViewModel {
        return ViewModelProviders.of(this).get(MainFragViewModel::class.java)
    }

    override fun onVisible(first: Boolean) {
        super.onVisible(first)
        StatusBarHelper.with(activity)
                .setLightIcon()
                .setPaddingTop(dataBinding.topBar)
    }

    override fun getLayout(): Int {
        return R.layout.fragment_main
    }

    override fun initView() {
        dataBinding.viewPager.adapter = PageAdapter(childFragmentManager).apply { pageAdapter = this }
        dataBinding.tabLayout.setViewPager(dataBinding.viewPager)
        dataBinding.tabLayout.setOnTabSelectListener(object : OnTabSelectListener {
            override fun onTabSelect(position: Int) {
            }

            override fun onTabReselect(position: Int) {
                (pageAdapter.getFragment(position) as IMainChildFragment).onReselect()
            }
        })
    }

    override fun initData() {
        viewModel.getData()
    }

    private class PageAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        private val fragments = SparseArray<Fragment>()

        companion object {
            val TITLES = arrayOf("首页", "电影")
        }

        fun getFragment(pos: Int): Fragment = fragments[pos]

        override fun getItem(i: Int): Fragment {
            return (when (i) {
                0 -> HomeFragment()
                else -> MovieFragment()
            } as Fragment).apply { fragments.put(i, this) }
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return TITLES[position]
        }

        override fun getCount(): Int {
            return TITLES.size
        }
    }
}
