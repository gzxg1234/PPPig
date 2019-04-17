package com.sanron.pppig.module.micaitu.movie

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import com.sanron.pppig.R
import com.sanron.pppig.base.BaseObserver
import com.sanron.pppig.base.BaseViewModel
import com.sanron.pppig.binding.CommonList
import com.sanron.pppig.data.Repo
import com.sanron.pppig.data.bean.micaitu.ListData
import com.sanron.pppig.data.bean.micaitu.VideoItem
import com.sanron.pppig.util.CLog
import com.sanron.pppig.util.SingleLiveEvent
import com.sanron.pppig.util.main
import io.reactivex.Observable
import io.reactivex.disposables.Disposable

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
            this[R.id.rb_dz] = "Action"
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
    val loadMoreState = MutableLiveData<Int>()

    val checkType = MutableLiveData<Int>()
    val checkCountry = MutableLiveData<Int>()
    val checkYear = MutableLiveData<Int>()

    var yearParam: String? = ""

    val tagsText = MutableLiveData<String>()

    val toggleFilterCmd = MutableLiveData<Boolean>()

    var page = 0

    var req: Disposable? = null

    init {
        data.value = mutableListOf()
        checkType.value = R.id.rb_type_all
        checkCountry.value = R.id.rb_country_all
        checkYear.value = R.id.rb_year_all
        toggleFilterCmd.value = false
        tagsText.value = "全部"
    }

    fun toggleFilterWindow() {
        toggleFilterCmd.value = !toggleFilterCmd.value!!
    }

    fun closeFilterWindow() {
        toggleFilterCmd.value = false
    }

    private fun getRequest(page: Int): Observable<ListData<VideoItem>> {
        val typeParam = ID_TYPE_MAP[checkType.value]
        val countryParam = ID_COUNTRY_MAP[checkCountry.value]
        return Repo.getAll(typeParam, countryParam, yearParam, page)
    }


    fun loadData(refresh: Boolean) {
        val reqPage = if (refresh) 1 else page + 1
        getRequest(reqPage)
                .main()
                .doOnSubscribe {
                    req?.dispose()
                    req = it
                }
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
                        if (t.hasMore) {
                            loadMoreState.value = CommonList.STATE_COMPLETE
                        } else {
                            loadMoreState.value = CommonList.STATE_END
                        }
                    }

                    override fun onError(e: Throwable) {
                        super.onError(e)
                        refreshing.value = false
                        loadMoreState.value = CommonList.STATE_FAIL
                    }

                    override fun onComplete() {
                        super.onComplete()
                        refreshing.value = false
                    }
                })

    }
}