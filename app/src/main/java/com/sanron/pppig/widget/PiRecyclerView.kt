package com.sanron.pppig.widget

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewConfiguration
import androidx.recyclerview.widget.RecyclerView


/**
 * Author:sanron
 * Time:2018/8/16
 * Description:
 * 下拉刷新
 */
class PiRecyclerView : RecyclerView {

    private var startY: Float = 0f
    private var startX: Float = 0f
    private var handleTouch: Boolean = true
    private var touchSlop: Int = 0
    var handleScrollHorizontalConflict = false

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle)

    init {
        touchSlop = ViewConfiguration.get(context).scaledTouchSlop
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        if (handleScrollHorizontalConflict) {
            when (ev.action) {
                MotionEvent.ACTION_DOWN -> {
                    // 记录手指按下的位置
                    startY = ev.y
                    startX = ev.x
                    // 初始化标记
                    handleTouch = true
                }
                MotionEvent.ACTION_MOVE -> {
                    // 如果viewpager正在拖拽中，那么不拦截它的事件，直接return false；
                    if (!handleTouch) {
                        return false
                    }

                    // 获取当前手指位置
                    val endY = ev.y
                    val endX = ev.x
                    val distanceX = Math.abs(endX - startX)
                    val distanceY = Math.abs(endY - startY)
                    // 如果X轴位移大于Y轴位移，那么将事件交给子view处理。
                    if (distanceX > touchSlop && distanceX > distanceY) {
                        handleTouch = false
                        return false
                    }
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL ->
                    // 初始化标记
                    handleTouch = true
            }
        }
        return super.onInterceptTouchEvent(ev)
    }
}
