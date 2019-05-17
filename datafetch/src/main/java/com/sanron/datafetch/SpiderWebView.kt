package com.sanron.datafetch

import android.content.Context
import android.os.Build
import android.webkit.WebSettings
import android.webkit.WebView

/**
 *
 * @author chenrong
 * @date 2019/5/17
 */
class SpiderWebView(context: Context) : WebView(context) {

    init {
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

        settings.userAgentString = "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.86 Mobile Safari/537.36"
    }

    fun reset() {
        stopLoading()
        loadData("", "", "")
        clearHistory()
        onPause()
        webViewClient = null
        webChromeClient = null
    }
}