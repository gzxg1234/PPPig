package com.sanron.pppig.module.play

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.viewpager.widget.ViewPager
import com.chad.library.adapter.base.BaseViewHolder
import com.sanron.datafetch_interface.video.bean.PlayLine
import com.sanron.lib.StatusBarHelper
import com.sanron.pppig.R
import com.sanron.pppig.base.BaseActivity
import com.sanron.pppig.base.CBaseAdapter
import com.sanron.pppig.common.LoadingView
import com.sanron.pppig.databinding.ActivityPlayerBinding
import com.sanron.pppig.util.dp2px
import com.sanron.pppig.util.gap
import com.sanron.pppig.widget.ViewPagerAdapter
import com.shuyu.gsyvideoplayer.GSYVideoBaseManager
import com.shuyu.gsyvideoplayer.GSYVideoManager
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack
import com.shuyu.gsyvideoplayer.model.VideoOptionModel
import com.shuyu.gsyvideoplayer.utils.OrientationUtils
import tv.danmaku.ijk.media.player.IjkMediaPlayer


/**
 * Author:sanron
 * Time:2019/5/8
 * Description:
 */
class PlayerAct : BaseActivity<ActivityPlayerBinding, PlayerVM>() {

    lateinit var orientationUtils: OrientationUtils

    companion object {
        const val ARG_PLAY_LINES = "play_line_list"
        const val ARG_TITLE = "title"
        const val ARG_SOURCE_POS = "page_pos"
        const val ARG_ITEM_POS = "item_pos"
        const val ARG_PLAY_TYPE = "play_type"
        const val ARG_SOURCE_ID = "source_id"
        const val TYPE_VIDEO = 0
        const val TYPE_LIVE = 1
    }

    val loadingView: LoadingView by lazy {
        LoadingView(this)
    }

    override fun createViewModel(): PlayerVM {
        return ViewModelProviders.of(this).get(PlayerVM::class.java)
    }

    override fun getLayout(): Int {
        return R.layout.activity_player
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.initIntent(intent)
        StatusBarHelper.with(this)
                .setStatusBarColor(0x60000000)
                .setLayoutBelowStatusBar(true)
                .setPaddingTop(dataBinding.playerView.getTopBar())
        dataBinding.lifecycleOwner = this
        dataBinding.model = viewModel
        initPlayer()
        setupObserver()
    }

    private fun setupObserver() {
        viewModel.videoSourceList.observe(this, Observer {
            if (!it.isNullOrEmpty()) {
                dataBinding.playerView.currentPlayer.setUp(it[0], false, viewModel.title.value)
                dataBinding.playerView.currentPlayer.startPlayLogic()
            }
        })

        viewModel.loading.observe(this, Observer {
            it?.let {
                if (it) {
                    loadingView.show()
                } else {
                    loadingView.hide()
                }
            }
        })

        viewModel.playLineList.observe(this, Observer {
            it?.let {
                dataBinding.viewPager.adapter = SourceAdapter(it)
                val titles = it.map { source ->
                    source.name
                }
                dataBinding.tabLayout.setViewPager(dataBinding.viewPager, titles.toTypedArray())
            }
        })
        viewModel.title.observe(this, Observer {
            dataBinding.playerView.titleTextView.text = it
        })

        viewModel.currentTab.observe(this, Observer {
            it?.let {
                dataBinding.tabLayout.currentTab = it
            }
        })
        dataBinding.viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                if (viewModel.currentTab.value != position) {
                    viewModel.currentTab.value = position
                }
            }
        })
        viewModel.currentItem.observe(this, Observer {
            dataBinding.playerView.currentPlayer.release()
            dataBinding.playerView.currentPlayer.onVideoReset()
        })
        viewModel.currentItemPos.observe(this, Observer {
            (dataBinding.viewPager.adapter as SourceAdapter).setSelectPos(
                    viewModel.currentPlayTab.value ?: -1,
                    it ?: -1)
        })
        viewModel.onItemsReverse.observe(this, Observer {
            it?.let { pos ->
                (dataBinding.viewPager.adapter as SourceAdapter).notifySourceChange(pos)
            }
        })
    }

    private fun initPlayer() {
        orientationUtils = OrientationUtils(this, dataBinding.playerView)
        orientationUtils.isEnable = false
        dataBinding.playerView.apply {
            titleTextView.text = title
            (gsyVideoManager as? GSYVideoBaseManager)?.let {
                it.optionModelList = mutableListOf()
                it.optionModelList.add(VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "dns_cache_clear", 1))
                it.optionModelList.add(VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "reconnect", 1))
                if (viewModel.playType == PlayerAct.TYPE_LIVE) {
                    it.optionModelList.add(VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_CODEC, "skip_loop_filter", 48))
                    it.optionModelList.add(VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "probesize", 10240))
                    it.optionModelList.add(VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "flush_packets", 1))
                    it.optionModelList.add(VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "packet-buffering", 0))
                    it.optionModelList.add(VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "framedrop", 1))
                } else {
                    //解决m3u8文件拖动问题 比如:一个3个多少小时的音频文件，开始播放几秒中，然后拖动到2小时左右的时间，要loading 10分钟
                    it.optionModelList.add(VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "fflags", "fastseek"))
                }
            }
            setIsTouchWiget(true)
            isRotateViewAuto = false
            isLockLand = false
            isAutoFullWithSize = true
            isShowFullAnimation = false
            isNeedLockFull = true
            setVideoAllCallBack(object : GSYSampleCallBack() {
                override fun onPrepared(url: String?, vararg objects: Any) {
                    super.onPrepared(url, *objects)
                    //开始播放了才能旋转和全屏
                    orientationUtils.isEnable = true
                }

                override fun onAutoComplete(url: String?, vararg objects: Any?) {
                    super.onAutoComplete(url, *objects)
                    viewModel.onPlayComplete()
                }

                override fun onQuitFullscreen(url: String?, vararg objects: Any) {
                    super.onQuitFullscreen(url, *objects)
                    orientationUtils.backToProtVideo()
                }

            })
            setLockClickListener { _, lock -> orientationUtils.isEnable = !lock }
            fullscreenButton.setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View) {
                    //直接横屏
                    orientationUtils.resolveByClick()

                    //第一个true是否需要隐藏actionbar，第二个true是否需要隐藏statusbar
                    val player = dataBinding.playerView.startWindowFullscreen(this@PlayerAct, true, true)
                    (player as PigPlayer).setPlayerViewModel(this@PlayerAct, viewModel)
                }
            })
            backButton.setOnClickListener {
                onBackPressed()
            }
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        dataBinding.playerView.onConfigurationChanged(this, newConfig, orientationUtils,
                true, true)
    }

    override fun onBackPressed() {
        orientationUtils.backToProtVideo()
        if (GSYVideoManager.backFromWindowFull(this)) {
            return
        }
        super.onBackPressed()
    }


    override fun onDestroy() {
        dataBinding.playerView.release()
        orientationUtils.releaseListener()
        super.onDestroy()
    }

    override fun initData() {
        super.initData()
        viewModel.startPlayCurrent()
    }

    override fun onResume() {
        super.onResume()
        dataBinding.playerView.onVideoResume()
    }

    override fun onPause() {
        dataBinding.playerView.onVideoPause()
        super.onPause()
    }

    inner class SourceAdapter(val data: List<PlayLine>?) : ViewPagerAdapter<PlayLine>(data) {

        private val pageViewList = SparseArray<androidx.recyclerview.widget.RecyclerView>()
        private var pagePos = -1
        private var itemPos = -1

        fun notifySourceChange(pos: Int) {
            (pageViewList[pos]?.adapter as? ItemAdapter)?.notifyDataSetChanged()
        }

        fun setSelectPos(page: Int, pos: Int) {
            for (i in 0 until pageViewList.size()) {
                val key = pageViewList.keyAt(i)
                val recyclerView = pageViewList.valueAt(i)
                if (key == page) {
                    (recyclerView.adapter as ItemAdapter).selectedPos = pos
                } else {
                    (recyclerView.adapter as ItemAdapter).selectedPos = -1
                }
            }
            this.pagePos = page
            this.itemPos = pos
        }

        override fun getView(container: ViewGroup, position: Int, item: PlayLine): View {
            val context = container.context
            val recyclerView = androidx.recyclerview.widget.RecyclerView(context)
            recyclerView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            val dp16 = context.dp2px(16f)
            recyclerView.layoutManager = GridLayoutManager(context, 4)
            recyclerView.setPadding(dp16, dp16, dp16, dp16)
            recyclerView.clipChildren = false
            recyclerView.clipToPadding = false
            recyclerView.gap(dp16, dp16)
            val adapter = ItemAdapter(context)
            adapter.setNewData(item.items)
            adapter.bindToRecyclerView(recyclerView)
            adapter.setOnItemClickListener { _, _, position2 ->
                data?.get(position)?.items?.get(position2)?.let {
                    viewModel.changePlayItem(position, position2)
                }
            }
            if (position == pagePos) {
                (recyclerView.adapter as ItemAdapter).selectedPos = itemPos
            } else {
                (recyclerView.adapter as ItemAdapter).selectedPos = -1
            }
            pageViewList.put(position, recyclerView)
            return recyclerView
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            super.destroyItem(container, position, `object`)
            pageViewList.remove(position)
        }
    }

    class ItemAdapter(context: Context) : CBaseAdapter<PlayLine.Item, BaseViewHolder>(context, R.layout.item_play_item) {

        var selectedPos = -1
            set(value) {
                field = value
                notifyDataSetChanged()
            }

        override fun convert(helper: BaseViewHolder?, item: PlayLine.Item?) {
            val text = helper!!.itemView as TextView
            text.text = item?.name
            text.isSelected = helper.adapterPosition == selectedPos
        }
    }
}
