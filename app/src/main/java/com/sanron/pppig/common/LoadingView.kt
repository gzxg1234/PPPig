package com.sanron.pppig.common

import android.app.Activity
import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ProgressBar
import com.sanron.pppig.util.CLog

/**
 * Author:sanron
 * Time:2019/5/6
 * Description:
 */


class LoadingView(val activity: Activity) {

    val loadingView by lazy {
        val view = ProgressBar(activity)
        view.isIndeterminate = true
        val contentView = activity.window.decorView.findViewById<ViewGroup>(android.R.id.content)
        val lp = FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT)
        lp.gravity = Gravity.CENTER
        contentView.addView(view,lp)
        return@lazy view
    }

    fun show() {
        loadingView.visibility = View.VISIBLE
    }

    fun hide() {
        loadingView.visibility = View.GONE
    }
}
