package com.sanron.datafetch.source.moyan

import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Author:sanron
 * Time:2019/2/20
 * Description:
 */
interface MoyanApi {

    companion object {
        const val BASE_URL = "https://www.moyantv.com"
    }

    @GET("https://www.moyantv.com/{path}")
    fun html(@Path(encoded = true, value = "path") path: String): Observable<ResponseBody>

    /**
     * 主页
     */
    @GET("https://www.moyantv.com/")
    fun home(): Observable<ResponseBody>

    /**
     * 热映电影
     */
    @GET("http://m.kkkkmao.com/top_mov.html")
    fun topMovie(): Observable<ResponseBody>
}
