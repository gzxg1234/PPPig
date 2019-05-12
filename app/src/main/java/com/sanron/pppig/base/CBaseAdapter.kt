package com.sanron.pppig.base


import android.content.Context
import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.sanron.pppig.R

/**
 * Author:sanron
 * Time:2018/9/12
 * Description:
 * 基类RecyclerViewAdapter
 */
abstract class CBaseAdapter<T, V : BaseViewHolder>(context: Context, layoutResId: Int, data: List<T>? = null)
    : BaseQuickAdapter<T, V>(layoutResId, data) {


    init {
        setLoadMoreView(CLoadMoreView())
        emptyView = View.inflate(context, R.layout.layout_empty_list, null)
    }

}
