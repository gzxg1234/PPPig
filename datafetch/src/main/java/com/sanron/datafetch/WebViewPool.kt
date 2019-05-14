package com.sanron.datafetch

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView

/**
 *Author:sanron
 *Time:2019/5/14
 *Description:
 */
object WebViewPool {
    private val webViewPool = mutableListOf<WebView>()

    private val handler = @SuppressLint("HandlerLeak")
    object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)
            (msg?.obj as? WebView)?.let {
                destroy(it)
            }
        }
    }

    fun acquire(context: Context): WebView {
        if (webViewPool.size > 0) {
            val instance = webViewPool.removeAt(0)
            instance.onResume()
            handler.removeMessages(0, instance)
            return instance
        } else {
            return newWebView(context)
        }
    }

    fun recycle(webView: WebView) {
        if (webViewPool.contains(webView)) {
            return
        }
        webView.stopLoading()
        webView.onPause()
        webView.webViewClient = null
        webView.webChromeClient = null
        webViewPool.add(webView)

        //10s后销毁webview
        val msg = Message.obtain(handler, 0)
        msg.obj = webView
        handler.sendMessageDelayed(msg, 10000)
    }

    private fun destroy(webView: WebView) {
        webViewPool.remove(webView)
        (webView.parent as? ViewGroup)?.removeView(webView)
        webView.destroy()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun newWebView(context: Context): WebView {
        return WebView(context).apply {
            settings.javaScriptEnabled = true
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            }
            settings.domStorageEnabled = true
            settings.setAppCacheEnabled(true)
            settings.setAppCachePath(context.cacheDir.absolutePath)
            settings.databaseEnabled = true
            settings.setAppCacheEnabled(true)
            settings.allowFileAccess = true
        }
    }
}