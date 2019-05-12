package com.sanron.pppig.data

import com.sanron.datafetch.SourceManagerImpl
import com.sanron.datafetch_interface.DataFetch
import com.sanron.datafetch_interface.SourceManager
import com.sanron.datafetch_interface.bean.*
import com.sanron.pppig.app.PiApp
import io.reactivex.Observable

/**
 * Author:sanron
 * Time:2019/2/20
 * Description:
 */
object Repo : DataFetch {

    var sourceManager: SourceManager = SourceManagerImpl().apply {
        initContext(context = PiApp.sInstance)
        setHttpClient(Injector.provideOkHttpClient())
    }

    var dataFetch: DataFetch = sourceManager.getSourceList().get(1).fetch


    override fun getHomeData(): Observable<Home> = dataFetch.getHomeData()

    override fun getTopMovie(): Observable<PageData<VideoItem>> = dataFetch.getTopMovie()

    override fun getVideoDetail(path: String): Observable<VideoDetail> = dataFetch.getVideoDetail(path)

    override fun getVideoSource(url: String) = dataFetch.getVideoSource(url)

    override fun getVideoListTypes(): List<VideoListType> = dataFetch.getVideoListTypes()

    override fun getVideoList(type: Int, param: Map<String, FilterItem>, page: Int): Observable<PageData<VideoItem>> = dataFetch.getVideoList(type, param, page)

    override fun getVideoListFilter(type: Int): Map<String, List<FilterItem>> = dataFetch.getVideoListFilter(type)

}
