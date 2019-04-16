package com.sanron.pppig.module.micaitu.movie

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
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
import com.sanron.pppig.util.CLog
import com.sanron.pppig.util.pauseFrescoOnScroll

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

    override fun initView() {
        val binding = binding!!
        val viewModel = viewModel!!
        binding.apply {
            lifecycleOwner = this@MovieFragment
            model = viewModel
            adapter = MovieAdapter(viewModel!!.data.value)
            adapter.lifecycleOwner = this@MovieFragment
            adapter.setEnableLoadMore(true)
            adapter.setOnLoadMoreListener({
                viewModel.loadData(false)
            }, recyclerView)
            recyclerView.pauseFrescoOnScroll()
            recyclerView.adapter = adapter
            recyclerView.layoutManager = GridLayoutManager(context, 3)
        }
        viewModel.apply {
            data.observe(this@MovieFragment, Observer {
                adapter.notifyDataSetChanged()
            })
            hasMore.observe(this@MovieFragment, Observer {
                if (it == false) {
                    adapter.loadMoreEnd()
                }
            })
            loading.observe(this@MovieFragment, Observer {
                if (it == false) {
                    adapter.loadMoreComplete()
                }
            })
            checkType.observe(this@MovieFragment, Observer {
                CLog.d("ss","asdasdasdasdasd")
            })
//            tagsText.apply {
//                val onChange = { t: Int? ->
//                    var typeText = ""
//                    if (checkType.value != R.id.rb_type_all) {
//                        typeText = "类型:" + binding.root.findViewById<RadioButton>(checkType.value
//                                ?: 0)?.text
//                    }
//                    var countryText = ""
//                    if (checkCountry.value != R.id.rb_country_all) {
//                        countryText = "国家:" + binding.root.findViewById<RadioButton>(checkCountry.value
//                                ?: 0)?.text
//                    }
//                    var yearText = ""
//                    if (checkYear.value != R.id.rb_year_all) {
//                        yearText = "年份:" + binding.root.findViewById<RadioButton>(checkYear.value
//                                ?: 0)?.text
//                    }
//                    value = TextUtils.join(" ", arrayOf(typeText, countryText, yearText))
//                    viewModel.loadData(true)
//                }
//                addSource(checkType, onChange)
//                addSource(checkCountry, onChange)
//                addSource(checkYear, onChange)
//            }
            toggleFilterCmd.observe(this@MovieFragment, Observer {
                val DURATION = 200L
                binding.llTags.clearAnimation()
                bgAnim?.end()
                var transAnim: Animation?
                if (it == true) {
                    bgAnim = ObjectAnimator.ofInt(binding.flBg, "backgroundColor", 0x00000000, 0x40000000)
                    transAnim = TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
                            Animation.RELATIVE_TO_SELF, -1f, Animation.RELATIVE_TO_SELF, 0f)
                    binding.flBg.visibility = View.VISIBLE
                } else {
                    bgAnim = ObjectAnimator.ofInt(binding.flBg, "backgroundColor", 0x30000000, 0x00000000)
                    bgAnim!!.addListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator?) {
                            super.onAnimationEnd(animation)
                            binding.flBg.visibility = View.GONE
                        }
                    })
                    transAnim = TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
                            Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, -1f)
                }
                bgAnim!!.duration = DURATION
                bgAnim!!.setEvaluator(ArgbEvaluator())
                bgAnim!!.start()
                transAnim.duration = DURATION
                binding.llTags.startAnimation(transAnim)
            })
        }
    }

}