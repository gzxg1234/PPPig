package com.sanron.datafetch.source.flifli

import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Author:sanron
 * Time:2019/2/20
 * Description:
 */
interface FliApi {


    @GET("http://flif.li/{path}")
    fun html(@Path(encoded = true, value = "path") path: String): Observable<ResponseBody>

    @GET("http://m.kkkkmao.com/{type}/index_{page}___{year}___{country}_1.html")
    fun movieList(@Path("type") type: String,
                  @Path("country") country: String,
                  @Path("year") year: String,
                  @Path("page") page: Int): Observable<ResponseBody>

    @GET("http://m.kkkkmao.com/tv/index_{page}_{type}_{end}_{year}___{country}_1.html")
    fun tvList(@Path("type") type: String,
               @Path("end") end: String,
               @Path("country") country: String,
               @Path("year") year: String,
               @Path("page") page: Int): Observable<ResponseBody>


    @GET("http://m.kkkkmao.com/Animation/index_{page}_{type}_{end}_{year}___{country}_1.html")
    fun animList(@Path("type") type: String,
                 @Path("end") end: String,
                 @Path("country") country: String,
                 @Path("year") year: String,
                 @Path("page") page: Int): Observable<ResponseBody>


    @GET("http://m.kkkkmao.com/Arts/index_{page}_{type}_{end}_{year}___{country}_1.html")
    fun varietyList(@Path("type") type: String,
                    @Path("end") end: String,
                    @Path("country") country: String,
                    @Path("year") year: String,
                    @Path("page") page: Int): Observable<ResponseBody>

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
