package com.sanron.pigplayer
import android.graphics.Bitmap
import android.view.Surface
import android.view.View
import tv.danmaku.ijk.media.player.IMediaPlayer

/**
 * Author:sanron
 * Time:2019/5/9
 * Description:
 */
interface RenderView {

    fun getSurface(): Surface?

    fun bindMediaPlayer(mediaPlayer: IMediaPlayer)

    fun clear()

    fun setRotation(rotation: Float)

    fun getView(): View

}
