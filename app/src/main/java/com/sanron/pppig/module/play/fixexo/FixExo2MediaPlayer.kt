package com.sanron.pppig.module.play.fixexo

import android.content.Context
import android.net.Uri
import tv.danmaku.ijk.media.exo2.IjkExo2MediaPlayer

/**
 *Author:sanron
 *Time:2019/5/17
 *Description:
 */
class FixExo2MediaPlayer(context: Context?) : IjkExo2MediaPlayer(context) {

    private var exoSourceManager: FixExoSourceManager? = null

    init {
        exoSourceManager = FixExoSourceManager.newInstance(context, mHeaders)
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
        if (mInternalPlayer == null || mInternalPlayer.isCurrentWindowDynamic) {
            return 0
        }
        return mInternalPlayer.currentPosition
    }

    override fun getDuration(): Long {
        if (mInternalPlayer == null || mInternalPlayer.isCurrentWindowDynamic) {
            return 0
        }
        return mInternalPlayer.contentDuration
    }
}