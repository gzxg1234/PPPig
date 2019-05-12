package com.sanron.pppig.module.play

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.chad.library.adapter.base.BaseViewHolder
import com.sanron.datafetch.WebHelper
import com.sanron.datafetch_interface.bean.PlaySource
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
        const val ARG_SOURCE = "source"
        const val ARG_TITLE = "title"
        const val ARG_SOURCE_POS = "page_pos"
        const val ARG_ITEM_POS = "item_pos"
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
        val title = intent?.getStringExtra(ARG_TITLE)
        StatusBarHelper.with(this)
                .setStatusBarColor(0x60000000)
                .setLayoutBelowStatusBar(true)
                .setPaddingTop(dataBinding.playerView.getTopBar())
        dataBinding.lifecycleOwner = this
        dataBinding.model = viewModel
        viewModel.videoSourceList.observe(this, Observer {
            if (!it.isNullOrEmpty()) {
                dataBinding.playerView.setUp(it[0], true, title)
                dataBinding.playerView.startPlayLogic()
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

        orientationUtils = OrientationUtils(this, dataBinding.playerView)
        orientationUtils.isEnable = false

        dataBinding.playerView.apply {
            titleTextView.text = title
            (gsyVideoManager as? GSYVideoBaseManager)?.let {
                it.optionModelList = mutableListOf()
                //SeekTo设置优化某些视频在SeekTo的时候，会跳回到拖动前的位置
                it.optionModelList.add(VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "enable-accurate-seek", 1))
                //解决m3u8文件拖动问题 比如:一个3个多少小时的音频文件，开始播放几秒中，然后拖动到2小时左右的时间，要loading 10分钟
                it.optionModelList.add(VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "fflags", "fastseek"))
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
                    dataBinding.playerView.startWindowFullscreen(this@PlayerAct, true, true)
                }
            })
            backButton.setOnClickListener {
                onBackPressed()
            }
        }

        val sourceList: List<PlaySource> = intent?.getSerializableExtra(ARG_SOURCE) as List<PlaySource>
        val pagePos = intent?.getIntExtra(ARG_SOURCE_POS, 0)!!
        val itemPos = intent?.getIntExtra(ARG_ITEM_POS, 0)!!
        dataBinding.viewPager.adapter = SourceAdapter(sourceList).apply {
            setSelectPos(pagePos, itemPos)
        }
        val titles = sourceList.map { source ->
            source.name
        }
        dataBinding.tabLayout.setViewPager(dataBinding.viewPager, titles.toTypedArray())
        dataBinding.tabLayout.currentTab = pagePos
        viewModel.playItem = sourceList[pagePos].items?.get(itemPos)

    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        dataBinding.playerView.onConfigurationChanged(this, newConfig, orientationUtils,
                true, true)
    }

    override fun onBackPressed() {
        if (orientationUtils != null) {
            orientationUtils.backToProtVideo()
        }
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
        viewModel.loadData()
    }

    override fun onResume() {
        super.onResume()
        dataBinding.playerView.onVideoResume()
    }

    override fun onPause() {
        dataBinding.playerView.onVideoPause()
        super.onPause()
    }

    private fun loadItem(item: PlaySource.Item) {
        dataBinding.playerView.release()
        viewModel.playItem = item
        viewModel.loadData()
    }

    inner class SourceAdapter(val data: List<PlaySource>?) : ViewPagerAdapter<PlaySource>(data) {

        private val pageViewList = SparseArray<RecyclerView>()
        private var pagePos = -1
        private var itemPos = -1

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
            val adapter = ItemAdapter(context)
            adapter.setNewData(item.items)
            adapter.bindToRecyclerView(recyclerView)
            adapter.setOnItemClickListener { _, _, position2 ->
                data?.get(position)?.items?.get(position2)?.let {
                    setSelectPos(position, position2)
                    loadItem(it)
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

    class ItemAdapter(context: Context) : CBaseAdapter<PlaySource.Item, BaseViewHolder>(context,R.layout.item_play_item) {

        var selectedPos = -1
            set(value) {
                val f = field
                field = value
                notifyItemChanged(f)
                notifyItemChanged(field)
            }

        override fun convert(helper: BaseViewHolder?, item: PlaySource.Item?) {
            val text = helper!!.itemView as TextView
            text.text = item?.name
            text.isSelected = helper.adapterPosition == selectedPos
        }
    }
}
