package com.sanron.datafetch.videosource.nianlun

import android.content.Context
import com.sanron.datafetch.WebHelper

/**
 * Author:sanron
 * Time:2019/5/8
 * Description:
 * 抓取html内容，主要是动态页面的html
 */
object NianlunVideoUrlHelper {

    /**
     * 获取视频源地址
     */
    fun getVideoSource(context: Context, url: String, header: Map<String, String>?, callback: WebHelper.Callback): WebHelper.Cancellable {
        val js = "var ___tryCount = 0;\n" +
                "      var ___getPlayUrl = null;\n" +
                "      ___getPlayUrl = function() {\n" +
                "        try {\n" +
                "          if (typeof(main) != 'undefined' && main) {\n" +
                "            Android.callback(window.location.protocol + '//' + window.location.host + main);" +
                "            return;\n" +
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

    /**
     * 获取播放页面信息2
     * 这一步可能可以直接获取到视频源地址
     */
    fun getVideoPageUrl2(context: Context, url: String, header: Map<String, String>?, callback: WebHelper.Callback): WebHelper.Cancellable {
        val js = "var ___tryCount = 0;\n" +
                "      var ___getPlayUrl = null;\n" +
                "      ___getPlayUrl = function() {\n" +
                "        try {\n" +
                "          if (MacPlayer) {\n" +
                "            //parse字段不为空,表示MacPlayer.PlayUrl为视频源地址,可直接播放\n" +
                "            if (MacPlayer.Parse) {\n" +
                "              Android.callback(JSON.stringify({\n" +
                "                \"url\": MacPlayer.PlayUrl,\n" +
                "                \"isSource\": true\n" +
                "              }));\n" +
                "            } else {\n" +
                "              Android.callback(JSON.stringify({\n" +
                "                \"url\": MacPlayer.Parse + MacPlayer.PlayUrl,\n" +
                "                \"isSource\": false\n" +
                "              }));\n" +
                "            }" +
                "            return;\n" +
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
        return WebHelper.evaluate(context, url, header, js, callback)
    }

    /**
     * 获取播放页面url
     */
    fun getVideoPageUrl(context: Context, url: String, header: Map<String, String>?, callback: WebHelper.Callback): WebHelper.Cancellable {
        val js = "var ___tryCount = 0;\n" +
                "      var ___getPlayUrl = null;\n" +
                "      ___getPlayUrl = function() {\n" +
                "        try {\n" +
                "          if (MacPlayer) {\n" +
                "            Android.callback(MacPlayer.Parse+MacPlayer.PlayUrl);\n" +
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
        return WebHelper.evaluate(context, url, header, js, callback)
    }

}
