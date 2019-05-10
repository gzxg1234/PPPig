package com.sanron.pigplayer

import android.annotation.SuppressLint
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.media.AudioManager
import android.os.Handler
import android.os.Message
import android.text.format.Formatter
import android.util.AttributeSet
import android.view.*
import android.widget.*
import tv.danmaku.ijk.media.player.IMediaPlayer
import tv.danmaku.ijk.media.player.IjkMediaPlayer
import java.io.IOException


/**
 * Author:sanron
 * Time:2019/5/8
 * Description:
 */
class PigPlayer : FrameLayout, IMediaPlayer.OnPreparedListener, IMediaPlayer.OnCompletionListener, IMediaPlayer.OnSeekCompleteListener, IMediaPlayer.OnErrorListener, IMediaPlayer.OnInfoListener, IMediaPlayer.OnBufferingUpdateListener, AudioManager.OnAudioFocusChangeListener {


    companion object {
        const val TAG: String = "PigPlayer"

        //同步播放进度
        const val SYNC_PROGRESS = 1
        //同步下载速度
        const val SYNC_TCP_SPEED = 2
        //延迟隐藏控制栏
        const val HIDE_CONTROL = 3

        //延迟隐藏控制栏时间
        const val HIDE_CONTROL_DELAY_TIME = 5000L
        const val RENDER_TEXTURE = 1
        const val RENDER_SURFACE = 2
    }

    private lateinit var playRoot: View
    private lateinit var topBar: View
    private lateinit var back: View
    private lateinit var title: TextView
    private lateinit var bufferingView: View
    private lateinit var bufferingText: TextView
    private lateinit var bottomController: View
    private lateinit var togglePlay: ImageView
    private lateinit var pbPosition: SeekBar
    private lateinit var toggleFullScreen: View
    private lateinit var renderView: RenderView
    private lateinit var renderViewContainer: ViewGroup
    private lateinit var positionStatus: TextView
    private lateinit var replayView: View
    private lateinit var tvReplay: TextView
    private lateinit var tvReplayReason: TextView
    private lateinit var volumeStatus: View
    private lateinit var pbVolume: ProgressBar
    private lateinit var brightnessStatus: View
    private lateinit var pbBrightness: ProgressBar

    var audioManager: AudioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private val mediaPlayer: IjkMediaPlayer by lazy {
        createPlayer()
    }

    private var surface: Surface? = null

    //当前播放状态
    private var currentState = PlayerConst.STATE_NORMAL

    //是否在准备中
    private var preparing = false

    //播放信息
    private var dataSource: DataSource? = null
    //是否全屏
    var isFullScreen = false
    //延迟准备到surface可用
    private var prepareWhenSurfaceAvailable = false
    //准备后暂停
    private var pauseAfterPrepare = false
    //准备后开始
    private var startAfterPrepare = false
    //准备后定位
    private var seekPositionAfterPrepare = -1L
    //是否在缓冲
    var buffering = false
    //缓冲进度百分比
    var bufferPercent = 0
    //手势
    val gestureDetector = GestureDetector(context, GestureListener())
    //是否正在滑动定位
    var scrollToAdjustPosition = false
    //是否正在滑动调节音量
    var scrollToAdjustVolume = false
    //是否正在滑动调节亮度
    var scrollToAdjustScreenBrightness = false

    //滑动定位操作完成后立即隐藏control
    private var hideControllerAfterAdjustPosition = false
    //控制布局可见性
    private var controllerVisible = false

    private val activity = context as Activity
    //保存activity原始方向
    private var activityOriginOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    //保存activityWindow原始flag
    private var windowOriginFlags: Int = 0
    //网络是否变化
    private var netWorkChange = false

    private val netWorkChangeListener = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            netWorkChange = true
        }
    }

    private val onSeekBarChangeListener = object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            val pos = 1f * getDuration() * seekBar!!.progress / seekBar.max
            positionStatus.text = generateTime(pos.toLong()) + "/" + generateTime(getDuration())
        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) {
            setSyncProgress(false)
        }

        override fun onStopTrackingTouch(seekBar: SeekBar?) {
            if (isPlaybackState()) {
                setSyncProgress(true)
            }
            seekTo((1f * getDuration() * seekBar!!.progress / seekBar.max).toLong())
        }
    }


    val taskHandler: Handler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)
            when (msg?.what) {
                SYNC_PROGRESS -> {
                    syncProgress()
                    sendEmptyMessageDelayed(SYNC_PROGRESS, 1000)
                }
                SYNC_TCP_SPEED -> {
                    syncTcpSpeed()
                    sendEmptyMessageDelayed(SYNC_TCP_SPEED, 500)
                }
                HIDE_CONTROL -> {
                    setControllerVisible(false)
                }
            }
        }
    }

    var onPrepareListener: ((IMediaPlayer?) -> Unit)? = null
    var onCompletionListener: ((IMediaPlayer?) -> Unit)? = null
    var onErrorListener: ((IMediaPlayer?, Int, Int) -> Unit)? = null
    var onInfoListener: ((IMediaPlayer?, Int, Int) -> Unit)? = null
    var onBufferUpdateListener: ((IMediaPlayer?, Int) -> Unit)? = null
    var onSeekCompleteListener: ((IMediaPlayer?) -> Unit)? = null

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    @SuppressLint("ClickableViewAccessibility")
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        View.inflate(context, R.layout.pig_player, this)
        initView()
        initListener()
        setRender(RENDER_TEXTURE)
    }

    /**
     * 设置渲染view
     */
    fun setRender(type: Int) {
        renderView = when (type) {
            RENDER_SURFACE -> {
                SurfaceRenderView(context)
            }
            RENDER_TEXTURE -> {
                TextureRenderView(context)
            }
            else -> {
                return
            }
        }
        renderView.bindMediaPlayer(mediaPlayer)
        renderViewContainer.removeAllViews()
        renderViewContainer.addView(renderView.getView(), ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT)
    }

    private fun initView() {
        playRoot = findViewById(R.id.root)
        topBar = findViewById(R.id.ll_top_bar)
        back = findViewById(R.id.iv_back)
        title = findViewById(R.id.tv_title)
        bufferingView = findViewById(R.id.ll_loading)
        bufferingText = findViewById(R.id.tv_loading_percent)
        bottomController = findViewById(R.id.bottom_control)
        togglePlay = findViewById(R.id.iv_toggle_play)
        pbPosition = findViewById(R.id.play_seek)
        toggleFullScreen = findViewById(R.id.iv_fullscreen)
        renderViewContainer = findViewById(R.id.render_view_container)
        positionStatus = findViewById(R.id.tv_progress)
        replayView = findViewById(R.id.ll_replay)
        tvReplay = findViewById(R.id.tv_replay)
        tvReplayReason = findViewById(R.id.tv_state)
        volumeStatus = findViewById(R.id.ll_volume_adjust)
        pbVolume = findViewById(R.id.pb_volume)
        brightnessStatus = findViewById(R.id.ll_bright_adjust)
        pbBrightness = findViewById(R.id.pb_brightness)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initListener() {
        togglePlay.setOnClickListener {
            if (currentState == PlayerConst.STATE_PLAYING) {
                pause()
            } else if (currentState == PlayerConst.STATE_PAUSE) {
                start()
            } else if (currentState == PlayerConst.STATE_COMPLETE) {
                seekTo(0)
                start()
            }
        }
        pbPosition.setOnTouchListener { v, event ->
            return@setOnTouchListener !isPlaybackState()
        }
        tvReplay.setOnClickListener {
            if (currentState == PlayerConst.STATE_COMPLETE) {
                replay()
            } else if (currentState == PlayerConst.STATE_ERROR) {
                retryPlay()
            }
        }
        toggleFullScreen.setOnClickListener {
            if (isFullScreen) {
                exitFullScreen()
            } else {
                enterFullScreen()
            }
        }
        back.setOnClickListener {
            exitFullScreen()
        }
        pbPosition.setOnSeekBarChangeListener(onSeekBarChangeListener)
        playRoot.setOnTouchListener { v, event ->
            var handled = gestureDetector.onTouchEvent(event)
            when (event!!.action) {
                MotionEvent.ACTION_UP -> {
                    if (scrollToAdjustPosition) {
                        //滑动定位，手指拿起开始seek
                        onSeekBarChangeListener.onStopTrackingTouch(pbPosition)
                        scrollToAdjustPosition = false
                        handled = true

                        //结束动作后，隐藏控制布局
                        if (hideControllerAfterAdjustPosition) {
                            setControllerVisible(false)
                            hideControllerAfterAdjustPosition = false
                        } else {
                            taskHandler.sendEmptyMessageDelayed(HIDE_CONTROL, HIDE_CONTROL_DELAY_TIME)
                        }
                    } else if (scrollToAdjustVolume) {
                        volumeStatus.visibility = View.GONE
                    } else if (scrollToAdjustScreenBrightness) {
                        brightnessStatus.visibility = View.GONE
                    }
                    scrollToAdjustPosition = false
                    scrollToAdjustVolume = false
                    scrollToAdjustScreenBrightness = false
                }
            }
            return@setOnTouchListener handled
        }
    }

    private fun adjustMediaVolume(raise: Boolean) {
        audioManager.adjustStreamVolume(
                AudioManager.STREAM_MUSIC,
                if (raise) AudioManager.ADJUST_RAISE else AudioManager.ADJUST_LOWER,
                AudioManager.FLAG_PLAY_SOUND)

        val volume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        pbVolume.max = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        pbVolume.progress = volume
    }

    private fun adjustScreenBrightness(v: Float) {
        val attr = activity.window.attributes
        if (attr.screenBrightness == WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE) {
            attr.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL
        }
        attr.screenBrightness = (attr.screenBrightness + v).limit(0f, 1f)
        activity.window.attributes = attr
        pbBrightness.progress = (attr.screenBrightness * 100).toInt()
    }

    private fun setSyncProgress(start: Boolean) {
        if (start) {
            taskHandler.sendEmptyMessage(SYNC_PROGRESS)
        } else {
            taskHandler.removeMessages(SYNC_PROGRESS)
        }
    }

    private fun syncProgress() {
        pbPosition.progress = (1f * getCurrentPosition() / getDuration() * 10000).toInt()
        pbPosition.secondaryProgress = (1f * bufferPercent / getDuration() * 10000).toInt()
        positionStatus.text = generateTime(getCurrentPosition()) + "/" + generateTime(getDuration())
    }


    private fun generateTime(time: Long): String {
        val totalSeconds = (time / 1000).toInt()
        val seconds = totalSeconds % 60
        val minutes = totalSeconds / 60 % 60
        val hours = totalSeconds / 3600
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }

    private fun setSyncTcpSpeed(start: Boolean) {
        if (start) {
            taskHandler.sendEmptyMessage(SYNC_TCP_SPEED)
        } else {
            taskHandler.removeMessages(SYNC_TCP_SPEED)
        }
    }

    private fun syncTcpSpeed() {
        bufferingText.text = "正在缓冲(${Formatter.formatFileSize(context, mediaPlayer.tcpSpeed)})"
    }

    fun release() {
        reset()
        abandonAudioFocus()
        surface?.release()
        mediaPlayer.release()
    }

    private fun retryPlay() {
        dataSource?.let {
            replayView.visibility = View.GONE
            setDataSource(dataSource!!)
            prepare()
            start()
        }
    }

    private fun replay() {
        dataSource?.let {
            replayView.visibility = View.GONE
            seekTo(0)
            setSyncProgress(true)
            keepScreenOn = true
            start()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    fun reset() {
        mediaPlayer.reset()
        renderView.clear()
        currentState = PlayerConst.STATE_NORMAL
        prepareWhenSurfaceAvailable = false
        pauseAfterPrepare = false
        startAfterPrepare = false
        pbPosition.progress = 0
        seekPositionAfterPrepare = -1
        positionStatus.text = "00:00/00:00"
        setSyncProgress(false)
        setSyncTcpSpeed(false)
    }

    fun setDataSource(dataSource: DataSource) {
        this.dataSource = dataSource
        try {
            reset()
            if (dataSource.header == null) {
                mediaPlayer.setDataSource(dataSource.url)
            } else {
                mediaPlayer.setDataSource(dataSource.url, dataSource.header)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun setTitle(text: String) {
        title.text = text
    }

    /**
     * 开始准备视频
     */
    fun prepare() {
        preparing = true
        mediaPlayer.setSurface(renderView.getSurface())
        mediaPlayer.prepareAsync()
        currentState = PlayerConst.STATE_PREPARING
        stateChange(currentState)
    }

    fun start() {
        if (preparing) {
            togglePlay.setImageResource(R.drawable.ic_play_arrow_white_24dp)
            startAfterPrepare = true
            return
        }
        if (!isPlaybackState()) {
            return
        }
        try {
            mediaPlayer.start()
            requestAudioFocus()
            currentState = PlayerConst.STATE_PLAYING
            stateChange(currentState)
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }
    }

    fun pause() {
        if (preparing) {
            togglePlay.setImageResource(R.drawable.ic_pause_white_24dp)
            pauseAfterPrepare = true
            return
        }
        if (!isPlaybackState()) {
            return
        }
        try {
            mediaPlayer.pause()
            currentState = PlayerConst.STATE_PAUSE
            stateChange(currentState)
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }
    }

    fun seekTo(pos: Long) {
        val position = pos.limit(max = getDuration() - 1)
        if (currentState == PlayerConst.STATE_PREPARING) {
            seekPositionAfterPrepare = position
        } else if (isPlaybackState()) {
            mediaPlayer.seekTo(position)
        }
    }

    fun getDuration(): Long {
        if (isPlaybackState()) {
            return mediaPlayer.duration
        }
        return 0
    }

    fun isPlaybackState(): Boolean {
        return currentState != PlayerConst.STATE_ERROR
                && currentState != PlayerConst.STATE_NORMAL
    }

    fun getCurrentPosition(): Long {
        if (isPlaybackState()) {
            return mediaPlayer.currentPosition
        }
        return 0
    }

    /**
     * 处理后退键
     */
    fun onBackPressed(): Boolean {
        if (isFullScreen) {
            exitFullScreen()
            return true
        }
        return false
    }

    override fun onPrepared(player: IMediaPlayer?) {
        CLog.d(TAG, "onPrepared")
        preparing = false
        bufferingView.visibility = View.GONE
        if (pauseAfterPrepare) {
            pause()
        } else if (startAfterPrepare) {
            start()
        }
        if (seekPositionAfterPrepare > -1) {
            seekTo(seekPositionAfterPrepare)
            seekPositionAfterPrepare = -1
        }
        setSyncProgress(true)
        onPrepareListener?.invoke(player)
    }

    override fun onSeekComplete(player: IMediaPlayer?) {
        CLog.d(TAG, "onSeekComplete")
        onSeekCompleteListener?.invoke(player)
    }

    override fun onError(player: IMediaPlayer?, what: Int, extra: Int): Boolean {
        CLog.d(TAG, "onError,what:$what，extra:$extra")
        abandonAudioFocus()
        currentState = PlayerConst.STATE_ERROR
        stateChange(currentState)
        onErrorListener?.invoke(player, what, extra)
        return true
    }

    override fun onCompletion(player: IMediaPlayer?) {
        CLog.d(TAG, "onComplete")
        abandonAudioFocus()
        currentState = PlayerConst.STATE_COMPLETE
        stateChange(currentState)
        if (netWorkChange) {
            //网络变化导致complet回调

        }

        onCompletionListener?.invoke(player)
    }

    override fun onBufferingUpdate(player: IMediaPlayer?, percent: Int) {
        CLog.d(TAG, "onBufferUpdate,percent:$percent")
        bufferPercent = percent
        onBufferUpdateListener?.invoke(player, percent)
    }

    override fun onInfo(player: IMediaPlayer?, what: Int, extra: Int): Boolean {
        CLog.d(TAG, "onInfo,what:$what，extra:$extra")
        onInfo(what, extra)
        onInfoListener?.invoke(player, what, extra)
        return false
    }

    private fun createPlayer(): IjkMediaPlayer {
        return IjkMediaPlayer().apply {
            //准备好后不自动播放
            setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "start-on-prepared", 0)
            //播放重连次数
            setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "reconnect", 5)
            //SeekTo设置优化某些视频在SeekTo的时候，会跳回到拖动前的位置
            setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "enable-accurate-seek", 1)
            //解决m3u8文件拖动问题 比如:一个3个多少小时的音频文件，开始播放几秒中，然后拖动到2小时左右的时间，要loading 10分钟
            setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "fflags", "fastseek")
            setAudioStreamType(AudioManager.STREAM_MUSIC)
            setOnPreparedListener(this@PigPlayer)
            setOnCompletionListener(this@PigPlayer)
            setOnSeekCompleteListener(this@PigPlayer)
            setOnErrorListener(this@PigPlayer)
            setOnInfoListener(this@PigPlayer)
            setOnBufferingUpdateListener(this@PigPlayer)
            setScreenOnWhilePlaying(true)
        }
    }

    fun onConfigChange() {

    }

    /**
     * 请求音频焦点
     */
    @Suppress("DEPRECATION")
    fun requestAudioFocus() {
        audioManager.requestAudioFocus(this,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN_TRANSIENT)
    }

    /**
     * 释放音频焦点
     */
    fun abandonAudioFocus() {
        audioManager.abandonAudioFocus(this)
    }

    override fun onAudioFocusChange(focusChange: Int) {
        when (focusChange) {
            AudioManager.AUDIOFOCUS_LOSS
                    or AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                CLog.d(TAG, "失去音频焦点")
                if (currentState == PlayerConst.STATE_PLAYING) {
                    CLog.d(TAG, "失去音频焦点，暂停播放")
                    pause()
                }
            }
            AudioManager.AUDIOFOCUS_GAIN -> {
                CLog.d(TAG, "获得音频焦点")
                mediaPlayer.setVolume(1f, 1f)
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                CLog.d(TAG, "失去音频焦点，需要降低音量")
                mediaPlayer.setVolume(0.1f, 0.1f)
            }
        }
    }

    private var restartOnResume = false

    fun onResume() {
        if (restartOnResume) {
            start()
            restartOnResume = false
        }
    }

    fun onPause() {
        if (currentState == PlayerConst.STATE_PLAYING) {
            //暂停播放
            pause()
            restartOnResume = true
        }
    }

    private fun enterFullScreen() {
        setControllerVisible(false)
        isFullScreen = true
        windowOriginFlags = activity.window.attributes.flags
        activityOriginOrientation = activity.requestedOrientation

        activity.window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        activity.window.attributes = activity.window.attributes
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        (playRoot.parent as ViewGroup).removeView(playRoot)
        (activity.window.decorView as ViewGroup).addView(playRoot)
    }

    private fun exitFullScreen() {
        setControllerVisible(false)
        isFullScreen = false
        activity.window.attributes.flags = windowOriginFlags
        activity.window.attributes = activity.window.attributes
        activity.requestedOrientation = activityOriginOrientation
        (playRoot.parent as ViewGroup).removeView(playRoot)
        addView(playRoot)
    }

    inner class GestureListener : GestureDetector.SimpleOnGestureListener() {

        override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
            setControllerVisible(!controllerVisible)
            if (controllerVisible) {
                //延时隐藏control
                taskHandler.removeMessages(HIDE_CONTROL)
                taskHandler.sendEmptyMessageDelayed(HIDE_CONTROL, HIDE_CONTROL_DELAY_TIME)
            }
            return true
        }

        override fun onDoubleTap(e: MotionEvent?): Boolean {
            if (currentState == PlayerConst.STATE_PAUSE) {
                start()
            } else if (currentState == PlayerConst.STATE_PLAYING) {
                pause()
            } else {
                return false
            }
            return true
        }

        override fun onDown(e: MotionEvent?): Boolean {
            return true
        }

        var dYCount = 0f

        override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
            val downX: Float = e1?.x ?: 0f
            dYCount += distanceY
            if (scrollToAdjustPosition) {
                val p = -1f * distanceX / playRoot.width
                val newProgress = (pbPosition.progress + pbPosition.max * p).toInt().limit(0, pbPosition.max)
                pbPosition.progress = newProgress
                return true
            } else if (scrollToAdjustScreenBrightness) {
                //10像素减少1%的亮度
                val p = distanceY / 1000
                adjustScreenBrightness(p)
            } else if (scrollToAdjustVolume) {
                //滑动高度的5%减少一级音量
                if (Math.abs(dYCount) > playRoot.height * 0.05f) {
                    adjustMediaVolume(dYCount > 0)
                    dYCount = 0f
                }
            } else {
                if (Math.abs(distanceX) > Math.abs(distanceY)) {
                    //横向滑动定位
                    scrollToAdjustPosition = true
                    onSeekBarChangeListener.onStartTrackingTouch(pbPosition)

                    //操作中，显示control布局
                    taskHandler.removeMessages(HIDE_CONTROL)
                    //操作时，如果当前control已经是可见的，则操作完成延时隐藏control，否则立即隐藏
                    hideControllerAfterAdjustPosition = !controllerVisible
                    setControllerVisible(true)
                } else {
                    dYCount = 0f
                    if (downX <= playRoot.width / 2f) {
                        scrollToAdjustScreenBrightness = true
                        scrollToAdjustVolume = false
                        brightnessStatus.visibility = View.VISIBLE
                        volumeStatus.visibility = View.GONE
                    } else {
                        scrollToAdjustScreenBrightness = false
                        scrollToAdjustVolume = true
                        brightnessStatus.visibility = View.GONE
                        volumeStatus.visibility = View.VISIBLE
                    }
                }
            }
            return super.onScroll(e1, e2, distanceX, distanceY)
        }
    }

    private fun setControllerVisible(visible: Boolean) {
        controllerVisible = visible
        if (visible) {
            bottomController.visibility = View.VISIBLE
            if (isFullScreen) {
                topBar.visibility = View.VISIBLE
            }
        } else {
            bottomController.visibility = View.GONE
            if (isFullScreen) {
                topBar.visibility = View.GONE
            }
        }
    }

    private fun onInfo(what: Int, extra: Int) {
        when (what) {
            PlayerConst.MEDIA_INFO_BUFFERING_START -> {
                bufferStateChange(true)
            }
            PlayerConst.MEDIA_INFO_BUFFERING_END -> {
                bufferStateChange(false)
            }
            PlayerConst.MEDIA_INFO_VIDEO_ROTATION_CHANGED -> {
                renderView.setRotation(extra.toFloat())
            }
        }
    }

    /**
     * 缓存状态改变
     */
    fun bufferStateChange(buffering: Boolean) {
        CLog.d(TAG, if (buffering) "buffer start" else "bufferend")
        this.buffering = buffering
        setSyncTcpSpeed(buffering)
        bufferingView.visibility = if (buffering) View.VISIBLE else View.GONE
    }

    /**
     * 播放状态改变
     */
    private fun stateChange(what: Int) {
        when (what) {
            PlayerConst.STATE_PAUSE -> {
                togglePlay.setImageResource(R.drawable.ic_play_arrow_white_24dp)
                replayView.visibility = View.GONE
                setSyncProgress(false)
            }
            PlayerConst.STATE_PLAYING -> {
                togglePlay.setImageResource(R.drawable.ic_pause_white_24dp)
                replayView.visibility = View.GONE
            }
            PlayerConst.STATE_PREPARING -> {
                bufferingView.visibility = View.VISIBLE
                replayView.visibility = View.GONE
                keepScreenOn = true
            }
            PlayerConst.STATE_COMPLETE -> {
                togglePlay.setImageResource(R.drawable.ic_play_arrow_white_24dp)
                bufferingView.visibility = View.GONE
                replayView.visibility = View.VISIBLE
                tvReplayReason.text = "播放完毕"
                setSyncProgress(false)
                setSyncTcpSpeed(false)
                keepScreenOn = false
            }
            PlayerConst.STATE_ERROR -> {
                togglePlay.setImageResource(R.drawable.ic_play_arrow_white_24dp)
                bufferingView.visibility = View.GONE
                replayView.visibility = View.VISIBLE
                tvReplayReason.text = "播放出错"
                setSyncProgress(false)
                setSyncTcpSpeed(false)
                keepScreenOn = false
            }
        }
    }
}
