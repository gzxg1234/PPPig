package com.sanron.datafetch

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
import android.webkit.WebView
import android.webkit.WebViewClient

/**
 * Author:sanron
 * Time:2019/5/8
 * Description:
 * 抓取html内容，主要是动态页面的html
 */
class WebHelper(val context: Context) {

    interface Cancellable {
        fun cancel()
    }

    companion object {
        val TAG = "WebHlper"

        fun getHtml(context: Context, url: String, header: Map<String, String>? = null, callback: Callback) {
            evaluate(context, url, header, "Android.callback(document.documentElement.outerHTML)", callback)
        }


        fun evaluate(context: Context, url: String, header: Map<String, String>? = null, js: String, callback: Callback): Cancellable {
            val webPageHelper = WebHelper(context)
            webPageHelper.evaluate(url, header, js, object : Callback {
                override fun success(result: String) {
                    webPageHelper.destroy()
                    callback.success(result)
                }

                override fun error(msg: String) {
                    webPageHelper.destroy()
                    callback.error(msg)
                }
            })
            return object : Cancellable {
                override fun cancel() {
                    webPageHelper.destroy()
                }
            }
        }
    }

    val webView: WebView by lazy {
        createWebView()
    }

    private var jsObj: JsObj? = null

    private val handler: Handler = Handler(Looper.getMainLooper())

    @SuppressLint("SetJavaScriptEnabled", "JavascriptInterface")
    private fun createWebView(): WebView {
        return WebView(context).apply {
            settings.javaScriptEnabled = true
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                settings.mixedContentMode = MIXED_CONTENT_ALWAYS_ALLOW
            }
            settings.domStorageEnabled = true
            settings.databaseEnabled = true
            settings.setAppCacheEnabled(true)
            settings.allowFileAccess = true
        }
    }

    @SuppressLint("JavascriptInterface")
    private fun resetJsObj(callback: Callback) {
        jsObj?.destroy = true
        webView.removeJavascriptInterface("Android")

        jsObj = JsObj()
        jsObj?.callback = callback
        webView.addJavascriptInterface(jsObj, "Android")
    }


    fun evaluate(url: String, header: Map<String, String>?, js: String, callback: Callback) {
        resetJsObj(callback)
        webView.stopLoading()
        val s = {
            webView.webChromeClient = object : WebChromeClient() {
                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                    super.onProgressChanged(view, newProgress)
                    if (newProgress == 100) {
                        view?.evaluateJavascript("javascript:$js") {}
                    }
                }

            }

            webView.webViewClient = object : WebViewClient() {
                override fun onReceivedError(view: WebView?, errorCode: Int, description: String?, failingUrl: String?) {
                    super.onReceivedError(view, errorCode, description, failingUrl)
                    callback.error("加载失败")
                }
            }
            webView.loadUrl(url, header)
        }
        if (Looper.getMainLooper().thread.id == Thread.currentThread().id) {
            s()
        } else {
            handler.post(s)
        }
    }


    fun destroy() {
        (webView.parent as? ViewGroup)?.removeView(webView)
        webView.stopLoading()
        webView.destroy()
    }

    interface Callback {
        fun success(result: String)
        fun error(msg: String)
    }

    class JsObj {
        var callback: Callback? = null
        var destroy = false

        @JavascriptInterface
        fun log(str: String) {
            Log.d(TAG, str)
        }

        @JavascriptInterface
        fun error(errMsg: String) {
            Log.d(TAG, "errMsg:$errMsg")
            MainHandler.post {
                if (destroy) {
                    return@post
                }
                callback?.error(errMsg)
            }
        }

        @JavascriptInterface
        fun callback(result: String) {
            Log.d(TAG, "result:$result")
            MainHandler.post {
                if (destroy) {
                    return@post
                }
                callback?.success(result)
            }
        }
    }
}
