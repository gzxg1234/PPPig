package com.sanron.pppig.widget

import android.content.Context
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.view.MotionEvent

/**
 * author:mingking
 * listData:2016/6/30
 */
class NoScrollViewPager : ViewPager {


    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {}

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        return false
    }

    override fun onInterceptTouchEvent(arg0: MotionEvent): Boolean {
        return false
    }


}
