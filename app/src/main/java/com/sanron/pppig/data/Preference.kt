package com.sanron.pppig.data

import android.content.Context
import android.content.SharedPreferences
import java.lang.reflect.Type
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

open class Preference<T>(
        val context: Context,
        val spName: String,
        val name: String,
        val default: T,
        val type: Type? = null)
    : ReadWriteProperty<Any?, T> {

    private val prefs: SharedPreferences by lazy {
        context.getSharedPreferences(spName, Context.MODE_PRIVATE)
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return findPreference(name, default)
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        putPreference(name, value)
    }


    @Suppress("UNCHECKED_CAST")
    private fun findPreference(name: String, default: T): T = with(prefs) {
        val res: Any = when (default) {
            is Long -> getLong(name, default)
            is String -> getString(name, default)
            is Int -> getInt(name, default)
            is Boolean -> getBoolean(name, default)
            is Float -> getFloat(name, default)
            else -> {
                if (type == null) {
                    throw IllegalArgumentException("必须传入类型")
                }
                val json = getString(name, "")
                return if (!json.isNullOrEmpty()) {
                    JsonUtil.fromJson(json, type)
                } else {
                    default
                }
            }
        }
        res as T
    }

    private fun <U> putPreference(name: String, value: U) = with(prefs.edit()) {
        when (value) {
            is Long -> putLong(name, value)
            is String -> putString(name, value)
            is Int -> putInt(name, value)
            is Boolean -> putBoolean(name, value)
            is Float -> putFloat(name, value)
            else -> {
                if (type == null) {
                    throw IllegalArgumentException("必须传入类型")
                }
                putString(name, JsonUtil.toJson(value))
            }
        }.apply()
    }

}