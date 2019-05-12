package com.sanron.pppig.util

import android.arch.lifecycle.LifecycleOwner
import android.view.View
import android.view.animation.Animation
import android.view.animation.RotateAnimation

/**
 *Author:sanron
 *Time:2019/5/7
 *Description:
 */
object AnimUtil {


    fun rotateCenter(v: View, from: Float, to: Float, duration: Long,lifecycleOwner: LifecycleOwner? = null): RotateAnimation {
        return RotateAnimation(from, to, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
                .apply {
                    this.duration = duration
                    this.fillAfter = true
                    v.startAnimation(this)
                    lifecycleOwner?.let {
                        bindLifecycle(lifecycleOwner)
                    }
                }
    }

}