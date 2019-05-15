package com.sanron.pppig.module.play

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
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