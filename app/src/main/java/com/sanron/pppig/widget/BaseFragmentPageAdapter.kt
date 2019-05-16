package com.sanron.pppig.widget

import android.util.SparseArray
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

/**
 *Author:sanron
 *Time:2019/5/16
 *Description:
 */
abstract class BaseFragmentPageAdapter(private val fm: FragmentManager) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private val fragmentTagList = mutableListOf<String>()


    fun clearFragments() {
        val transaction = fm.beginTransaction()
        fragmentTagList.forEach { tag ->
            fm.findFragmentByTag(tag)?.let { fragment ->
                transaction.remove(fragment)
            }
        }
        transaction.commitNowAllowingStateLoss()
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        fragmentTagList.add(makeFragmentName(container.id, getItemId(position)))
        return super.instantiateItem(container, position)
    }

    companion object {
        fun makeFragmentName(viewId: Int, id: Long): String {
            return "android:switcher:" + viewId + ":" + id
        }
    }
}