package com.sanron.pppig.app

import android.content.Context
import android.content.Intent
import com.sanron.datafetch_interface.bean.PlaySource
import com.sanron.pppig.module.play.PlayerAct
import com.sanron.pppig.module.videodetail.VideoDetailAct

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

    fun playVideo(context: Context, title: String? = "", items: List<PlaySource>, sourcePos:Int, itemPos:Int): Intent {
        val intent = Intent(context, PlayerAct::class.java)
        intent.putExtra(PlayerAct.ARG_SOURCE_POS, sourcePos)
        intent.putExtra(PlayerAct.ARG_ITEM_POS, itemPos)
        intent.putExtra(PlayerAct.ARG_TITLE, title)
        intent.putExtra(PlayerAct.ARG_SOURCE, ArrayList(items))
        return intent
    }
}