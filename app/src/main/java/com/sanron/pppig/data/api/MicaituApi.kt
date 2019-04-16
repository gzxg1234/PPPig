package com.sanron.pppig.data.api

import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Author:sanron
 * Time:2019/2/20
 * Description:
 */
interface MicaituApi {
    @GET("http://m.kkkkmao.com/{type}/index_{page}___{year}___{country}_1.html")
    fun all(@Path("type") type: String,
            @Path("page") page: Int,
            @Path("year") year:String,
            @Path("country") country: String): Observable<ResponseBody>
    /**
     * 主页
     */
    @GET("http://m.kkkkmao.com")
    fun home(): Observable<ResponseBody>

    /**
     * 热映电影
     */
    @GET("http://m.kkkkmao.com/top_mov.html")
    fun topMovie(): Observable<ResponseBody>
}
