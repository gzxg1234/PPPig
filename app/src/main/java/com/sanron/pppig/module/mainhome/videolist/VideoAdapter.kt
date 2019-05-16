package com.sanron.pppig.module.mainhome.videolist

import android.app.Application
import androidx.lifecycle.LifecycleOwner
import android.content.Context
import androidx.databinding.DataBindingUtil
import com.chad.library.adapter.base.BaseViewHolder
import com.sanron.datafetch_interface.video.bean.VideoItem
import com.sanron.pppig.R
import com.sanron.pppig.base.CBaseAdapter
import com.sanron.pppig.databinding.ItemVideoBinding

/**
 *Author:sanron
 *Time:2019/4/16
 *Description:
 */
class VideoAdapter(context: Context, var lifecycleOwner: LifecycleOwner, data: List<VideoItem>?)
    : CBaseAdapter<VideoItem, BaseViewHolder>(context, R.layout.item_video, data) {

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