package com.sanron.pppig.module.play


import android.content.Context
import android.media.AudioManager
import android.net.Uri
import android.os.Message
import com.sanron.pppig.module.play.fixexo.FixExo2MediaPlayer
import com.sanron.pppig.util.limit
import com.shuyu.gsyvideoplayer.cache.ICacheManager
import com.shuyu.gsyvideoplayer.model.GSYModel
import com.shuyu.gsyvideoplayer.model.VideoOptionModel
import tv.danmaku.ijk.media.exo2.Exo2PlayerManager
import tv.danmaku.ijk.media.exo2.IjkExo2MediaPlayer

class PigVideoManager : Exo2PlayerManager() {

    override fun initVideoPlayer(context: Context, msg: Message, optionModelList: MutableList<VideoOptionModel>?, cacheManager: ICacheManager?) {
        super.initVideoPlayer(context, msg, optionModelList, cacheManager)
        val field = Exo2PlayerManager::class.java.getDeclaredField("mediaPlayer")
        field.isAccessible = true
        field.set(this, createFixMediaPlayer(context, msg, cacheManager))
    }

    override fun getDuration(): Long {
        return super.getDuration()
    }

    private fun createFixMediaPlayer(context: Context, msg: Message, cacheManager: ICacheManager?): IjkExo2MediaPlayer {
        var mediaPlayer = FixExo2MediaPlayer(context)
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)
        //使用自己的cache模式
        val gsyModel = msg.obj as GSYModel
        try {
            mediaPlayer.isLooping = gsyModel.isLooping
            mediaPlayer.setPreview(gsyModel.mapHeadData != null && gsyModel.mapHeadData.size > 0)
            if (gsyModel.isCache && cacheManager != null) {
                //通过管理器处理
                cacheManager.doCacheLogic(context, mediaPlayer, gsyModel.url, gsyModel.mapHeadData, gsyModel.cachePath)
            } else {
                //通过自己的内部缓存机制
                mediaPlayer.setCache(gsyModel.isCache)
                mediaPlayer.setCacheDir(gsyModel.cachePath)
                mediaPlayer.setOverrideExtension(gsyModel.overrideExtension)
                mediaPlayer.setDataSource(context, Uri.parse(gsyModel.url), gsyModel.mapHeadData)
            }
            if (gsyModel.speed != 1f && gsyModel.speed > 0) {
                mediaPlayer.setSpeed(gsyModel.speed, 1f)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return mediaPlayer
    }


    override fun seekTo(time: Long) {
        //修复seek到最大值时，一直loading的问题
        super.seekTo(time.limit(0L, (duration - 1).limit(min = 0L)))
    }

}