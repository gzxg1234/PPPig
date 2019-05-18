package com.sanron.datafetch.videosource.moyan

import android.text.TextUtils
import com.sanron.datafetch.FetchLog
import com.sanron.datafetch_interface.exception.ParseException
import com.sanron.datafetch_interface.video.bean.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

/**
 * Author:sanron
 * Time:2019/4/12
 * Description:
 */
object MoyanParser {

    const val BANNER_MAX_SIZE = 9
    const val HOME_CAT_VIDEO_MAX_SIZE = 9
    val TAG: String = MoyanParser::class.java.simpleName

    /**
     * 解析banner轮播
     */
    private fun parseBanner(doc: Document): List<Banner> {
        val banners = mutableListOf<Banner>()
        doc.select(".container>.row>.stui-pannel>.stui-pannel-box>.stui-pannel_hd>.stui-pannel__head>.title")
                .find {
                    it.ownText().contains("热播推荐")
                }?.let { titleE ->
                    run {
                        titleE.parent()?.parent()?.nextElementSibling()?.select("ul.stui-vodlist>li")?.forEach {
                            it.selectFirst(".stui-vodlist__box>.stui-vodlist__thumb")?.apply {
                                val banner = Banner()
                                banner.image = attr("data-original")
                                banner.link = attr("href")
                                banner.title = attr("title")
                                banners.add(banner)
                            }
                            if (banners.size == BANNER_MAX_SIZE) {
                                return@run
                            }
                        }
                    }
                }
        FetchLog.d(TAG, "banner size = ${banners.size}")
        return banners
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
                        item.img = it2.attr("data-original")
                        item.link = it2.attr("href")
                        item.label = it2.selectFirst("span.pic-text.text-right")?.ownText() ?: ""
                        item.score = ""
                        list.add(item)
                        if (list.size == HOME_CAT_VIDEO_MAX_SIZE) {
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
     * 解析电影页
     */
    fun parseVideoList(html: String): PageData<VideoItem> {
        var data = PageData<VideoItem>()
        data.data = mutableListOf()
        val doc = Jsoup.parse(html)
        doc?.let {
            //是否有下一页
            doc.selectFirst(".container>.row>.stui-page>li>span.num")?.let {
                val numText = it.ownText()
                val arr = numText.split("/")
                data.hasMore = arr.size == 2 && arr[0] != arr[1]
            }
            doc.select(".container>.row>.stui-pannel>.stui-pannel-box>.stui-pannel_bd>.stui-vodlist>li>.stui-vodlist__box>a")?.forEach {
                val item = VideoItem()
                item.name = it.attr("title")
                item.link = it.attr("href")
                item.img = it.attr("data-original")
                item.label = it.selectFirst("span.pic-text.text-right")?.ownText() ?: ""
                item.score = ""
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
            throw ParseException("解析错误", e)
        }
    }


    /**
     * 解析电影详情
     */
    fun parseVideoDetail(html: String): VideoDetail? {
        try {
            val doc = Jsoup.parse(html)
            doc?.apply {
                val detail = VideoDetail()
                //介绍信息
                detail.intro = ""
                detail.infoList = mutableListOf()
                selectFirst(".container>.row>div>.stui-pannel>.stui-pannel-box>.stui-content__thumb>.stui-vodlist__thumb>img")?.let {
                    detail.image = it.attr("data-original")
                }
                //解析一些电影信息
                selectFirst(".container>.row>div>.stui-pannel>.stui-pannel-box>.stui-content__detail")?.let {
                    it.selectFirst("h1.title")?.let {
                        detail.title = it.ownText()
                    }
                    it.select("p.data>span.text-muted")?.forEachIndexed { index, element ->
                        val ownerText = element.ownText()
                        if (ownerText.contains("类型")) {
                            element.nextElementSibling()?.let { a ->
                                detail.infoList?.add("类型: ${a.ownText()}")
                            }
                        } else if (ownerText.contains("地区")) {
                            element.nextElementSibling()?.let { a ->
                                detail.infoList?.add("地区: ${a.ownText()}")
                            }
                        } else if (ownerText.contains("年份")) {
                            element.nextElementSibling()?.let { a ->
                                detail.infoList?.add("年份: ${a.ownText()}")
                            }
                        } else if (ownerText.contains("导演")) {
                            element.nextElementSibling()?.let { a ->
                                detail.infoList?.add("导演: ${a.ownText()}")
                            }
                        } else if (ownerText.contains("主演")) {
                            element.parent()?.select("a")?.map {
                                it.ownText()
                            }?.let { list ->
                                detail.infoList?.add("主演: ${TextUtils.join(" ", list.toTypedArray())}")
                            }
                        }
                    }
                }

                //解析播放源
                select(".container>.row>div>.stui-pannel>div.stui-pannel-box.playlist")?.forEach {
                    detail.mLine ?: run {
                        detail.mLine = mutableListOf()
                    }
                    var playSource = PlayLine()
                    it.selectFirst(".stui-pannel_hd>.stui-pannel__head>h3.title")?.let {
                        playSource.name = it.ownText().trim()
                    }
                    playSource.items = it.select(".stui-pannel_bd>ul.stui-content__playlist>li>a")?.map {
                        return@map MoyanPlayItem().apply {
                            name = it.ownText()
                            link = it.attr("href")
                        }
                    }?.toMutableList()
                    detail.mLine?.add(playSource)
                }
                return detail
            }
            throw ParseException("解析失败")
        } catch (e: Throwable) {
            throw ParseException("解析失败", e)
        }
    }


    fun parseSearchResult(html: String?): PageData<VideoItem>? {
        var data = PageData<VideoItem>()
        data.data = mutableListOf()
        Jsoup.parse(html)?.let { doc ->
            doc.select(".container>.row ul.stui-vodlist__media>li>.thumb>.stui-vodlist__thumb")?.forEach {
                val item = VideoItem()
                item.name = it.attr("title")
                item.img = it.attr("data-original")
                item.link = it.attr("href")
                item.label = it.selectFirst("span.pic-text")?.ownText()
                data.data?.add(item)
            }
            //是否有下一页
            doc.selectFirst(".stui-page>li>span.num")?.let {
                val numText = it.ownText()
                val arr = numText.split("/")
                data.hasMore = arr.size == 2 && arr[0] != arr[1]
            }
        }
        return data
    }
}
