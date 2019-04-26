package com.sanron.pppig.app

import android.content.Context
import android.content.Intent
import com.sanron.pppig.module.micaitu.moviedetail.MovieDetailAct

/**
 * Author:sanron
 * Time:2019/4/24
 * Description:
 */
object Intents {
    fun movieDetail(context: Context, url: String?, imgUrl: String? = null, videoName: String? = null): Intent {
        val intent = Intent(context, MovieDetailAct::class.java)
        intent.putExtra(MovieDetailAct.ARG_URL, url)
        intent.putExtra(MovieDetailAct.ARG_IMG_URL, imgUrl)
        intent.putExtra(MovieDetailAct.ARG_NAME, videoName)
        return intent
    }
}