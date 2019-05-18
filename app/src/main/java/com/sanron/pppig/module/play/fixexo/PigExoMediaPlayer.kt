package com.sanron.pppig.module.play.fixexo

import android.content.Context
import android.net.Uri
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.source.BehindLiveWindowException
import tv.danmaku.ijk.media.exo2.IjkExo2MediaPlayer
import tv.danmaku.ijk.media.player.IMediaPlayer

/**
 *Author:sanron
 *Time:2019/5/17
 *Description:
 */
class PigExoMediaPlayer(context: Context?) : IjkExo2MediaPlayer(context) {

    private var exoSourceManager: PigExoSourceManager? = null

    companion object {
        const val MEDIA_ERROR_BEBIND_LIVE_WINDOW = -10000001
    }

    init {
        exoSourceManager = PigExoSourceManager.newInstance(context, mHeaders)
    }

    override fun setDataSource(context: Context?, uri: Uri?) {
        mDataSource = uri.toString()
        mMediaSource = exoSourceManager?.getMediaSource(mDataSource, isPreview, isCache, isLooping, mCacheDir, overrideExtension)
    }

    override fun reset() {
        super.reset()
        exoSourceManager?.release()
    }

    override fun getCurrentPosition(): Long {
        if (isLive()) {
            return 0
        }
        return mInternalPlayer?.currentPosition?:0
    }

    override fun getDuration(): Long {
        if (isLive()) {
            return 0
        }
        return mInternalPlayer?.contentDuration?:0
    }

    override fun onPlayerError(error: ExoPlaybackException) {
        if (isBehindLiveWindow(error)) {
            notifyOnError(MEDIA_ERROR_BEBIND_LIVE_WINDOW, IMediaPlayer.MEDIA_ERROR_UNKNOWN)
        } else {
            super.onPlayerError(error)
        }
    }

    fun isLive(): Boolean {
        return mInternalPlayer != null && mInternalPlayer.isCurrentWindowDynamic
    }

    private fun isBehindLiveWindow(e: ExoPlaybackException): Boolean {
        if (e.type != ExoPlaybackException.TYPE_SOURCE) {
            return false
        }
        var cause: Throwable? = e.sourceException
        while (cause != null) {
            if (cause is BehindLiveWindowException) {
                return true
            }
            cause = cause.cause
        }
        return false
    }
}