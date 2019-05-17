package com.sanron.datafetch_interface

import java.io.Serializable

/**
 * Author:sanron
 * Time:2019/5/17
 * Description:
 */
open class BaseBean : Serializable {
    /**
     * 扩展字段
     */
    var ext: Map<String, Any?>? = null

    operator fun set(key: String, value: Any?) {
        if (ext == null) {
            ext = mutableMapOf()
        }
        (ext as MutableMap<String, Any?>)[key] = value
    }

    operator fun <T> get(key: String): T? {
        return ext?.get(key) as T?
    }

}
