package com.sanron.datafetch

/**
 *Author:sanron
 *Time:2019/5/17
 *Description:
 */

fun <K,C> Map<K, *>.getAs(key: K): C? {
    return get(key) as C?
}