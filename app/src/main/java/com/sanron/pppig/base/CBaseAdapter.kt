package com.sanron.pppig.base


import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

/**
 * Author:sanron
 * Time:2018/9/12
 * Description:
 * 基类RecyclerViewAdapter
 */
abstract class CBaseAdapter<T, V : BaseViewHolder> : BaseQuickAdapter<T, V> {

    constructor(layoutResId: Int, data: List<T>?) : super(layoutResId, data) {
        setLoadMoreView(CLoadMoreView())
    }

    constructor(data: List<T>?) : super(data) {
        setLoadMoreView(CLoadMoreView())
    }

    constructor(layoutResId: Int) : super(layoutResId) {
        setLoadMoreView(CLoadMoreView())
    }

}
