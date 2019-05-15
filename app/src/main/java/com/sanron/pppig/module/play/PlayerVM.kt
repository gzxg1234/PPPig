package com.sanron.pppig.module.play

import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.lifecycle.MutableLiveData
import com.sanron.datafetch_interface.Source
import com.sanron.datafetch_interface.bean.PlaySource
import com.sanron.pppig.base.BaseObserver
import com.sanron.pppig.base.BaseViewModel
import com.sanron.pppig.common.MsgFactory
import com.sanron.pppig.data.AppPref
import com.sanron.pppig.data.FetchManager
import com.sanron.pppig.util.SingleLiveEvent
import com.sanron.pppig.util.main


/**
 * Author:sanron
 * Time:2019/5/8
 * Description:
 */
class PlayerVM(application: Application) : BaseViewModel(application) {


    var videoName = ""

    var autoNext = MutableLiveData<Boolean>()

    var title = MutableLiveData<String>()

    var currentSourcePos = MutableLiveData<Int>()

    var currentItemPos = MutableLiveData<Int>()

    var currentItemList = MutableLiveData<List<PlaySource.Item>>()

    var currentItem = MutableLiveData<PlaySource.Item>()

    var loading = MutableLiveData<Boolean>()

    var playSourceList = MutableLiveData<List<PlaySource>>()

    var videoSourceList = MutableLiveData<List<String>>()

    //列表逆序事件
    var onItemsReverse = SingleLiveEvent<Int>()

    //视频源地址缓存
    var videoUrlCache = mutableMapOf<String, List<String>>()

    lateinit var source: Source

    init {
        autoNext.value = AppPref.autoPlayNext
    }

    fun initIntent(intent: Intent?) {
        videoName = intent?.getStringExtra(PlayerAct.ARG_TITLE) ?: ""
        title.value = videoName
        playSourceList.value = intent?.getSerializableExtra(PlayerAct.ARG_PLAY_SOURCE) as List<PlaySource>
        currentSourcePos.value = intent.getIntExtra(PlayerAct.ARG_SOURCE_POS, 0)
        currentItemList.value = playSourceList.value?.get(currentSourcePos.value!!)?.items
        currentItemPos.value = intent.getIntExtra(PlayerAct.ARG_ITEM_POS, 0)
        currentItem.value = playSourceList.value?.get(currentSourcePos.value!!)?.items?.get(currentItemPos.value!!)
        source = FetchManager.getSourceById(intent.getStringExtra(PlayerAct.ARG_SOURCE_ID) ?: "")!!
    }

    fun changePlayItem(itemPos: Int) {
        changePlayItem(currentSourcePos.value!!, itemPos)
    }

    fun changePlayItem(sourcePos: Int, itemPos: Int) {
        currentSourcePos.value = sourcePos
        currentItemPos.value = itemPos
        currentItemList.value = playSourceList.value?.get(currentSourcePos.value!!)?.items
        currentItem.value = currentItemList.value?.get(itemPos)
        title.value = videoName + "-" + currentItem.value!!.name
        startPlayCurrent()
        preParseNextVideoUrl()
    }

    /**
     * 如果开启自动播放则预解析下一集的url
     */
    private fun preParseNextVideoUrl() {
        if (autoNext.value == true
                && currentItemPos.value != currentItemList.value!!.size - 1) {
            val nextItemLink = currentItemList.value!![currentItemPos.value!! + 1].link ?: ""
            videoUrlCache[nextItemLink]?.let {
                videoSourceList.value = it
                return
            }

            source.fetch.getVideoSource(nextItemLink)
                    .compose(autoDispose())
                    .subscribe(object : BaseObserver<List<String>>() {
                        override fun onNext(t: List<String>) {
                            super.onNext(t)
                            videoUrlCache[nextItemLink] = t
                        }
                    })
        }
    }

    fun startPlayCurrent() {
        val link = currentItem.value?.link ?: ""

        //先从缓存取
        videoUrlCache[link]?.let {
            videoSourceList.value = it
            return
        }

        source.fetch.getVideoSource(link)
                .main()
                .compose(autoDispose("getVideoSourceUrl"))
                .doOnSubscribe {
                    loading.value = true
                }
                .doFinally {
                    loading.value = false
                }
                .subscribe(object : BaseObserver<List<String>>() {
                    override fun onNext(t: List<String>) {
                        super.onNext(t)
                        videoSourceList.value = t
                        if (t.isNullOrEmpty()) {
                            toastMsg.value = "未解析到可用播放资源"
                        } else {
                            videoUrlCache[link] = t
                        }
                    }

                    override fun onError(e: Throwable) {
                        super.onError(e)
                        toastMsg.value = MsgFactory.get(e)
                    }
                })
    }

    fun setAutoNext(b: Boolean) {
        autoNext.value = b
    }

    fun reverseList() {
        playSourceList.value?.let { playSourceList ->
            currentSourcePos.value?.let { currentSourcePos ->
                playSourceList[currentSourcePos].items?.let {
                    currentItemPos.value?.let { currentItemPos ->
                        playSourceList[currentSourcePos].items?.let {
                            it.reverse()
                            this@PlayerVM.currentItemPos.value = it.size - currentItemPos - 1
                            onItemsReverse.value = currentSourcePos
                        }
                    }
                }
            }
        }
    }

    fun copyUrl() {
        //先从缓存取
        videoUrlCache[currentItem.value?.link ?: ""]?.let {
            if (!it.isNullOrEmpty()) {
                val clipManager = getApplication<Application>().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("视频源地址", it[0])
                clipManager.primaryClip = clip
                toastMsg.value = "已复制到粘贴板：${it[0]}"
                return
            }
        }
        toastMsg.value = "未获取到有效的视频源"
    }

    /**
     * 播放完毕
     */
    fun onPlayComplete() {
        if (autoNext.value == true
                && currentItemPos.value != currentItemList.value!!.size - 1) {
            changePlayItem(currentItemPos.value!! + 1)
        }
    }
}
