package com.sanron.pppig.module.splash

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import com.sanron.pppig.R
import com.sanron.pppig.base.BaseActivity
import com.sanron.pppig.base.BaseViewModel
import com.sanron.pppig.data.FetchManager
import com.sanron.pppig.databinding.ActivitySplashBinding
import com.sanron.pppig.module.main.MainActivity

/**
 * Author:sanron
 * Time:2019/5/13
 * Description:
 */
class SplashAct : BaseActivity<ActivitySplashBinding, BaseViewModel>() {
    override fun getLayout(): Int {
        return R.layout.activity_splash
    }

    override fun createViewModel(): BaseViewModel? {
        return null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FetchManager.init(this, {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, { msg ->
            AlertDialog.Builder(this)
                    .setMessage("APP初始化失败，无法启动")
                    .setNegativeButton("确定") { dialog, which ->
                        dialog.dismiss()
                        finish()
                    }.show()
        })
    }
}
