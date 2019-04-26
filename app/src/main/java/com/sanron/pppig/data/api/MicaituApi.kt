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


    @GET("http://m.kkkkmao.com/{path}")
    fun html(@Path("path") path: String): Observable<ResponseBody>

    @GET("http://m.kkkkmao.com/{typeParam}/index_{page}___{year}___{countryParam}_1.html")
    fun all(@Path("typeParam") type: String,
            @Path("page") page: Int,
            @Path("year") year:String,
            @Path("countryParam") country: String): Observable<ResponseBody>
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
