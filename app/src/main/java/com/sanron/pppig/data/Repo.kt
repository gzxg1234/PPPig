package com.sanron.pppig.data

import com.sanron.pppig.data.bean.micaitu.Home
import com.sanron.pppig.data.bean.micaitu.PageData
import com.sanron.pppig.data.bean.micaitu.VideoDetail
import com.sanron.pppig.data.bean.micaitu.VideoItem
import com.sanron.pppig.data.kkkkmao.KMaoFetch
import io.reactivex.Observable

/**
 * Author:sanron
 * Time:2019/2/20
 * Description:
 */
object Repo : DataFetch {

    var dataFetch: DataFetch = KMaoFetch()

    fun setSource(type: Int) {
        if (type == 0) {
            dataFetch = KMaoFetch()
        }
    }

    override fun getMicaituHome(): Observable<Home> = dataFetch.getMicaituHome()

    override fun getTopMovie(): Observable<PageData<VideoItem>> = dataFetch.getTopMovie()

    override fun getVideoDetail(path: String): Observable<VideoDetail> = dataFetch.getVideoDetail(path)

    override fun getVideoSource(url: String, webPageHelper: WebPageHelper) = dataFetch.getVideoSource(url, webPageHelper)

    override fun getAll(type: String?, country: String?, year: String?, page: Int) = dataFetch.getAll(type, country, year, page)

}
