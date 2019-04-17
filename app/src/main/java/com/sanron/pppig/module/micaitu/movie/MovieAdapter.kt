package com.sanron.pppig.module.micaitu.movie

import android.app.Application
import android.arch.lifecycle.LifecycleOwner
import android.databinding.DataBindingUtil
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.sanron.pppig.R
import com.sanron.pppig.base.CBaseAdapter
import com.sanron.pppig.data.bean.micaitu.VideoItem
import com.sanron.pppig.databinding.ItemMovieBinding

/**
 *Author:sanron
 *Time:2019/4/16
 *Description:
 */
class MovieAdapter(data: List<VideoItem>?) : CBaseAdapter<VideoItem, BaseViewHolder>(R.layout.item_movie, data) {


    var lifecycleOwner: LifecycleOwner? = null

    override fun convert(helper: BaseViewHolder, item: VideoItem) {
        val dataBinding = DataBindingUtil.bind<ItemMovieBinding>(helper.itemView)!!
        dataBinding.model ?: run {
            dataBinding.model = ItemMovieViewModel(mContext.applicationContext as Application)
            dataBinding.lifecycleOwner = lifecycleOwner
        }
        dataBinding.model?.apply {
            label.value = item.label
            name.value = item.name
            img.value = item.img
        }
        dataBinding.executePendingBindings()
    }
}