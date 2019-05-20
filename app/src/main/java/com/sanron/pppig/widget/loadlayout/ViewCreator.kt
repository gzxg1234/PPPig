package com.sanron.pppig.widget.loadlayout

import android.view.LayoutInflater
import android.view.View
import androidx.annotation.LayoutRes

/**
 *Author:sanron
 *Time:2019/5/20
 *Description:
 */
abstract class ViewCreator {

    private var rootView: View? = null

    internal var onReloadListener: (() -> Unit)? = null

    internal var viewCreated = false

    abstract fun onCreateView(loadLayout: LoadLayout): View

    protected open fun onViewCreated(view: View) {}

    internal open fun onHide() {}

    internal open fun onShow() {}

    internal fun getRootView(loadLayout: LoadLayout): View? {
        if (rootView == null) {
            rootView = onCreateView(loadLayout)
            rootView!!.visibility = View.GONE
        }
        viewCreated = true
        onViewCreated(rootView!!)
        return rootView
    }

    open class SimpleViewCreator private constructor(private val view: View?, private val layoutRes: Int?) : ViewCreator() {

        constructor(@LayoutRes res: Int) : this(null, res)
        constructor(view: View) : this(view, null)

        override fun onCreateView(loadLayout: LoadLayout): View {
            if (layoutRes != null) {
                return LayoutInflater.from(loadLayout.context).inflate(layoutRes!!, loadLayout, false)
            } else {
                return view!!
            }
        }
    }
}