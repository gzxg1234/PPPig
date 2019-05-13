package com.sanron.pppig.app

import android.content.Context
import android.content.Intent
import com.sanron.datafetch_interface.bean.PlaySource
import com.sanron.pppig.module.play.PlayerAct
import com.sanron.pppig.module.search.SearchAct
import com.sanron.pppig.module.videodetail.VideoDetailAct

/**
 * Author:sanron
 * Time:2019/4/24
 * Description:
 */
object Intents {

    fun videoDetail(context: Context, url: String?,sourceId:String): Intent {
        val intent = Intent(context, VideoDetailAct::class.java)
        intent.putExtra(VideoDetailAct.ARG_URL, url)
        intent.putExtra(VideoDetailAct.ARG_SOURCE_ID, sourceId)
        return intent
    }

    fun playVideo(context: Context, title: String? = "", items: List<PlaySource>, sourcePos: Int, itemPos: Int,sourceId:String): Intent {
        val intent = Intent(context, PlayerAct::class.java)
        intent.putExtra(PlayerAct.ARG_SOURCE_POS, sourcePos)
        intent.putExtra(PlayerAct.ARG_ITEM_POS, itemPos)
        intent.putExtra(PlayerAct.ARG_TITLE, title)
        intent.putExtra(PlayerAct.ARG_PLAY_SOURCE, ArrayList(items))
        intent.putExtra(PlayerAct.ARG_SOURCE_ID, sourceId)
        return intent
    }

    fun search(context: Context): Intent {
        val intent = Intent(context, SearchAct::class.java)
        return intent
    }
}