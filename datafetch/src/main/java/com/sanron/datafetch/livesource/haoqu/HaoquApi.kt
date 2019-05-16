package com.sanron.datafetch.livesource.haoqu

import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Path

/**
 *Author:sanron
 *Time:2019/5/16
 *Description:
 */
interface HaoquApi{

    companion object {
        const val BASE_URL = "http://m.haoqu.net"
    }

    @GET("$BASE_URL/{path}")
    fun html(@Path(encoded = true, value = "path") path: String): Observable<ResponseBody>
}