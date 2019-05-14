package com.sanron.pppig.module.search

import android.app.Application
import com.sanron.datafetch_interface.Source
import com.sanron.datafetch_interface.bean.PageData
import com.sanron.datafetch_interface.bean.VideoItem
import com.sanron.pppig.base.BaseViewModel
import com.sanron.pppig.common.PageLoader
import com.sanron.pppig.data.FetchManager
import io.reactivex.Observable

/**
 *Author:sanron
 *Time:2019/5/13
 *Description:
 */
class SearchVM(application: Application) : BaseViewModel(application) {

    var word = ""

    private var source: Source? = null
    var pageLoader = PageLoader { page ->
        getRequest(page)
    }

    fun setSource(id:String){
        source = FetchManager.getSourceById(id)
    }

    fun refresh() {
        if (word.isEmpty()) {
            return
        }
        pageLoader.refresh()
    }

    private fun getRequest(page: Int): Observable<PageData<VideoItem>> {
        return source!!.fetch.getSearchResult(word, page).compose(autoDispose())
    }
}