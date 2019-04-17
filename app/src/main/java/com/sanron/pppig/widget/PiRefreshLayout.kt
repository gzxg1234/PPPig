package com.sanron.pppig.widget

import android.content.Context
import android.support.v4.widget.SwipeRefreshLayout
import android.util.AttributeSet
import com.sanron.pppig.R
import com.scwang.smartrefresh.header.MaterialHeader

/**
 * Author:sanron
 * Time:2018/8/16
 * Description:
 * 下拉刷新
 */
class PiRefreshLayout : SwipeRefreshLayout {

    private var mIsAttachedToWindow = false

    private var mRefreshWhenAttachWindow = false

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        setColorSchemeResources(R.color.colorPrimary, R.color.colorAccent)
    }
}
