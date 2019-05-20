package com.sanron.pppig.module.mainhome.home

import androidx.databinding.DataBindingUtil
import com.alibaba.android.vlayout.layout.SingleLayoutHelper
import com.sanron.datafetch_interface.video.bean.HomeCat
import com.sanron.pppig.R
import com.sanron.pppig.databinding.ItemVideoListTitleBinding
import com.sanron.pppig.module.mainhome.home.HomeFragment.Companion.TYPE_TITLE

internal class TitleAdapter(binding: ItemVideoListTitleBinding, val homeCat: HomeCat, marginTop: Int)
    : SimpleViewAdapter(binding.root, SingleLayoutHelper().apply { this.marginTop = marginTop }) {

    override fun getItemViewType(position: Int): Int {
        return TYPE_TITLE
    }

    override fun onBindViewHolder(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int) {
        val binding = DataBindingUtil.getBinding<ItemVideoListTitleBinding>(holder.itemView)!!
        val icon = when (homeCat.type) {
            HomeCat.MOVIE -> R.drawable.ic_title_movie
            HomeCat.TV -> R.drawable.ic_title_tv
            HomeCat.ANIM -> R.drawable.ic_title_anim
            else -> R.drawable.ic_title_verity
        }
        binding.tvTitle.text = homeCat.name
        binding.ivIcon.setImageResource(icon)
        binding.executePendingBindings()
    }
}
