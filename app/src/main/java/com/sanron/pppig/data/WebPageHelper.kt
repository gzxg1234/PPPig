package com.sanron.pppig.data

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.webkit.*
import java.util.*

/**
 * Author:sanron
 * Time:2019/5/8
 * Description:
 * 抓取html内容，主要是动态页面的html
 */
class WebPageHelper(val context: Context) {

    private val webView: WebView by lazy {
        createWebView()
    }

    private val jsObj: JsObj by lazy {
        JsObj(this)
    }

    //防止回调错乱
    private var taskId = ""

    private val handler: Handler = Handler(Looper.getMainLooper())

    @SuppressLint("SetJavaScriptEnabled", "JavascriptInterface")
    private fun createWebView(): WebView {
        return WebView(context).apply {
            settings.javaScriptEnabled = true
            settings.javaScriptCanOpenWindowsAutomatically = false
            settings.domStorageEnabled = true
            addJavascriptInterface(jsObj, "JsObj")
        }
    }

    fun getHtml(url: String, header: Map<String, String>?, callback: Callback) {
        val s = {
            val id = UUID.randomUUID().toString()
            taskId = id
            jsObj.callback = callback
            webView.webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    handler.postDelayed({
                        view?.evaluateJavascript("javascript:JsObj.html(document.documentElement.outerHTML,'$id')") {}
                    }, 1000)
                }

                override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                    super.onReceivedError(view, request, error)
                    callback.error("加载失败")
                }
            }
            webView.stopLoading()
            webView.loadUrl(url, header)
        }
        if (Looper.getMainLooper().thread.id == Thread.currentThread().id) {
            s()
        } else {
            handler.post(s)
        }
    }


    fun destroy() {
        webView.stopLoading()
        webView.destroy()
    }

    interface Callback {
        fun success(html: String)
        fun error(msg: String)
    }

    class JsObj(val webPageHelper: WebPageHelper) {
        var callback: Callback? = null

        @JavascriptInterface
        fun html(html: String, id: String) {
            if (webPageHelper.taskId == id) {
                callback?.success(html)
            }
        }
    }
}
