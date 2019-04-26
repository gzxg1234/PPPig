package com.sanron.pppig.base

import android.databinding.ViewDataBinding
import android.support.annotation.CallSuper

import com.sanron.pppig.util.CLog


/**
 * Author:sanron
 * Time:2018/7/26
 * Description:
 * 懒加载fragment，用于viewpager中数据懒加载
 */
abstract class LazyFragment<T : ViewDataBinding, M : BaseViewModel> : BaseFragment<T, M>() {

    private var mFirstVisible = true

    var isActive = false

    private var mInitedData = false

    //加载数据
    abstract fun initData()

    @CallSuper
    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        val change = isVisibleToUser != userVisibleHint
        super.setUserVisibleHint(isVisibleToUser)
        if (isResumed && change) {
            if (userVisibleHint) {
                invokeVisible()
            } else {
                invokeInvisible()
            }
        }
    }

    private fun invokeInvisible() {
        isActive = false
        onInvisible()
    }

    private fun invokeVisible() {
        isActive = true
        onVisible(mFirstVisible)
        if (mFirstVisible) {
            mFirstVisible = false
        }
        if (!mInitedData) {
            initData()
            mInitedData = true
        }
    }

    /**
     * 重置initData为false，在下一次可见时再次调用initdata
     */
    protected fun reInitDataInVisible() {
        mInitedData = false
    }

    protected open fun onVisible(first: Boolean) {
        CLog.d(TAG, this.javaClass.simpleName + " onVisible")
    }

    protected open fun onInvisible() {
        CLog.d(TAG, this.javaClass.simpleName + " onInvisible")
    }


    override fun onResume() {
        super.onResume()
        if (userVisibleHint && !isHidden) {
            invokeVisible()
        }
    }

    override fun onPause() {
        super.onPause()
        if (userVisibleHint && !isHidden) {
            onInvisible()
        }
    }

    companion object {

        private val TAG = LazyFragment::class.java.simpleName
    }

}
