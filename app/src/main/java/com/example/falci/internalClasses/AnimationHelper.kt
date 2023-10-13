package com.example.falci.internalClasses

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.view.View
import android.widget.HorizontalScrollView
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.cardview.widget.CardView
import com.example.falci.*
import com.example.falci.internalClasses.InternalFunctions.SetVisibilityFunctions.setViewGone
import com.example.falci.internalClasses.InternalFunctions.SetVisibilityFunctions.setViewInvisible
import com.example.falci.internalClasses.InternalFunctions.SetVisibilityFunctions.setViewVisible
import com.example.falci.internalClasses.InternalFunctions.SetVisibilityFunctions.setViewVisibleWithAnimation

class AnimationHelper(private val context: Context) {

    fun animateBurcCardIn(burcCard: CardView, burcCardInnerLayout: RelativeLayout, MiraBurcCardTop: ImageView, MiraBurcCardTopTriangle: ImageView, backArrowCard: CardView) {
        val scale = context.resources.displayMetrics.density
        val params = burcCard.layoutParams as RelativeLayout.LayoutParams
        val newWidth = (370 * scale + 0.5f).toInt()
        val newHeight = (450 * scale + 0.5f).toInt()
        isBurcCardOpen = true
        animateBurcCardSize(burcCard, newWidth, newHeight, {
            setViewVisibleWithAnimation(context, MiraBurcCardTop, MiraBurcCardTopTriangle,backArrowCard,burcCardInnerLayout)
        }, {
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 0)
            params.addRule(RelativeLayout.CENTER_IN_PARENT, 1)
        })
    }



    fun animateBurcCardOut(burcCard: CardView, MiraBurcCardTop: ImageView, MiraBurcCardTopTriangle: ImageView, backArrowCard: CardView,settingsButtonCard:CardView,clickToGetHoroscopeText:TextView) {
        val scale = context.resources.displayMetrics.density
        val params = burcCard.layoutParams as RelativeLayout.LayoutParams
        isBurcCardOpen = false
        isGeneralModeSelected = false
        isLoveModeSelected = false
        isCareerModeSelected = false

        val oldWidth = (340 * scale + 0.5f).toInt()
        val oldHeight = (80 * scale + 0.5f).toInt()

        val learnYourBurcButton = burcCard.findViewById<AppCompatButton>(R.id.learnYourBurcButton)
        val burcCardInnerLayout = burcCard.findViewById<RelativeLayout>(R.id.burcCardInnerLayout)
        val timeIntervalsScrollContainer = burcCard.findViewById<HorizontalScrollView>(R.id.timeIntervalsScrollContainer)

        animateBurcCardSize(burcCard,oldWidth,oldHeight,  {
            setViewInvisible(burcCardInnerLayout, learnYourBurcButton,timeIntervalsScrollContainer, MiraBurcCardTop,MiraBurcCardTopTriangle)
            burcCard.findViewById<CardView>(R.id.generalSignBackground).visibility = View.INVISIBLE
            burcCard.findViewById<CardView>(R.id.loveSignBackground).visibility = View.INVISIBLE
            burcCard.findViewById<CardView>(R.id.careerSignBackground).visibility = View.INVISIBLE
            burcCard.findViewById<ImageView>(R.id.timeIntervalDailySelectedBG).visibility = View.INVISIBLE
            burcCard.findViewById<ImageView>(R.id.timeIntervalWeeklySelectedBG).visibility = View.INVISIBLE
            burcCard.findViewById<ImageView>(R.id.timeIntervalMonthlySelectedBG).visibility = View.INVISIBLE
            burcCard.findViewById<ImageView>(R.id.timeIntervalYearlySelectedBG).visibility = View.INVISIBLE
            params.addRule(RelativeLayout.CENTER_IN_PARENT, 0)
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 1)
        }, {
            setViewVisible(settingsButtonCard,clickToGetHoroscopeText)
            setViewGone(backArrowCard)
        })
    }

     fun animateBurcCardSize(burcCard: CardView,  newWidth: Int, newHeight: Int, onAnimationStart: () -> Unit, onAnimationEnd: () -> Unit) {
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
        animatorSet.duration = 130

        animatorSet.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator) {
                onAnimationStart()
            }
            override fun onAnimationEnd(animation: Animator) {
                onAnimationEnd()
            }
        })
        animatorSet.start()
    }
}
