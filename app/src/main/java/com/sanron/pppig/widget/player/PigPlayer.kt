package com.sanron.pppig.widget.player

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.text.format.Formatter
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.SeekBar
import android.widget.TextView
import com.sanron.pppig.R
import com.shuyu.gsyvideoplayer.utils.CommonUtil
import com.shuyu.gsyvideoplayer.utils.Debuger
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer
import com.shuyu.gsyvideoplayer.video.base.GSYVideoView

/**
 * Author:sanron
 * Time:2019/5/10
 * Description:
 */
class PigPlayer : StandardGSYVideoPlayer {

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

    private val activity = context as Activity


    fun getTopBar(): ViewGroup = mTopContainer

    private val taskHandler = Handler()

    private val syncSpeed = object : Runnable {
        override fun run() {
            tvSpeed.text = context.getString(R.string.player_loading_text, Formatter.formatFileSize(context, netSpeed))
            taskHandler.postDelayed(this, 500)
        }
    }

    constructor(context: Context?, fullFlag: Boolean?) : super(context, fullFlag)
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    override fun init(context: Context?) {
        super.init(context)
        enlargeImageRes = R.drawable.ic_fullscreen_white_24dp
        shrinkImageRes = R.drawable.ic_fullscreen_exit_white_24dp
    }

    override fun getLayoutId(): Int {
        return R.layout.pig_player_layout
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

    override fun setViewShowState(view: View?, visibility: Int) {
        super.setViewShowState(view, visibility)
        if (view == loadingView) {
            if (visibility == View.VISIBLE) {
                taskHandler.post(syncSpeed)
            } else {
                taskHandler.removeCallbacks(syncSpeed)
            }
        }else if(view == mTopContainer){
            if(!mIfCurrentIsFullscreen){
                if (visibility == View.VISIBLE) {
                    activity.window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
                } else {
                    activity.window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
                }
            }
        }
    }
    /**
     * 获取当前播放进度
     */
    override fun getCurrentPositionWhenPlaying(): Int {
        var position = 0
        if (mCurrentState == CURRENT_STATE_PLAYING || mCurrentState == CURRENT_STATE_PAUSE
                || mCurrentState == GSYVideoView.CURRENT_STATE_PLAYING_BUFFERING_START) {
            try {
                position = gsyVideoManager.currentPosition.toInt()
            } catch (e: Exception) {
                e.printStackTrace()
                return position
            }
        }
        return if (position == 0 && mCurrentPosition > 0) {
            mCurrentPosition.toInt()
        } else position
    }


    override fun changeUiToNormal() {
        super.changeUiToNormal()
        setProgressAndTime(0,0,0,0)

    }

    override fun setSecondaryProgress(secProgress: Int) {
        if (mProgressBar != null) {
            if (secProgress >= 0 && !gsyVideoManager.isCacheFile) {
                mProgressBar.secondaryProgress = secProgress
            }
        }
        if (mBottomProgressBar != null) {
            if (secProgress >= 0 && !gsyVideoManager.isCacheFile) {
                mBottomProgressBar.secondaryProgress = secProgress
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
        setSecondaryProgress(secProgress)

        timeTextView.text = CommonUtil.stringForTime(currentTime) + "/" + CommonUtil.stringForTime(totalTime)
        if (mBottomProgressBar != null) {
            if (progress >=0) mBottomProgressBar.progress = progress
            setSecondaryProgress(secProgress)
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
