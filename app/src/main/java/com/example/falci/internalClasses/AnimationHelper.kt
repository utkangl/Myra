package com.example.falci.internalClasses

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat.getColor
import com.example.falci.R
import com.example.falci.internalClasses.InternalFunctions.SetVisibilityFunctions.setViewGone
import com.example.falci.internalClasses.InternalFunctions.SetVisibilityFunctions.setViewVisible

class AnimationHelper(private val context: Context) {

    private lateinit var burcCard: CardView
    private lateinit var generalSign: CardView
    private lateinit var loveSign: CardView
    private lateinit var careerSign: CardView
    private lateinit var dailyButton: AppCompatButton
    private lateinit var monthlyButton: AppCompatButton
    private lateinit var yearlyButton: AppCompatButton
    private lateinit var learnyourburcButton: AppCompatButton
    private lateinit var backarrowcard: CardView
    private lateinit var tarotFali: CardView
    private lateinit var fortuneCookie: CardView
    private lateinit var settingsbuttoncard: CardView
    private lateinit var chatwithmiraButton: ImageView
    private lateinit var empty: TextView

    fun initializeViews(
        burcCard: CardView,
        generalSign: CardView,
        loveSign: CardView,
        careerSign: CardView,
        dailyButton: AppCompatButton,
        monthlyButton: AppCompatButton,
        yearlyButton: AppCompatButton,
        learnyourburcButton: AppCompatButton,
        backarrowcard: CardView,
        fortuneCookie: CardView,
        tarotFali: CardView,
        settingsbuttoncard: CardView,
        chatwithmiraButton: ImageView,
        empty: TextView
    ) {
        this.burcCard = burcCard
        this.generalSign = generalSign
        this.loveSign = loveSign
        this.careerSign = careerSign
        this.dailyButton = dailyButton
        this.monthlyButton = monthlyButton
        this.yearlyButton = yearlyButton
        this.learnyourburcButton = learnyourburcButton
        this.backarrowcard = backarrowcard
        this.tarotFali = tarotFali
        this.fortuneCookie = fortuneCookie
        this.settingsbuttoncard = settingsbuttoncard
        this.chatwithmiraButton = chatwithmiraButton
        this.empty = empty
    }

    fun animateBurcCardIn() {
        val scale = context.resources.displayMetrics.density
        val newWidth = (370 * scale + 0.5f).toInt()
        val newHeight = (550 * scale + 0.5f).toInt()
        val burcCardMarginBottom = (-190 * scale + 0.5f).toInt()

        val params = burcCard.layoutParams as RelativeLayout.LayoutParams

        val animatorWidth = ValueAnimator.ofInt(burcCard.width, newWidth)
        animatorWidth.addUpdateListener { animation ->
            val value = animation.animatedValue as Int
            params.width = value
            burcCard.layoutParams = params
        }

        val animatorHeight = ValueAnimator.ofInt(burcCard.height, newHeight)
        animatorHeight.addUpdateListener { animation ->
            val value = animation.animatedValue as Int
            params.height = value
            params.bottomMargin = burcCardMarginBottom
            burcCard.layoutParams = params
        }

        val animatorSet = AnimatorSet()
        animatorSet.playTogether(animatorWidth, animatorHeight)
        animatorSet.duration = 500

        animatorSet.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                setViewVisible(backarrowcard, generalSign, loveSign, careerSign)
            }

            override fun onAnimationStart(animation: Animator) {
                setViewGone(chatwithmiraButton, tarotFali, fortuneCookie, dailyButton, monthlyButton, yearlyButton, learnyourburcButton, settingsbuttoncard)
                params.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE)
            }
        })

        animatorSet.start()

        val generalSignCardparams = generalSign.layoutParams as RelativeLayout.LayoutParams
        val loveSignCardparams = loveSign.layoutParams as RelativeLayout.LayoutParams
        val careerSignCardparams = careerSign.layoutParams as RelativeLayout.LayoutParams

        val dpMarginBottom = 25
        val newMarginBottom = (dpMarginBottom * scale + 0.5f).toInt()

        generalSignCardparams.bottomMargin = newMarginBottom
        loveSignCardparams.bottomMargin = newMarginBottom
        careerSignCardparams.bottomMargin = newMarginBottom

        generalSign.layoutParams = generalSignCardparams
        loveSign.layoutParams = loveSignCardparams
        careerSign.layoutParams = careerSignCardparams
    }


    fun animateBurcCardOut() {
        val scale = context.resources.displayMetrics.density
        val oldWidth = (340 * scale + 0.5f).toInt()
        val oldHeight = (200 * scale + 0.5f).toInt()
        val burcCardMarginBottom = (-190 * scale + 0.5f).toInt()

        val params = burcCard.layoutParams as RelativeLayout.LayoutParams

        val animatorWidth = ValueAnimator.ofInt(params.width, oldWidth)
        animatorWidth.addUpdateListener { animation ->
            val value = animation.animatedValue as Int
            params.width = value
            params.bottomMargin = burcCardMarginBottom
            burcCard.layoutParams = params
        }

        val animatorHeight = ValueAnimator.ofInt(params.height, oldHeight)
        animatorHeight.addUpdateListener { animation ->
            val value = animation.animatedValue as Int
            params.height = value
            burcCard.layoutParams = params
        }

        val animatorSet = AnimatorSet()
        animatorSet.playTogether(animatorWidth, animatorHeight)
        animatorSet.duration = 500

        animatorSet.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator) {
                setViewGone(generalSign, loveSign, careerSign, dailyButton, monthlyButton, yearlyButton, learnyourburcButton)
                setViewVisible(empty, settingsbuttoncard)
            }

            override fun onAnimationEnd(animation: Animator) {
                setViewVisible(chatwithmiraButton, tarotFali, fortuneCookie)
                setViewGone(backarrowcard)
            }
        })

        animatorSet.start()

        val generalSignCardparams = generalSign.layoutParams as RelativeLayout.LayoutParams
        val loveSignCardparams = loveSign.layoutParams as RelativeLayout.LayoutParams
        val careerSignCardparams = careerSign.layoutParams as RelativeLayout.LayoutParams

        val dpMarginBottom = 25
        val newMarginBottom = (dpMarginBottom * scale + 0.5f).toInt()

        generalSignCardparams.bottomMargin = newMarginBottom
        loveSignCardparams.bottomMargin = newMarginBottom
        careerSignCardparams.bottomMargin = newMarginBottom

        generalSign.layoutParams = generalSignCardparams
        loveSign.layoutParams = loveSignCardparams
        careerSign.layoutParams = careerSignCardparams

        generalSign.setCardBackgroundColor(getColor(context, R.color.passivesigncard))
        loveSign.setCardBackgroundColor(getColor(context,R.color.passivesigncard))
        careerSign.setCardBackgroundColor(getColor(context,R.color.passivesigncard))
    }

    fun animateBurcCardOutt() {
        val scale = context.resources.displayMetrics.density
        val oldWidth = (340 * scale + 0.5f).toInt()
        val oldHeight = (200 * scale + 0.5f).toInt()
        val burcCardMarginBottom = (-190 * scale + 0.5f).toInt()

        val params = burcCard.layoutParams as RelativeLayout.LayoutParams

        val animatorWidth = ValueAnimator.ofInt(params.width, oldWidth)
        animatorWidth.addUpdateListener { animation ->
            val value = animation.animatedValue as Int
            params.width = value
            params.bottomMargin = burcCardMarginBottom
            burcCard.layoutParams = params
        }

        val animatorHeight = ValueAnimator.ofInt(params.height, oldHeight)
        animatorHeight.addUpdateListener { animation ->
            val value = animation.animatedValue as Int
            params.height = value
            burcCard.layoutParams = params
        }

        val animatorSet = AnimatorSet()
        animatorSet.playTogether(animatorWidth, animatorHeight)
        animatorSet.duration = 500

        animatorSet.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator) {
                setViewGone(generalSign, loveSign, careerSign, dailyButton, monthlyButton, yearlyButton, learnyourburcButton)
                setViewVisible(empty, settingsbuttoncard)
            }

            override fun onAnimationEnd(animation: Animator) {
                setViewGone(backarrowcard)
            }
        })

        animatorSet.start()

        val generalSignCardparams = generalSign.layoutParams as RelativeLayout.LayoutParams
        val loveSignCardparams = loveSign.layoutParams as RelativeLayout.LayoutParams
        val careerSignCardparams = careerSign.layoutParams as RelativeLayout.LayoutParams

        val dpMarginBottom = 25
        val newMarginBottom = (dpMarginBottom * scale + 0.5f).toInt()

        generalSignCardparams.bottomMargin = newMarginBottom
        loveSignCardparams.bottomMargin = newMarginBottom
        careerSignCardparams.bottomMargin = newMarginBottom

        generalSign.layoutParams = generalSignCardparams
        loveSign.layoutParams = loveSignCardparams
        careerSign.layoutParams = careerSignCardparams

        generalSign.setCardBackgroundColor(getColor(context, R.color.passivesigncard))
        loveSign.setCardBackgroundColor(getColor(context,R.color.passivesigncard))
        careerSign.setCardBackgroundColor(getColor(context,R.color.passivesigncard))
    }
}