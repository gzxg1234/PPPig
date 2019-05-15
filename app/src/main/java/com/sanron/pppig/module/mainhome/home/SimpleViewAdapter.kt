package com.sanron.pppig.module.mainhome.home

import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.alibaba.android.vlayout.DelegateAdapter
import com.alibaba.android.vlayout.LayoutHelper


open class SimpleViewAdapter constructor(val view: View, private val layoutHelper: LayoutHelper) : DelegateAdapter.Adapter<androidx.recyclerview.widget.RecyclerView.ViewHolder>() {
    override fun getItemCount() = 1

    override fun onCreateLayoutHelper(): LayoutHelper {
        return layoutHelper
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): androidx.recyclerview.widget.RecyclerView.ViewHolder {
        return SimpleViewHolder(view)
    }

    override fun onBindViewHolder(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int) {

    }
}

class SimpleViewHolder(view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view)