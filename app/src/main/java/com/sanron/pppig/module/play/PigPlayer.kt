package com.sanron.pppig.module.play

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.text.format.Formatter
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sanron.pppig.R
import com.sanron.pppig.util.dp2px
import com.sanron.pppig.util.gap
import com.sanron.pppig.util.limit
import com.shuyu.gsyvideoplayer.utils.CommonUtil
import com.shuyu.gsyvideoplayer.utils.CommonUtil.hideNavKey
import com.shuyu.gsyvideoplayer.video.base.GSYVideoView

/**
 * Author:sanron
 * Time:2019/5/10
 * Description:
 */
class PigPlayer : FixPlayer {

    private val timeTextView: TextView by lazy {
        findViewById<TextView>(R.id.tv_time)
    }

    private val volumeLayout: View by lazy {
        findViewById<View>(R.id.ll_volume_adjust)
    }
    private val pbVolume: ProgressBar by lazy {
        findViewById<ProgressBar>(R.id.pb_volume)
    }
    private val brightnessLayout: View by lazy {
        findViewById<View>(R.id.ll_bright_adjust)
    }
    private val pbBrightness: ProgressBar by lazy {
        findViewById<ProgressBar>(R.id.pb_brightness)
    }

    private val seekPosLayout: View by lazy {
        findViewById<View>(R.id.ll_seek_position)
    }
    private val tvCurPos: TextView by lazy {
        findViewById<TextView>(R.id.tv_current)
    }
    private val tvDuration: TextView by lazy {
        findViewById<TextView>(R.id.tv_duration)
    }
    private val pbSeekPos: ProgressBar by lazy {
        findViewById<ProgressBar>(R.id.pb_seek_position)
    }
    private val loadingView: View by lazy {
        findViewById<View>(R.id.loading)
    }
    private val tvSpeed: TextView by lazy {
        findViewById<TextView>(R.id.tv_speed)
    }
    private val tvSelectEpisode: TextView by lazy {
        findViewById<TextView>(R.id.tv_select_episode).apply {
            setOnClickListener(this@PigPlayer)
        }
    }
    private val llSelectItemContent: View by lazy {
        findViewById<View>(R.id.ll_select_items_content)
    }
    private val switchAutoNext: SwitchCompat by lazy {
        findViewById<SwitchCompat>(R.id.switch_auto_next)
    }
    private val rvEpisodeItems: RecyclerView by lazy {
        findViewById<RecyclerView>(R.id.rv_items)
    }
    private val selectEpisodeView: View by lazy {
        findViewById<View>(R.id.ll_select_items).apply {
            setOnClickListener(this@PigPlayer)
        }
    }

    private val activity = context as Activity

    fun getTopBar(): ViewGroup = mTopContainer

    private val taskHandler = Handler()

    var itemAdapter = PlayerAct.ItemAdapter(context)

    private val syncSpeed = object : Runnable {
        override fun run() {
            tvSpeed.text = context.getString(R.string.player_loading_text, Formatter.formatFileSize(context, netSpeed))
            taskHandler.postDelayed(this, 500)
        }
    }

    constructor(context: Context?, fullFlag: Boolean?) : super(context, fullFlag) {
        if (fullFlag == true) {
            tvSelectEpisode.visibility = View.VISIBLE
        } else {
            tvSelectEpisode.visibility = View.GONE
        }
    }

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    override fun init(context: Context?) {
        super.init(context)
        enlargeImageRes = R.drawable.ic_fullscreen_white_24dp
        shrinkImageRes = R.drawable.ic_fullscreen_exit_white_24dp
        post {
            //init执行在基类构造方法里，此时dismissControlTime尚未初始化，需延迟设置
            dismissControlTime = 4000
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.pig_player_layout
    }

    fun setPlayerViewModel(act: PlayerAct, vm: PlayerVM) {
        rvEpisodeItems.layoutManager = GridLayoutManager(context, 3)
        rvEpisodeItems.gap(context.dp2px(8f), context.dp2px(8f))
        itemAdapter.setNewData(vm.playSourceList.value!![vm.currentSourcePos.value!!].items)
        itemAdapter.setOnItemClickListener { adapter, view, position ->
            vm.changePlayItem(position)
        }
        itemAdapter.bindToRecyclerView(rvEpisodeItems)
        vm.currentItemPos.observe(act, Observer {
            it?.let {
                itemAdapter.selectedPos = it
            }
        })
        vm.title.observe(act, Observer {
            titleTextView.text = it
        })
        vm.autoNext.observe(act, Observer {
            it?.let {
                switchAutoNext.isChecked = it
            }
        })
        switchAutoNext.setOnCheckedChangeListener { buttonView, isChecked ->
            vm.setAutoNext(isChecked)
        }
    }

    override fun seekTo(position: Long) {
        super.seekTo(position.limit(0L, duration.toLong() - 1))
    }

    fun hideAllControl() {
        if ((mCurrentState != GSYVideoView.CURRENT_STATE_NORMAL
                        && mCurrentState != GSYVideoView.CURRENT_STATE_ERROR
                        && mCurrentState != GSYVideoView.CURRENT_STATE_AUTO_COMPLETE)) {
            if (activityContext != null) {
                Handler(Looper.getMainLooper()).post {
                    hideAllWidget()
                    setViewShowState(mLockScreen, View.GONE)
                    if (mHideKey && mIfCurrentIsFullscreen && mShowVKey) {
                        hideNavKey(mContext)
                    }
                }
            }
        }
    }

    override fun showVolumeDialog(deltaY: Float, volumePercent: Int) {
        volumeLayout.visibility = View.VISIBLE
        pbVolume.progress = volumePercent
    }

    override fun dismissVolumeDialog() {
        volumeLayout.visibility = View.GONE
    }

    override fun showBrightnessDialog(percent: Float) {
        brightnessLayout.visibility = View.VISIBLE
        pbBrightness.progress = (percent * 100).toInt()
    }

    override fun dismissBrightnessDialog() {
        brightnessLayout.visibility = View.GONE
    }

    override fun showProgressDialog(deltaX: Float, seekTime: String?, seekTimePosition: Int, totalTime: String?, totalTimeDuration: Int) {
        seekPosLayout.visibility = View.VISIBLE
        if (totalTimeDuration > 0) {
            pbSeekPos.progress = seekTimePosition * 100 / totalTimeDuration
        } else {
            pbSeekPos.progress = 0
        }
        tvCurPos.text = seekTime
        tvDuration.text = "/" + totalTime
    }

    override fun dismissProgressDialog() {
        seekPosLayout.visibility = View.GONE
    }

    fun setSelectEpisodeVisible(v: Boolean) {
        if (v) {
            selectEpisodeView.visibility = View.VISIBLE
            val slideIn = TranslateAnimation(Animation.RELATIVE_TO_SELF, 1f, Animation.RELATIVE_TO_SELF, 0f,
                    Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f)
            slideIn.duration = 200
            slideIn.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationRepeat(animation: Animation?) {
                }

                override fun onAnimationEnd(animation: Animation?) {
                }

                override fun onAnimationStart(animation: Animation?) {
                }
            })
            slideIn.fillAfter = true
            llSelectItemContent.startAnimation(slideIn)
        } else {
            val slideOut = TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 1f,
                    Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f)
            slideOut.duration = 200
            slideOut.fillAfter = true
            slideOut.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationRepeat(animation: Animation?) {
                }

                override fun onAnimationEnd(animation: Animation?) {
                    selectEpisodeView.visibility = View.GONE
                }

                override fun onAnimationStart(animation: Animation?) {
                }
            })
            llSelectItemContent.startAnimation(slideOut)
        }
    }

    override fun onClick(v: View?) {
        super.onClick(v)
        when (v!!.id) {
            R.id.tv_select_episode -> {
                if (mIfCurrentIsFullscreen) {
                    hideAllControl()
                    setSelectEpisodeVisible(true)
                }
            }
            R.id.ll_select_items -> {
                setSelectEpisodeVisible(false)
            }
        }
    }

    override fun setViewShowState(view: View?, visibility: Int) {
        super.setViewShowState(view, visibility)
        if (view == loadingView) {
            if (visibility == View.VISIBLE) {
                taskHandler.post(syncSpeed)
            } else {
                taskHandler.removeCallbacks(syncSpeed)
            }
        } else if (view == mTopContainer) {
            if (!mIfCurrentIsFullscreen) {
                if (visibility == View.VISIBLE) {
                    activity.window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
                } else {
                    activity.window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
                }
            }
        }
    }

    override fun setProgressAndTime(progress: Int, secProgress1: Int, currentTime: Int, totalTime: Int) {
        super.setProgressAndTime(progress, secProgress1, currentTime, totalTime)
        var secProgress = secProgress1

        if (mGSYVideoProgressListener != null && mCurrentState == GSYVideoView.CURRENT_STATE_PLAYING) {
            mGSYVideoProgressListener.onProgress(progress, secProgress, currentTime, totalTime)
        }

        if (mProgressBar == null) {
            return
        }

        if (!mTouchingProgressBar) {
            if (progress >= 0) mProgressBar.progress = progress
        }
        if (gsyVideoManager.bufferedPercentage > 0) {
            secProgress = gsyVideoManager.bufferedPercentage
        }
        if (secProgress > 94) secProgress = 100
        super.setSecondaryProgress(secProgress)

        timeTextView.text = CommonUtil.stringForTime(currentTime) + "/" + CommonUtil.stringForTime(totalTime)
        if (mBottomProgressBar != null) {
            if (progress >= 0) mBottomProgressBar.progress = progress
            super.setSecondaryProgress(secProgress)
        }
    }

    /**
     * 定义开始按键显示
     */
    override fun updateStartImage() {
        if (mStartButton is ImageView) {
            if (mCurrentState == GSYVideoView.CURRENT_STATE_PLAYING) {
                (mStartButton as ImageView).setImageResource(R.drawable.ic_pause_circle_outline_white_36dp)
            } else if (mCurrentState == GSYVideoView.CURRENT_STATE_ERROR) {
                (mStartButton as ImageView).setImageResource(R.drawable.ic_error_outline_white_36dp)
            } else {
                (mStartButton as ImageView).setImageResource(R.drawable.ic_play_circle_outline_white_36dp)
            }
        }
    }
}
