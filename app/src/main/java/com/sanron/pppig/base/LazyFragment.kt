package com.sanron.pppig.base

import androidx.databinding.ViewDataBinding


/**
 * Author:sanron
 * Time:2018/7/26
 * Description:
 * 懒加载fragment，用于viewpager中数据懒加载
 */
abstract class LazyFragment<T : ViewDataBinding, M : BaseViewModel> : BaseFragment<T, M>() {

    private var mFirstVisible = true

    var isActive = false

    private var mDataLoaded = false

    //加载数据
    abstract fun initData()

    private fun invokeInactive() {
        isActive = false
        onInActive()
    }

    private fun invokeActive() {
        isActive = true
        onActive(mFirstVisible)
        if (mFirstVisible) {
            mFirstVisible = false
        }
        if (!mDataLoaded) {
            initData()
            mDataLoaded = true
        }
    }

    /**
     * 重置initData为false，在下一次可见时再次调用initdata
     */
    protected fun reloadDataInNextActive() {
        mDataLoaded = false
    }

    protected open fun onActive(first: Boolean) {
    }

    protected open fun onInActive() {
    }

    override fun onResume() {
        super.onResume()
        invokeActive()
    }

    override fun onPause() {
        super.onPause()
        invokeInactive()
    }

    companion object {
        private val TAG = LazyFragment::class.java.simpleName
    }

}
