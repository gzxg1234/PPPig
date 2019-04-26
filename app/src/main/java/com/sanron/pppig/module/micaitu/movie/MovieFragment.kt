package com.sanron.pppig.module.micaitu.movie

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.support.v7.widget.GridLayoutManager
import android.view.View
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.RadioButton
import com.sanron.pppig.R
import com.sanron.pppig.base.LazyFragment
import com.sanron.pppig.databinding.FragmentMovieBinding
import com.sanron.pppig.module.home.IMainChildFragment
import com.sanron.pppig.util.dp2px
import com.sanron.pppig.util.gap
import com.sanron.pppig.util.inflater
import com.sanron.pppig.util.pauseFrescoOnScroll

/**
 *Author:sanron
 *Time:2019/4/16
 *Description:
 */
class MovieFragment : LazyFragment<FragmentMovieBinding, MovieVM>(), IMainChildFragment {

    private lateinit var adapter: MovieAdapter
    private var bgAnim: ObjectAnimator? = null

    override fun initData() {
        setupFilter()
    }

    override fun getLayout() = R.layout.fragment_movie

    override fun createViewModel(): MovieVM? {
        return ViewModelProviders.of(this).get(MovieVM::class.java)
    }

    override fun onReselect() {
        viewModel.pageLoader.apply {
            dataBinding.recyclerView.smoothScrollToPosition(0)
            refresh()
        }
    }

    private fun setupFilter() {
        viewModel.apply {
            val refreshData = {
                if (isActive) {
                    pageLoader.refresh()
                }
            }
            checkType.observe(this@MovieFragment, Observer {
                refreshData()
            })
            checkCountry.observe(this@MovieFragment, Observer {
                refreshData()
            })
            checkYear.observe(this@MovieFragment, Observer {
                refreshData()
            })
        }
    }

    @SuppressLint("SetTextI18n")
    override fun initView() {
        dataBinding.apply {
            lifecycleOwner = this@MovieFragment
            model = viewModel
            model!!.pageLoader.lifecycleOwner = this@MovieFragment
            recyclerView.pauseFrescoOnScroll()
            recyclerView.layoutManager = GridLayoutManager(context, 3)
            recyclerView.gap(context!!.dp2px(8f), context!!.dp2px(8f))

            adapter = MovieAdapter(this@MovieFragment, viewModel.pageLoader.listData.value)
            adapter.lifecycleOwner = this@MovieFragment
            adapter.bindToRecyclerView(recyclerView)
        }
        viewModel.apply {

            toggleFilterCmd.observe(this@MovieFragment, Observer {
                showFilterWindow(it)
            })
        }
        buildFilter()
    }

    private fun showFilterWindow(show: Boolean?) {
        val DURATION = 200L
        dataBinding.apply {
            llTags.clearAnimation()
            bgAnim?.end()
            val transAnim: Animation?
            if (show == true) {
                bgAnim = ObjectAnimator.ofInt(flBg, "backgroundColor", 0x00000000, 0x40000000)
                transAnim = TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
                        Animation.RELATIVE_TO_SELF, -1f, Animation.RELATIVE_TO_SELF, 0f)
                flBg.visibility = View.VISIBLE
            } else {
                bgAnim = ObjectAnimator.ofInt(flBg, "backgroundColor", 0x30000000, 0x00000000)
                bgAnim!!.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator?) {
                        super.onAnimationEnd(animation)
                        flBg.visibility = View.GONE
                    }
                })
                transAnim = TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
                        Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, -1f)
            }
            bgAnim!!.duration = DURATION
            bgAnim!!.setEvaluator(ArgbEvaluator())
            bgAnim!!.start()
            transAnim.duration = DURATION
            llTags.startAnimation(transAnim)
        }
    }

    private fun buildFilter() {
        for ((id, type) in viewModel.TYPES.withIndex()) {
            val rb = context!!.inflater.inflate(R.layout.tag_button, dataBinding.rgType, false) as RadioButton
            rb.id = id
            rb.text = type.first
            dataBinding.rgType.addView(rb)
        }
        for ((id, type) in viewModel.COUNTRYS.withIndex()) {
            val rb = context!!.inflater.inflate(R.layout.tag_button, dataBinding.rgCountry, false) as RadioButton
            rb.id = id
            rb.text = type.first
            dataBinding.rgCountry.addView(rb)
        }
        for ((id, type) in viewModel.YEARS.withIndex()) {
            val rb = context!!.inflater.inflate(R.layout.tag_button, dataBinding.rgYear, false) as RadioButton
            rb.id = id
            rb.text = type.first
            dataBinding.rgYear.addView(rb)
        }
    }

}

