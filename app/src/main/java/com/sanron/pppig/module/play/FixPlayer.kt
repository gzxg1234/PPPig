package com.sanron.pppig.module.play

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.*
import com.shuyu.gsyvideoplayer.R
import com.shuyu.gsyvideoplayer.utils.Debuger
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer
import com.shuyu.gsyvideoplayer.video.base.GSYVideoView
import java.util.*

/**
 *Author:sanron
 *Time:2019/5/15
 *Description:
 * 修复一些bug
 */
open class FixPlayer : StandardGSYVideoPlayer {

    private inner class SyncProgressTask : TimerTask() {
        override fun run() {
            if (mCurrentState == GSYVideoView.CURRENT_STATE_PLAYING || mCurrentState == GSYVideoView.CURRENT_STATE_PAUSE) {
                Handler(Looper.getMainLooper()).post { setTextAndProgress(-1) }
            }
        }
    }

    private var syncProgressTask: SyncProgressTask? = null

    constructor(context: Context?, fullFlag: Boolean?) : super(context, fullFlag)
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)


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
        if (syncProgressTask != null) {
            syncProgressTask?.cancel()
            syncProgressTask = null
        }
    }

    override fun startProgressTimer() {
        cancelProgressTimer()
        updateProcessTimer = Timer()
        syncProgressTask = SyncProgressTask()
        updateProcessTimer.schedule(syncProgressTask, 0, 300)
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