package com.sanron.datafetch.livesource.haoqu

import com.sanron.datafetch.MediaSearch
import com.sanron.datafetch.SourceManagerImpl
import com.sanron.datafetch.WebHelper
import com.sanron.datafetch.http.HttpUtil
import com.sanron.datafetch_interface.exception.ParseException
import com.sanron.datafetch_interface.live.LiveDataFetch
import com.sanron.datafetch_interface.live.bean.LiveCat
import com.sanron.datafetch_interface.live.bean.LiveItem
import com.sanron.datafetch_interface.video.bean.PlayLine
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import org.json.JSONException
import org.json.JSONObject
import java.nio.charset.Charset

/**
 *Author:sanron
 *Time:2019/5/16
 *Description:
 */
class HaoquFetch : LiveDataFetch {
    companion object {
        const val BASE_URL = "http://m.haoqu.net"
    }

    override fun getLiveCats(): Observable<List<LiveCat>> {
        return HttpUtil.api.url("$BASE_URL/zhibo")
                .map { it ->
                    return@map HaoquParser.parserCat(String(it.bytes(), Charset.forName("gb2312")))
                }
    }

    override fun getCatItems(liveCat: LiveCat): Observable<List<LiveItem>> {
        val link = (liveCat as HaoquLiveCat).link
        if (link.isNullOrEmpty()) {
            return Observable.just(emptyList())
        } else {
            return HttpUtil.api.url(link)
                    .map {
                        return@map HaoquParser.parseItem(String(it.bytes(), Charset.forName("gb2312")))
                    }
        }
    }

    override fun getPlayLineList(item: LiveItem): Observable<List<PlayLine>> {
        val link = (item as HaoquLiveItem).link
        if (link.isNullOrEmpty()) {
            return Observable.just(emptyList())
        } else {
            return HttpUtil.api.url(link)
                    .map {
                        return@map HaoquParser.parsePlayLine(String(it.bytes(), Charset.forName("gb2312")))
                    }
        }
    }

    override fun getLiveSourceUrl(item: PlayLine.Item): Observable<List<String>> {
        val link = "$BASE_URL/e/extend/tv.php?id=${(item as HaoquPlayItem).id}"
        return Observable.create(ObservableOnSubscribe<JSONObject> { emitter ->
            val task = HaoquUrlHelper.getVideoSource(SourceManagerImpl.context,
                    link, null, object : WebHelper.Callback {
                override fun success(result: String) {
                    var json: JSONObject? = null
                    try {
                        json = JSONObject(result)
                    } catch (e: JSONException) {
                    }
                    if (json == null) {
                        emitter.tryOnError(ParseException("解析失败"))
                    } else {
                        emitter.onNext(json)
                        emitter.onComplete()
                    }
                }

                override fun error(msg: String) {
                    emitter.tryOnError(ParseException("解析失败"))
                }
            })
            emitter.setCancellable {
                task.cancel()
            }
        }).flatMap { jsonObj ->
            val isSource = jsonObj.optBoolean("isSource")
            val url = jsonObj.optString("url")
            if (isSource) {
                return@flatMap Observable.just(listOf(url))
            }
            return@flatMap Observable.create(ObservableOnSubscribe<List<String>> { emitter ->
                val cancellable = MediaSearch.search(SourceManagerImpl.context,
                        url, null, 1, object : MediaSearch.Callback {
                    override fun success(result: List<String>) {
                        emitter.onNext(result)
                        emitter.onComplete()
                    }

                    override fun error(msg: String) {
                        emitter.tryOnError(ParseException("解析失败"))
                    }
                })
                emitter.setCancellable {
                    cancellable.cancel()
                }
            })
        }
    }
}