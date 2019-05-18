package com.sanron.datafetch.livesource.ika112

import com.sanron.datafetch.SourceManagerImpl
import com.sanron.datafetch.http.HttpUtil
import com.sanron.datafetch_interface.exception.ParseException
import com.sanron.datafetch_interface.live.LiveDataFetch
import com.sanron.datafetch_interface.live.bean.LiveCat
import com.sanron.datafetch_interface.live.bean.LiveItem
import com.sanron.datafetch_interface.video.bean.PlayLine
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import java.nio.charset.Charset

/**
 *Author:sanron
 *Time:2019/5/16
 *Description:
 */
class IkaFetch : LiveDataFetch {
    companion object {
        const val BASE_URL = "http://ika112.com/"
    }


    override fun getLiveCats(): Observable<List<LiveCat>> {
        return HttpUtil.api.url(BASE_URL)
                .map {
                    return@map IkaParser.parserCat(String(it.bytes(), Charset.forName("utf-8")))
                }
    }

    override fun getCatItems(liveCat: LiveCat): Observable<List<LiveItem>> {
        val link = (liveCat as IkaLiveCat).link
        if (link.isNullOrEmpty()) {
            return Observable.just(emptyList())
        } else {
            return HttpUtil.api.url(link)
                    .map {
                        return@map IkaParser.parseItem(String(it.bytes(), Charset.forName("utf-8")))
                    }
        }
    }

    override fun getPlayLineList(item: LiveItem): Observable<List<PlayLine>> {
        val link = (item as IkaLiveItem).link
        if (link.isNullOrEmpty()) {
            return Observable.just(emptyList())
        } else {
            return HttpUtil.api.url(link)
                    .map {
                        val list = IkaParser.parsePlayLine(String(it.bytes(), Charset.forName("utf-8")))
                        list.forEach {
                            it.items?.forEach {
                                (it as IkaPlayItem).pageUrl = link
                            }
                        }
                        return@map list
                    }
        }
    }

    override fun getLiveSourceUrl(item: PlayLine.Item): Observable<List<String>> {
        return Observable.create(ObservableOnSubscribe<List<String>> { emitter ->
            val task = Ika112MediaSearch.search(SourceManagerImpl.context,
                    item as IkaPlayItem, object : Ika112MediaSearch.Callback {
                override fun success(result: List<String>) {
                    emitter.onNext(result)
                    emitter.onComplete()
                }

                override fun error(msg: String) {
                    emitter.tryOnError(ParseException("解析失败"))
                }
            })
            emitter.setCancellable {
                task.cancel()
            }
        }).subscribeOn(AndroidSchedulers.mainThread())
    }
}