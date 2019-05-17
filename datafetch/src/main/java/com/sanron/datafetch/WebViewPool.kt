package com.sanron.datafetch

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.ViewGroup

/**
 *Author:sanron
 *Time:2019/5/14
 *Description:
 */
object WebViewPool {
    private val webViewPool = mutableListOf<SpiderWebView>()

    private val handler = @SuppressLint("HandlerLeak")
    object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)
            (msg?.obj as? SpiderWebView)?.let {
                destroy(it)
            }
        }
    }

    fun acquire(context: Context): SpiderWebView {
        if (webViewPool.size > 0) {
            val instance = webViewPool.removeAt(0)
            handler.removeMessages(0, instance)
            instance.onResume()
            return instance
        } else {
            return newWebView(context)
        }
    }

    fun recycle(webView: SpiderWebView) {
        if (webViewPool.contains(webView)) {
            return
        }
        webView.reset()
        webViewPool.add(webView)

        //10s后销毁webview
        val msg = Message.obtain(handler, 0)
        msg.obj = webView
        handler.sendMessageDelayed(msg, 10000)
    }

    private fun destroy(webView: SpiderWebView) {
        webViewPool.remove(webView)
        (webView.parent as? ViewGroup)?.removeView(webView)
        webView.destroy()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun newWebView(context: Context): SpiderWebView {
        return SpiderWebView(context)
    }
}