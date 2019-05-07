package com.sanron.pppig.widget

import android.support.v4.view.PagerAdapter
import android.view.View
import android.view.ViewGroup

/**
 * Author:sanron
 * Time:2019/2/21
 * Description:
 */
abstract class ViewPagerAdapter<T>(private val data: List<T>?) : PagerAdapter() {

    override fun getCount(): Int {
        return data?.size ?: 0
    }

    abstract fun getView(container: ViewGroup, position: Int, item: T): View

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = getView(container, position, data!![position])
        container.addView(view)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun isViewFromObject(view: View, o: Any): Boolean {
        return view === o
    }
}
