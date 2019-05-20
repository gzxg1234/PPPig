package com.sanron.pppig.data

import androidx.lifecycle.MutableLiveData
import com.google.gson.reflect.TypeToken
import com.sanron.pppig.app.PiApp
import java.util.*

/**
 *Author:sanron
 *Time:2019/5/20
 *Description:
 */
object HistoryManager {

    const val MAX_SIZE = 20

    var searchHistory: LinkedList<String> by Preference(PiApp.sInstance, "SEARCH_HISTORY", "history",
            LinkedList(), object : TypeToken<LinkedList<String>>() {}.type)


    val history = MutableLiveData<LinkedList<String>>()

    init {
        history.value = searchHistory
    }

    fun add(str: String) {
        if (history.value!!.contains(str)) {
            history.value!!.remove(str)
        } else if (history.value!!.size >= MAX_SIZE) {
            return
        }
        history.value!!.add(0, str)
        searchHistory = history.value!!
        history.value = history.value
    }

    fun clear() {
        history.value!!.clear()
        searchHistory = history.value!!
        history.value = history.value
    }

    fun remove(str: String) {
        if (history.value!!.remove(str)) {
            searchHistory = history.value!!
            history.value = history.value
        }
    }
}