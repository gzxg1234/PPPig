package com.sanron.datafetch.http

import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Url

/**
 *Author:sanron
 *Time:2019/5/16
 *Description:
 */
interface CommonApi {

    @Headers(value = [
        "User-Agent: Mozilla/5.0 (Linux; Android 5.0; SM-G900P Build/LRX21T) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.86 Mobile Safari/537.36"
    ])
    @GET
    fun url(@Url url: String): Observable<ResponseBody>
}