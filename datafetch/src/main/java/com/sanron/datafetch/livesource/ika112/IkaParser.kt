package com.sanron.datafetch.livesource.ika112

import com.sanron.datafetch.completeUrl
import com.sanron.datafetch_interface.live.bean.LiveCat
import com.sanron.datafetch_interface.live.bean.LiveItem
import com.sanron.datafetch_interface.video.bean.PlayLine
import org.jsoup.Jsoup

/**
 *Author:sanron
 *Time:2019/5/16
 *Description:
 */
object IkaParser {

    fun parseItem(html: String): List<LiveItem> {
        val items = mutableListOf<LiveItem>()
        Jsoup.parse(html)?.let {
            it.getElementsByAttributeValue("data-role", "listview").first()?.let {
                it.select("li>a")?.forEach {
                    val item = IkaLiveItem()
                    item.name = it.ownText()
                    item.link = it.attr("href").completeUrl(IkaFetch.BASE_URL)
                    items.add(item)
                }
            }
        }
        return items
    }

    fun parserCat(html: String): List<LiveCat> {
        val cats = mutableListOf<LiveCat>()
        Jsoup.parse(html)?.let {
            it.getElementsByAttributeValue("data-role", "listview").first()?.let {
                it.select("li>a")?.forEach {
                    val cat = IkaLiveCat()
                    cat.name = it.ownText()
                    cat.link = it.attr("href").completeUrl(IkaFetch.BASE_URL)
                    cats.add(cat)
                }
            }
        }
        return cats
    }

    fun parsePlayLine(html: String): List<PlayLine> {
        val playLineList = mutableListOf<PlayLine>()
        Jsoup.parse(html)?.let { doc ->
            doc.select("select#playURL>option")?.forEach { e ->
                val playLine = PlayLine()
                playLine.name = e.ownText()
                playLine.items = mutableListOf(IkaPlayItem().apply {
                    name = "直播"
                    url = e.attr("value")
                })
                playLineList.add(playLine)
            }
        }
        return playLineList
    }

}