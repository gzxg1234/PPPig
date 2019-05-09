package com.sanron.pppig.app

import android.content.Context
import android.content.Intent
import com.sanron.pppig.module.micaitu.play.PlayerAct
import com.sanron.pppig.module.micaitu.videodetail.VideoDetailAct

/**
 * Author:sanron
 * Time:2019/4/24
 * Description:
 */
object Intents {

    fun videoDetail(context: Context, url: String?): Intent {
        val intent = Intent(context, VideoDetailAct::class.java)
        intent.putExtra(VideoDetailAct.ARG_URL, url)
        return intent
    }

    fun playVideo(context: Context, url: String?): Intent {
        val intent = Intent(context, PlayerAct::class.java)
        intent.putExtra(PlayerAct.ARG_URL, url)
        return intent
    }
}