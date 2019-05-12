package com.sanron.pppig.module.mainhome.videolist

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import com.sanron.datafetch_interface.bean.FilterItem
import com.sanron.datafetch_interface.bean.PageData
import com.sanron.datafetch_interface.bean.VideoItem
import com.sanron.pppig.base.BaseViewModel
import com.sanron.pppig.common.PageLoader
import com.sanron.pppig.data.Repo
import io.reactivex.Observable

/**
 *Author:sanron
 *Time:2019/4/16
 *Description:
 */
class VideoListVM(application: Application) : BaseViewModel(application) {

    var type:Int = 0

    val toggleFilterCmd = MutableLiveData<Boolean>()

    val params = mutableMapOf<String, FilterItem>()

    var pageLoader = PageLoader { page ->
        getRequest(page)
    }

    init {
        toggleFilterCmd.value = false
    }

    fun toggleFilterWindow() {
        toggleFilterCmd.value = !toggleFilterCmd.value!!
    }

    fun closeFilterWindow() {
        toggleFilterCmd.value = false
    }

    fun getFilter(): Map<String, List<FilterItem>> {
        return Repo.getVideoListFilter(type)
    }

    private fun getRequest(page: Int): Observable<PageData<VideoItem>> {
        return Repo.getVideoList(type,params,page).compose(addDisposable())
    }

}