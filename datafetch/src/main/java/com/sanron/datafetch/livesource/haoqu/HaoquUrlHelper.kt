package com.sanron.datafetch.livesource.haoqu

import android.content.Context
import com.sanron.datafetch.WebHelper

/**
 * Author:sanron
 * Time:2019/5/8
 * Description:
 * 抓取html内容，主要是动态页面的html
 */
object HaoquUrlHelper {

    /**
     * 获取视频源地址
     */
    fun getVideoSource(context: Context, url: String, header: Map<String, String>?, callback: WebHelper.Callback): WebHelper.Cancellable {
        val js = "var ___tryCount = 0;\n" +
                "      var ___getPlayUrl = null;\n" +
                "      ___getPlayUrl = function() {\n" +
                "        try {\n" +
                "          Android.log('setSignal='+typeof(setSignal))\n" +
                "          if (typeof(setSignal) != 'undefined') {\n" +
                "            setSignal('');\n" +
                "            Android.log('signal='+typeof(signal));\n" +
                "            if (typeof(signal) != 'undefined' && signal) {\n" +
                "              var data = signal.split('\$')\n" +
                "              var url = data[1]\n" +
                "              var type = data[2]\n" +
                "              Android.callback(JSON.stringify({\n" +
                "                url: url,\n" +
                "                isSource: type == 'm3u8'\n" +
                "              }))\n" +
                "              return;\n" +
                "            }\n" +
                "          }\n" +
                "\n" +
                "          if (___tryCount < 10) {\n" +
                "            ___tryCount++;\n" +
                "            setTimeout(___getPlayUrl, 1000);\n" +
                "          } else {\n" +
                "            Android.error(\"无法解析地址\");\n" +
                "          }\n" +
                "        } catch (e) {\n" +
                "          Android.error(e);\n" +
                "        }\n" +
                "      }\n" +
                "      ___getPlayUrl();"
        return WebHelper.evaluate(context, url, header, "$js", callback)
    }
}
