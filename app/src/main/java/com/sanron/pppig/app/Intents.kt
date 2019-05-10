package com.sanron.pppig.app

import android.content.Context
import android.content.Intent
import com.sanron.pppig.data.bean.micaitu.PlaySource
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

    fun playVideo(context: Context, url: String?, title: String? = "", items: List<PlaySource.Item>? = null): Intent {
        val intent = Intent(context, PlayerAct::class.java)
        intent.putExtra(PlayerAct.ARG_URL, url)
        intent.putExtra(PlayerAct.ARG_TITLE, title)
        intent.putExtra(PlayerAct.ARG_SOURCE_ITEMS, arrayListOf(items))
        return intent
    }
}