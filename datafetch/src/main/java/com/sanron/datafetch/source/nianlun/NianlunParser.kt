package com.sanron.datafetch.source.nianlun

import com.sanron.datafetch.FetchLog
import com.sanron.datafetch.source.kkkkmao.KKMaoParser
import com.sanron.datafetch.source.moyan.MoyanParser
import com.sanron.datafetch_interface.bean.*
import com.sanron.datafetch_interface.exception.ParseException
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.util.regex.Pattern

/**
 * Author:sanron
 * Time:2019/4/12
 * Description:
 */
object NianlunParser {

    const val BANNER_MAX_SIZE = 9
    const val HOME_CAT_VIDEO_MAX_SIZE = 9
    val TAG: String = KKMaoParser::class.java.simpleName
    val BANNER_IMG_PATTERN: Pattern = Pattern.compile("background: url\\(([\\s\\S]*)\\)")


    /**
     * 解析banner轮播
     */
    private fun parseBanner(doc: Document): List<Banner> {
        val banners = mutableListOf<Banner>()
        doc.select("#banner>.carousel-inner>div.item>a.stui-banner__pic")?.forEach {
            val banner = Banner()
            banner.link = it.attr("href")
            it.attr("style")?.let {
                val matcher = BANNER_IMG_PATTERN.matcher(it)
                if (matcher.find() ) {
                    banner.image = completeUrl(matcher.group(1))
                }
            }
            banners.add(banner)
        }
        FetchLog.d(TAG, "banner size = ${banners.size}")
        return banners
    }

    /**
     * 解析列表
     */
    private fun parseCommonList(doc: Document, title: String): MutableList<VideoItem> {
        var list = mutableListOf<VideoItem>()
        doc.select(".modo_title.top>h2>a[title='$title']").first()?.apply {
            this.parent()?.parent()?.nextElementSibling()?.apply {
                val els = this.select(".all_tab>#resize_list>li>a")
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
                    if (list.size == HOME_CAT_VIDEO_MAX_SIZE) {
                        return@apply
                    }
                }
            }
        }
        return list
    }

    private fun parseAnim(doc: Document): MutableList<VideoItem> {
        val list = parseCommonList(doc, "动漫")
        FetchLog.d(TAG, "anim :" + list.size)
        return list
    }

    private fun parseVariety(doc: Document): MutableList<VideoItem> {
        val list = parseCommonList(doc, "综艺")
        FetchLog.d(TAG, "variety :" + list.size)
        return list
    }

    private fun parseTv(doc: Document): MutableList<VideoItem> {
        val list = parseCommonList(doc, "电视剧")
        FetchLog.d(TAG, "hot tv :" + list.size)
        return list
    }

    private fun parseHotMovie(doc: Document): MutableList<VideoItem> {
        val list = parseCommonList(doc, "电影")
        FetchLog.d(TAG, "hot movie :" + list.size)
        return list
    }

    private fun completeUrl(path: String?): String? {
        return path?.let {
            if (it.startsWith("/")) {
                return@let NianlunApi.BASE_URL + it
            } else {
                return@let it
            }
        }
    }

    /**
     * 解析电影页
     */
    fun parseVideoList(html: String): PageData<VideoItem> {
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
        FetchLog.d(TAG, "top movie :" + data.data?.size)
        return data
    }


    /**
     * 解析列表
     */
    private fun parseCategories(doc: Document): MutableList<HomeCat> {
        val categories = mutableListOf<HomeCat>()
        doc.select(".container>.row>.stui-pannel.stui-pannel-bg")?.forEach { it1 ->
            val title = it1.selectFirst(".stui-pannel-box>.stui-pannel_hd>.stui-pannel__head>h3.title>a")?.ownText()
                    ?: ""
            if (!title.contains("热播推荐")) {
                val cat = HomeCat()
                if (title.contains("电影")) {
                    cat.name = "电影"
                    cat.type = HomeCat.MOVIE
                } else if (title.contains("连续剧")) {
                    cat.name = "电视剧"
                    cat.type = HomeCat.TV
                } else if (title.contains("综艺")) {
                    cat.name = "综艺"
                    cat.type = HomeCat.VARIETY
                } else if (title.contains("动漫")) {
                    cat.name = "动漫"
                    cat.type = HomeCat.ANIM
                } else {
                    return@forEach
                }
                val list = mutableListOf<VideoItem>()
                run {
                    it1.select(".stui-pannel-box>div.stui-pannel_bd>div>ul.stui-vodlist>li>.stui-vodlist__box>.stui-vodlist__thumb")?.forEach { it2 ->
                        val item = VideoItem()
                        item.name = it2.attr("title")
                        item.img = completeUrl(it2.attr("data-original"))
                        item.link = it2.attr("href")
                        item.label = it2.selectFirst("span.pic-text.text-right")?.ownText() ?: ""
                        item.score = ""
                        list.add(item)
                        if (list.size == MoyanParser.HOME_CAT_VIDEO_MAX_SIZE) {
                            return@run
                        }
                    }
                }
                cat.items = list
                categories.add(cat)
            }
        }
        return categories
    }


    /**
     * 解析首页数据
     */
    fun parseHome(html: String): Home? {
        try {
            var home: Home? = null
            val doc = Jsoup.parse(html)
            doc?.let {
                home = Home()
                home!!.banner = parseBanner(doc)
                home!!.categories = parseCategories(doc)
            }
            return home
        } catch (e: Throwable) {
            throw ParseException("解析出错", e)
        }
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
                if (it.id() == "xigua"
                        || it.id() == "pan") {
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
