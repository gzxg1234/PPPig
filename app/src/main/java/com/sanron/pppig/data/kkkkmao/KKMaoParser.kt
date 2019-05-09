package com.sanron.pppig.data.kkkkmao

import com.sanron.pppig.data.bean.micaitu.*
import com.sanron.pppig.util.CLog
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.util.regex.Pattern

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
        val TAG: String = KKMaoParser::class.java.simpleName
        val PATTERN_TITLE: Pattern = Pattern.compile("\\[([\\s\\S]*)]")

    }

    /**
     * 解析banner轮播
     */
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
                //去除两端括号
                val matcher = PATTERN_TITLE.matcher(banner.title)
                if (matcher.find() && matcher.groupCount() > 1) {
                    banner.title = matcher.group(1)
                }

                banners.add(banner)
            }
        }
        CLog.d(TAG, "banner size = ${banners.size}")
        return banners
    }

    /**
     * 解析列表
     */
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

    /**
     * 解析电影页
     */
    fun parseMovieList(html: String): PageData<VideoItem> {
        var data = PageData<VideoItem>()
        data.data = mutableListOf()
        val doc = Jsoup.parse(html)
        doc?.let {
            //是否有下一页按钮
            doc.select(".next.pagegbk")?.first()?.let {
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
                data.data?.add(item)
            }
        }
        return data
    }

    /**
     * 解析top电影
     */
    fun parseTopMovie(html: String): PageData<VideoItem> {
        var data = PageData<VideoItem>()
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
                data.data?.add(item)
            }
        }
        CLog.d(TAG, "top movie :" + data.data?.size)
        return data
    }

    /**
     * 解析首页数据
     */
    fun parseHome(html: String): Home? {
        var home: Home? = null
        val doc = Jsoup.parse(html)
        doc?.let {
            home = Home()
            home!!.banner = parseBaner(doc)
            home!!.categories.add(HomeCat().apply {
                name = "热映电影"
                type = HomeCat.MOVIE
                items = parseHotMovie(doc)
            })
            home!!.categories.add(HomeCat().apply {
                name = "热播电视"
                type = HomeCat.TV
                items = parseTv(doc)
            })
            home!!.categories.add(HomeCat().apply {
                name = "动漫"
                type = HomeCat.ANIM
                items = parseAnim(doc)
            })
            home!!.categories.add(HomeCat().apply {
                name = "综艺"
                type = HomeCat.VARIETY
                items = parseVariety(doc)
            })
        }
        return home
    }


    /**
     * 解析电影详情
     */
    fun parseVideoDetail(html: String): VideoDetail? {
        val doc = Jsoup.parse(html)
        doc?.apply {
            val detail = VideoDetail()
            //介绍信息
            detail.infoList = mutableListOf()
            select(".vod-n-l>p")?.forEach {
                detail.infoList!!.add(it.text())
            }
            select(".vod-n-l>h1")?.forEach {
                detail.title = it.text()
            }

            select("#resize_vod.main>.vod-l>.vod-n-img>img")?.first()?.apply {
                detail.image = attr("src")
            }

            //简介
            selectFirst(".vod-play-info.main>.vod-info-tab>.vod_content")?.apply {
                detail.intro = ownText()
            }

            //解析播放列表
            detail.source = mutableListOf()
            select(".vod-play-info.main>#con_vod_1>.play-box")?.forEach {
                if(it.id() == "xigua"
                        ||it.id()=="pan"){
                    //西瓜和网盘过滤
                    return@forEach
                }
                val playSource = PlaySource()
                playSource.name = "播放源"
                playSource.items = mutableListOf()
                //找播放源名称
                select(".vod-play-info.main>#con_vod_1>.play-title>#${it.id()}>a").first()?.let { titleE ->
                    playSource.name = titleE.ownText()
                }
                it.select(".plau-ul-list>li>a").forEach { itemE ->
                    val item = PlaySource.Item()
                    item.name = itemE.attr("title")
                    item.link = itemE.attr("href")
                    playSource.items?.add(item)
                }
                detail.source?.add(playSource)


            }
            return detail
        }
        return null
    }

    fun parsePlayPageUrl(html: String): String? {
        val doc = Jsoup.parse(html)
        doc?.apply {
            select(".playerbox>iframe").first()?.apply {
                return this.attr("src")
            }
        }
        return null
    }
}
