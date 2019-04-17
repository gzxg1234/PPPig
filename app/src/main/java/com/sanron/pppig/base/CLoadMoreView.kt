package com.sanron.pppig.base

import com.chad.library.adapter.base.loadmore.LoadMoreView
import com.sanron.pppig.R

/**
 *Author:sanron
 *Time:2019/4/17
 *Description:
 */
class CLoadMoreView:LoadMoreView(){

    override fun getLayoutId(): Int {
        return R.layout.load_more
    }

    override fun getLoadingViewId(): Int {
        return R.id.loading
    }

    override fun getLoadEndViewId(): Int {
        return R.id.end
    }

    override fun getLoadFailViewId(): Int {
        return R.id.fail
    }

}