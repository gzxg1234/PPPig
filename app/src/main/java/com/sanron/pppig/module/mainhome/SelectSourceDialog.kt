package com.sanron.pppig.module.mainhome

import android.content.Context
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import com.blankj.utilcode.util.ScreenUtils
import com.chad.library.adapter.base.BaseViewHolder
import com.sanron.datafetch_interface.Source
import com.sanron.pppig.R
import com.sanron.pppig.base.BaseDialog
import com.sanron.pppig.base.CBaseAdapter
import com.sanron.pppig.data.FetchManager
import com.sanron.pppig.util.getColorCompat
import com.sanron.pppig.widget.LimitHeightRecyclerView

class SelectSourceDialog(context: Context,val onChange: () -> Unit) : BaseDialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setGrivity(Gravity.BOTTOM)
        setContentView(R.layout.dlg_select_source)
        width = ViewGroup.LayoutParams.MATCH_PARENT
        height = ViewGroup.LayoutParams.WRAP_CONTENT
        val listView = findViewById<LimitHeightRecyclerView>(R.id.recycler_view)
        var itemAdapter = ItemAdapter(context)
        listView.maxHeight = (ScreenUtils.getScreenHeight() * 0.5f).toInt()
        val curSourcePos = FetchManager.currentSourceIndex()
        listView.layoutManager = LinearLayoutManager(context)
        listView.itemAnimator = null
        itemAdapter.bindToRecyclerView(listView)
        itemAdapter.setNewData(FetchManager.sourceManager.getSourceList())
        itemAdapter.selectedPos = curSourcePos
        itemAdapter.setOnItemClickListener { adapter, view, position ->
            itemAdapter.selectedPos = position
        }
        findViewById<View>(R.id.btn_ok).setOnClickListener {
            dismiss()
            if (itemAdapter.selectedPos != curSourcePos) {
                FetchManager.changeSource(itemAdapter.getItem(itemAdapter.selectedPos)!!.id, true)
                onChange()
            }
        }
        findViewById<View>(R.id.btn_cancel).setOnClickListener {
            cancel()
        }
    }

    class ItemAdapter(context: Context) : CBaseAdapter<Source, BaseViewHolder>(context, R.layout.item_source) {

        var selectedPos: Int = -1
            set(value) {
                val old = field
                field = value
                notifyItemChanged(old)
                notifyItemChanged(field)
            }

        override fun convert(helper: BaseViewHolder?, item: Source?) {
            val tvName = helper?.getView<TextView>(R.id.tv_text)!!
            val rb = helper.getView<RadioButton>(R.id.rb_check)!!
            tvName.text = item?.name
            if (selectedPos == helper.adapterPosition) {
                tvName.setTextColor(context.getColorCompat(R.color.colorAccent))
                rb.visibility = View.VISIBLE
            } else {
                tvName.setTextColor(context.getColorCompat(R.color.textColor1))
                rb.visibility = View.GONE
            }
        }
    }
}