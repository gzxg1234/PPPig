package com.sanron.pppig.module.micaitu.movie

import android.app.Application
import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import com.sanron.pppig.R
import com.sanron.pppig.base.BaseObserver
import com.sanron.pppig.base.BaseViewModel
import com.sanron.pppig.data.Repo
import com.sanron.pppig.data.bean.micaitu.ListData
import com.sanron.pppig.data.bean.micaitu.VideoItem
import com.sanron.pppig.util.SingleLiveEvent
import com.sanron.pppig.util.main
import io.reactivex.Observable

/**
 *Author:sanron
 *Time:2019/4/16
 *Description:
 */
class MovieViewModel(application: Application) : BaseViewModel(application) {
    companion object {
        val ID_TYPE_MAP = mutableMapOf<Int, String>().apply {
            this[R.id.rb_type_all] = "movie"
            this[R.id.rb_xj] = "Comedy"
            this[R.id.rb_yg] = "yugaopian"
            this[R.id.rb_kh] = "Sciencefiction"
            this[R.id.rb_kb] = "Horror"
            this[R.id.rb_aq] = "Love"
            this[R.id.rb_zz] = "War"
            this[R.id.rb_jq] = "Drama"
        }
        val ID_COUNTRY_MAP = mutableMapOf<Int, String>().apply {
            this[R.id.rb_country_all] = ""
            this[R.id.rb_dl] = "大陆"
            this[R.id.rb_xg] = "香港"
            this[R.id.rb_tw] = "台湾"
            this[R.id.rb_mg] = "美国"
            this[R.id.rb_hg] = "韩国"
            this[R.id.rb_rb] = "日本"
            this[R.id.rb_tg] = "泰国"
            this[R.id.rb_xjp] = "新加坡"
            this[R.id.rb_mlxy] = "马来西亚"
            this[R.id.rb_yd] = "印度"
            this[R.id.rb_fg] = "法国"
            this[R.id.rb_jnd] = "加拿大"
        }
    }

    val refreshing = MutableLiveData<Boolean>()
    val data = MutableLiveData<MutableList<VideoItem>>()
    val hasMore = MutableLiveData<Boolean>()
    val loading = MutableLiveData<Boolean>()

    val checkType = MutableLiveData<Int>()
    val checkCountry = MutableLiveData<Int>()
    val checkYear = MutableLiveData<Int>()

    val type = Transformations.map(checkType) {
        ID_TYPE_MAP[it]
    }
    val country = Transformations.map(checkCountry) {
        ID_COUNTRY_MAP[it]
    }
    var year = ""
    var page = 0

    val tagsText = MediatorLiveData<String>()

    val toggleFilterCmd = SingleLiveEvent<Boolean>()

    init {
        loading.value = false
        data.value = mutableListOf()
        hasMore.value = false
        checkType.value = R.id.rb_type_all
        checkCountry.value = R.id.rb_country_all
        checkYear.value = R.id.rb_year_all
        toggleFilterCmd.value = false
    }

    fun toggleFilterWindow() {
        toggleFilterCmd.value = !toggleFilterCmd.value!!
    }

    fun closeFilterWindow() {
        toggleFilterCmd.value = false
    }

    fun getRequest(page: Int): Observable<ListData<VideoItem>> {
        return Repo.getAll(type.value, country.value, year, page)
    }

    fun loadData(refresh: Boolean) {
        val reqPage = if (refresh) 1 else page + 1
        getRequest(reqPage)
                .main()
                .subscribe(object : BaseObserver<ListData<VideoItem>>() {
                    override fun onNext(t: ListData<VideoItem>) {
                        super.onNext(t)
                        page = reqPage
                        data.value = data.value!!.apply {
                            if (refresh) {
                                clear()
                            }
                            addAll(t.data)
                        }
                        hasMore.value = t.hasMore
                    }

                    override fun onError(e: Throwable) {
                        super.onError(e)
                        refreshing.value = false
                        loading.value = false
                    }

                    override fun onComplete() {
                        super.onComplete()
                        refreshing.value = false
                        loading.value = false
                    }
                })

    }
}