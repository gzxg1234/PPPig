package com.sanron.pppig.module.mainhome.home

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.app.SharedElementCallback
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.alibaba.android.vlayout.DelegateAdapter
import com.alibaba.android.vlayout.VirtualLayoutManager
import com.alibaba.android.vlayout.layout.SingleLayoutHelper
import com.facebook.drawee.view.SimpleDraweeView
import com.sanron.datafetch_interface.video.bean.Banner
import com.sanron.datafetch_interface.video.bean.Home
import com.sanron.pppig.R
import com.sanron.pppig.app.Intents
import com.sanron.pppig.base.LazyFragment
import com.sanron.pppig.binding.bindStateValue
import com.sanron.pppig.data.FetchManager
import com.sanron.pppig.databinding.FragmentHomeBinding
import com.sanron.pppig.databinding.ItemVideoListTitleBinding
import com.sanron.pppig.module.mainhome.IMainChildFragment
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
        viewModel.refresh()
    }

    override fun createViewModel(): HomeVM? {
        return ViewModelProviders.of(this).get(HomeVM::class.java)
    }

    override fun getLayout(): Int {
        return R.layout.fragment_home
    }

    override fun onReselect() {
        viewModel.refresh()
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
        setupObserver()
    }

    private fun setupObserver() {
        dataBinding.loadLayout.setOnReloadListener {
            viewModel.refresh()
        }
        dataBinding.loadLayout.bindStateValue(this@HomeFragment, viewModel.loadState)

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
