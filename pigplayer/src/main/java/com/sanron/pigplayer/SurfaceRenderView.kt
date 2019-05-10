package com.sanron.pigplayer

import android.content.Context
import android.graphics.*
import android.view.Surface
import android.view.SurfaceView
import android.view.TextureView
import android.view.View
import tv.danmaku.ijk.media.player.IMediaPlayer

/**
 * Author:sanron
 * Time:2019/5/9
 * Description:
 */
class SurfaceRenderView(context: Context?) : SurfaceView(context), RenderView {
    override fun getView() = this


    override fun bindMediaPlayer(mediaPlayer: IMediaPlayer) {
        mediaPlayer.setDisplay(holder)
    }

    override fun getSurface(): Surface? {
        return holder.surface
    }

    override fun clear() {
        val canvas = holder.surface?.lockCanvas(Rect(0, 0, width, height))
        canvas?.let {
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
            holder.surface.unlockCanvasAndPost(canvas)
        }
    }

}
