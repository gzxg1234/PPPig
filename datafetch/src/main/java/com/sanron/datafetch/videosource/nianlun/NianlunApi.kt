package com.sanron.datafetch.videosource.nianlun

import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Author:sanron
 * Time:2019/2/20
 * Description:
 */
interface NianlunApi {

    companion object{
        const val BASE_URL = "http://www.nlmp4.com"
    }

    @GET("$BASE_URL/{path}")
    fun html(@Path(encoded = true, value = "path") path: String): Observable<ResponseBody>

    @GET("${BASE_URL}/vodsearch/{word}----------{page}---.html")
    fun search(@Path("word") word: String,
               @Path("page") page: Int): Observable<ResponseBody>
    /**
     * 主页
     */
    @GET("$BASE_URL")
    fun home(): Observable<ResponseBody>

    /**
     * 热映电影
     */
    @GET("http://m.kkkkmao.com/top_mov.html")
    fun topMovie(): Observable<ResponseBody>
}
