package com.sanron.pppig.module.micaitu.movie

import android.app.Application
import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.MutableLiveData
import android.text.TextUtils
import com.sanron.pppig.base.BaseViewModel
import com.sanron.pppig.common.PageLoader
import com.sanron.pppig.data.Repo
import com.sanron.pppig.data.bean.micaitu.PageData
import com.sanron.pppig.data.bean.micaitu.VideoItem
import io.reactivex.Observable
import java.util.*

/**
 *Author:sanron
 *Time:2019/4/16
 *Description:
 */
class MovieVM(application: Application) : BaseViewModel(application) {
    val TYPES = arrayOf(
            Pair("全部", "movie"),
            Pair("喜剧", "Comedy"),
            Pair("动作", "Action"),
            Pair("预告片", "yugaopian"),
            Pair("科幻", "Sciencefiction"),
            Pair("惊悚", "Horror"),
            Pair("爱情", "Love"),
            Pair("战争", "War"),
            Pair("剧情", "Drama")
    )
    val COUNTRYS = arrayOf(
            Pair("全部", ""),
            Pair("大陆", "大陆"),
            Pair("香港", "香港"),
            Pair("台湾", "台湾"),
            Pair("美国", "美国"),
            Pair("韩国", "韩国"),
            Pair("日本", "日本"),
            Pair("泰国", "泰国"),
            Pair("新加坡", "新加坡"),
            Pair("马来西亚", "马来西亚"),
            Pair("印度", "印度"),
            Pair("法国", "法国"),
            Pair("加拿大", "加拿大")
    )

    val YEARS = run {
        val yearsList = mutableListOf<Pair<String, String>>()
        val nowYear = Date().year + 1900
        val years = nowYear / 10 * 10
        //添加当前年代的年份
        for (i in nowYear downTo years) {
            yearsList.add(Pair(i.toString(), i.toString()))
        }
        //添加历史年代,1980开始
        for (i in years downTo 1980 step 10) {
            yearsList.add(Pair("${i}年代", "$i,${i + 9}"))
        }
        //更早年代
        yearsList.add(Pair("更早", "1900,1979"))
        return@run yearsList.toTypedArray()
    }

    val checkType = MutableLiveData<Int>()
    val checkCountry = MutableLiveData<Int>()
    val checkYear = MutableLiveData<Int>()


    val tagsText = MediatorLiveData<String>().apply {
        val onChange = { _: Int? ->
            val typeText = "类型:" + TYPES[checkType.value!!].first
            val countryText = "国家:" + COUNTRYS[checkCountry.value!!].first
            val yearText = "年份:" + YEARS[checkYear.value!!].first
            value = TextUtils.join(" ", arrayOf(typeText, countryText, yearText))
        }
        addSource(checkType, onChange)
        addSource(checkCountry, onChange)
        addSource(checkYear, onChange)
    }

    val toggleFilterCmd = MutableLiveData<Boolean>()

    var pageLoader = PageLoader { page ->
        getRequest(page)
    }

    init {
        checkType.value = 0
        checkYear.value = 0
        checkCountry.value = 0
        toggleFilterCmd.value = false
        tagsText.value = "全部"
    }

    fun toggleFilterWindow() {
        toggleFilterCmd.value = !toggleFilterCmd.value!!
    }

    fun closeFilterWindow() {
        toggleFilterCmd.value = false
    }

    private fun getRequest(page: Int): Observable<PageData<VideoItem>> {
        val typeParam = TYPES[checkType.value!!].second
        val countryParam = COUNTRYS[checkCountry.value!!].second
        val yearParam = YEARS[checkYear.value!!].second
        return Repo.getAll(typeParam, countryParam, yearParam, page).compose(addDisposable())
    }

}