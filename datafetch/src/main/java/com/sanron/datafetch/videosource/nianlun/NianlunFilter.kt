package com.sanron.datafetch.videosource.nianlun

import com.sanron.datafetch_interface.video.bean.FilterItem
import java.util.*

/**
 *
 * @author chenrong
 * @date 2019/5/12
 */
object NianlunFilter {


    internal fun moveListFilter(): Map<String, List<FilterItem>> {
        val types = listOf(
                FilterItem("全部", ""),
                FilterItem("喜剧", "喜剧"),
                FilterItem("爱情", "爱情"),
                FilterItem("恐怖", "恐怖"),
                FilterItem("动作", "动作"),
                FilterItem("科幻", "科幻"),
                FilterItem("剧情", "剧情"),
                FilterItem("战争", "战争")
        )
        val countrys = listOf(
                FilterItem("全部", ""),
                FilterItem("大陆", "大陆"),
                FilterItem("香港", "香港"),
                FilterItem("台湾", "台湾"),
                FilterItem("美国", "美国"),
                FilterItem("法国", "法国"),
                FilterItem("英国", "英国"),
                FilterItem("韩国", "韩国"),
                FilterItem("日本", "日本"),
                FilterItem("泰国", "泰国"),
                FilterItem("印度", "印度"),
                FilterItem("其他", "其他")
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
                "地区" to countrys,
                "年代" to years
        )
    }


    internal fun tvListFilter(): Map<String, List<FilterItem>> {
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
                FilterItem("法国", "法国"),
                FilterItem("英国", "英国"),
                FilterItem("其他", "其他")
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
                "地区" to countrys,
                "年代" to years
        )
    }


    internal fun varietyListFilter(): Map<String, List<FilterItem>> {
        val types = listOf(
                FilterItem("全部", ""),
                FilterItem("综艺", "综艺"),
                FilterItem("记录", "记录")
        )

        val countrys = listOf(
                FilterItem("全部", ""),
                FilterItem("大陆", "大陆"),
                FilterItem("香港", "香港"),
                FilterItem("韩国", "韩国"),
                FilterItem("台湾", "台湾"),
                FilterItem("日本", "日本"),
                FilterItem("欧美", "欧美")
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
                "地区" to countrys,
                "年代" to years
        )
    }

    internal fun animListFilter(): Map<String, List<FilterItem>> {
        val countrys = listOf(
                FilterItem("全部", ""),
                FilterItem("日本", "日本"),
                FilterItem("大陆", "大陆"),
                FilterItem("欧美", "欧美"),
                FilterItem("韩国", "韩国")
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
                "地区" to countrys,
                "年代" to years
        )
    }

}