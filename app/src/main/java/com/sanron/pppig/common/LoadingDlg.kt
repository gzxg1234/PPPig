package com.sanron.pppig.common

import android.content.Context
import android.os.Bundle
import com.sanron.pppig.R
import com.sanron.pppig.base.BaseDialog

/**
 *Author:sanron
 *Time:2019/5/17
 *Description:
 */
class LoadingDlg(context: Context) : BaseDialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dlg_loading)
        setCanceledOnTouchOutside(false)
        setCancelable(true)
    }
}