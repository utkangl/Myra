package com.example.falci.internalClasses

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.view.View
import android.widget.HorizontalScrollView
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat.getColor
import com.example.falci.R
import com.example.falci.internalClasses.InternalFunctions.SetVisibilityFunctions.setViewGone
import com.example.falci.internalClasses.InternalFunctions.SetVisibilityFunctions.setViewInvisible
import com.example.falci.internalClasses.InternalFunctions.SetVisibilityFunctions.setViewVisible
import com.example.falci.internalClasses.InternalFunctions.SetVisibilityFunctions.setViewVisibleWithAnimation
import com.example.falci.internalClasses.dataClasses.postHoroscopeData

class AnimationHelper(private val context: Context) {

    fun animateBurcCardIn(burcCard: CardView, burcCardInnerLayout: RelativeLayout, MiraBurcCardTop: ImageView, MiraBurcCardTopTriangle: ImageView, backArrowCard: CardView) {
        val scale = context.resources.displayMetrics.density
        val params = burcCard.layoutParams as RelativeLayout.LayoutParams
        val newWidth = (370 * scale + 0.5f).toInt()
        val newHeight = (450 * scale + 0.5f).toInt()
        animateBurcCardSize(burcCard, newWidth, newHeight, {
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 0)
            params.addRule(RelativeLayout.CENTER_IN_PARENT, 1)
            setViewVisibleWithAnimation(context, MiraBurcCardTop, MiraBurcCardTopTriangle,backArrowCard,burcCardInnerLayout)

        }, {
//            setViewVisibleWithAnimation(context, MiraBurcCardTop, MiraBurcCardTopTriangle,backArrowCard,burcCardInnerLayout)
        })
    }



    fun animateBurcCardOut(burcCard: CardView, MiraBurcCardTop: ImageView, MiraBurcCardTopTriangle: ImageView, backArrowCard: CardView,settingsButtonCard:CardView,clickToGetHoroscopeText:TextView) {
        val scale = context.resources.displayMetrics.density
        val params = burcCard.layoutParams as RelativeLayout.LayoutParams

        val oldWidth = (340 * scale + 0.5f).toInt()
        val oldHeight = (80 * scale + 0.5f).toInt()

        val learnYourBurcButton = burcCard.findViewById<AppCompatButton>(R.id.learnYourBurcButton)
        val burcCardInnerLayout = burcCard.findViewById<RelativeLayout>(R.id.burcCardInnerLayout)
        val timeIntervalsScrollContainer = burcCard.findViewById<HorizontalScrollView>(R.id.timeIntervalsScrollContainer)

        animateBurcCardSize(burcCard,oldWidth,oldHeight,  {
            setViewInvisible(burcCardInnerLayout, learnYourBurcButton,timeIntervalsScrollContainer, MiraBurcCardTop,MiraBurcCardTopTriangle)
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
