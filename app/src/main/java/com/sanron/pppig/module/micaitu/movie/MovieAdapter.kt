package com.sanron.pppig.module.micaitu.movie

import android.app.Application
import android.arch.lifecycle.LifecycleOwner
import android.databinding.DataBindingUtil
import com.chad.library.adapter.base.BaseViewHolder
import com.sanron.pppig.R
import com.sanron.pppig.base.CBaseAdapter
import com.sanron.pppig.data.bean.micaitu.VideoItem
import com.sanron.pppig.databinding.ItemVideoBinding

/**
 *Author:sanron
 *Time:2019/4/16
 *Description:
 */
class MovieAdapter(var lifecycleOwner: LifecycleOwner, data: List<VideoItem>?) : CBaseAdapter<VideoItem, BaseViewHolder>(R.layout.item_video, data) {

    override fun convert(helper: BaseViewHolder, item: VideoItem) {
        val dataBinding = DataBindingUtil.bind<ItemVideoBinding>(helper.itemView)!!
        dataBinding.model ?: run {
            dataBinding.model = ItemVideoVM(mContext.applicationContext as Application)
            dataBinding.lifecycleOwner = lifecycleOwner
        }
        dataBinding.model?.item?.value = item
        dataBinding.executePendingBindings()
    }
}