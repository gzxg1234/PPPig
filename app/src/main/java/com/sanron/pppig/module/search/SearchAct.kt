package com.sanron.pppig.module.search

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.util.SparseArray
import android.view.inputmethod.EditorInfo
import com.sanron.datafetch_interface.Source
import com.sanron.lib.StatusBarHelper
import com.sanron.pppig.R
import com.sanron.pppig.base.BaseActivity
import com.sanron.pppig.base.BaseViewModel
import com.sanron.pppig.data.FetchManager
import com.sanron.pppig.databinding.ActivitySearchBinding
import com.sanron.pppig.util.bindClear
import com.sanron.pppig.util.getColorCompat

/**
 *
 * @author chenrong
 * @date 2019/5/11
 */
class SearchAct : BaseActivity<ActivitySearchBinding, BaseViewModel>() {
    var currentWord = ""

    override fun getLayout(): Int {
        return R.layout.activity_search
    }

    override fun createViewModel(): BaseViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatusBarHelper.with(this)
                .setLayoutBelowStatusBar(false)
                .setStatusBarColor(getColorCompat(R.color.colorPrimary))
        dataBinding.ivBack.setOnClickListener {
            finish()
        }
        dataBinding.etWord.bindClear(dataBinding.ivClear)
        dataBinding.etWord.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                startSearch(v.text.toString())
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }
        dataBinding.viewPager.adapter = PageAdapter(FetchManager.sourceManager.getSourceList(), supportFragmentManager)
        dataBinding.tabLayout.setViewPager(dataBinding.viewPager)
    }

    private fun startSearch(word: String) {
        if (word == currentWord) {
            return
        }
        (dataBinding.viewPager.adapter as PageAdapter).setWord(word)
    }

    private class PageAdapter(val sourceList: List<Source>, fm: FragmentManager) : FragmentPagerAdapter(fm) {

        private val fragments = SparseArray<SearchFragment>()
        private var titles: List<String> = sourceList.map {
            it.name
        }

        fun setWord(word: String) {
            for (i in 0 until fragments.size()) {
                fragments.valueAt(i).setWord(word)
            }
        }

        override fun getItem(i: Int): Fragment {
            return (SearchFragment.new(sourceList[i].id)).apply {
                fragments.put(i, this)
            }
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return titles[position]
        }

        override fun getCount(): Int {
            return titles.size
        }
    }
}