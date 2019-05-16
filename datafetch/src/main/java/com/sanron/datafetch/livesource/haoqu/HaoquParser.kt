package com.sanron.datafetch.livesource.haoqu

import com.sanron.datafetch_interface.live.bean.LiveCat
import com.sanron.datafetch_interface.live.bean.LiveItem
import org.jsoup.Jsoup

/**
 *Author:sanron
 *Time:2019/5/16
 *Description:
 */
object HaoquParser {

    fun parseIem(html: String): List<LiveItem> {
        val items = mutableListOf<LiveItem>()
        Jsoup.parse(html)?.let {
            it.select(".list-box.J-medal>.xhbox>li>a")?.forEach {
                val item = LiveItem()
                item.link = it.attr("href")
                item.name = it.ownText()
                items.add(item)
            }
        }
        return items
    }

    fun parserCat(html: String): List<LiveCat> {
        val cats = mutableListOf<LiveCat>()
        Jsoup.parse(html)?.let {
            it.select(".nav-box>.J-tabset>li>a")?.forEach {
                val cat = LiveCat()
                cat.name = it.ownText()
                cat.link = it.attr("href")
                if (cat.name?.contains("省级") == true) {
                    return@forEach
                }
                cats.add(cat)
            }
        }
        return cats
    }
}