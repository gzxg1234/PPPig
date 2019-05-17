package com.sanron.pppig.common

import android.app.Dialog
import android.content.Context
import com.sanron.pppig.util.SingleLiveEvent
import io.reactivex.Observable

/**
 *Author:sanron
 *Time:2019/5/17
 *Description:
c */

fun showLoading(context: Context): Dialog {
    val dlg = LoadingDlg(context)
    dlg.show()
    return dlg
}