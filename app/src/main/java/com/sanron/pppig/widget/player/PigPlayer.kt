package com.sanron.pppig.widget.player

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.Rect
import android.graphics.SurfaceTexture
import android.os.Handler
import android.os.Message
import android.text.format.Formatter
import android.util.AttributeSet
import android.view.*
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import com.sanron.pppig.R
import com.sanron.pppig.util.CLog
import com.sanron.pppig.util.limit
import tv.danmaku.ijk.media.player.IMediaPlayer
import tv.danmaku.ijk.media.player.IjkMediaPlayer
import java.io.IOException


/**
 * Author:sanron
 * Time:2019/5/8
 * Description:
 */
class PigPlayer : FrameLayout, IMediaPlayer.OnPreparedListener, IMediaPlayer.OnCompletionListener, IMediaPlayer.OnSeekCompleteListener, IMediaPlayer.OnErrorListener, IMediaPlayer.OnInfoListener, IMediaPlayer.OnBufferingUpdateListener {


    companion object {
        const val TAG: String = "IjkPlayerView"

        //同步播放进度
        const val SYNC_PROGRESS = 1
        //同步下载速度
        const val SYNC_TCP_SPEED = 2
        //延迟隐藏控制栏
        const val HIDE_CONTROL = 3
        //延迟隐藏控制栏时间
        val HIDE_CONTROL_DELAY_TIME = 5000L
    }

    private lateinit var root: View
    private lateinit var topBar: View
    private lateinit var back: View
    private lateinit var title: TextView
    private lateinit var loadingView: View
    private lateinit var loadingText: TextView
    private lateinit var bottomControl: View
    private lateinit var togglePlay: ImageView
    private lateinit var seekBar: SeekBar
    private lateinit var fullScreen: View
    private lateinit var textureView: TextureView
    private lateinit var progressInfo: TextView
    private lateinit var replayView: View
    private lateinit var replay: TextView
    private lateinit var stateInfo: TextView

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
    var scrollToSeek = false

    private val onSeekBarChangeListener = object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            val pos = 1f * getDuration() * seekBar!!.progress / seekBar.max
            progressInfo.text = generateTime(pos.toLong()) + "/" + generateTime(getDuration())
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
                    bottomControl.visibility = View.GONE
                    topBar.visibility = View.GONE
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
    }

    private fun initView() {
        root = findViewById(R.id.root)
        topBar = findViewById(R.id.ll_top_bar)
        back = findViewById(R.id.iv_back)
        title = findViewById(R.id.tv_title)
        loadingView = findViewById(R.id.ll_loading)
        loadingText = findViewById(R.id.tv_loading_percent)
        bottomControl = findViewById(R.id.bottom_control)
        togglePlay = findViewById(R.id.iv_toggle_play)
        seekBar = findViewById(R.id.play_seek)
        fullScreen = findViewById(R.id.iv_fullscreen)
        textureView = findViewById(R.id.texture_view)
        progressInfo = findViewById(R.id.tv_progress)
        replayView = findViewById(R.id.ll_replay)
        replay = findViewById(R.id.tv_replay)
        stateInfo = findViewById(R.id.tv_state)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initListener() {
        textureView.surfaceTextureListener = object : TextureView.SurfaceTextureListener {
            override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture?, width: Int, height: Int) {
            }

            override fun onSurfaceTextureUpdated(surface: SurfaceTexture?) {
            }

            override fun onSurfaceTextureDestroyed(surface: SurfaceTexture?): Boolean {
                this@PigPlayer.surface?.release()
                this@PigPlayer.surface = null
                return false
            }

            override fun onSurfaceTextureAvailable(surface: SurfaceTexture?, width: Int, height: Int) {
                this@PigPlayer.surface = Surface(surface)
                mediaPlayer.setSurface(this@PigPlayer.surface)
            }
        }

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
        replay.setOnClickListener {
            if (currentState == PlayerConst.STATE_COMPLETE
                    || currentState == PlayerConst.STATE_ERROR) {
                replay()
            }
        }
        seekBar.setOnTouchListener { v, event ->
            return@setOnTouchListener !isPlaybackState()
        }
        fullScreen.setOnClickListener {
            if (isFullScreen) {
                exitFullScreen()
            } else {
                enterFullScreen()
            }
        }
        back.setOnClickListener {
            exitFullScreen()
        }
        seekBar.setOnSeekBarChangeListener(onSeekBarChangeListener)
    }

    private fun setSyncProgress(start: Boolean) {
        if (start) {
            taskHandler.sendEmptyMessage(SYNC_PROGRESS)
        } else {
            taskHandler.removeMessages(SYNC_PROGRESS)
        }
    }

    private fun syncProgress() {
        seekBar.progress = (1f * getCurrentPosition() / getDuration() * 10000).toInt()
        seekBar.secondaryProgress = (1f * bufferPercent / getDuration() * 10000).toInt()
        progressInfo.text = generateTime(getCurrentPosition()) + "/" + generateTime(getDuration())
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
        loadingText.text = "正在缓冲(${Formatter.formatFileSize(context, mediaPlayer.tcpSpeed)})"
    }

    fun release() {
        reset()
        surface?.release()
        mediaPlayer.release()
    }

    private fun replay() {
        dataSource?.let {
            replayView.visibility = View.GONE
            setDataSource(dataSource!!)
            prepare()
            start()
        }
    }

    private fun clearSurface() {
        surface?.apply {
            val canvas = lockCanvas(Rect(0, 0, textureView.width, textureView.height))
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
            unlockCanvasAndPost(canvas)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    fun reset() {
        mediaPlayer.reset()
        clearSurface()
        currentState = PlayerConst.STATE_NORMAL
        prepareWhenSurfaceAvailable = false
        pauseAfterPrepare = false
        seekBar.progress = 0
        progressInfo.text = "00:00/00:00"
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
        mediaPlayer.setSurface(surface)
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
        try {
            mediaPlayer.start()
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
    fun onBackpress(): Boolean {
        if (isFullScreen) {
            exitFullScreen()
            return true
        }
        return false
    }

    override fun onPrepared(player: IMediaPlayer?) {
        CLog.d(TAG, "onPrepared")
        preparing = false
        loadingView.visibility = View.GONE
        syncProgress()
        if (pauseAfterPrepare) {
            pause()
        } else if (startAfterPrepare) {
            start()
        }
        if (seekPositionAfterPrepare > -1) {
            seekTo(seekPositionAfterPrepare)
            seekPositionAfterPrepare = -1
        }
        onPrepareListener?.invoke(player)
    }

    override fun onSeekComplete(player: IMediaPlayer?) {
        CLog.d(TAG, "onSeekComplete")
        onSeekCompleteListener?.invoke(player)
    }

    override fun onError(player: IMediaPlayer?, what: Int, extra: Int): Boolean {
        CLog.d(TAG, "onError,what:$what，extra:$extra")
        currentState = PlayerConst.STATE_ERROR
        stateChange(currentState)
        onErrorListener?.invoke(player, what, extra)
        return false
    }

    override fun onCompletion(player: IMediaPlayer?) {
        CLog.d(TAG, "onComplete")
        currentState = PlayerConst.STATE_COMPLETE
        stateChange(currentState)
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
            setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "reconnect", 1)
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

    private fun getActivity() = context as Activity
    private var activityOriginOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    private var windowOriginFlags: Int = 0

    private fun enterFullScreen() {
        isFullScreen = true
        windowOriginFlags = getActivity().window.attributes.flags
        activityOriginOrientation = getActivity().requestedOrientation

        getActivity().window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        getActivity().window.attributes = getActivity().window.attributes
        getActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        bottomControl.visibility = View.GONE
        topBar.visibility = View.GONE
        (root.parent as ViewGroup).removeView(root)
        (getActivity().window.decorView as ViewGroup).addView(root)
    }

    private fun exitFullScreen() {
        isFullScreen = false
        getActivity().window.attributes.flags = windowOriginFlags
        getActivity().window.attributes = getActivity().window.attributes
        getActivity().requestedOrientation = activityOriginOrientation
        topBar.visibility = View.GONE
        (root.parent as ViewGroup).removeView(root)
        addView(root)
    }

    inner class GestureListener : GestureDetector.SimpleOnGestureListener() {

        override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
            if (bottomControl.visibility == View.VISIBLE) {
                bottomControl.visibility = View.GONE
                if (isFullScreen) {
                    topBar.visibility = View.GONE
                }
            } else {
                bottomControl.visibility = View.VISIBLE
                if (isFullScreen) {
                    topBar.visibility = View.VISIBLE
                }
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

        override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
            if (scrollToSeek) {
                val p = -1f * distanceX / width
                val newProgress = (seekBar.progress + seekBar.max * p).toInt().limit(0, seekBar.max)
                seekBar.progress = newProgress
                return true
            } else {
                if (Math.abs(distanceX) > Math.abs(distanceY)) {
                    //横向滑动定位
                    scrollToSeek = true
                    onSeekBarChangeListener.onStartTrackingTouch(seekBar)
                }
            }
            return super.onScroll(e1, e2, distanceX, distanceY)
        }
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return super.onInterceptTouchEvent(ev)
    }


    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        var handled = gestureDetector.onTouchEvent(event)
        when (event!!.action) {
            MotionEvent.ACTION_UP -> {
                if (scrollToSeek) {
                    //滑动定位，手指拿起开始seek
                    onSeekBarChangeListener.onStopTrackingTouch(seekBar)
                    scrollToSeek = false
                    handled = true
                }
            }
        }
        return handled
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
                textureView.rotation = extra.toFloat()
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
        loadingView.visibility = if (buffering) View.VISIBLE else View.GONE
    }

    /**
     * 播放状态改变
     */
    private fun stateChange(what: Int) {
        when (what) {
            PlayerConst.STATE_PAUSE -> {
                togglePlay.setImageResource(R.drawable.ic_play_arrow_white_24dp)
                loadingView.visibility = View.GONE
                replayView.visibility = View.GONE
                setSyncProgress(false)
            }
            PlayerConst.STATE_PLAYING -> {
                togglePlay.setImageResource(R.drawable.ic_pause_white_24dp)
                loadingView.visibility = View.GONE
                replayView.visibility = View.GONE
                setSyncProgress(true)
            }
            PlayerConst.STATE_PREPARING -> {
                loadingView.visibility = View.VISIBLE
                replayView.visibility = View.GONE
            }
            PlayerConst.STATE_COMPLETE -> {
                togglePlay.setImageResource(R.drawable.ic_play_arrow_white_24dp)
                loadingView.visibility = View.GONE
                replayView.visibility = View.VISIBLE
                stateInfo.text = "播放完毕"
                setSyncProgress(false)
                setSyncTcpSpeed(false)
            }
            PlayerConst.STATE_ERROR -> {
                togglePlay.setImageResource(R.drawable.ic_play_arrow_white_24dp)
                loadingView.visibility = View.GONE
                replayView.visibility = View.VISIBLE
                stateInfo.text = "播放出错"
                setSyncProgress(false)
                setSyncTcpSpeed(false)
            }
        }
    }
}
