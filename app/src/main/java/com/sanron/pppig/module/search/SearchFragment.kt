package com.sanron.pppig.module.search

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProviders
import com.sanron.pppig.R
import com.sanron.pppig.app.Intents
import com.sanron.pppig.base.LazyFragment
import com.sanron.pppig.common.bindRecyclerView
import com.sanron.pppig.common.bindRefreshLayout
import com.sanron.pppig.databinding.FragmentSearchResultBinding
import com.sanron.pppig.module.mainhome.IMainChildFragment
import com.sanron.pppig.module.mainhome.videolist.VideoAdapter
import com.sanron.pppig.util.dp2px
import com.sanron.pppig.util.gap
import com.sanron.pppig.util.pauseFrescoOnScroll
import com.sanron.pppig.util.runInMainIdle

/**
 *Author:sanron
 *Time:2019/4/16
 *Description:
 */
class SearchFragment : LazyFragment<FragmentSearchResultBinding, SearchVM>(), IMainChildFragment {

    companion object {

        fun new(sourceId: String, word: String): SearchFragment {
            return SearchFragment().apply {
                val args = Bundle(1)
                args.putString("id", sourceId)
                args.putString("word", word)
                arguments = args
            }
        }
    }

    private lateinit var adapter: VideoAdapter

    override fun initData() {
        runInMainIdle(this) {
            viewModel.refresh()
        }
    }

    override fun getLayout() = R.layout.fragment_search_result

    override fun createViewModel(): SearchVM? {
        return ViewModelProviders.of(this).get(SearchVM::class.java)
    }

    override fun onReselect() {
        viewModel.pageLoader.apply {
            dataBinding.recyclerView.smoothScrollToPosition(0)
            refresh()
        }
    }

    fun setWord(word: String) {
        if (lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
            viewModel.word = word
            adapter.data.clear()
            adapter.notifyDataSetChanged()
            if (isActive) {
                viewModel.refresh()
            } else {
                reloadDataInNextActive()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun initView() {
        dataBinding.apply {
            model = viewModel
            recyclerView.pauseFrescoOnScroll()
            recyclerView.layoutManager = androidx.recyclerview.widget.GridLayoutManager(context, 3)
            recyclerView.gap(context!!.dp2px(8f), context!!.dp2px(8f))

            adapter = VideoAdapter(context!!, this@SearchFragment, viewModel.pageLoader.listData.value)
            adapter.setOnItemClickListener { adapter1, view, position ->
                startActivity(Intents.videoDetail(context!!, adapter.getItem(position)?.link, arguments?.getString("id")
                        ?: "" ?: ""))
            }
            adapter.lifecycleOwner = this@SearchFragment
            adapter.bindToRecyclerView(recyclerView)
        }
        viewModel.pageLoader.bindRecyclerView(this, dataBinding.recyclerView)
        viewModel.pageLoader.bindRefreshLayout(this, dataBinding.refreshLayout)
        viewModel.setSource(arguments?.getString("id") ?: "")
        viewModel.word = (arguments?.getString("word") ?: "")
    }
}

