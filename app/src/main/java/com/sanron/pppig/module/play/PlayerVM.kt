package com.sanron.pppig.module.play

import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.lifecycle.MutableLiveData
import com.sanron.datafetch_interface.live.LiveSource
import com.sanron.datafetch_interface.video.VideoSource
import com.sanron.datafetch_interface.video.bean.PlayLine
import com.sanron.pppig.base.BaseObserver
import com.sanron.pppig.base.BaseViewModel
import com.sanron.pppig.common.MsgFactory
import com.sanron.pppig.data.AppPref
import com.sanron.pppig.data.FetchManager
import com.sanron.pppig.util.SingleLiveEvent
import com.sanron.pppig.util.main
import io.reactivex.Observable


/**
 * Author:sanron
 * Time:2019/5/8
 * Description:
 */
class PlayerVM(application: Application) : BaseViewModel(application) {


    var videoName = ""

    val autoNext = MutableLiveData<Boolean>()

    val title = MutableLiveData<String>()

    val playLineList = MutableLiveData<List<PlayLine>>()

    //用户当前查看的线路pos
    val currentTab = MutableLiveData<Int>()

    //用户当前播放源对应的线路pos
    val currentPlayTab = MutableLiveData<Int>()

    //用户当前播放源pos
    val currentItemPos = MutableLiveData<Int>()

    //用户当前播放源list
    val currentItemList = MutableLiveData<List<PlayLine.Item>>()

    //用户当前播放源
    val currentItem = MutableLiveData<PlayLine.Item>()

    val loading = MutableLiveData<Boolean>()

    val videoSourceList = MutableLiveData<List<String>>()

    //列表逆序事件
    val onItemsReverse = SingleLiveEvent<Int>()

    //视频源地址缓存
    val videoUrlCache = mutableMapOf<PlayLine.Item, List<String>>()

    var playType = -1

    lateinit var videoSource: VideoSource
    lateinit var liveSource: LiveSource

    init {
        autoNext.value = AppPref.autoPlayNext
    }

    fun initIntent(intent: Intent?) {
        videoName = intent?.getStringExtra(PlayerAct.ARG_TITLE) ?: ""
        title.value = videoName
        playLineList.value = intent?.getSerializableExtra(PlayerAct.ARG_PLAY_LINES) as List<PlayLine>
        currentTab.value = intent.getIntExtra(PlayerAct.ARG_SOURCE_POS, 0)
        currentPlayTab.value = currentTab.value
        currentItemList.value = playLineList.value?.get(currentPlayTab.value!!)?.items
        currentItemPos.value = intent.getIntExtra(PlayerAct.ARG_ITEM_POS, 0)
        currentItem.value = playLineList.value?.get(currentPlayTab.value!!)?.items?.get(currentItemPos.value!!)
        playType = intent.getIntExtra(PlayerAct.ARG_PLAY_TYPE, PlayerAct.TYPE_VIDEO)
        if (playType == PlayerAct.TYPE_VIDEO) {
            videoSource = FetchManager.getVideoSourceById(intent.getStringExtra(PlayerAct.ARG_SOURCE_ID)
                    ?: "")!!
        } else {
            liveSource = FetchManager.getLiveSourceById(intent.getStringExtra(PlayerAct.ARG_SOURCE_ID)
                    ?: "")!!
        }
    }

    fun changePlayItem(itemPos: Int) {
        changePlayItem(currentPlayTab.value!!, itemPos)
    }

    fun changePlayItem(sourcePos: Int, itemPos: Int) {
        currentPlayTab.value = sourcePos
        currentItemPos.value = itemPos
        currentItemList.value = playLineList.value?.get(currentPlayTab.value!!)?.items
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
            currentItemList.value!![currentItemPos.value!! + 1].let { nextItem ->
                videoUrlCache[nextItem]?.let {
                    return
                }

                getPlaySourceUrl(nextItem)
                        .compose(autoDispose())
                        .subscribe(object : BaseObserver<List<String>>() {
                            override fun onNext(t: List<String>) {
                                super.onNext(t)
                                videoUrlCache[nextItem] = t
                            }
                        })
            }
        }
    }

    private fun getPlaySourceUrl(item: PlayLine.Item): Observable<List<String>> {
        return if (playType == PlayerAct.TYPE_LIVE) {
            liveSource.dataFetch.getLiveSourceUrl(item)
        } else {
            videoSource.dataFetch.getVideoSource(item)
        }
    }

    fun startPlayCurrent() {
        currentItem.value?.let { currentItem ->
            //先从缓存取
            videoUrlCache[currentItem]?.let {
                videoSourceList.value = it
                return
            }

            getPlaySourceUrl(currentItem)
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
                                videoUrlCache[currentItem] = t
                            }
                        }

                        override fun onError(e: Throwable) {
                            super.onError(e)
                            toastMsg.value = MsgFactory.get(e)
                        }
                    })
        }

    }

    fun setAutoNext(b: Boolean) {
        autoNext.value = b
    }

    fun reverseList() {
        playLineList.value?.let { playSourceList ->
            currentTab.value?.let { currentTab ->
                currentPlayTab.value?.let { currentPlayTab ->
                    playSourceList[currentPlayTab].items?.let {
                        currentItemPos.value?.let { currentItemPos ->
                            playSourceList[currentTab].items?.let {
                                if (it.size > 1) {
                                    it.reverse()
                                    onItemsReverse.value = currentTab
                                    if (currentTab == currentPlayTab) {
                                        this@PlayerVM.currentItemPos.value = it.size - currentItemPos - 1
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    fun copyUrl() {
        currentItem.value?.let { currentItem ->
            //先从缓存取
            videoUrlCache[currentItem]?.let {
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
