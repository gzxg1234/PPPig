package com.sanron.datafetch.source.moyan

import android.content.Context
import com.sanron.datafetch.WebHelper

/**
 * Author:sanron
 * Time:2019/5/8
 * Description:
 * 抓取html内容，主要是动态页面的html
 */
class MoyanVideoUrlHelper(val context: Context) {

    companion object {
        /**
         * 获取视频源地址
         */
        fun getVideoSource(context: Context, url: String, header: Map<String, String>?, callback: WebHelper.Callback): WebHelper.Cancellable {
            val js ="var ___getPlayCount = 0\n" +
                    "\t\tvar ___getPlayUrl = null;\n" +
                    "\t\t___getPlayUrl = function() {\n" +
                    "\t\t\ttry {\n" +
                    "\t\t\t\tAndroid.log('dp=' + dp)\n" +
                    "\t\t\t\tAndroid.log('option=' + dp.option)\n" +
                    "\t\t\t\tAndroid.log('options=' + dp.options)\n" +
                    "\t\t\t\tif (dp) {\n" +
                    "\t\t\t\t\tvar url\n" +
                    "\t\t\t\t\tif (dp.options && dp.options.video && dp.options.video.url) {\n" +
                    "\t\t\t\t\t\turl = dp.options.video.url\n" +
                    "\t\t\t\t\t} else if (dp.option && dp.option.video && dp.option.video.url) {\n" +
                    "\t\t\t\t\t\turl = dp.option.video.url\n" +
                    "\t\t\t\t\t}\n" +
                    "\t\t\t\t\tif (url) {\n" +
                    "\t\t\t\t\t\tif (Array.isArray(url)) {\n" +
                    "\t\t\t\t\t\t\tAndroid.callback(url[0])\n" +
                    "\t\t\t\t\t\t} else {\n" +
                    "\t\t\t\t\t\t\tAndroid.callback(url)\n" +
                    "\t\t\t\t\t\t}\n" +
                    "\t\t\t\t\t\treturn\n" +
                    "\t\t\t\t\t}\n" +
                    "\t\t\t\t}\n" +
                    "\n" +
                    "\t\t\t\tif (___getPlayCount < 10) {\n" +
                    "\t\t\t\t\t___getPlayCount++\n" +
                    "\t\t\t\t\tsetTimeout(___getPlayUrl, 1000)\n" +
                    "\t\t\t\t} else {\n" +
                    "\t\t\t\t\tAndroid.error(\"无法解析地址\")\n" +
                    "\t\t\t\t}\n" +
                    "\t\t\t} catch (e) {\n" +
                    "\t\t\t\tAndroid.log(e)\n" +
                    "\t\t\t}\n" +
                    "\t\t}\n" +
                    "\t\t___getPlayUrl()"
            return WebHelper.evaluate(context, url, header, "$js", callback)
        }

        /**
         * 获取播放页面url
         */
        fun getVideoPageUrl(context: Context, url: String, header: Map<String, String>?, callback: WebHelper.Callback): WebHelper.Cancellable {
           return WebHelper.evaluate(context, url, header, "Android.callback(MacPlayer.Parse + MacPlayer.PlayUrl)", callback)
        }
    }

}
