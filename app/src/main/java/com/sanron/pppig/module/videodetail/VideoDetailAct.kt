package com.sanron.pppig.module.videodetail

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.v4.app.ActivityCompat
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.TranslateAnimation
import android.widget.TextView
import com.chad.library.adapter.base.BaseViewHolder
import com.sanron.datafetch_interface.bean.PlaySource
import com.sanron.lib.StatusBarHelper
import com.sanron.pppig.R
import com.sanron.pppig.app.Intents
import com.sanron.pppig.base.BaseActivity
import com.sanron.pppig.base.CBaseAdapter
import com.sanron.pppig.common.LoadingView
import com.sanron.pppig.databinding.ActivityVideoDetailBinding
import com.sanron.pppig.module.play.PlayerAct
import com.sanron.pppig.util.*
import com.sanron.pppig.widget.ViewPagerAdapter


/**
 * Author:sanron
 * Time:2019/4/24
 * Description:
 */
class VideoDetailAct : BaseActivity<ActivityVideoDetailBinding, VideoDetailVM>() {

    companion object {
        const val ARG_URL = "url"
    }

    //标题是电影名称
    private var isVideoNameTitle = false

    val loadingView by lazy {
        LoadingView(this)
    }

    override fun getLayout(): Int {
        return R.layout.activity_video_detail
    }

    override fun createViewModel(): VideoDetailVM {
        return ViewModelProviders.of(this).get(VideoDetailVM::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatusBarHelper.with(this@VideoDetailAct)
                .setStatusBarColor(0)
                .setDarkIcon(0.5f)
                .setLayoutBelowStatusBar(true)
                .setPaddingTop(dataBinding.flTopWrap)
                .setPaddingTop(dataBinding.coordinator)
        dataBinding.lifecycleOwner = this@VideoDetailAct
        dataBinding.model = viewModel
        dataBinding.ivBack.setOnClickListener {
            ActivityCompat.finishAfterTransition(this@VideoDetailAct)
        }
        dataBinding.appbarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { p0, p1 ->
            onAppBarScroll(p1)
        })
        viewModel.url = intent?.getStringExtra(ARG_URL)
        viewModel.loading.observe(this@VideoDetailAct, Observer {
            it?.let { b ->
                if (b) {
                    loadingView.show()
                } else {
                    loadingView.hide()
                }
            }
        })
        viewModel.infoList.observe(this@VideoDetailAct, Observer {
            dataBinding.llInfos.childForEach { _, v ->
                if (v != dataBinding.tvVideoName) {
                    dataBinding.llInfos.removeView(v)
                }
            }
            it?.forEach { text ->
                val tv = TextView(activity)
                tv.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT)
                tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, activity.dp2px(14f).toFloat())
                tv.maxLines = 2
                tv.text = text
                activity.getColorCompat(R.color.textColor1).let { it1 -> tv.setTextColor(it1) }

                dataBinding.llInfos.addView(tv)
            }
        })
        viewModel.playSourceList.observe(this@VideoDetailAct, Observer {
            it?.let { sourceList ->
                if (sourceList.isEmpty()) {
                    showToast("暂无可播放资源")
                    return@let
                }
                val titles = sourceList.map { source ->
                    source.name
                }
                dataBinding.viewPager.adapter = SourceAdapter(viewModel.title.value, sourceList)
                dataBinding.tabLayout.setViewPager(dataBinding.viewPager, titles.toTypedArray())
            }
        })

        //初始化顶栏电影名称TextView状态
        initTitle2State()
    }

    private fun initTitle2State() {
        val alphaHideAnim = AlphaAnimation(0f, 0f)
        val transAnim = TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 1f, Animation.RELATIVE_TO_SELF, 1f)
        val anim = AnimationSet(true).apply {
            addAnimation(alphaHideAnim)
            addAnimation(transAnim)
            fillAfter = true
            this.duration = 1
        }
        dataBinding.tvTitleVideoName.animation = anim
        anim.startNow()
    }

    private fun onAppBarScroll(scrollY: Int) {
        val max = dataBinding.topBar.height
        val fraction = (Math.abs(scrollY.toFloat()) / max).limit(0f, 1.0f)
        if (max == 0 || dataBinding.topBarDivider.alpha == fraction) {
            return
        }
        val alpha = (fraction * 255).toInt()
        val color = (alpha and 0xFF shl 24) or (0xf5f5f5 and 0xffffff)
        dataBinding.flTopWrap.setBackgroundColor(color)
        dataBinding.topBarDivider.alpha = fraction

        //标题动画
        val alphaShowAnim = AlphaAnimation(0f, 1f)
        val alphaHideAnim = AlphaAnimation(1f, 0f)
        val duration = 300L
        if (fraction == 1f && !isVideoNameTitle) {
            var transAnim = TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
                    Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, -1f)
            var anim = AnimationSet(true).apply {
                addAnimation(alphaHideAnim)
                addAnimation(transAnim)
                fillAfter = true
                this.duration = duration
                bindLifecycle(this@VideoDetailAct)
            }
            dataBinding.tvTitle.startAnimation(anim)
            transAnim = TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
                    Animation.RELATIVE_TO_SELF, 1f, Animation.RELATIVE_TO_SELF, 0f)
            anim = AnimationSet(true).apply {
                addAnimation(alphaShowAnim)
                addAnimation(transAnim)
                fillAfter = true
                this.duration = duration
                bindLifecycle(this@VideoDetailAct)
            }
            dataBinding.tvTitleVideoName.startAnimation(anim)
            isVideoNameTitle = true
        } else if (fraction == 0f && isVideoNameTitle) {
            var transAnim = TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
                    Animation.RELATIVE_TO_SELF, -1f, Animation.RELATIVE_TO_SELF, 0f)
            var anim = AnimationSet(true).apply {
                addAnimation(alphaShowAnim)
                addAnimation(transAnim)
                fillAfter = true
                this.duration = duration
                bindLifecycle(this@VideoDetailAct)
            }
            dataBinding.tvTitle.startAnimation(anim)
            transAnim = TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
                    Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 1f)
            anim = AnimationSet(true).apply {
                addAnimation(alphaHideAnim)
                addAnimation(transAnim)
                fillAfter = true
                this.duration = duration
                bindLifecycle(this@VideoDetailAct)
            }
            dataBinding.tvTitleVideoName.startAnimation(anim)
            isVideoNameTitle = false
        }
    }

    override fun initData() {
        super.initData()
        viewModel.loadData()
    }


    class SourceAdapter(val title: String?, val data: List<PlaySource>?) : ViewPagerAdapter<PlaySource>(data) {

        override fun getView(container: ViewGroup, position: Int, item: PlaySource): View {
            val context = container.context
            val recyclerView = RecyclerView(context)
            recyclerView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            val dp16 = context.dp2px(16f)
            recyclerView.layoutManager = GridLayoutManager(context, 4)
            recyclerView.setPadding(dp16, dp16, dp16, dp16)
            recyclerView.clipChildren = false
            recyclerView.clipToPadding = false
            recyclerView.gap(dp16, dp16)
            val adapter = PlayerAct.ItemAdapter(context)
            adapter.setNewData(item.items)
            adapter.bindToRecyclerView(recyclerView)
            adapter.setOnItemClickListener { _, _, position2 ->
                if (!data.isNullOrEmpty()) {
                    (context as Activity).startActivity(Intents.playVideo(context, title, data, position, position2))
                }
            }
            return recyclerView
        }
    }

    class ItemAdapter(context: Context) : CBaseAdapter<PlaySource.Item, BaseViewHolder>(context, R.layout.item_play_item) {

        override fun convert(helper: BaseViewHolder?, item: PlaySource.Item?) {
            val text = helper!!.itemView as TextView
            text.text = item?.name
        }
    }

}
