package com.example.falci.internalClasses

import android.content.Context
import android.view.View
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatButton
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat.getColor
import com.example.falci.R
import com.example.falci.internalClasses.InternalFunctions.SetVisibilityFunctions.setViewGone
import com.example.falci.internalClasses.InternalFunctions.SetVisibilityFunctions.setViewVisible

class SignCardViewUpdater(private val context: Context) {
    private lateinit var burcCard: CardView
    private lateinit var generalSign: CardView
    private lateinit var loveSign: CardView
    private lateinit var careerSign: CardView
    private lateinit var dailyButton: AppCompatButton
    private lateinit var monthlyButton: AppCompatButton
    private lateinit var yearlyButton: AppCompatButton
    private lateinit var learnYourBurcButton: AppCompatButton

    fun initializeViews(
        burcCard: CardView,
        generalSign: CardView,
        loveSign: CardView,
        careerSign: CardView,
        dailyButton: AppCompatButton,
        monthlyButton: AppCompatButton,
        yearlyButton: AppCompatButton,
        learnYourBurcButton: AppCompatButton
    ) {
        this.burcCard = burcCard
        this.generalSign = generalSign
        this.loveSign = loveSign
        this.careerSign = careerSign
        this.dailyButton = dailyButton
        this.monthlyButton = monthlyButton
        this.yearlyButton = yearlyButton
        this.learnYourBurcButton = learnYourBurcButton
    }

    fun updateUIForCardClick(selectedCard: View) {
        val scale = context.resources.displayMetrics.density
        val newHeight = (680 * scale + 0.5f).toInt()
        val newMarginBottom = (105 * scale + 0.5f).toInt()
//        val burcCardMarginBottom = (-270 * scale + 0.5f).toInt()
        val dailyButtonMarginBottom = (25 * scale + 0.5f).toInt()

        val burcCardParams = burcCard.layoutParams as RelativeLayout.LayoutParams
        val generalSignCardParams = generalSign.layoutParams as RelativeLayout.LayoutParams
        val loveSignCardParams = loveSign.layoutParams as RelativeLayout.LayoutParams
        val careerSignCardParams = careerSign.layoutParams as RelativeLayout.LayoutParams
        val dailyButtonParams = dailyButton.layoutParams as RelativeLayout.LayoutParams
        val monthlyButtonParams = monthlyButton.layoutParams as RelativeLayout.LayoutParams
        val yearlyButtonParams = yearlyButton.layoutParams as RelativeLayout.LayoutParams

        burcCardParams.height = newHeight
        burcCardParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)


        generalSignCardParams.bottomMargin = newMarginBottom
        loveSignCardParams.bottomMargin = newMarginBottom
        careerSignCardParams.bottomMargin = newMarginBottom

        dailyButtonParams.bottomMargin = dailyButtonMarginBottom
        monthlyButtonParams.bottomMargin = dailyButtonMarginBottom
        yearlyButtonParams.bottomMargin = dailyButtonMarginBottom

        burcCard.layoutParams = burcCardParams
        generalSign.layoutParams = generalSignCardParams
        loveSign.layoutParams = loveSignCardParams
        careerSign.layoutParams = careerSignCardParams
        dailyButton.layoutParams = dailyButtonParams
        monthlyButton.layoutParams = monthlyButtonParams
        yearlyButton.layoutParams = yearlyButtonParams

        generalSign.setCardBackgroundColor(getColor(context, R.color.passivesigncard))
        loveSign.setCardBackgroundColor(getColor(context, R.color.passivesigncard))
        careerSign.setCardBackgroundColor(getColor(context, R.color.passivesigncard))

        when (selectedCard) {
            generalSign -> generalSign.setCardBackgroundColor(getColor(context,
                R.color.nameinputbackground
            ))
            loveSign -> loveSign.setCardBackgroundColor(getColor(context,
                R.color.nameinputbackground
            ))
            careerSign -> careerSign.setCardBackgroundColor(getColor(context,
                R.color.nameinputbackground
            ))
        }

        setViewVisible(dailyButton, monthlyButton, yearlyButton)
        setViewGone(learnYourBurcButton)

        dailyButton.setBackgroundResource(R.drawable.button_passive)
        monthlyButton.setBackgroundResource(R.drawable.button_passive)
        yearlyButton.setBackgroundResource(R.drawable.button_passive)
    }
}