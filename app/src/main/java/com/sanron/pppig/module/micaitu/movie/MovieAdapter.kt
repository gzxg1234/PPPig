package com.sanron.pppig.module.micaitu.movie

import android.app.Application
import com.sanron.pppig.R
import com.sanron.pppig.base.DataBindingAdapter
import com.sanron.pppig.data.bean.micaitu.VideoItem
import com.sanron.pppig.databinding.ItemMovieBinding

/**
 *Author:sanron
 *Time:2019/4/16
 *Description:
 */
class MovieAdapter(data: List<VideoItem>?) : DataBindingAdapter<VideoItem, ItemMovieBinding>(R.layout.item_movie, data) {

    override fun bind(dataBinding: ItemMovieBinding, item: VideoItem) {
        dataBinding.model ?: run {
            dataBinding.model = ItemMovieViewModel(mContext.applicationContext as Application)
        }
        dataBinding.model?.apply {
            label.value = item.label
            name.value = item.name
            img.value = item.img
        }
        dataBinding.executePendingBindings()
    }

}