package com.sanron.pppig.module.live

import android.content.Context
import android.os.Bundle
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseViewHolder
import com.kingja.loadsir.core.LoadService
import com.kingja.loadsir.core.LoadSir
import com.sanron.datafetch_interface.live.bean.LiveCat
import com.sanron.datafetch_interface.live.bean.LiveItem
import com.sanron.pppig.R
import com.sanron.pppig.base.CBaseAdapter
import com.sanron.pppig.base.LazyFragment
import com.sanron.pppig.base.state.bindStateValue
import com.sanron.pppig.util.getColorCompat

/**
 *Author:sanron
 *Time:2019/5/16
 *Description:
 */
class LiveListFragment : LazyFragment<com.sanron.pppig.databinding.FragmentLiveListBinding, LiveListVM>() {
    private val catLoadService: LoadService<Any> by lazy {
        LoadSir.getDefault().register(dataBinding.llContent) {
            viewModel.loadCatList()
        }
    }
    private val itemLoadService: LoadService<Any> by lazy {
        LoadSir.getDefault().register(dataBinding.listItem) {
            viewModel.loadItemList()
        }
    }

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
        dataBinding.lifecycleOwner = this
        catLoadService.bindStateValue(this, viewModel.catLoadingState)
        itemLoadService.bindStateValue(this, viewModel.itemLoadingState)
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
        LiveItemAdapter(context!!).bindToRecyclerView(dataBinding.listItem)
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

        override fun convert(helper: BaseViewHolder, item: LiveCat) {
            val text = helper.getView<TextView>(R.id.tv_name)
            text.text = item.name
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
        override fun convert(helper: BaseViewHolder, item: LiveItem) {
            val text = helper.getView<TextView>(R.id.tv_name)
            text.text = item.name
        }
    }
}