package com.sanron.pppig.common

import com.sanron.datafetch_interface.exception.ParseException
import retrofit2.HttpException
import java.io.IOException
import java.net.ConnectException

/**
 * Author:sanron
 * Time:2019/5/6
 * Description:
 */
object MsgFactory {


    fun get(e: Throwable): String {
        return when (e) {
            is ConnectException -> {
                "服务器连接失败"
            }
            is HttpException -> {
                "网络错误"
            }
            is IOException -> {
                "请求失败"
            }
            is ParseException -> {
                "数据解析失败，请尝试检查更新"
            }
            else -> {
                "未知错误"
            }
        }
    }
}
