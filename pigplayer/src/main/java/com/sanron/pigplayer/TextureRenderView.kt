package com.sanron.pigplayer

import android.content.Context
import android.graphics.*
import android.view.Surface
import android.view.TextureView
import android.view.View
import tv.danmaku.ijk.media.player.IMediaPlayer

/**
 * Author:sanron
 * Time:2019/5/9
 * Description:
 */
class TextureRenderView(context: Context?) : TextureView(context), RenderView {

    private var mediaPlayer: IMediaPlayer? = null
    private var surface: Surface? = null

    override fun bindMediaPlayer(mediaPlayer: IMediaPlayer) {
        this.mediaPlayer = mediaPlayer
    }

    override fun getSurface(): Surface? {
        return surface
    }

    override fun getView() = this

    override fun clear() {
        val canvas = surface?.lockCanvas(Rect(0, 0, width, height))
        canvas?.let {
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
            surface?.unlockCanvasAndPost(canvas)
        }
    }

    init {
        surfaceTextureListener = object : SurfaceTextureListener {
            override fun onSurfaceTextureSizeChanged(surfaceTexture: SurfaceTexture?, width: Int, height: Int) {
            }

            override fun onSurfaceTextureUpdated(surfaceTexture: SurfaceTexture?) {
            }

            override fun onSurfaceTextureDestroyed(surfaceTexture: SurfaceTexture?): Boolean {
                surface?.release()
                surface = null
                mediaPlayer?.setSurface(null)
                return false
            }

            override fun onSurfaceTextureAvailable(surfaceTexture: SurfaceTexture?, width: Int, height: Int) {
                surface = Surface(surfaceTexture)
                mediaPlayer?.setSurface(surface)
            }
        }
    }
}
