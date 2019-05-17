package com.sanron.pppig.module.live

import android.app.Application
import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import com.sanron.datafetch_interface.live.LiveDataFetch
import com.sanron.datafetch_interface.live.bean.LiveCat
import com.sanron.datafetch_interface.live.bean.LiveItem
import com.sanron.datafetch_interface.video.bean.PlayLine
import com.sanron.pppig.base.BaseObserver
import com.sanron.pppig.base.BaseViewModel
import com.sanron.pppig.base.state.State
import com.sanron.pppig.common.MsgFactory
import com.sanron.pppig.data.FetchManager
import com.sanron.pppig.util.SingleLiveEvent
import com.sanron.pppig.util.main
import io.reactivex.disposables.Disposable

/**
 * Author:sanron
 * Time:2019/5/16
 * Description:
 */
class LiveListVM(application: Application) : BaseViewModel(application) {

    var liveSourceId = ""
    val catList = MutableLiveData<List<LiveCat>>()
    val currentCatPos = MutableLiveData<Int>()
    val catLoadingState = MutableLiveData<Int>()

    val itemList = MutableLiveData<List<LiveItem>>()
    val itemLoadingState = MutableLiveData<Int>()
    val toPlayPage = SingleLiveEvent<Pair<LiveItem, List<PlayLine>>>()

    lateinit var fetch: LiveDataFetch

    fun init(args: Bundle) {
        liveSourceId = args.getString("sourceId", "")
        fetch = FetchManager.sourceManager.getLiveSourceList().find {
            return@find it.id == liveSourceId
        }?.dataFetch!!
    }

    fun setCurrentCatPos(pos: Int) {
        currentCatPos.value = pos
        loadItemList()
    }

    fun loadCatList() {
        fetch.getLiveCats()
                .main()
                .compose(autoDispose())
                .subscribe(object : BaseObserver<List<LiveCat>>() {
                    override fun onSubscribe(d: Disposable) {
                        super.onSubscribe(d)
                        catLoadingState.value = State.LOADING
                    }

                    override fun onNext(t: List<LiveCat>) {
                        super.onNext(t)
                        catList.value = t
                        setCurrentCatPos(0)
                        catLoadingState.value = State.SUCCESS
                    }

                    override fun onError(e: Throwable) {
                        super.onError(e)
                        catLoadingState.value = State.ERROR
                        toastMsg.value = MsgFactory.get(e)
                    }
                })
    }


    fun onClickItem(pos: Int) {
        itemList.value!![pos].let {
            fetch.getPlayLineList(it)
                    .main()
                    .compose(withLoading())
                    .compose(autoDispose("getPlayLine"))
                    .subscribe(object : BaseObserver<List<PlayLine>>() {
                        override fun onSubscribe(d: Disposable) {
                            super.onSubscribe(d)
                        }

                        override fun onNext(t: List<PlayLine>) {
                            super.onNext(t)
                            if (t.isEmpty()) {
                                toastMsg.value = "没有可播放的资源"
                                return
                            }
                            toPlayPage.value = Pair(it, t)
                        }

                        override fun onError(e: Throwable) {
                            super.onError(e)
                            toastMsg.value = MsgFactory.get(e)
                        }
                    })

        }
    }

    fun loadItemList() {
        currentCatPos.value?.let { pos ->
            catList.value?.let { catList ->
                fetch.getCatItems(catList[pos])
                        .main()
                        .compose(autoDispose("loadItemList"))
                        .subscribe(object : BaseObserver<List<LiveItem>>() {
                            override fun onSubscribe(d: Disposable) {
                                super.onSubscribe(d)
                                itemLoadingState.value = State.LOADING
                            }

                            override fun onNext(t: List<LiveItem>) {
                                super.onNext(t)
                                itemList.value = t
                                itemLoadingState.value = State.SUCCESS
                            }

                            override fun onError(e: Throwable) {
                                super.onError(e)
                                itemLoadingState.value = State.ERROR
                                toastMsg.value = MsgFactory.get(e)
                            }
                        })
            }
        }
    }
}
