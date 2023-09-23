package com.example.falci.internalClasses

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.widget.RelativeLayout
import android.widget.ScrollView
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
    private lateinit var learnYourBurcButton: AppCompatButton
    private lateinit var backArrowCard: CardView
    private lateinit var settingsButtonCard: CardView
    private lateinit var zodiacSign: CardView
    private lateinit var burcCardExplanationTextScroll: ScrollView


    fun initializeViews(
        burcCard: CardView,
        generalSign: CardView,
        loveSign: CardView,
        careerSign: CardView,
        dailyButton: AppCompatButton,
        monthlyButton: AppCompatButton,
        yearlyButton: AppCompatButton,
        learnYourBurcButton: AppCompatButton,
        backArrowCard: CardView,
        settingsButtonCard: CardView,
        zodiac_sign: CardView,
        burcCardExplanationTextScroll: ScrollView,
    ) {
        this.burcCard = burcCard
        this.generalSign = generalSign
        this.loveSign = loveSign
        this.careerSign = careerSign
        this.dailyButton = dailyButton
        this.monthlyButton = monthlyButton
        this.yearlyButton = yearlyButton
        this.learnYourBurcButton = learnYourBurcButton
        this.backArrowCard = backArrowCard
        this.settingsButtonCard = settingsButtonCard
        this.zodiacSign = zodiac_sign
        this.burcCardExplanationTextScroll = burcCardExplanationTextScroll
    }

    fun animateBurcCardIn() {
        val scale = context.resources.displayMetrics.density
        val newWidth = (370 * scale + 0.5f).toInt()
        val newHeight = (600 * scale + 0.5f).toInt()

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
            burcCard.layoutParams = params
        }

        val animatorSet = AnimatorSet()
        animatorSet.playTogether(animatorWidth, animatorHeight)
        animatorSet.duration = 300

        animatorSet.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                setViewVisible(backArrowCard, generalSign, loveSign, careerSign, zodiacSign, burcCardExplanationTextScroll)
            }

            override fun onAnimationStart(animation: Animator) {
                setViewGone(dailyButton, monthlyButton, yearlyButton, learnYourBurcButton, settingsButtonCard)
                params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 0)
                params.addRule(RelativeLayout.CENTER_IN_PARENT, 1)
            }
        })

        animatorSet.start()

        val generalSignCardParams = generalSign.layoutParams as RelativeLayout.LayoutParams
        val loveSignCardParams = loveSign.layoutParams as RelativeLayout.LayoutParams
        val careerSignCardParams = careerSign.layoutParams as RelativeLayout.LayoutParams

        val dpMarginBottom = 25
        val newMarginBottom = (dpMarginBottom * scale + 0.5f).toInt()

        generalSignCardParams.bottomMargin = newMarginBottom
        loveSignCardParams.bottomMargin = newMarginBottom
        careerSignCardParams.bottomMargin = newMarginBottom

        generalSign.layoutParams = generalSignCardParams
        loveSign.layoutParams = loveSignCardParams
        careerSign.layoutParams = careerSignCardParams
    }


    fun animateBurcCardOut() {
        val scale = context.resources.displayMetrics.density
        val oldWidth = (340 * scale + 0.5f).toInt()
        val oldHeight = (80 * scale + 0.5f).toInt()

        val params = burcCard.layoutParams as RelativeLayout.LayoutParams

        val animatorWidth = ValueAnimator.ofInt(params.width, oldWidth)
        animatorWidth.addUpdateListener { animation ->
            val value = animation.animatedValue as Int
            params.width = value
//            params.bottomMargin = burcCardMarginBottom
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
        animatorSet.duration = 300

        animatorSet.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator) {
                setViewGone(generalSign, loveSign, careerSign, dailyButton, monthlyButton, yearlyButton, learnYourBurcButton, zodiacSign, burcCardExplanationTextScroll)
                params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 1)
            }

            override fun onAnimationEnd(animation: Animator) {
                setViewVisible( settingsButtonCard)
                setViewGone(backArrowCard)
            }
        })

        animatorSet.start()

        val generalSignCardParams = generalSign.layoutParams as RelativeLayout.LayoutParams
        val loveSignCardParams = loveSign.layoutParams as RelativeLayout.LayoutParams
        val careerSignCardParams = careerSign.layoutParams as RelativeLayout.LayoutParams

        val dpMarginBottom = 25
        val newMarginBottom = (dpMarginBottom * scale + 0.5f).toInt()

        generalSignCardParams.bottomMargin = newMarginBottom
        loveSignCardParams.bottomMargin = newMarginBottom
        careerSignCardParams.bottomMargin = newMarginBottom

        generalSign.layoutParams = generalSignCardParams
        loveSign.layoutParams = loveSignCardParams
        careerSign.layoutParams = careerSignCardParams

        generalSign.setCardBackgroundColor(getColor(context, R.color.passivesigncard))
        loveSign.setCardBackgroundColor(getColor(context,R.color.passivesigncard))
        careerSign.setCardBackgroundColor(getColor(context,R.color.passivesigncard))
    }

}