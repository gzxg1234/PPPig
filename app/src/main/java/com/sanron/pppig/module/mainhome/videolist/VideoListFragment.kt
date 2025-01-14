package com.sanron.pppig.module.mainhome.videolist

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.sanron.pppig.R
import com.sanron.pppig.app.Intents
import com.sanron.pppig.base.LazyFragment
import com.sanron.pppig.binding.bindPageLoader
import com.sanron.pppig.data.FetchManager
import com.sanron.pppig.databinding.FragmentVideoListBinding
import com.sanron.pppig.module.mainhome.IMainChildFragment
import com.sanron.pppig.util.*

/**
 *Author:sanron
 *Time:2019/4/16
 *Description:
 */
class VideoListFragment : LazyFragment<FragmentVideoListBinding, VideoListVM>(), IMainChildFragment {

    companion object {
        const val SHOW_FILTER_DURATION = 200L

        fun new(type: Int): VideoListFragment {
            return VideoListFragment().apply {
                val args = Bundle(1)
                args.putInt("type", type)
                arguments = args
            }
        }
    }

    private lateinit var adapter: VideoAdapter

    private var bgAnim: ObjectAnimator? = null


    override fun initData() {
        buildFilter()
        refreshData()
    }

    override fun getLayout() = R.layout.fragment_video_list

    override fun createViewModel(): VideoListVM? {
        return ViewModelProviders.of(this).get(VideoListVM::class.java)
    }

    override fun onReselect() {
        dataBinding.recyclerView.smoothScrollToPosition(0)
        viewModel.pageLoader.refresh()
    }

    private fun refreshData(reset: Boolean = false) {
        if (reset) {
            viewModel.pageLoader.reset()
        }
        viewModel.pageLoader.refresh()
    }

    @SuppressLint("SetTextI18n")
    override fun initView() {
        dataBinding.apply {
            model = viewModel
            recyclerView.pauseFrescoOnScroll()
            recyclerView.layoutManager = androidx.recyclerview.widget.GridLayoutManager(context, 3)
            recyclerView.gap(context!!.dp2px(8f), context!!.dp2px(8f))

            adapter = VideoAdapter(context!!, this@VideoListFragment, viewModel.pageLoader.listData.value)
            adapter.setOnItemClickListener { adapter1, view, position ->
                startActivity(Intents.videoDetail(context!!, adapter.getItem(position)?.link, FetchManager.currentVideoSourceId()
                        ?: ""))
            }
            adapter.lifecycleOwner = this@VideoListFragment
            adapter.bindToRecyclerView(recyclerView)
            refreshLayout.handleScrollHorizontalConflict = true
        }
        viewModel.apply {
            type = arguments?.getInt("type") ?: 0
            toggleFilterCmd.observe(this@VideoListFragment, Observer {
                it?.let {
                    setFilterWindowVisible(it)
                }
            })
            dataBinding.refreshLayout.bindPageLoader(this@VideoListFragment, pageLoader)
            dataBinding.loadLayout.bindPageLoader(this@VideoListFragment, pageLoader)
            dataBinding.recyclerView.bindPageLoader(this@VideoListFragment, pageLoader)
        }
    }

    override fun onInActive() {
        super.onInActive()
        setFilterWindowVisible(false)
    }

    private fun setFilterWindowVisible(show: Boolean) {
        dataBinding.apply {
            llTags.clearAnimation()
            bgAnim?.end()
            val transAnim: Animation?
            if (show) {
                AnimUtil.rotateCenter(ibArrow, 0f, 180f, SHOW_FILTER_DURATION, this@VideoListFragment)
                bgAnim = ObjectAnimator.ofInt(flBg, "backgroundColor", 0x00000000, 0x40000000)
                transAnim = TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
                        Animation.RELATIVE_TO_SELF, -1f, Animation.RELATIVE_TO_SELF, 0f)
                flBg.visibility = View.VISIBLE
            } else {
                AnimUtil.rotateCenter(ibArrow, 180f, 0f, SHOW_FILTER_DURATION, this@VideoListFragment)
                bgAnim = ObjectAnimator.ofInt(flBg, "backgroundColor", 0x30000000, 0x00000000)
                bgAnim!!.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator?) {
                        super.onAnimationEnd(animation)
                        flBg.visibility = View.GONE
                    }
                })
                transAnim = TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
                        Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, -1f)
            }
            bgAnim!!.duration = SHOW_FILTER_DURATION
            bgAnim!!.setEvaluator(ArgbEvaluator())
            bgAnim!!.start()
            transAnim.duration = SHOW_FILTER_DURATION
            llTags.startAnimation(transAnim)
        }
    }


    fun buildFilter() {
        val filter = viewModel.getFilter()
        for ((filterName, items) in filter) {
            val filterView = context!!.inflater.inflate(R.layout.video_filter_group, dataBinding.llTags, false)
            val tvFilterName = filterView.findViewById<TextView>(R.id.tv_filter_name)
            val rgItems = filterView.findViewById<RadioGroup>(R.id.rg_filter_items)
            tvFilterName.text = filterName
            items.forEachIndexed { index, filterItem ->
                val rb = context!!.inflater.inflate(R.layout.tag_button, rgItems, false) as RadioButton
                //设置不保存view状态，解决页面切换回来后checked错误，其实也可以用View,generateId解决
                rb.isSaveEnabled = false
                rb.id = index
                rb.text = filterItem.name
                rgItems.addView(rb)
            }
            val onCheckedChange = { group: RadioGroup, checkedId: Int ->
                viewModel.params[filterName] = items[checkedId]
                val texts = mutableListOf<String>()
                for ((n, v) in viewModel.params) {
                    texts.add(n + ":" + v.name)
                }
                dataBinding.tvFilterDesc.text = TextUtils.join(" | ", texts.toTypedArray())
                refreshData(true)
            }
            rgItems.check(0)
            onCheckedChange(rgItems, 0)
            rgItems.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener(onCheckedChange))
            dataBinding.llTags.addView(filterView)
        }
    }
}

