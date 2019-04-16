package com.sanron.pppig.data.parser

import com.sanron.pppig.data.bean.micaitu.*
import com.sanron.pppig.util.CLog
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

/**
 * Author:sanron
 * Time:2019/4/12
 * Description:
 */
class KKMaoParser {


    companion object {
        val instance by lazy {
            KKMaoParser()
        }
        const val MAX_LIST_SIZE = 6
        val TAG = KKMaoParser::class.java.simpleName
    }

    private fun parseBaner(doc: Document): List<Banner> {
        val banners = mutableListOf<Banner>()
        val items = doc.select("#focus>.focusList>.con>a")
        items?.let {
            items.forEach {
                val banner = Banner()
                banner.link = it.attr("href")
                it.select("img").first()?.let {
                    banner.image = it.attr("data-src")
                }
                it.select(".sTxt>em").first()?.let {
                    banner.title = it.text()
                }
                banners.add(banner)
            }
        }
        CLog.d(TAG, "banner size = ${banners.size}")
        return banners
    }

    private fun parseCommonList(doc: Document, title: String): MutableList<VideoItem> {
        var list = mutableListOf<VideoItem>()
        doc.select(".modo_title.top>h2>a[title='$title']").first()?.apply {
            this.parent()?.parent()?.nextElementSibling()?.apply {
                val els = this@apply.select(".all_tab>#resize_list>li>a")
                els?.forEach {
                    val item = VideoItem()
                    item.name = it.attr("title")
                    item.link = it.attr("href")
                    it.select(".picsize>img").first()?.apply {
                        item.img = this.attr("src")
                    }
                    it.select(".title").first()?.apply {
                        item.label = this.text()
                    }
                    it.select(".score").first()?.apply {
                        item.score = this.text()
                    }
                    list.add(item)
                }
            }
        }
        if (list.size >= MAX_LIST_SIZE) {
            list = list.subList(0, MAX_LIST_SIZE)
        }
        return list
    }

    private fun parseAnim(doc: Document): MutableList<VideoItem> {
        val list = parseCommonList(doc, "动漫")
        CLog.d(TAG, "anim :" + list.size)
        return list
    }

    private fun parseVariety(doc: Document): MutableList<VideoItem> {
        val list = parseCommonList(doc, "综艺")
        CLog.d(TAG, "variety :" + list.size)
        return list
    }

    private fun parseTv(doc: Document): MutableList<VideoItem> {
        val list = parseCommonList(doc, "电视剧")
        CLog.d(TAG, "hot tv :" + list.size)
        return list
    }

    private fun parseHotMovie(doc: Document): MutableList<VideoItem> {
        val list = parseCommonList(doc, "电影")
        CLog.d(TAG, "hot movie :" + list.size)
        return list
    }

    fun parseMovieList(html: String): ListData<VideoItem> {
        var data = ListData<VideoItem>()
        val doc = Jsoup.parse(html)
        doc?.let {
            //是否有下一页按钮
            doc.select(".next.pagebk")?.let {
                data.hasMore = true
            }
            doc.select(".main.top>.list_vod>#vod_list>li>a")?.forEach {
                val item = VideoItem()
                item.link = it.attr("href")
                item.name = it.attr("title")
                it.select(".picsize>img").first().apply {
                    item.img = this.attr("src")
                }
                it.select(".picsize>.score").first().apply {
                    item.score = this.text()
                }
                it.select(".picsize>.title").first().apply {
                    item.label = this.text()
                }
                data.data.add(item)
            }
        }
        return data
    }

    fun parseTopMovie(html: String): ListData<VideoItem> {
        var data = ListData<VideoItem>()
        val doc = Jsoup.parse(html)
        doc?.let {
            doc.select(".main.top>.all_tab>#resize_list>li>a")?.forEach {
                val item = VideoItem()
                item.link = it.attr("href")
                item.name = it.attr("title")
                it.select(".picsize>img").first().apply {
                    item.img = this.attr("src")
                }
                it.select(".picsize>.name").first().apply {
                    item.label = this.text()
                }
                data.data.add(item)
            }
        }
        CLog.d(TAG, "top movie :" + data.data.size)
        return data
    }

    fun parseHome(html: String): Home? {
        var home: Home? = null
        val doc = Jsoup.parse(html)
        doc?.let {
            home = Home()
            home!!.banner = parseBaner(doc)
            home!!.categories.add(HomeCat().apply {
                name = "热映电影"
                type = HomeCat.Companion.MOVIE
                items = parseHotMovie(doc)
            })
            home!!.categories.add(HomeCat().apply {
                name = "热播电视"
                type = HomeCat.Companion.TV
                items = parseTv(doc)
            })
            home!!.categories.add(HomeCat().apply {
                name = "动漫"
                type = HomeCat.Companion.ANIM
                items = parseAnim(doc)
            })
            home!!.categories.add(HomeCat().apply {
                name = "综艺"
                type = HomeCat.Companion.VARIETY
                items = parseVariety(doc)
            })
        }
        return home
    }
}
