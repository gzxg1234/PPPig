package com.sanron.datafetch.videosource.kkkkmao

import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Author:sanron
 * Time:2019/2/20
 * Description:
 */
interface KmaoApi {

    companion object {
        const val BASE_URL = "http://m.kkkkmao.com"
    }


    @GET("$BASE_URL/{path}")
    fun html(@Path(encoded = true, value = "path") path: String): Observable<ResponseBody>

    @GET("$BASE_URL/movie/index_{page}_{type}__{year}___{country}_1.html")
    fun movieList(@Path("type") type: String,
                  @Path("country") country: String,
                  @Path("year") year: String,
                  @Path("page") page: Int): Observable<ResponseBody>

    @GET("$BASE_URL/tv/index_{page}_{type}_{end}_{year}___{country}_1.html")
    fun tvList(@Path("type") type: String,
               @Path("end") end: String,
               @Path("country") country: String,
               @Path("year") year: String,
               @Path("page") page: Int): Observable<ResponseBody>


    @GET("$BASE_URL/Animation/index_{page}_{type}_{end}_{year}___{country}_1.html")
    fun animList(@Path("type") type: String,
                 @Path("end") end: String,
                 @Path("country") country: String,
                 @Path("year") year: String,
                 @Path("page") page: Int): Observable<ResponseBody>


    @GET("$BASE_URL/Arts/index_{page}_{type}_{end}_{year}___{country}_1.html")
    fun varietyList(@Path("type") type: String,
                    @Path("end") end: String,
                    @Path("country") country: String,
                    @Path("year") year: String,
                    @Path("page") page: Int): Observable<ResponseBody>

    @GET("$BASE_URL/vod-search-wd-{word}-p-{page}.html")
    fun search(@Path("word") word: String,
               @Path("page") page: Int): Observable<ResponseBody>

    /**
     * 主页
     */
    @GET(BASE_URL)
    fun home(): Observable<ResponseBody>

    /**
     * 热映电影
     */
    @GET("$BASE_URL/top_mov.html")
    fun topMovie(): Observable<ResponseBody>
}
