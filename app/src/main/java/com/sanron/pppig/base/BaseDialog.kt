package com.sanron.pppig.base

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.ViewGroup
import com.sanron.pppig.R

/**
 *Author:sanron
 *Time:2019/5/13
 *Description:
 */
open class BaseDialog(context: Context) : Dialog(context) {

    var width = ViewGroup.LayoutParams.WRAP_CONTENT
    var height = ViewGroup.LayoutParams.WRAP_CONTENT

    init {
        window?.setBackgroundDrawable(ColorDrawable(0))
    }

    fun setGrivity(value: Int) {
        window?.let {
            it.attributes.gravity = value
            if (value == Gravity.BOTTOM) {
                it.attributes.windowAnimations = R.style.WindowBottomAnim
            }
        }
    }

    override fun show() {
        super.show()
        window?.apply {
            val lp = attributes
            lp.height = height
            lp.width = width
            attributes = lp
        }
    }
}