package com.sanron.pppig.module.micaitu.play

import android.content.Context
import android.graphics.SurfaceTexture
import android.util.AttributeSet
import android.view.Surface
import android.view.TextureView
import android.view.View
import android.widget.FrameLayout
import com.sanron.pppig.R
import com.sanron.pppig.util.CLog
import tv.danmaku.ijk.media.player.IMediaPlayer
import tv.danmaku.ijk.media.player.IjkMediaPlayer
import java.io.IOException


/**
 * Author:sanron
 * Time:2019/5/8
 * Description:
 */
class IjkPlayerView : FrameLayout {

    private val TAG: String = "IjkPlayerView"

    private var mediaPlayer: IjkMediaPlayer? = null

    private var delayPrepare = false

    private var surface: Surface? = null

    private val textureView: TextureView by lazy {
        findViewById<TextureView>(R.id.display)
    }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        View.inflate(context, R.layout.player_view_layout, this)
        textureView.surfaceTextureListener = object : TextureView.SurfaceTextureListener {
            override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture?, width: Int, height: Int) {
            }

            override fun onSurfaceTextureUpdated(surface: SurfaceTexture?) {
            }

            override fun onSurfaceTextureDestroyed(surface: SurfaceTexture?): Boolean {
                this@IjkPlayerView.surface?.release()
                this@IjkPlayerView.surface = null
                return false
            }

            override fun onSurfaceTextureAvailable(surface: SurfaceTexture?, width: Int, height: Int) {
                this@IjkPlayerView.surface = Surface(surface)
                if (delayPrepare) {
                    prepare()
                    delayPrepare = false
                }
            }
        }
    }

    fun release() {
        surface?.release()
        mediaPlayer?.release()
    }

    var onPrepareListener: ((IMediaPlayer) -> Unit)? = null
    var onCompletionListener: ((IMediaPlayer) -> Unit)? = null
    var onErrorListener: ((IMediaPlayer, Int, Int) -> Unit)? = null
    var onInfoListener: ((IMediaPlayer, Int, Int) -> Unit)? = null
    var onBufferUpdateListener: ((IMediaPlayer, Int) -> Unit)? = null
    var onSeekCompleteListener: ((IMediaPlayer) -> Unit)? = null

    fun setDataSource(url: String, headers: Map<String, String>? = null) {
        createPlayer()
        try {
            if (headers == null) {
                mediaPlayer?.setDataSource(url)
            } else {
                mediaPlayer?.setDataSource(url, headers)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        if (surface == null) {
            delayPrepare = true
        } else {
            prepare()
        }
    }

    fun start() {
        mediaPlayer?.start()
    }

    fun pause() {
        mediaPlayer?.pause()
    }

    fun stop() {
        mediaPlayer?.stop()
    }

    fun seekTo(s: Long) {
        mediaPlayer?.seekTo(s)
    }

    fun getDuration(): Long {
        return mediaPlayer?.duration ?: -1
    }

    private fun prepare() {
        //给mediaPlayer设置视图
        mediaPlayer?.setSurface(surface)
        mediaPlayer?.prepareAsync()
    }

    private fun createPlayer() {
        mediaPlayer?.apply {
            stop()
            setDisplay(null)
            release()
        }
        mediaPlayer = IjkMediaPlayer()        //开启硬解码
        mediaPlayer?.apply {
            setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", 1)
            setOnPreparedListener {
                CLog.d(TAG, "onPrepare")
                onPrepareListener?.invoke(it)
            }
            setOnCompletionListener { player ->
                CLog.d(TAG, "onComplete")
                onCompletionListener?.invoke(player)
            }
            setOnErrorListener { player, what, extra ->
                CLog.d(TAG, "onError,what:$what，extra:$extra")
                onErrorListener?.invoke(player, what, extra)
                false
            }
            setOnInfoListener { player, what, extra ->
                CLog.d(TAG, "onInfo,what:$what，extra:$extra")
                onInfoListener?.invoke(player, what, extra)
                false
            }
            setOnBufferingUpdateListener { player, percent ->
                CLog.d(TAG, "onBufferUpdate,percent:$percent")
                onBufferUpdateListener?.invoke(player, percent)
            }
            setOnSeekCompleteListener { player ->
                CLog.d(TAG, "onSeekComplete")
                onSeekCompleteListener?.invoke(player)
            }
        }
    }
}
