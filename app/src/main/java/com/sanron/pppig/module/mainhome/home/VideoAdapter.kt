package com.sanron.pppig.module.mainhome.home

import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import com.alibaba.android.vlayout.DelegateAdapter
import com.alibaba.android.vlayout.LayoutHelper
import com.alibaba.android.vlayout.layout.GridLayoutHelper
import com.sanron.datafetch_interface.video.bean.VideoItem
import com.sanron.pppig.R
import com.sanron.pppig.app.Intents
import com.sanron.pppig.app.PiApp
import com.sanron.pppig.data.FetchManager
import com.sanron.pppig.databinding.ItemVideoBinding
import com.sanron.pppig.module.mainhome.home.HomeFragment.Companion.TYPE_VIDEO
import com.sanron.pppig.module.mainhome.videolist.ItemVideoVM
import com.sanron.pppig.util.dp2px
import com.sanron.pppig.util.inflater

internal class VideoAdapter(val fragment: HomeFragment, private val items: List<VideoItem>?) : DelegateAdapter.Adapter<androidx.recyclerview.widget.RecyclerView.ViewHolder>() {

    val context = fragment.context!!

    override fun getItemCount() = items?.size ?: 0

    override fun onCreateLayoutHelper(): LayoutHelper {
        val helper = GridLayoutHelper(3)
        helper.setWeights(floatArrayOf(33.3f, 33.3f, 33.3f))
        helper.hGap = context.dp2px(8f)
        helper.vGap = context.dp2px(8f)
        helper.marginLeft = context.dp2px(16f)
        helper.marginRight = context.dp2px(16f)
        return helper
    }

    override fun onBindViewHolder(p0: androidx.recyclerview.widget.RecyclerView.ViewHolder, p1: Int) {
        val binding = DataBindingUtil.getBinding<ItemVideoBinding>(p0.itemView)!!
        binding.model!!.item.value = items!![p1]
        binding.executePendingBindings()
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): androidx.recyclerview.widget.RecyclerView.ViewHolder {
        val binding = DataBindingUtil.inflate<ItemVideoBinding>(p0.context.inflater, R.layout.item_video, p0, false)
        val viewHolder = SimpleViewHolder(binding.root)
        binding.lifecycleOwner = p0.context as LifecycleOwner
        binding.model = ItemVideoVM(PiApp.sInstance)
        binding.root.setOnClickListener {
            val item = binding.model!!.item.value!!
            val intent = Intents.videoDetail(context, item.link, FetchManager.currentVideoSourceId()
                    ?: "")
            fragment.startActivity(intent)
        }
        return viewHolder
    }


    override fun getItemViewType(position: Int): Int {
        return TYPE_VIDEO
    }
}
