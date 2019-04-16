package com.sanron.pppig.widget

import android.content.Context
import android.util.AttributeSet

import com.sanron.pppig.R
import com.scwang.smartrefresh.header.MaterialHeader
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.scwang.smartrefresh.layout.constant.RefreshState

/**
 * Author:sanron
 * Time:2018/8/16
 * Description:
 * 下拉刷新
 */
class PiRefreshLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : SmartRefreshLayout(context, attrs, defStyleAttr) {


    private var mIsAttachedToWindow = false

    private var mRefreshWhenAttachWindow = false

    init {
        val materialHeader = MaterialHeader(context)
        materialHeader.setColorSchemeResources(R.color.colorPrimary)
        setRefreshHeader(materialHeader)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        mIsAttachedToWindow = true
        if (mRefreshWhenAttachWindow) {
            super.autoRefresh(0)
            mRefreshWhenAttachWindow = false
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mIsAttachedToWindow = false
    }

    override fun autoRefresh(): Boolean {
        if (mIsAttachedToWindow) {
            return super.autoRefresh(0)
        } else {
            if (mState == RefreshState.None && isEnableRefreshOrLoadMore(mEnableRefresh)) {
                mRefreshWhenAttachWindow = true
                return true
            }
            return false
        }
    }
}
