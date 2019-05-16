package com.sanron.datafetch.videosource.moyan

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

    @GET("$BASE_URL/{path}")
    fun html(@Path(encoded = true, value = "path") path: String): Observable<ResponseBody>

    @GET("$BASE_URL/index.php/vod/search/page/{page}/wd/{word}.html")
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
    @GET("$BASE_URL/top_mov.html")
    fun topMovie(): Observable<ResponseBody>
}
