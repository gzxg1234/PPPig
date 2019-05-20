package com.sanron.pppig.widget.loadlayout

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.IntRange

/**
 * Description:TODO
 * Create Time:2017/9/2 17:02
 * Author:KingJA
 * Email:kingjavip@gmail.com
 */

class LoadLayout : FrameLayout {
    companion object {
        const val STATE_CONTENT = -1
        const val STATE_NONE = -2
        var initializer: (LoadLayout.() -> Unit)? = null
    }

    private var onStateChangeListener: OnStateChangeListener? = null

    private val viewStates = mutableMapOf<Int, ViewCreator>()

    private var currentState: Int = STATE_NONE

    constructor(context: Context) : this(context, null) {
        initializer?.invoke(this)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    override fun onFinishInflate() {
        super.onFinishInflate()
        if (childCount > 0) {
            if (childCount > 1) {
                throw IllegalStateException("只能有一个子view作为内容视图")
            }
            getChildAt(0).visibility = View.GONE
            innerAddState(STATE_CONTENT, ViewCreator.SimpleViewCreator(getChildAt(0)))
        }
        initializer?.invoke(this)
    }

    fun setOnReloadListener(run: () -> Unit) {
        viewStates.forEach {
            it.value.onReloadListener = run
        }
    }

    fun setOnStateChangeListener(onStateChangeListener: OnStateChangeListener) {
        this.onStateChangeListener = onStateChangeListener
    }

    private fun innerAddState(state: Int, viewCreator: ViewCreator) {
        val last = viewStates.remove(state)
        if (last != null && last.viewCreated) {
            removeView(last.getRootView(this))
        }
        viewStates[state] = viewCreator
    }

    fun addState(@IntRange(from = 0) state: Int, viewCreator: ViewCreator) {
        innerAddState(state, viewCreator)
    }

    fun showState(@IntRange(from = 0) state: Int) {
        if (state != STATE_NONE && viewStates[state] == null) {
            throw IllegalStateException("未注册状态:$state")
        }
        viewStates.forEach {
            if (it.key == state) {
                val rootView = it.value.getRootView(this)!!
                if (rootView.parent == null) {
                    addView(rootView)
                }
                rootView.visibility = View.VISIBLE
                it.value.onShow()
            } else {
                if (it.value.viewCreated) {
                    it.value.getRootView(this)!!.visibility = View.GONE
                    it.value.onHide()
                }
            }
        }
        onStateChangeListener?.onChange(state)
    }

    interface OnStateChangeListener {
        fun onChange(newState: Int)
    }
}
