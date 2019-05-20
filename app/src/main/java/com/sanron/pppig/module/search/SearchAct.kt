package com.sanron.pppig.module.search

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.SparseArray
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseViewHolder
import com.sanron.datafetch_interface.video.VideoSource
import com.sanron.lib.StatusBarHelper
import com.sanron.pppig.R
import com.sanron.pppig.base.BaseActivity
import com.sanron.pppig.base.BaseViewModel
import com.sanron.pppig.base.CBaseAdapter
import com.sanron.pppig.data.FetchManager
import com.sanron.pppig.data.HistoryManager
import com.sanron.pppig.databinding.ActivitySearchBinding
import com.sanron.pppig.util.bindClear
import com.sanron.pppig.util.getColorCompat
import com.sanron.pppig.util.hideInput
import com.sanron.pppig.widget.BaseFragmentPageAdapter

/**
 *
 * @author chenrong
 * @date 2019/5/11
 */
class SearchAct : BaseActivity<ActivitySearchBinding, BaseViewModel>() {
    var currentWord = ""

    val historyAdapter: HistoryItemAdapter by lazy {
        HistoryItemAdapter(activity).apply {
            isUseEmpty(false)
        }
    }

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
            }
            return@setOnEditorActionListener false
        }
        dataBinding.viewPager.adapter = PageAdapter(FetchManager.sourceManager.getVideoSourceList(), supportFragmentManager)
        dataBinding.tabLayout.setViewPager(dataBinding.viewPager)

        dataBinding.rvHistory.layoutManager = LinearLayoutManager(activity)
        historyAdapter.bindToRecyclerView(dataBinding.rvHistory)
        HistoryManager.history.observe(this, Observer {
            historyAdapter.setNewData(it)
        })
        historyAdapter.setOnItemChildClickListener { adapter, view, position ->
            if (view.id == R.id.iv_delete) {
                historyAdapter.getItem(position)?.let { HistoryManager.remove(it) }
            }
        }
        historyAdapter.setOnItemClickListener { adapter, view, position ->
            historyAdapter.getItem(position)?.let { word ->
                dataBinding.etWord.setText(word)
                activity.hideInput(dataBinding.etWord)
                startSearch(word)
            }
        }
        dataBinding.tvClearHistory.setOnClickListener {
            HistoryManager.clear()
        }
        dataBinding.etWord.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrEmpty()) {
                    dataBinding.llHistory.visibility = View.VISIBLE
                    dataBinding.llResult.visibility = View.GONE
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
    }


    private fun startSearch(word: String) {
        if (word.isEmpty()) {
            return
        }
        HistoryManager.add(word)
        dataBinding.llHistory.visibility = View.GONE
        dataBinding.llResult.visibility = View.VISIBLE
        if (word != currentWord) {
            currentWord = word
            (dataBinding.viewPager.adapter as PageAdapter).setWord(word)
        }
    }

    class HistoryItemAdapter(context: Context) : CBaseAdapter<String, BaseViewHolder>(context,
            R.layout.item_search_history) {

        override fun convert(helper: BaseViewHolder, item: String?) {
            helper.setText(R.id.tv_word, item)
            helper.addOnClickListener(R.id.iv_delete)
        }
    }

    private class PageAdapter(val videoSourceList: List<VideoSource>, fm: FragmentManager) : BaseFragmentPageAdapter(fm) {

        private val fragments = SparseArray<SearchResultFragment>()
        private var word: String = ""
        private var titles: List<String> = videoSourceList.map {
            it.name
        }

        fun setWord(word: String) {
            this.word = word
            for (i in 0 until fragments.size()) {
                fragments.valueAt(i).setWord(word)
            }
        }

        override fun getItem(i: Int): Fragment {
            return SearchResultFragment.new(videoSourceList[i].id, word).let { fragment ->
                fragments.put(i, fragment)
                return@let fragment
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