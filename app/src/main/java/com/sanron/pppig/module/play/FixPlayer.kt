package com.sanron.pppig.module.play

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.media.AudioManager
import android.os.Handler
import android.os.Message
import android.util.AttributeSet
import android.view.*
import android.widget.SeekBar
import com.sanron.pppig.module.play.fixexo.PigExoMediaPlayer
import com.sanron.pppig.module.play.fixexo.PigExoPlayerManager
import com.sanron.pppig.util.CLog
import com.shuyu.gsyvideoplayer.R
import com.shuyu.gsyvideoplayer.utils.CommonUtil.hideNavKey
import com.shuyu.gsyvideoplayer.utils.Debuger
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer
import com.shuyu.gsyvideoplayer.video.base.GSYVideoView

/**
 *Author:sanron
 *Time:2019/5/15
 *Description:
 * 修复一些bug
 */
open class FixPlayer : StandardGSYVideoPlayer {
    companion object {
        val TAG: String = FixPlayer::class.java.simpleName
    }

    private var startHideControlAfterStopTracking = false
    private var trackingProgress = false

    private val hideControl = 0
    private val syncProgress = 1

    private val taskHandler: Handler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                hideControl -> {
                    if ((mCurrentState != GSYVideoView.CURRENT_STATE_NORMAL
                                    && mCurrentState != GSYVideoView.CURRENT_STATE_ERROR
                                    && mCurrentState != GSYVideoView.CURRENT_STATE_AUTO_COMPLETE)) {
                        if (activityContext != null) {
                            hideAllWidget()
                            setViewShowState(mLockScreen, View.GONE)
                            if (mHideKey && mIfCurrentIsFullscreen && mShowVKey) {
                                hideNavKey(mContext)
                            }
                        }
                    }
                }
                syncProgress -> {
                    if (mCurrentState == GSYVideoView.CURRENT_STATE_PLAYING || mCurrentState == GSYVideoView.CURRENT_STATE_PAUSE) {
                        setTextAndProgress(-1)
                    }
                    sendEmptyMessageDelayed(syncProgress, 300)
                }
            }
        }
    }

    constructor(context: Context?, fullFlag: Boolean?) : super(context, fullFlag)
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    @SuppressLint("ClickableViewAccessibility")
    override fun init(context: Context?) {
        super.init(context)
        post {
            //init执行在基类构造方法里，此时dismissControlTime尚未初始化，需延迟设置
            mProgressBar.setOnTouchListener { v, event ->
                return@setOnTouchListener isPlayingLive()
            }
            dismissControlTime = 4000
        }
    }

    override fun onBrightnessSlide(percent: Float) {
        mBrightnessData = (mContext as Activity).window.attributes.screenBrightness
        if (mBrightnessData == WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE) {
            mBrightnessData = 1f
        } else if (mBrightnessData < 0.01f) {
            mBrightnessData = 0.01f
        }
        val lpa = (mContext as Activity).window.attributes
        lpa.screenBrightness = mBrightnessData + percent
        if (lpa.screenBrightness > 1.0f) {
            lpa.screenBrightness = 1.0f
        } else if (lpa.screenBrightness < 0.01f) {
            lpa.screenBrightness = 0.01f
        }
        showBrightnessDialog(lpa.screenBrightness)
        (mContext as Activity).window.attributes = lpa
    }

    /**
     * 当前是否为直播
     */
    private fun isPlayingLive(): Boolean {
        return (gsyVideoManager.player as? PigExoPlayerManager)?.isLive == true
    }

    override fun onVideoResume() {
        onVideoResume(!isPlayingLive())
    }

    override fun onVideoResume(seek: Boolean) {
        mPauseBeforePrepared = false
        if (mCurrentState == CURRENT_STATE_PAUSE) {
            try {
//                if (mCurrentPosition > 0 && getGSYVideoManager() != null) {
                if (mCurrentPosition >= 0 && gsyVideoManager != null) {
                    if (seek) {
                        gsyVideoManager.seekTo(mCurrentPosition)
                    }
                    gsyVideoManager.start()
                    setStateAndUi(CURRENT_STATE_PLAYING)
                    if (mAudioManager != null && !mReleaseWhenLossAudio) {
                        mAudioManager.requestAudioFocus(onAudioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT)
                    }
                    mCurrentPosition = 0
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    override fun startDismissControlViewTimer() {
        cancelDismissControlViewTimer()
        taskHandler.sendEmptyMessageDelayed(hideControl, mDismissControlTime.toLong())
    }


    override fun onError(what: Int, extra: Int) {
        super.onError(what, extra)
        //hls协议播放特别是直播时经常会出现此异常，此时重新prepare即可
        if (what == PigExoMediaPlayer.MEDIA_ERROR_BEBIND_LIVE_WINDOW) {
            CLog.i(TAG, "收到BehindLiveWindowException，尝试重新播放")
            startPlayLogic()
        }
    }


    override fun cancelDismissControlViewTimer() {
        taskHandler.removeMessages(hideControl)
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
        super.onStartTrackingTouch(seekBar)
        trackingProgress = true
        startHideControlAfterStopTracking = taskHandler.hasMessages(hideControl)
        cancelDismissControlViewTimer()
        cancelProgressTimer()
    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
        super.onStopTrackingTouch(seekBar)
        trackingProgress = false
        startProgressTimer()
        if (startHideControlAfterStopTracking) {
            startDismissControlViewTimer()
        }
    }

    /**
     * 双击
     */
    protected var gestureDetector2 = GestureDetector(context.applicationContext, object : GestureDetector.SimpleOnGestureListener() {
        override fun onDoubleTap(e: MotionEvent): Boolean {
            touchDoubleUp()
            return super.onDoubleTap(e)
        }

        override fun onDown(e: MotionEvent): Boolean {
            touchSurfaceDown(e.x, e.y)
            return super.onDown(e)
        }

        override fun onScroll(e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
            var deltaX = e2.x - mDownX
            val deltaY = e2.y - mDownY

            val absDeltaX = Math.abs(deltaX)
            val absDeltaY = Math.abs(deltaY)

            if (mIfCurrentIsFullscreen && mIsTouchWigetFull || mIsTouchWiget && !mIfCurrentIsFullscreen) {
                if (!mChangePosition && !mChangeVolume && !mBrightness) {
                    val old = mChangePosition
                    touchSurfaceMoveFullLogic(absDeltaX, absDeltaY)
                    if (duration == 0) {
                        mChangePosition = false
                    }
                    if (mChangePosition && !old) {
                        mDownX = e2.x
                        deltaX = 0f
                    }
                }
            }
            touchSurfaceMove(deltaX, deltaY, e2.y)
            return super.onScroll(e1, e2, distanceX, distanceY)
        }

        override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
            if (!mChangePosition && !mChangeVolume && !mBrightness) {
                onClickUiToggle()
            }
            return super.onSingleTapConfirmed(e)
        }
    })

    /**
     * 亮度、进度、音频
     */
    override fun onTouch(v: View, event: MotionEvent): Boolean {

        val id = v.id
        val x = event.x
        val y = event.y

        if (mIfCurrentIsFullscreen && mLockCurScreen && mNeedLockFull) {
            onClickUiToggle()
            startDismissControlViewTimer()
            return true
        }

        if (id == R.id.fullscreen) {
            return false
        }

        if (id == R.id.surface_container) {
            when (event.action) {
                MotionEvent.ACTION_UP -> {

                    startDismissControlViewTimer()

                    touchSurfaceUp()


                    Debuger.printfLog(this.hashCode().toString() + "------------------------------ surface_container ACTION_UP")

                    startProgressTimer()

                    //不要和隐藏虚拟按键后，滑出虚拟按键冲突
                    if (mHideKey && mShowVKey) {
                        return true
                    }
                }
            }
            gestureDetector2.onTouchEvent(event)
        } else if (id == R.id.progress) {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    cancelDismissControlViewTimer()
                    cancelProgressTimer()
                    var vpdown: ViewParent? = parent
                    while (vpdown != null) {
                        vpdown.requestDisallowInterceptTouchEvent(true)
                        vpdown = vpdown.parent
                    }
                }
                MotionEvent.ACTION_MOVE -> {
                    cancelProgressTimer()
                    var vpdown: ViewParent? = parent
                    while (vpdown != null) {
                        vpdown.requestDisallowInterceptTouchEvent(true)
                        vpdown = vpdown.parent
                    }
                }
                MotionEvent.ACTION_UP -> {
                    startDismissControlViewTimer()

                    Debuger.printfLog(this.hashCode().toString() + "------------------------------ progress ACTION_UP")
                    startProgressTimer()
                    var vpup: ViewParent? = parent
                    while (vpup != null) {
                        vpup.requestDisallowInterceptTouchEvent(false)
                        vpup = vpup.parent
                    }
                    mBrightnessData = -1f
                }
            }
        }

        return false
    }

    override fun cancelProgressTimer() {
        if (updateProcessTimer != null) {
            updateProcessTimer.cancel()
            updateProcessTimer = null
        }
        taskHandler.removeMessages(syncProgress)
    }

    override fun startProgressTimer() {
        cancelProgressTimer()
        if (trackingProgress) {
            return
        }
        taskHandler.sendEmptyMessage(syncProgress)
    }

    override fun setTextAndProgress(secProgress: Int) {
        val position = currentPositionWhenPlaying
        val duration = duration
        if (duration == 0) {
            setProgressAndTime(0, 0, 0, 0)
        } else {
            val progress = position * 100 / duration
            setProgressAndTime(progress, secProgress, position, duration)
        }
    }

    override fun setSecondaryProgress(secProgressArg: Int) {
        val secProgress = if (duration == 0) 0 else secProgressArg
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
        setProgressAndTime(0, 0, 0, 0)
    }

}