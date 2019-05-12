package com.sanron.datafetch_interface

import android.content.Context

/**
 *
 * @author chenrong
 * @date 2019/5/11
 */
interface SourceManager {

    fun init(context: Context)

    fun getSourceList(): List<Source>

}