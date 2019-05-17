package com.sanron.datafetch

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.util.Log
import android.webkit.*

/**
 * Author:sanron
 * Time:2019/5/8
 * Description:
 * 抓取html内容，主要是动态页面的html
 */
object WebHelper {
    val TAG = WebHelper::class.java.simpleName

    interface Cancellable {
        fun cancel()
    }

    private class Task(private val callback: Callback, private var webView: SpiderWebView) : Cancellable, Callback {
        internal var canceled = false

        override fun success(result: String) {
            runOnUiThread {
                if (!canceled) {
                    callback.success(result)
                }
                cancel()
            }
        }

        override fun error(msg: String) {
            runOnUiThread {
                if (!canceled) {
                    callback.error(msg)
                }
                cancel()
            }
        }

        override fun cancel() {
            if (canceled) {
                return
            }
            canceled = true
            WebViewPool.recycle(webView)
        }
    }

    @SuppressLint("JavascriptInterface")
    private fun resetJsObj(webView: WebView, callback: Callback) {
        webView.removeJavascriptInterface("Android")
        webView.addJavascriptInterface(JsObj(callback), "Android")
    }

    fun getHtml(context: Context, url: String, header: Map<String, String>? = null, callback: Callback): Cancellable {
        return evaluate(context, url, header, "Android.callback(document.documentElement.outerHTML)", callback)
    }

    fun evaluate(context: Context, url: String, header: Map<String, String>?, js: String, callback: Callback): Cancellable {
        val webView = WebViewPool.acquire(context)
        var task = Task(callback, webView)
        resetJsObj(webView, task)
        webView.webChromeClient = object : WebChromeClient() {
            var evaluated = false
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                if (newProgress == 100 && !evaluated && !task.canceled) {
                    view?.evaluateJavascript("javascript:$js") {}
                    evaluated = true
                }
            }
        }

        webView.webViewClient = object : WebViewClient() {

            override fun shouldInterceptRequest(view: WebView?, url: String?): WebResourceResponse? {
                if (url!=null && !isRequiredResource(url)) {
                    //非网页加载必须的文件，过滤掉，加快加载速度
                    return null
                }
                return super.shouldInterceptRequest(view, url)
            }

            override fun onReceivedError(view: WebView?, errorCode: Int, description: String?, failingUrl: String?) {
                super.onReceivedError(view, errorCode, description, failingUrl)
                task.error("加载失败")
            }
        }
        webView.loadUrl(url, header)
        return task
    }


    /**
     * 是否网页加载必要的文件js和html
     */
    private fun isRequiredResource(url: String): Boolean {
        val uri = Uri.parse(url)
        uri.lastPathSegment?.let {
            return it.endsWith(".js")
                    || it.endsWith(".html")
        }
        return false
    }

    interface Callback {
        fun success(result: String)
        fun error(msg: String)
    }

    class JsObj(val callback: Callback) {

        @JavascriptInterface
        fun log(str: String) {
            Log.d(TAG, str)
        }

        @JavascriptInterface
        fun error(errMsg: String) {
            Log.d(TAG, "errMsg:$errMsg")
            callback.error(errMsg)
        }

        @JavascriptInterface
        fun callback(result: String) {
            Log.d(TAG, "result:$result")
            callback.success(result)
        }
    }
}
