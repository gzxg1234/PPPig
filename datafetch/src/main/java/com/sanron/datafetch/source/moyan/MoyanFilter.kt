package com.sanron.datafetch.source.moyan

import com.sanron.datafetch_interface.bean.FilterItem
import java.util.*

/**
 *
 * @author chenrong
 * @date 2019/5/12
 */
object MoyanFilter {

    internal fun moveListFilter(): Map<String, List<FilterItem>> {
        val types = listOf(
                FilterItem("全部", ""),
                FilterItem("喜剧", "喜剧"),
                FilterItem("爱情", "爱情"),
                FilterItem("恐怖", "恐怖"),
                FilterItem("动作", "动作"),
                FilterItem("科幻", "科幻"),
                FilterItem("剧情", "剧情"),
                FilterItem("战争", "战争"),
                FilterItem("警匪", "警匪"),
                FilterItem("犯罪", "犯罪"),
                FilterItem("动画", "动画"),
                FilterItem("奇幻", "奇幻"),
                FilterItem("武侠", "武侠"),
                FilterItem("冒险", "冒险"),
                FilterItem("枪战", "冒险"),
                FilterItem("悬疑", "悬疑"),
                FilterItem("惊悚", "惊悚"),
                FilterItem("经典", "经典"),
                FilterItem("青春", "青春"),
                FilterItem("文艺", "文艺"),
                FilterItem("微电影", "微电影"),
                FilterItem("古装", "古装"),
                FilterItem("历史", "历史"),
                FilterItem("运动", "运动"),
                FilterItem("农村", "农村"),
                FilterItem("儿童", "儿童"),
                FilterItem("网络电影", "网络电影")
        )
        val countrys = listOf(
                FilterItem("全部", ""),
                FilterItem("大陆", "大陆"),
                FilterItem("香港", "香港"),
                FilterItem("台湾", "台湾"),
                FilterItem("美国", "美国"),
                FilterItem("法国", "法国"),
                FilterItem("英国", "英国"),
                FilterItem("日本", "日本"),
                FilterItem("韩国", "韩国"),
                FilterItem("德国", "德国"),
                FilterItem("泰国", "泰国"),
                FilterItem("印度", "印度"),
                FilterItem("意大利", "意大利"),
                FilterItem("西班牙", "西班牙"),
                FilterItem("加拿大", "加拿大"),
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
                yearsList.add(FilterItem("${i}年代", "$i-${i + 9}"))
            }
            //更早年代
            yearsList.add(FilterItem("更早", "1900-1979"))
            return@run yearsList
        }
        return linkedMapOf(
                "类型" to types,
                "地区" to countrys,
                "年代" to years
        )
    }


    internal fun tvListFilter(): Map<String, List<FilterItem>> {
        val types = listOf(
                FilterItem("全部", ""),
                FilterItem("古装", "古装"),
                FilterItem("战争", "战争"),
                FilterItem("青春偶像", "青春偶像"),
                FilterItem("喜剧", "喜剧"),
                FilterItem("家庭", "家庭"),
                FilterItem("犯罪", "犯罪"),
                FilterItem("动作", "动作"),
                FilterItem("科幻", "科幻"),
                FilterItem("剧情", "剧情"),
                FilterItem("历史", "历史"),
                FilterItem("经典", "经典"),
                FilterItem("乡村", "乡村"),
                FilterItem("情景", "情景"),
                FilterItem("商战", "商战"),
                FilterItem("网剧", "网剧"),
                FilterItem("其他", "其他")
        )

        val countrys = listOf(
                FilterItem("全部", ""),
                FilterItem("大陆", "大陆"),
                FilterItem("香港", "香港"),
                FilterItem("台湾", "台湾"),
                FilterItem("韩国", "韩国"),
                FilterItem("日本", "日本"),
                FilterItem("美国", "美国"),
                FilterItem("英国", "英国"),
                FilterItem("法国", "法国"),
                FilterItem("泰国", "泰国"),
                FilterItem("新加坡", "新加坡"),
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
                yearsList.add(FilterItem("${i}年代", "$i-${i + 9}"))
            }
            //更早年代
            yearsList.add(FilterItem("更早", "1900-1979"))
            return@run yearsList
        }
        return linkedMapOf(
                "类型" to types,
                "地区" to countrys,
                "年代" to years
        )
    }


    internal fun varietyListFilter(): Map<String, List<FilterItem>> {
        val types = listOf(
                FilterItem("全部", ""),
                FilterItem("选秀", "选秀"),
                FilterItem("情感", "情感"),
                FilterItem("访谈", "访谈"),
                FilterItem("播报", "播报"),
                FilterItem("旅游", "旅游"),
                FilterItem("音乐", "音乐"),
                FilterItem("美食", "美食"),
                FilterItem("纪实", "纪实"),
                FilterItem("曲艺", "曲艺"),
                FilterItem("生活", "生活"),
                FilterItem("游戏", "游戏"),
                FilterItem("财经", "财经"),
                FilterItem("求职", "求职")
        )
        val countrys = listOf(
                FilterItem("全部", ""),
                FilterItem("大陆", "大陆"),
                FilterItem("港台", "港台"),
                FilterItem("日韩", "日韩"),
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
                yearsList.add(FilterItem("${i}年代", "$i-${i + 9}"))
            }
            //更早年代
            yearsList.add(FilterItem("更早", "1900-1979"))
            return@run yearsList
        }
        return linkedMapOf(
                "类型" to types,
                "地区" to countrys,
                "年代" to years
        )
    }

    internal fun animListFilter(): Map<String, List<FilterItem>> {
        val types = listOf(
                FilterItem("全部", ""),
                FilterItem("情感", "情感"),
                FilterItem("科幻", "科幻"),
                FilterItem("热血", "热血"),
                FilterItem("推理", "推理"),
                FilterItem("搞笑", "搞笑"),
                FilterItem("冒险", "冒险"),
                FilterItem("萝莉", "萝莉"),
                FilterItem("校园", "校园"),
                FilterItem("动作", "动作"),
                FilterItem("机战", "机战"),
                FilterItem("运动", "运动"),
                FilterItem("战争", "战争"),
                FilterItem("少年", "少年"),
                FilterItem("少女", "少女"),
                FilterItem("社会", "社会"),
                FilterItem("原创", "原创"),
                FilterItem("亲子", "亲子"),
                FilterItem("益智", "益智"),
                FilterItem("励志", "励志"),
                FilterItem("其他", "其他")
        )

        val countrys = listOf(
                FilterItem("全部", ""),
                FilterItem("大陆", "大陆"),
                FilterItem("日本", "日本"),
                FilterItem("欧美", "欧美"),
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
                yearsList.add(FilterItem("${i}年代", "$i-${i + 9}"))
            }
            //更早年代
            yearsList.add(FilterItem("更早", "1900-1979"))
            return@run yearsList
        }
        return linkedMapOf(
                "类型" to types,
                "地区" to countrys,
                "年代" to years
        )
    }

}