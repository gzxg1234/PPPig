package com.sanron.pppig.module.micaitu.play

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.chad.library.adapter.base.BaseViewHolder
import com.sanron.lib.StatusBarHelper
import com.sanron.pppig.R
import com.sanron.pppig.base.BaseActivity
import com.sanron.pppig.base.CBaseAdapter
import com.sanron.pppig.common.LoadingView
import com.sanron.pppig.data.bean.micaitu.PlaySource
import com.sanron.pppig.databinding.ActivityPlayerBinding
import com.sanron.pppig.util.showToast
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
        const val ARG_URL = "url"
        const val ARG_SOURCE_ITEMS = "items"
        const val ARG_TITLE = "title"
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
        val url = intent?.getStringExtra(ARG_URL)
        val title = intent?.getStringExtra(ARG_TITLE)
        val itmes = intent?.getStringExtra(ARG_SOURCE_ITEMS)
        StatusBarHelper.with(this)
                .setStatusBarColor(0)
                .setLayoutBelowStatusBar(true)
                .setPaddingTop(dataBinding.topBar)
        if (url.isNullOrEmpty()) {
            finish()
            showToast("无效url")
            return
        }
        viewModel.url = url
        dataBinding.lifecycleOwner = this
        dataBinding.model = viewModel
        dataBinding.ivBack.setOnClickListener {
            finish()
        }

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
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        dataBinding.playerView.onConfigurationChanged(this, newConfig, orientationUtils,
                true, true)
    }

    override fun onBackPressed() {
        if (orientationUtils != null) {
            orientationUtils.backToProtVideo();
        }
        if (GSYVideoManager.backFromWindowFull(this)) {
            return;
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


    class ItemAdapter() : CBaseAdapter<PlaySource.Item, BaseViewHolder>(R.layout.item_play_item) {

        override fun convert(helper: BaseViewHolder?, item: PlaySource.Item?) {
            val text = helper!!.itemView as TextView
            text.text = item?.name
        }
    }
}
