package com.sanron.pppig.module.micaitu.play

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.res.Configuration
import android.os.Bundle
import com.sanron.pppig.R
import com.sanron.pppig.base.BaseActivity
import com.sanron.pppig.common.LoadingView
import com.sanron.pppig.databinding.ActivityPlayerBinding
import com.sanron.pppig.util.showToast
import com.sanron.pppig.widget.player.DataSource

/**
 * Author:sanron
 * Time:2019/5/8
 * Description:
 */
class PlayerAct : BaseActivity<ActivityPlayerBinding, PlayerVM>() {


    companion object {
        const val ARG_URL = "url"
        const val ARG_SOURCE_ITEMS = "items"
        const val ARG_TITLE = "title"
    }

    val loadingView: LoadingView by lazy {
        LoadingView(this)
    }

    override fun createViewModel(): PlayerVM {
        return ViewModelProviders.of(this).get(PlayerVM::class.java)
    }

    override fun getLayout(): Int {
        return R.layout.activity_player
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val url = intent?.getStringExtra(ARG_URL)
        val title = intent?.getStringExtra(ARG_TITLE)
        val itmes = intent?.getStringExtra(ARG_SOURCE_ITEMS)
        if (url.isNullOrEmpty()) {
            finish()
            showToast("无效url")
            return
        }
        viewModel.url = url
        dataBinding.lifecycleOwner = this
        dataBinding.model = viewModel
        dataBinding.ivBack.setOnClickListener {
            finish()
        }

        dataBinding.playerView.setTitle(title ?: "")
        viewModel.videoSourceList.observe(this, Observer {
            if (!it.isNullOrEmpty()) {
                dataBinding.playerView.setDataSource(DataSource(it[0]))
                dataBinding.playerView.prepare()
                dataBinding.playerView.start()
            }
        })

        viewModel.loading.observe(this, Observer {
            it?.let {
                if (it) {
                    loadingView.show()
                } else {
                    loadingView.hide()
                }
            }
        })
    }

    private fun fullScreen() {

    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)

    }

    override fun onBackPressed() {
        if(dataBinding.playerView.onBackpress()){
            return
        }
        super.onBackPressed()
    }

    override fun onDestroy() {
        dataBinding.playerView.release()
        super.onDestroy()
    }

    override fun initData() {
        super.initData()
        viewModel.loadData()
    }
}
