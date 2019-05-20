package com.sanron.pppig.module.search

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProviders
import com.sanron.pppig.R
import com.sanron.pppig.app.Intents
import com.sanron.pppig.base.LazyFragment
import com.sanron.pppig.binding.bindPageLoader
import com.sanron.pppig.databinding.FragmentSearchResultBinding
import com.sanron.pppig.module.mainhome.IMainChildFragment
import com.sanron.pppig.module.mainhome.videolist.VideoAdapter
import com.sanron.pppig.util.dp2px
import com.sanron.pppig.util.gap
import com.sanron.pppig.util.pauseFrescoOnScroll

/**
 *Author:sanron
 *Time:2019/4/16
 *Description:
 */
class SearchResultFragment : LazyFragment<FragmentSearchResultBinding, SearchVM>(), IMainChildFragment {

    companion object {

        fun new(sourceId: String, word: String): SearchResultFragment {
            return SearchResultFragment().apply {
                val args = Bundle(1)
                args.putString("id", sourceId)
                args.putString("word", word)
                arguments = args
            }
        }
    }

    private lateinit var adapter: VideoAdapter

    override fun initData() {
        viewModel.refresh()
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
        arguments?.putString("word", word)
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

            adapter = VideoAdapter(context!!, this@SearchResultFragment, viewModel.pageLoader.listData.value)
            adapter.setOnItemClickListener { adapter1, view, position ->
                startActivity(Intents.videoDetail(context!!, adapter.getItem(position)?.link, arguments?.getString("id")
                        ?: "" ?: ""))
            }
            adapter.lifecycleOwner = this@SearchResultFragment
            adapter.bindToRecyclerView(recyclerView)
        }
        dataBinding.refreshLayout.bindPageLoader(this, viewModel.pageLoader)
        dataBinding.recyclerView.bindPageLoader(this, viewModel.pageLoader)
        dataBinding.loadLayout.bindPageLoader(this, viewModel.pageLoader)
        viewModel.setSource(arguments?.getString("id") ?: "")
        viewModel.word = (arguments?.getString("word") ?: "")
    }
}

