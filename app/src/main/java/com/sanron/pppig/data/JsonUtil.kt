package com.sanron.pppig.data

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.lang.reflect.Type

/**
 *Author:sanron
 *Time:2019/5/20
 *Description:
 */
object JsonUtil {

    val gson: Gson by lazy {
        GsonBuilder()
                .create()
    }

    fun toJson(any: Any?): String? {
        return gson.toJson(any)
    }

    fun <T> fromJson(json: String, clazz: Class<out T>): T {
        return gson.fromJson(json, clazz)
    }


    fun <T> fromJson(json: String, type: Type): T {
        return gson.fromJson(json, type)
    }

}