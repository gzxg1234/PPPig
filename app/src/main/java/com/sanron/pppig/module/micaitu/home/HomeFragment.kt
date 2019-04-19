package com.sanron.pppig.module.micaitu.home

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.alibaba.android.vlayout.DelegateAdapter
import com.alibaba.android.vlayout.LayoutHelper
import com.alibaba.android.vlayout.VirtualLayoutManager
import com.alibaba.android.vlayout.layout.GridLayoutHelper
import com.alibaba.android.vlayout.layout.SingleLayoutHelper
import com.facebook.drawee.view.SimpleDraweeView
import com.sanron.pppig.R
import com.sanron.pppig.app.PiApp
import com.sanron.pppig.base.LazyFragment
import com.sanron.pppig.data.bean.micaitu.Banner
import com.sanron.pppig.data.bean.micaitu.Home
import com.sanron.pppig.data.bean.micaitu.HomeCat
import com.sanron.pppig.data.bean.micaitu.VideoItem
import com.sanron.pppig.databinding.FragmentHomeBinding
import com.sanron.pppig.databinding.ItemHomeVideoBinding
import com.sanron.pppig.databinding.ItemVideoListTitleBinding
import com.sanron.pppig.module.home.IMainChildFragment
import com.sanron.pppig.module.micaitu.home.HomeFragment.Companion.TYPE_TITLE
import com.sanron.pppig.module.micaitu.home.HomeFragment.Companion.TYPE_VIDEO
import com.sanron.pppig.util.*
import com.tmall.ultraviewpager.UltraViewPager

/**
 * Author:sanron
 * Time:2019/2/21
 * Description:
 */
class HomeFragment : LazyFragment<FragmentHomeBinding, HomeViewModel>(), IMainChildFragment {

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
        viewModel!!.refresh.value = true
        viewModel!!.loadData()
    }

    override fun createViewModel(): HomeViewModel? {
        return ViewModelProviders.of(this).get(HomeViewModel::class.java)
    }

    override fun getLayout(): Int {
        return R.layout.fragment_home
    }

    override fun onReselect() {
        viewModel!!.refresh.value = true
        viewModel!!.loadData()
        binding!!.recyclerView.smoothScrollToPosition(0)
    }

    fun initBannerAdapter(): SimpleViewAdapter {
        val banner = context!!.inflater.inflate(R.layout.item_banner, binding!!.recyclerView, false) as UltraViewPager
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
                .setFocusColor(resources.getColor(R.color.colorPrimary))
                .setNormalColor(resources.getColor(R.color.white))
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
        binding!!.apply {
            lifecycleOwner = this@HomeFragment
            model = viewModel
            recyclerView.pauseFrescoOnScroll()
            layoutManager = VirtualLayoutManager(context!!)
            recyclerView.layoutManager = layoutManager
            layoutManager.setNestedScrolling(false)
            recyclerView.setRecycledViewPool(RecyclerView.RecycledViewPool().apply {
                setMaxRecycledViews(TYPE_VIDEO, 9)
                setMaxRecycledViews(TYPE_BANNER, 1)
                setMaxRecycledViews(TYPE_TITLE, 5)
            })
            adapter = DelegateAdapter(layoutManager, true)
            recyclerView.adapter = adapter
        }
        viewModel!!.homeData.observe(this@HomeFragment, Observer<Home> { homeData ->
            binding?.apply {
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
                        return view
                    }
                }
                adapters.add(bannerAdapter)
                homeData?.categories?.forEachIndexed { index, homeCat ->
                    val binding = DataBindingUtil.inflate<ItemVideoListTitleBinding>(context!!.inflater, R.layout.item_video_list_title, binding!!.recyclerView, false)
                    binding.model = CatTitleModel(PiApp.sInstance)
                    binding.lifecycleOwner = this@HomeFragment
                    adapters.add(TitleAdapter(binding, homeCat, context!!.dp2px(12f)))
                    adapters.add(VideoAdapter(context!!, homeCat.items))
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

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding = DataBindingUtil.getBinding<ItemVideoListTitleBinding>(holder.itemView)
        val icon = when (homeCat.type) {
            HomeCat.MOVIE -> R.drawable.ic_title_movie
            HomeCat.TV -> R.drawable.ic_title_tv
            HomeCat.ANIM -> R.drawable.ic_title_anim
            else -> R.drawable.ic_title_verity
        }
        binding?.model?.setVariable(homeCat.name, icon)
        binding?.executePendingBindings()
    }
}

class VideoAdapter(val context: Context, val items: List<VideoItem>?) : DelegateAdapter.Adapter<RecyclerView.ViewHolder>() {

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

    override fun onBindViewHolder(p0: RecyclerView.ViewHolder, p1: Int) {
        val binding = DataBindingUtil.getBinding<ItemHomeVideoBinding>(p0.itemView)
        binding?.model?.apply {
            img.value = items!![p1].img
            name.value = items[p1].name
            score.value = items[p1].score
            label.value = items[p1].label
        }
        binding?.executePendingBindings()
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder {
        val binding = DataBindingUtil.inflate<ItemHomeVideoBinding>(p0.context.inflater, R.layout.item_home_video, p0, false)
        binding.lifecycleOwner = p0.context as LifecycleOwner
        binding.model = ItemVideoViewModel(PiApp.sInstance)
        return SimpleViewHolder(binding.root)
    }


    override fun getItemViewType(position: Int): Int {
        return TYPE_VIDEO
    }
}
