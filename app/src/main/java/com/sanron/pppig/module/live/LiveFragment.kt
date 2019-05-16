package com.sanron.pppig.module.live

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.sanron.datafetch_interface.live.LiveSource
import com.sanron.lib.StatusBarHelper
import com.sanron.pppig.R
import com.sanron.pppig.base.BaseViewModel
import com.sanron.pppig.base.LazyFragment
import com.sanron.pppig.data.FetchManager
import com.sanron.pppig.widget.BaseFragmentPageAdapter

/**
 * Author:sanron
 * Time:2019/2/21
 * Description:
 */
class LiveFragment : LazyFragment<com.sanron.pppig.databinding.FragmentLiveBinding, BaseViewModel>() {
    override fun initData() {
    }

    override fun createViewModel(): BaseViewModel? {
        return null
    }

    override fun getLayout(): Int {
        return R.layout.fragment_live
    }

    override fun initView() {
        StatusBarHelper.with(activity)
                .setPaddingTop(dataBinding.topBar)
        dataBinding.viewPager.adapter = PageAdapter(FetchManager.sourceManager.getLiveSourceList(), childFragmentManager)
        dataBinding.tabLayout.setViewPager(dataBinding.viewPager)

    }

    class PageAdapter(val sourceList: List<LiveSource>, fm: FragmentManager) : BaseFragmentPageAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            return LiveListFragment.new(sourceList[position].id)
        }

        override fun getCount() = sourceList.size

        override fun getPageTitle(position: Int): CharSequence? {
            return sourceList[position].name
        }
    }
}
