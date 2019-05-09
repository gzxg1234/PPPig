package com.sanron.pppig.util

import java.text.SimpleDateFormat
import java.util.*

/**
 *Author:sanron
 *Time:2019/5/9
 *Description:
 */
object DateUtils {


    fun format(time: Long, pattern: String):String{
        val dateFormatter = SimpleDateFormat(pattern, Locale.getDefault())
        return dateFormatter.format(Date(time))
    }
}