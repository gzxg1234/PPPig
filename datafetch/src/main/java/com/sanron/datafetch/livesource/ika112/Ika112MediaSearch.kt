package com.sanron.datafetch.livesource.ika112

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.webkit.WebChromeClient
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import com.sanron.datafetch.*
import com.sanron.datafetch_interface.video.bean.PlayLine

/**
 * Author:sanron
 * Time:2019/5/8
 * Description:
 * 抓取html内容，主要是动态页面的html
 */
object Ika112MediaSearch {

    val TAG = Ika112MediaSearch::class.java.simpleName

    interface Cancellable {
        fun cancel()
    }

    private class Task(private val callback: Callback, private var webView: SpiderWebView) : Cancellable, Callback {
        internal var canceled = false

        override fun success(result: List<String>) {
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


    fun search(context: Context, playItem: PlayLine.Item, callback: Callback): Cancellable {

        val pageUrl = playItem.get<String?>("pageUrl")
        val url = playItem.get<String?>("url")
        val webView = WebViewPool.acquire(context)
        val task = Task(callback, webView)

        //最大等待时间
        val maxTime = 10000L
        val timeout = Runnable {
            task.error("已超时")
        }
        val list = mutableListOf<String>()
        webView.webChromeClient = object : WebChromeClient() {

            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                if (newProgress == 100 && !task.canceled) {
                    val js = "window.addEventListener(\"onready\", function() {\n" +
                            "            Android.log(\"onready\");\n" +
                            "            var player = document.getElementById(\"vstPlayer\");\n" +
                            "            Android.log('vstPlayer=' + player);\n" +
                            "            player.volume = 0.0;\n" +
                            "        });\n" +
                            "        window.addEventListener(\"onload\", function() {\n" +
                            "            Android.log(\"onload\");\n" +
                            "            startPlayer($url);\n" +
                            "        });"
                    view?.evaluateJavascript(js) {
                        FetchLog.d(TAG, "evaluate js success")
                    }
                }
            }
        }

        webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                MainHandler.postDelayed(timeout, maxTime)
            }

            override fun shouldInterceptRequest(view: WebView?, url: String?): WebResourceResponse? {
                if (url != null) {
                    if (isMediaRequest(url)) {
                        if (list.size < 1) {
                            list.add(url)
                            MainHandler.removeCallbacks(timeout)
                            task.success(list)
                        }
                    } else if (!isRequiredResource(url)) {
                        //非网页加载必须的文件，过滤掉，加快加载速度
                        return null
                    }
                }
                return super.shouldInterceptRequest(view, url)
            }

            override fun onReceivedError(view: WebView?, errorCode: Int, description: String?, failingUrl: String?) {
                super.onReceivedError(view, errorCode, description, failingUrl)
                MainHandler.removeCallbacks(timeout)
                task.error("加载失败")
            }
        }
        webView.loadUrl(pageUrl)
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

    /**
     * 判断是否媒体文件
     */
    private fun isMediaRequest(url: String): Boolean {
        val uri = Uri.parse(url)
        if (uri.lastPathSegment != null) {
            FetchLog.d(TAG, "resource request:" + uri.lastPathSegment)
            uri.lastPathSegment?.let {
                return it.endsWith(".m3u8")
                        || it.endsWith(".mp4")
                        || it.endsWith(".avi")
                        || it.endsWith(".mpg")
                        || it.endsWith(".mpeg")
                        || it.endsWith(".rmvb")
                        || it.endsWith(".mov")
                        || it.endsWith(".wmv")
                        || it.endsWith(".mkv")
                        || it.endsWith(".rm")
                        || it.endsWith(".3gp")
            }
        }
        return false
    }

    interface Callback {
        fun success(result: List<String>)
        fun error(msg: String)
    }

}
