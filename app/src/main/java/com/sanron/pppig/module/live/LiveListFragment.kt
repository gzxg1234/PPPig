package com.sanron.pppig.module.live

import android.content.Context
import android.os.Bundle
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseViewHolder
import com.sanron.datafetch_interface.live.bean.LiveCat
import com.sanron.datafetch_interface.live.bean.LiveItem
import com.sanron.pppig.R
import com.sanron.pppig.app.Intents
import com.sanron.pppig.base.CBaseAdapter
import com.sanron.pppig.base.LazyFragment
import com.sanron.pppig.binding.bindStateValue
import com.sanron.pppig.databinding.FragmentLiveListBinding
import com.sanron.pppig.module.play.PlayerAct
import com.sanron.pppig.util.getColorCompat

/**
 *Author:sanron
 *Time:2019/5/16
 *Description:
 */
class LiveListFragment : LazyFragment<FragmentLiveListBinding, LiveListVM>() {
    companion object {
        fun new(liveSourceId: String): LiveListFragment {
            return LiveListFragment().apply {
                this.arguments = Bundle().apply {
                    putString("sourceId", liveSourceId)
                }
            }
        }
    }

    override fun initData() {
        viewModel.loadCatList()
    }

    override fun getLayout() = R.layout.fragment_live_list

    override fun createViewModel(): LiveListVM? {
        return ViewModelProviders.of(this).get(LiveListVM::class.java)
    }

    override fun initView() {
        dataBinding.model = viewModel
        dataBinding.loadLayoutFirst.bindStateValue(this, viewModel.catLoadingState)
        dataBinding.loadLayoutSecond.bindStateValue(this, viewModel.itemLoadingState)
        dataBinding.loadLayoutFirst.setOnReloadListener {
            viewModel.loadCatList()
        }
        dataBinding.loadLayoutSecond.setOnReloadListener {
            viewModel.loadItemList()
        }
        dataBinding.listCat.layoutManager = LinearLayoutManager(context)
        LiveCatAdapter(context!!).apply {
            viewModel.currentCatPos.observe(this@LiveListFragment, Observer {
                selectedPos = it
            })
            setOnItemClickListener { adapter, view, position ->
                selectedPos = position
                viewModel.setCurrentCatPos(position)
            }
            bindToRecyclerView(dataBinding.listCat)
        }
        dataBinding.listItem.layoutManager = LinearLayoutManager(context)
        LiveItemAdapter(context!!).apply {
            setOnItemClickListener { adapter, view, position ->
                viewModel.onClickItem(position)
            }
            bindToRecyclerView(dataBinding.listItem)
        }

        viewModel.toPlayPage.observe(this, Observer {
            it?.let {
                startActivity(Intents.playVideo(context!!, it.first.name, it.second, 0, 0, PlayerAct.TYPE_LIVE,
                        viewModel.liveSourceId))
            }
        })
        viewModel.init(arguments!!)
    }

    class LiveCatAdapter(context: Context)
        : CBaseAdapter<LiveCat, BaseViewHolder>(context, R.layout.item_live_cat) {

        var selectedPos = -1
            set(value) {
                val f = field
                field = value
                notifyItemChanged(f)
                notifyItemChanged(field)
            }

        override fun convert(helper: BaseViewHolder, item: LiveCat?) {
            val text = helper.getView<TextView>(R.id.tv_name)
            text.text = item?.name
            if (selectedPos == helper.adapterPosition) {
                text.setTextColor(context.getColorCompat(R.color.colorPrimary))
                helper.itemView.setBackgroundColor(context.getColorCompat(R.color.white))
            } else {
                text.setTextColor(context.getColorCompat(R.color.textColor1))
                helper.itemView.setBackgroundColor(context.getColorCompat(R.color.transparent))
            }
        }
    }


    class LiveItemAdapter(context: Context)
        : CBaseAdapter<LiveItem, BaseViewHolder>(context, R.layout.item_live_cat) {
        override fun convert(helper: BaseViewHolder, item: LiveItem?) {
            val text = helper.getView<TextView>(R.id.tv_name)
            text.text = item?.name
        }
    }
}