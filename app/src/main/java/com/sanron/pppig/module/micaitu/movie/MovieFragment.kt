package com.sanron.pppig.module.micaitu.movie

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.support.v7.widget.GridLayoutManager
import android.text.TextUtils
import android.view.View
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.RadioButton
import com.sanron.pppig.R
import com.sanron.pppig.base.LazyFragment
import com.sanron.pppig.databinding.FragmentMovieBinding
import com.sanron.pppig.util.dp2px
import com.sanron.pppig.util.gap
import com.sanron.pppig.util.inflater
import com.sanron.pppig.util.pauseFrescoOnScroll
import java.util.*

/**
 *Author:sanron
 *Time:2019/4/16
 *Description:
 */
class MovieFragment : LazyFragment<FragmentMovieBinding, MovieViewModel>() {

    private lateinit var adapter: MovieAdapter
    private var bgAnim: ObjectAnimator? = null

    override fun initData() {
        viewModel!!.refreshing.value = true
        viewModel!!.loadData(true)
    }

    override fun getLayout() = R.layout.fragment_movie

    override fun createViewModel(): MovieViewModel? {
        return ViewModelProviders.of(this).get(MovieViewModel::class.java)
    }

    @SuppressLint("SetTextI18n")
    override fun initView() {
        val binding = binding!!
        val viewModel = viewModel!!
        binding.apply {
            lifecycleOwner = this@MovieFragment
            model = viewModel

            recyclerView.pauseFrescoOnScroll()
            recyclerView.layoutManager = GridLayoutManager(context, 3)
            recyclerView.gap(context!!.dp2px(8f), context!!.dp2px(8f))

            adapter = MovieAdapter(viewModel!!.data.value)
            adapter.lifecycleOwner = this@MovieFragment
            adapter.bindToRecyclerView(recyclerView)
        }
        viewModel.apply {
            data.observe(this@MovieFragment, Observer {
                adapter.notifyDataSetChanged()
            })
            val updateTagText = {
                val typeText = "类型:" + binding.root.findViewById<RadioButton>(checkType.value
                        ?: 0)?.text.toString()
                val countryText = "国家:" + binding.root.findViewById<RadioButton>(checkCountry.value
                        ?: 0)?.text.toString()
                val yearText = "年份:" + binding.root.findViewById<RadioButton>(checkYear.value
                        ?: 0)?.text.toString()
                tagsText.value = TextUtils.join(" ", arrayOf(typeText, countryText, yearText))
                refreshing.value = true
                loadData(true)
            }
            checkType.observe(this@MovieFragment, Observer {
                updateTagText()
            })
            checkCountry.observe(this@MovieFragment, Observer {
                updateTagText()
            })
            checkYear.observe(this@MovieFragment, Observer {
                yearParam = binding.root.findViewById<RadioButton>(it
                        ?: 0)?.getTag(R.id.action_bar) as String? ?: ""
                updateTagText()
            })
            toggleFilterCmd.observe(this@MovieFragment, Observer {
                showFilterWindow(it)
            })
        }
        buildYear()
    }

    private fun showFilterWindow(show: Boolean?) {
        val DURATION = 200L
        binding?.apply {
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

    /**
     * 动态添加年份标签
     */
    private fun buildYear() {
        binding!!.apply {
            val nowYear = Date().year + 1900
            val years = nowYear / 10 * 10
            //添加当前年代的年份
            for (i in nowYear downTo years) {
                val rb = context!!.inflater.inflate(R.layout.tag_button, rgYear, false) as RadioButton
                rb.id = View.generateViewId()
                rb.text = i.toString()
                rb.setTag(R.id.action_bar, i.toString())
                rgYear.addView(rb)
            }
            //添加历史年代
            for (i in years downTo 1980 step 10) {
                val rb = context!!.inflater.inflate(R.layout.tag_button, rgYear, false) as RadioButton
                rb.id = View.generateViewId()
                rb.text = "${i}年代"
                rb.setTag(R.id.action_bar, "$i,${i + 9}")
                rgYear.addView(rb)
            }
            //更早年代
            val rb = context!!.inflater.inflate(R.layout.tag_button, rgYear, false) as RadioButton
            rb.id = View.generateViewId()
            rb.text = "更早"
            rb.setTag(R.id.action_bar, "1900,1979")
            rgYear.addView(rb)
        }
    }
}

