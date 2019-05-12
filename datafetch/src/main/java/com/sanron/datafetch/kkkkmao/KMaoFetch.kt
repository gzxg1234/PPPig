package com.sanron.datafetch.kkkkmao

import com.google.gson.GsonBuilder
import com.sanron.datafetch.Injector
import com.sanron.datafetch.SourceManagerImpl
import com.sanron.datafetch.WebHelper
import com.sanron.datafetch.exception.ParseException
import com.sanron.datafetch_interface.DataFetch
import com.sanron.datafetch_interface.bean.*
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import okhttp3.OkHttpClient
import org.jsoup.Jsoup
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.*

/**
 * Author:sanron
 * Time:2019/5/8
 * Description:
 */
class KMaoFetch : DataFetch {
    companion object {
        const val TYPE_MOVIE = 1
        const val TYPE_TV = 2
        const val TYPE_VARIETY = 3
        const val TYPE_ANIM = 4
        val TYPES = listOf(
                VideoListType("电影", TYPE_MOVIE),
                VideoListType("电视剧", TYPE_TV),
                VideoListType("综艺", TYPE_VARIETY),
                VideoListType("动漫", TYPE_ANIM)
        )
    }

    override fun getVideoListTypes(): List<VideoListType> {
        return TYPES
    }

    private val mRetrofit: Retrofit by lazy {
        val gsonBuilder = GsonBuilder()
        gsonBuilder.setDateFormat("yyyy-MM-dd HH:mm:ss")
        return@lazy Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createAsync())
                .addConverterFactory(GsonConverterFactory.create(gsonBuilder.create()))
                .client(createOkHttp())
                .baseUrl("https://m.kkkkmao.com")
                .build()
    }

    private val mApis = HashMap<Class<*>, Any>()

    private fun <T> getService(clazz: Class<T>): T {
        var t = mApis[clazz] as T?
        if (t == null) {
            t = mRetrofit.create(clazz)
            mApis[clazz] = t!! as Any
        }
        return t
    }

    private fun createOkHttp(): OkHttpClient {
        val builder = Injector.provideOkHttpClient()
                .newBuilder()
        return builder.build()
    }

    override fun getHomeData(): Observable<Home> {
        return getService(KmaoApi::class.java)
                .home()
                .map { s ->
                    val data = KKMaoParser.instance.parseHome(s.string())
                    return@map data
                }
    }

    override fun getTopMovie(): Observable<PageData<VideoItem>> {
        return getService(KmaoApi::class.java)
                .topMovie()
                .map { s -> KKMaoParser.instance.parseTopMovie(s.string()) }
    }

    override fun getVideoDetail(path: String): Observable<VideoDetail> {
        return getService(KmaoApi::class.java)
                .html(path)
                .map { responseBody -> KKMaoParser.instance.parseVideoDetail(responseBody.string()) }
    }

    override fun getVideoSource(url: String): Observable<List<String>> {
        return getService(KmaoApi::class.java)
                .html(url)
                .flatMap { responseBody ->
                    val sourceUrl = KKMaoParser.instance.parsePlayPageUrl(responseBody.string())
                            ?: throw ParseException("解析url失败")
                    val referer = "http://m.kkkkmao.com/$url"
                    val header = mutableMapOf<String, String>()
                    header["Referer"] = referer
                    Observable.create(ObservableOnSubscribe<String> { })
                    return@flatMap Observable.create(ObservableOnSubscribe<List<String>> {
                        WebHelper.getHtml(SourceManagerImpl.context, sourceUrl, header, object : WebHelper.Callback {
                            override fun success(result: String) {
                                val doc = Jsoup.parse(result)
                                val list = mutableListOf<String>()
                                doc.select("video").forEach {
                                    list.add(it.attr("src"))
                                }
                                it.onNext(list)
                                it.onComplete()
                            }

                            override fun error(msg: String) {
                                it.onError(IOException(msg))
                            }
                        })
                    })
                }
    }

    private fun moveListFilter(): Map<String, List<FilterItem>> {
        val types = listOf(
                FilterItem("全部", "movie"),
                FilterItem("喜剧", "Comedy"),
                FilterItem("动作", "Action"),
                FilterItem("预告片", "yugaopian"),
                FilterItem("科幻", "Sciencefiction"),
                FilterItem("惊悚", "Horror"),
                FilterItem("爱情", "Love"),
                FilterItem("战争", "War"),
                FilterItem("剧情", "Drama")
        )
        val countrys = listOf(
                FilterItem("全部", ""),
                FilterItem("大陆", "大陆"),
                FilterItem("香港", "香港"),
                FilterItem("台湾", "台湾"),
                FilterItem("美国", "美国"),
                FilterItem("韩国", "韩国"),
                FilterItem("日本", "日本"),
                FilterItem("泰国", "泰国"),
                FilterItem("新加坡", "新加坡"),
                FilterItem("马来西亚", "马来西亚"),
                FilterItem("印度", "印度"),
                FilterItem("法国", "法国"),
                FilterItem("加拿大", "加拿大")
        )

        val years = run {
            val yearsList = mutableListOf<FilterItem>()
            yearsList.add(FilterItem("全部", ""))
            val nowYear = Date().year + 1900
            val years = nowYear / 10 * 10
            //添加当前年代的年份
            for (i in nowYear downTo years) {
                yearsList.add(FilterItem(i.toString(), i.toString()))
            }
            //添加历史年代,1980开始
            for (i in years downTo 1980 step 10) {
                yearsList.add(FilterItem("${i}年代", "$i,${i + 9}"))
            }
            //更早年代
            yearsList.add(FilterItem("更早", "1900,1979"))
            return@run yearsList
        }
        return linkedMapOf(
                "类型" to types,
                "国家" to countrys,
                "年代" to years
        )
    }


    private fun tvListFilter(): Map<String, List<FilterItem>> {
        val types = listOf(
                FilterItem("全部", ""),
                FilterItem("动作", "133"),
                FilterItem("惊悚", "134"),
                FilterItem("犯罪", "66"),
                FilterItem("农村", "113"),
                FilterItem("恐怖", "112"),
                FilterItem("剧情", "110"),
                FilterItem("青春", "130"),
                FilterItem("都市", "88"),
                FilterItem("言情", "87"),
                FilterItem("时装", "86"),
                FilterItem("家庭", "85"),
                FilterItem("年代", "84"),
                FilterItem("励志", "83"),
                FilterItem("生活", "82"),
                FilterItem("偶像", "81"),
                FilterItem("历史", "79"),
                FilterItem("古装", "78"),
                FilterItem("武侠", "77"),
                FilterItem("警匪", "76"),
                FilterItem("刑侦", "75"),
                FilterItem("战争", "74"),
                FilterItem("神话", "73"),
                FilterItem("军旅", "72"),
                FilterItem("商战", "70"),
                FilterItem("校园", "69"),
                FilterItem("穿越", "68"),
                FilterItem("悬疑", "67"),
                FilterItem("抗日", "123")
        )
        val status = listOf(
                FilterItem("全部", ""),
                FilterItem("连载中", "1"),
                FilterItem("已完结", "2")
        )

        val countrys = listOf(
                FilterItem("全部", ""),
                FilterItem("大陆", "大陆"),
                FilterItem("香港", "香港"),
                FilterItem("台湾", "台湾"),
                FilterItem("美国", "美国"),
                FilterItem("韩国", "韩国"),
                FilterItem("日本", "日本"),
                FilterItem("泰国", "泰国"),
                FilterItem("新加坡", "新加坡"),
                FilterItem("马来西亚", "马来西亚"),
                FilterItem("印度", "印度"),
                FilterItem("法国", "法国"),
                FilterItem("加拿大", "加拿大")
        )
        val years = run {
            val yearsList = mutableListOf<FilterItem>()
            yearsList.add(FilterItem("全部", ""))
            val nowYear = Date().year + 1900
            val years = nowYear / 10 * 10
            //添加当前年代的年份
            for (i in nowYear downTo years) {
                yearsList.add(FilterItem(i.toString(), i.toString()))
            }
            //添加历史年代,1980开始
            for (i in years downTo 1980 step 10) {
                yearsList.add(FilterItem("${i}年代", "$i,${i + 9}"))
            }
            //更早年代
            yearsList.add(FilterItem("更早", "1900,1979"))
            return@run yearsList
        }
        return linkedMapOf(
                "类型" to types,
                "状态" to status,
                "国家" to countrys,
                "年代" to years
        )
    }


    private fun varietyListFilter(): Map<String, List<FilterItem>> {
        val types = listOf(
                FilterItem("全部", ""),
                FilterItem("综艺", "155"),
                FilterItem("新闻", "25"),
                FilterItem("晚会", "16"),
                FilterItem("娱乐", "98"),
                FilterItem("财经", "17"),
                FilterItem("体育", "18"),
                FilterItem("纪实", "19"),
                FilterItem("生活", "20"),
                FilterItem("歌舞", "21"),
                FilterItem("故事", "22"),
                FilterItem("军事", "23"),
                FilterItem("汽车", "121"),
                FilterItem("情感", "89"),
                FilterItem("访谈", "90"),
                FilterItem("时尚", "91"),
                FilterItem("音乐", "92"),
                FilterItem("游戏", "93"),
                FilterItem("美食", "94"),
                FilterItem("益智", "127"),
                FilterItem("旅游", "95"),
                FilterItem("职场", "96"),
                FilterItem("看点", "97"),
                FilterItem("选秀", "99"),
                FilterItem("搞笑", "100"),
                FilterItem("真人秀", "101"),
                FilterItem("脱口秀", "102"),
                FilterItem("少儿", "24"),
                FilterItem("竞技", "144")
        )
        val status = listOf(
                FilterItem("全部", ""),
                FilterItem("连载中", "1"),
                FilterItem("已完结", "2")
        )

        val countrys = listOf(
                FilterItem("全部", ""),
                FilterItem("大陆", "大陆"),
                FilterItem("香港", "香港"),
                FilterItem("台湾", "台湾"),
                FilterItem("美国", "美国"),
                FilterItem("韩国", "韩国"),
                FilterItem("日本", "日本"),
                FilterItem("泰国", "泰国"),
                FilterItem("新加坡", "新加坡"),
                FilterItem("马来西亚", "马来西亚"),
                FilterItem("印度", "印度"),
                FilterItem("法国", "法国"),
                FilterItem("加拿大", "加拿大")
        )
        val years = run {
            val yearsList = mutableListOf<FilterItem>()
            yearsList.add(FilterItem("全部", ""))
            val nowYear = Date().year + 1900
            val years = nowYear / 10 * 10
            //添加当前年代的年份
            for (i in nowYear downTo years) {
                yearsList.add(FilterItem(i.toString(), i.toString()))
            }
            //添加历史年代,1980开始
            for (i in years downTo 1980 step 10) {
                yearsList.add(FilterItem("${i}年代", "$i,${i + 9}"))
            }
            //更早年代
            yearsList.add(FilterItem("更早", "1900,1979"))
            return@run yearsList
        }
        return linkedMapOf(
                "类型" to types,
                "状态" to status,
                "国家" to countrys,
                "年代" to years
        )
    }

    private fun animListFilter(): Map<String, List<FilterItem>> {
        val types = listOf(
                FilterItem("全部", ""),
                FilterItem("热血", "59"),
                FilterItem("搞笑", "58"),
                FilterItem("冒险", "60"),
                FilterItem("格斗", "48"),
                FilterItem("少女", "57"),
                FilterItem("惊悚", "128"),
                FilterItem("家庭", "62"),
                FilterItem("神话", "61"),
                FilterItem("励志", "64"),
                FilterItem("恋爱", "104"),
                FilterItem("剧情", "105"),
                FilterItem("神魔", "106"),
                FilterItem("历史", "107"),
                FilterItem("青春", "108"),
                FilterItem("科幻", "109"),
                FilterItem("宠物", "116"),
                FilterItem("运动", "120"),
                FilterItem("魔幻", "56"),
                FilterItem("机战", "55"),
                FilterItem("推理", "54"),
                FilterItem("竞技", "52"),
                FilterItem("益智", "50"),
                FilterItem("通话", "49"),
                FilterItem("魔法", "47"),
                FilterItem("经典", "46"),
                FilterItem("微电影", "153")
        )
        val status = listOf(
                FilterItem("全部", ""),
                FilterItem("连载中", "1"),
                FilterItem("已完结", "2")
        )

        val countrys = listOf(
                FilterItem("全部", ""),
                FilterItem("大陆", "大陆"),
                FilterItem("香港", "香港"),
                FilterItem("台湾", "台湾"),
                FilterItem("美国", "美国"),
                FilterItem("韩国", "韩国"),
                FilterItem("日本", "日本"),
                FilterItem("泰国", "泰国"),
                FilterItem("新加坡", "新加坡"),
                FilterItem("马来西亚", "马来西亚"),
                FilterItem("印度", "印度"),
                FilterItem("法国", "法国"),
                FilterItem("加拿大", "加拿大")
        )
        val years = run {
            val yearsList = mutableListOf<FilterItem>()
            yearsList.add(FilterItem("全部", ""))
            val nowYear = Date().year + 1900
            val years = nowYear / 10 * 10
            //添加当前年代的年份
            for (i in nowYear downTo years) {
                yearsList.add(FilterItem(i.toString(), i.toString()))
            }
            //添加历史年代,1980开始
            for (i in years downTo 1980 step 10) {
                yearsList.add(FilterItem("${i}年代", "$i,${i + 9}"))
            }
            //更早年代
            yearsList.add(FilterItem("更早", "1900,1979"))
            return@run yearsList
        }
        return linkedMapOf(
                "类型" to types,
                "状态" to status,
                "国家" to countrys,
                "年代" to years
        )
    }

    override fun getVideoListFilter(type: Int): Map<String, List<FilterItem>> {
        if (type == TYPE_MOVIE) {
            return moveListFilter()
        } else if (type == TYPE_TV) {
            return tvListFilter()
        } else if (type == TYPE_VARIETY) {
            return varietyListFilter()
        } else if (type == TYPE_ANIM) {
            return animListFilter()
        }
        return mapOf()
    }

    private fun getTvList(params: Map<String, FilterItem>, page: Int): Observable<PageData<VideoItem>> {
        return getService(KmaoApi::class.java)
                .tvList(params["类型"]?.value ?: "", params["状态"]?.value
                        ?: "", params["国家"]?.value ?: "", params["年代"]?.value ?: "", page)
                .map { s -> KKMaoParser.instance.parseVideoList(s.string()) }
    }

    private fun getMovieList(params: Map<String, FilterItem>, page: Int): Observable<PageData<VideoItem>> {
        return getService(KmaoApi::class.java)
                .movieList(params["类型"]?.value ?: "movie", params["国家"]?.value
                        ?: "", params["年代"]?.value ?: "", page)
                .map { s -> KKMaoParser.instance.parseVideoList(s.string()) }
    }

    private fun getVarietyList(params: Map<String, FilterItem>, page: Int): Observable<PageData<VideoItem>> {
        return getService(KmaoApi::class.java)
                .varietyList(params["类型"]?.value ?: "", params["状态"]?.value
                        ?: "", params["国家"]?.value ?: "", params["年代"]?.value ?: "", page)
                .map { s -> KKMaoParser.instance.parseVideoList(s.string()) }
    }

    private fun getAnimList(params: Map<String, FilterItem>, page: Int): Observable<PageData<VideoItem>> {
        return getService(KmaoApi::class.java)
                .animList(params["类型"]?.value ?: "", params["状态"]?.value
                        ?: "", params["国家"]?.value ?: "", params["年代"]?.value ?: "", page)
                .map { s -> KKMaoParser.instance.parseVideoList(s.string()) }
    }

    override fun getVideoList(type: Int, param: Map<String, FilterItem>, page: Int): Observable<PageData<VideoItem>> {
        if (type == TYPE_MOVIE) {
            return getMovieList(param, page)
        } else if (type == TYPE_TV) {
            return getTvList(param, page)
        } else if (type == TYPE_VARIETY) {
            return getVarietyList(param, page)
        } else if (type == TYPE_ANIM) {
            return getAnimList(param, page)
        }
        return Observable.empty()
    }
}
