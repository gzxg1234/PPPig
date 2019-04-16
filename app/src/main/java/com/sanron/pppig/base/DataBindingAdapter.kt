package com.sanron.pppig.base


import android.arch.lifecycle.LifecycleOwner
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.view.View
import android.view.ViewGroup

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

/**
 * Author:sanron
 * Time:2018/9/12
 * Description:
 * 基类RecyclerViewAdapter
 */
abstract class DataBindingAdapter<T, B : ViewDataBinding> : BaseQuickAdapter<T, BaseViewHolder> {

    constructor(layoutResId: Int, data: List<T>?) : super(layoutResId, data) {}

    constructor(data: List<T>?) : super(data) {}

    constructor(layoutResId: Int) : super(layoutResId) {}

    var lifecycleOwner: LifecycleOwner? = null

    override fun convert(helper: BaseViewHolder, item: T) {
        val dataBinding = DataBindingUtil.getBinding<B>(helper.itemView)
        bind(dataBinding!!, item)
    }

    protected fun createDataBinding(dataBinding: B) {}

    protected abstract fun bind(dataBinding: B, item: T)

    override fun getItemView(layoutResId: Int, parent: ViewGroup): View {
        val viewDataBinding = DataBindingUtil.inflate<ViewDataBinding>(mLayoutInflater, layoutResId, parent, false)
        viewDataBinding?.apply {
            lifecycleOwner = this@DataBindingAdapter.lifecycleOwner
        }
        return viewDataBinding.root
    }
}
