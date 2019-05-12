package com.sanron.pppig.module.mainhome

import android.arch.lifecycle.ViewModelProviders
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.util.SparseArray
import com.flyco.tablayout.listener.OnTabSelectListener
import com.sanron.datafetch_interface.bean.VideoListType
import com.sanron.lib.StatusBarHelper
import com.sanron.pppig.R
import com.sanron.pppig.base.LazyFragment
import com.sanron.pppig.data.Repo
import com.sanron.pppig.databinding.FragmentMainBinding
import com.sanron.pppig.module.mainhome.home.HomeFragment
import com.sanron.pppig.module.mainhome.videolist.VideoListFragment

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
        dataBinding.viewPager.adapter = PageAdapter(Repo.getVideoListTypes(), childFragmentManager).apply { pageAdapter = this }
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

    private class PageAdapter(val videoTypeList: List<VideoListType>, fm: FragmentManager) : FragmentPagerAdapter(fm) {

        private val fragments = SparseArray<Fragment>()
        val TITLES = mutableListOf<String>()

        init {
            TITLES.add("首页")
            videoTypeList.forEach {
                TITLES.add(it.name)
            }
        }

        fun getFragment(pos: Int): Fragment = fragments[pos]

        override fun getItem(i: Int): Fragment {
            return (when (i) {
                0 -> HomeFragment()
                else -> VideoListFragment.new(videoTypeList[i - 1].type)
            } as Fragment).apply { fragments.put(i, this) }
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return TITLES[position]
        }

        override fun getCount(): Int {
            return 1 + videoTypeList.size
        }
    }
}