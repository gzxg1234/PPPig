package com.sanron.pppig.module.mainhome.home

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.app.SharedElementCallback
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.alibaba.android.vlayout.DelegateAdapter
import com.alibaba.android.vlayout.LayoutHelper
import com.alibaba.android.vlayout.VirtualLayoutManager
import com.alibaba.android.vlayout.layout.GridLayoutHelper
import com.alibaba.android.vlayout.layout.SingleLayoutHelper
import com.facebook.drawee.view.SimpleDraweeView
import com.sanron.datafetch_interface.video.bean.Banner
import com.sanron.datafetch_interface.video.bean.Home
import com.sanron.datafetch_interface.video.bean.HomeCat
import com.sanron.datafetch_interface.video.bean.VideoItem
import com.sanron.pppig.R
import com.sanron.pppig.app.Intents
import com.sanron.pppig.app.PiApp
import com.sanron.pppig.base.LazyFragment
import com.sanron.pppig.data.FetchManager
import com.sanron.pppig.databinding.FragmentHomeBinding
import com.sanron.pppig.databinding.ItemVideoBinding
import com.sanron.pppig.databinding.ItemVideoListTitleBinding
import com.sanron.pppig.module.mainhome.IMainChildFragment
import com.sanron.pppig.module.mainhome.home.HomeFragment.Companion.TYPE_TITLE
import com.sanron.pppig.module.mainhome.home.HomeFragment.Companion.TYPE_VIDEO
import com.sanron.pppig.module.mainhome.videolist.ItemVideoVM
import com.sanron.pppig.util.*
import com.sanron.pppig.widget.ViewPagerAdapter
import com.tmall.ultraviewpager.UltraViewPager

/**
 * Author:sanron
 * Time:2019/2/21
 * Description:
 */
class HomeFragment : LazyFragment<FragmentHomeBinding, HomeVM>(), IMainChildFragment {

    companion object {
        const val TYPE_BANNER = 1
        const val TYPE_TITLE = 2
        const val TYPE_VIDEO = 3
    }

    val bannerAdapter: SimpleViewAdapter by lazy {
        initBannerAdapter()
    }
    lateinit var layoutManager: VirtualLayoutManager
    lateinit var adapter: DelegateAdapter

    override fun initData() {
        viewModel.loadData()
    }

    override fun createViewModel(): HomeVM? {
        return ViewModelProviders.of(this).get(HomeVM::class.java)
    }

    override fun getLayout(): Int {
        return R.layout.fragment_home
    }

    override fun onReselect() {
        viewModel.loadData()
        dataBinding.recyclerView.smoothScrollToPosition(0)
    }

    fun initBannerAdapter(): SimpleViewAdapter {
        val banner = context!!.inflater.inflate(R.layout.item_banner, dataBinding.recyclerView, false) as UltraViewPager
        banner.setPageTransformer(true) { p0, p1 ->
            if (p1 < 0) {
                p0.scaleX = 1 + p1 * 0.1f
                p0.scaleY = 1 + p1 * 0.1f
            } else {
                p0.scaleX = 1 - p1 * 0.1f
                p0.scaleY = 1 - p1 * 0.1f
            }
        }
        banner.initIndicator()
                .setFocusColor(activity!!.getColorCompat(R.color.colorPrimary))
                .setNormalColor(activity!!.getColorCompat(R.color.white))
                .setRadius(context!!.dp2px(4f))
                .setGravity(Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM)
                .setStrokeColor(0xffeeeeee.toInt())
                .setStrokeWidth(context!!.dp2px(0.5f))
                .build()
        banner.viewPager.setPadding(paddingBottom = context!!.dp2px(15f))
        return object : SimpleViewAdapter(banner, SingleLayoutHelper()) {
            override fun getItemViewType(position: Int): Int {
                return TYPE_BANNER
            }
        }
    }

    override fun initView() {
        dataBinding.apply {
            model = viewModel
            recyclerView.pauseFrescoOnScroll()
            layoutManager = VirtualLayoutManager(context!!)
            recyclerView.layoutManager = layoutManager
            layoutManager.setNestedScrolling(false)
            recyclerView.setRecycledViewPool(androidx.recyclerview.widget.RecyclerView.RecycledViewPool().apply {
                setMaxRecycledViews(TYPE_VIDEO, 9)
                setMaxRecycledViews(TYPE_BANNER, 1)
                setMaxRecycledViews(TYPE_TITLE, 5)
            })
            adapter = DelegateAdapter(layoutManager, true)
            recyclerView.adapter = adapter
            refreshLayout.handleScrollHorizontalConflict = true
            recyclerView.handleScrollHorizontalConflict = true
        }
        viewModel.toVideoDetail.observe(this, Observer {
            val intent = Intents.videoDetail(context!!, it?.link, FetchManager.currentVideoSourceId()
                    ?: "")
            startActivity(intent)
        })
        activity?.setExitSharedElementCallback(object : SharedElementCallback() {
            override fun onSharedElementEnd(sharedElementNames: MutableList<String>?, sharedElements: MutableList<View>?, sharedElementSnapshots: MutableList<View>?) {
                super.onSharedElementEnd(sharedElementNames, sharedElements, sharedElementSnapshots)
                if (sharedElements != null) {
                    for (v in sharedElements) {
                        //fresco用shareElement回来后会被设置为invisible导致不见
                        if (v is SimpleDraweeView) {
                            v.visibility = View.VISIBLE
                        }
                    }
                }
            }
        })
        viewModel.homeData.observe(this@HomeFragment, Observer<Home> { homeData ->
            dataBinding.apply {
                val adapters = mutableListOf<DelegateAdapter.Adapter<*>>()
                val viewPager = bannerAdapter.view as UltraViewPager
                if (homeData?.banner?.size ?: 0 <= 1) {
                    viewPager.disableAutoScroll()
                } else {
                    viewPager.setAutoScroll(4000)
                }
                viewPager.visibility = if (homeData?.banner?.size ?: 0 == 0) View.GONE else View.VISIBLE
                viewPager.adapter = object : ViewPagerAdapter<Banner>(homeData?.banner) {
                    override fun getView(container: ViewGroup, position: Int, item: Banner): View {
                        val view = LayoutInflater.from(container.context).inflate(R.layout.item_banner_item, container, false)
                        val sdv = view.findViewById<SimpleDraweeView>(R.id.sdv_img)
                        val tv = view.findViewById<TextView>(R.id.tv_title)
                        sdv.setImageURI(item.image)
                        tv.text = item.title
                        view.setOnClickListener { _ ->
                            startActivity(Intents.videoDetail(context!!, item.link, FetchManager.currentVideoSourceId()
                                    ?: ""))
                        }
                        return view
                    }
                }
                adapters.add(bannerAdapter)
                homeData?.categories?.forEachIndexed { index, homeCat ->
                    val binding = DataBindingUtil.inflate<ItemVideoListTitleBinding>(context!!.inflater, R.layout.item_video_list_title, dataBinding.recyclerView, false)
                    adapters.add(TitleAdapter(binding, homeCat, context!!.dp2px(12f)))

                    val videoAdapter = VideoAdapter(this@HomeFragment, homeCat.items)
                    adapters.add(videoAdapter)
                }
                adapter.setAdapters(adapters)
            }
        })
    }
}

class TitleAdapter(binding: ItemVideoListTitleBinding, val homeCat: HomeCat, marginTop: Int)
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

class VideoAdapter(val fragment: HomeFragment, private val items: List<VideoItem>?) : DelegateAdapter.Adapter<androidx.recyclerview.widget.RecyclerView.ViewHolder>() {

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
