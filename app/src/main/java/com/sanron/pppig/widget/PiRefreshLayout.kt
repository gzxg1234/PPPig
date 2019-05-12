package com.sanron.pppig.widget

import android.content.Context
import android.support.v4.widget.SwipeRefreshLayout
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewConfiguration


/**
 * Author:sanron
 * Time:2018/8/16
 * Description:
 * 下拉刷新
 */
class PiRefreshLayout : SwipeRefreshLayout {

    private var startY: Float = 0f
    private var startX: Float = 0f
    private var handleTouch: Boolean = true
    private var touchSlop: Int = 0

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        setColorSchemeResources(com.sanron.pppig.R.color.colorPrimary, com.sanron.pppig.R.color.colorAccent)
        touchSlop = ViewConfiguration.get(context).scaledTouchSlop;
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        val action = ev.action
        when (action) {
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
                // 如果X轴位移大于Y轴位移，那么将事件交给viewPager处理。
                if (distanceX > touchSlop && distanceX > distanceY) {
                    handleTouch = false
                    return false
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL ->
                // 初始化标记
                handleTouch = true
        }
        // 如果是Y轴位移大于X轴，事件交给swipeRefreshLayout处理。
        return super.onInterceptTouchEvent(ev)
    }
}
