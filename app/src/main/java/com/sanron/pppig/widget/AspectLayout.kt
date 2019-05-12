package com.sanron.pppig.widget

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.support.annotation.AttrRes
import android.support.annotation.StyleRes
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.sanron.pppig.R

/**
 * 设置宽高比布局
 * @author chenrong
 * @date 2017/11/17
 */
class AspectLayout : FrameLayout {

    /**
     * 宽高比
     */
    private var mAspectRatio: Float = 0.toFloat()

    var aspectRatio: Float
        get() = mAspectRatio
        set(aspectRatio) {
            if (aspectRatio == mAspectRatio) {
                return
            }
            mAspectRatio = aspectRatio
            requestLayout()
        }

    constructor(context: Context) : super(context) {
        init(context, null, 0, 0)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs, 0, 0)
    }

    constructor(context: Context, attrs: AttributeSet?, @AttrRes defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs, defStyleAttr, 0)
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet?, @AttrRes defStyleAttr: Int, @StyleRes defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        init(context, attrs, defStyleAttr, defStyleRes)
    }

    private fun init(context: Context, attrs: AttributeSet?, @AttrRes defStyleAttr: Int, @StyleRes defStyleRes: Int) {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.AspectLayout)
        mAspectRatio = ta.getFloat(R.styleable.AspectLayout_cc_aspect, -1f)
        ta.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var finalWidth = widthMeasureSpec
        var finalHeight = heightMeasureSpec

        val heightPadding = paddingTop + paddingBottom
        val widthPadding = paddingLeft + paddingRight

        val layoutParams = layoutParams
        if (mAspectRatio > 0 && layoutParams != null) {
            if (shouldAdjust(layoutParams.height)) {
                val widthSpecSize = View.MeasureSpec.getSize(widthMeasureSpec)
                val desiredHeight = ((widthSpecSize - widthPadding) / mAspectRatio + heightPadding).toInt()
                val resolvedHeight = View.resolveSize(desiredHeight, heightMeasureSpec)
                finalHeight = View.MeasureSpec.makeMeasureSpec(resolvedHeight, View.MeasureSpec.EXACTLY)
            } else if (shouldAdjust(layoutParams.width)) {
                val heightSpecSize = View.MeasureSpec.getSize(finalHeight)
                val desiredWidth = ((heightSpecSize - heightPadding) * mAspectRatio + widthPadding).toInt()
                val resolvedWidth = View.resolveSize(desiredWidth, widthMeasureSpec)
                finalWidth = View.MeasureSpec.makeMeasureSpec(resolvedWidth, View.MeasureSpec.EXACTLY)
            }
        }

        super.onMeasure(finalWidth, finalHeight)
    }

    private fun shouldAdjust(layoutDimension: Int): Boolean {
        // Note: wrap_content is supported for backwards compatibility, but should not be used.
        return layoutDimension == 0 || layoutDimension == ViewGroup.LayoutParams.WRAP_CONTENT
    }
}
