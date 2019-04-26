package com.sanron.pppig.module.micaitu.moviedetail

import android.animation.ValueAnimator
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.graphics.ImageDecoder
import android.graphics.drawable.Animatable
import android.os.Build
import android.os.Bundle
import android.support.design.animation.ArgbEvaluatorCompat
import android.support.v4.app.ActivityCompat
import android.support.v7.graphics.Palette
import android.transition.Fade
import android.transition.TransitionSet
import android.util.TypedValue
import android.view.ViewGroup
import android.widget.TextView
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.controller.BaseControllerListener
import com.facebook.drawee.drawable.ScalingUtils
import com.facebook.drawee.view.DraweeTransition
import com.facebook.imagepipeline.image.ImageInfo
import com.sanron.lib.StatusBarHelper
import com.sanron.pppig.R
import com.sanron.pppig.base.BaseActivity
import com.sanron.pppig.databinding.ActivityMovieDetailBinding
import com.sanron.pppig.util.ColorUtil
import com.sanron.pppig.util.FrescoUtil
import com.sanron.pppig.util.dp2px


/**
 * Author:sanron
 * Time:2019/4/24
 * Description:
 */
class MovieDetailAct : BaseActivity<ActivityMovieDetailBinding, MovieDetailVM>() {

    companion object {
        val ARG_URL = "url"
        val ARG_IMG_URL = "img_url"
        var ARG_NAME = "name"
    }

    override val layout: Int
        get() = R.layout.activity_movie_detail

    override fun createViewModel(): MovieDetailVM {
        return ViewModelProviders.of(this).get(MovieDetailVM::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding.apply {
            lifecycleOwner = this@MovieDetailAct
            model = viewModel
            StatusBarHelper.with(this@MovieDetailAct)
                    .setStatusBarColor(0)
                    .setLayoutBelowStatusBar(true)
                    .setPaddingTop(flTopWrap)
                    .setPaddingTop(scrollView)
            viewModel.image.observe(this@MovieDetailAct, Observer {
                //提取图片的柔和暗色
                it?.let {
                    FrescoUtil.getBitmap(it) {
                        Palette.from(it!!).generate {
                            var color = it?.mutedSwatch?.rgb
                                    ?: it?.vibrantSwatch?.rgb
                                    ?: it?.darkMutedSwatch?.rgb
                                    ?: it?.darkVibrantSwatch?.rgb
                                    ?: it?.lightMutedSwatch?.rgb
                                    ?: it?.lightVibrantSwatch?.rgb
                                    ?: resources.getColor(R.color.colorPrimaryDark)
                            color = ColorUtil.covertToDark(color)
                            val anim = ValueAnimator.ofInt(0xFF999999.toInt(), color)
                            anim.setEvaluator(ArgbEvaluatorCompat.getInstance())
                            anim.duration = 500
                            anim.addUpdateListener {
                                scrollView.setBackgroundColor(it.animatedValue as Int)
                            }
                            anim.start()
                        }
                    }
                }
            })
            sdvImage.controller = Fresco.newDraweeControllerBuilder().apply {
                oldController = sdvImage.controller
                controllerListener = object : BaseControllerListener<ImageInfo>() {
                    override fun onFailure(id: String?, throwable: Throwable?) {
                        super.onFailure(id, throwable)
                    }

                    override fun onRelease(id: String?) {
                        super.onRelease(id)
                    }

                    override fun onFinalImageSet(id: String?, imageInfo: ImageInfo?, animatable: Animatable?) {
                        super.onFinalImageSet(id, imageInfo, animatable)
                    }
                }
            }.build()
            ivBack.setOnClickListener {
                ActivityCompat.finishAfterTransition(this@MovieDetailAct)
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.enterTransition = Fade()
                window.exitTransition = Fade()
                val transitionSet = TransitionSet()
                transitionSet.addTransition(DraweeTransition.createTransitionSet(ScalingUtils.ScaleType.CENTER_CROP, ScalingUtils.ScaleType.CENTER_CROP))
                transitionSet.addTarget(sdvImage)
                window.sharedElementEnterTransition = transitionSet
                window.sharedElementReturnTransition = transitionSet
            }
        }
        viewModel.apply {
            viewModel.url = intent?.getStringExtra(ARG_URL)
            viewModel.image.value = intent?.getStringExtra(ARG_IMG_URL)
            viewModel.title.value = intent?.getStringExtra(ARG_NAME)
            infoList.observe(this@MovieDetailAct, Observer {
                it?.forEach {
                    val tv = TextView(activity)
                    tv.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT)
                    tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, activity.dp2px(14f).toFloat())
                    tv.maxLines = 2
                    tv.text = it
                    activity.resources.getColor(R.color.white).let { it1 -> tv.setTextColor(it1) }

                    dataBinding.llInfos.addView(tv)
                }
            })
        }
        viewModel.loadData()
    }
}
