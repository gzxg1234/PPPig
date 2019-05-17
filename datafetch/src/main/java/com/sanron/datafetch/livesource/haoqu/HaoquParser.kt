package com.sanron.datafetch.livesource.haoqu

import com.sanron.datafetch_interface.live.bean.LiveCat
import com.sanron.datafetch_interface.live.bean.LiveItem
import com.sanron.datafetch_interface.video.bean.PlayLine
import org.jsoup.Jsoup

/**
 *Author:sanron
 *Time:2019/5/16
 *Description:
 */
object HaoquParser {

    fun parseItem(html: String): List<LiveItem> {
        val items = mutableListOf<LiveItem>()
        Jsoup.parse(html)?.let {
            it.select(".list-box.J-medal>.xhbox>li>a")?.forEach {
                val item = LiveItem()
                item.name = it.ownText()
                item["link"] = it.attr("href")
                items.add(item)
            }
        }
        return items
    }

    fun parsePlayLine(html: String): List<PlayLine> {
        val playLineList = mutableListOf<PlayLine>()
        Jsoup.parse(html)?.let { doc ->
            doc.select(".tab-syb>.buttons>.playlist>option")?.forEach { e ->
                val playLine = PlayLine()
                playLine.name = e.ownText()
                playLine.items = mutableListOf()
                val item = PlayLine.Item().apply {
                    name = "直播"
                    set("id", e.attr("value"))
                }
                playLine.items?.add(item)
                playLineList.add(playLine)
            }
        }
        return playLineList
    }

    fun parserCat(html: String): List<LiveCat> {
        val cats = mutableListOf<LiveCat>()
        Jsoup.parse(html)?.let {
            it.select(".nav-box>.J-tabset>li>a")?.forEach {
                val cat = LiveCat()
                cat.name = it.ownText()
                cat["link"] = it.attr("href")
                if (cat.name?.contains("省级") == true) {
                    return@forEach
                }
                cats.add(cat)
            }
        }
        return cats
    }
}