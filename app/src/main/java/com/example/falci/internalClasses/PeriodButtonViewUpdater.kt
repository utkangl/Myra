package com.example.falci.internalClasses

import android.app.Activity
import android.content.Context
import android.widget.Button
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatButton
import androidx.cardview.widget.CardView
import com.example.falci.R
import com.example.falci.internalClasses.InternalFunctions.SetVisibilityFunctions.setViewVisible

//class PeriodButtonViewUpdater(private val context: Context) {
//    fun  updateButtonView(button: Button, animationHelper: AnimationHelper) {
//        val scale = context.resources.displayMetrics.density
//
//        val newHeight = (780 * scale + 0.5f).toInt()
//        val newWidth = (370 * scale + 0.5f).toInt()
////        val burcCardMarginBottom = (-370 * scale + 0.5f).toInt()
//        val dailyButtonMarginBottom = (125 * scale + 0.5f).toInt()
//        val signsMarginBottom = (205 * scale + 0.5f).toInt()
//
//        val burcCard = (context as Activity).findViewById<CardView>(R.id.burcCard)
//        val dailyButton = context.findViewById<AppCompatButton>(R.id.dailyButton)
//        val monthlyButton = context.findViewById<AppCompatButton>(R.id.monthlyButton)
//        val yearlyButton = context.findViewById<AppCompatButton>(R.id.yearlyButton)
//        val generalSign = context.findViewById<CardView>(R.id.generalSign)
//        val loveSign = context.findViewById<CardView>(R.id.loveSign)
//        val careerSign = context.findViewById<CardView>(R.id.careerSign)
//
//        val learnYourBurcButton = context.findViewById<AppCompatButton>(R.id.learnYourBurcButton)
//
//        val burcCardParams = burcCard.layoutParams as RelativeLayout.LayoutParams
//        val dailyButtonParams = dailyButton.layoutParams as RelativeLayout.LayoutParams
//        val monthlyButtonParams = monthlyButton.layoutParams as RelativeLayout.LayoutParams
//        val yearlyButtonParams = yearlyButton.layoutParams as RelativeLayout.LayoutParams
//        val generalSignParams = generalSign.layoutParams as RelativeLayout.LayoutParams
//        val loveSignParams = loveSign.layoutParams as RelativeLayout.LayoutParams
//        val careerSignParams = careerSign.layoutParams as RelativeLayout.LayoutParams
//
//
//        animationHelper.animateBurcCardSize(newWidth,newHeight,{
//
//        }, {
//            setViewVisible(learnYourBurcButton)
//            burcCardParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
//            dailyButtonParams.bottomMargin = dailyButtonMarginBottom
//            monthlyButtonParams.bottomMargin = dailyButtonMarginBottom
//            yearlyButtonParams.bottomMargin = dailyButtonMarginBottom
//            generalSignParams.bottomMargin = signsMarginBottom
//            loveSignParams.bottomMargin = signsMarginBottom
//            careerSignParams.bottomMargin = signsMarginBottom
//
//            burcCard.layoutParams = burcCardParams
//            dailyButton.layoutParams = dailyButtonParams
//            generalSign.layoutParams = generalSignParams
//            loveSign.layoutParams = loveSignParams
//            careerSign.layoutParams = careerSignParams
//        })
//
//        //burcCardParams.height = newHeight
////        burcCardParams.bottomMargin = burcCardMarginBottom
//
//        dailyButton.setBackgroundResource(if (button == dailyButton) R.drawable.common_next_button else R.drawable.button_passive)
//        monthlyButton.setBackgroundResource(if (button == monthlyButton) R.drawable.common_next_button else R.drawable.button_passive)
//        yearlyButton.setBackgroundResource(if (button == yearlyButton) R.drawable.common_next_button else R.drawable.button_passive)
//
//    }
//}