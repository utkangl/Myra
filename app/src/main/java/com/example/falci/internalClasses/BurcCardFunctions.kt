package com.example.falci.internalClasses

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.HorizontalScrollView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.appcompat.widget.AppCompatButton
import androidx.cardview.widget.CardView
import com.example.falci.*
import com.example.falci.internalClasses.InternalFunctions.AnimateCardSize.animateCardSize
import com.example.falci.internalClasses.InternalFunctions.SetVisibilityFunctions.setViewGone
import com.example.falci.internalClasses.InternalFunctions.SetVisibilityFunctions.setViewInvisible
import com.example.falci.internalClasses.InternalFunctions.SetVisibilityFunctions.setViewVisible
import com.example.falci.internalClasses.InternalFunctions.SetVisibilityFunctions.setViewVisibleWithAnimation
import com.example.falci.internalClasses.dataClasses.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.*
import java.io.IOException

class BurcCardFunctions(
    private val context: Context,
    private val learnYourBurcButton: AppCompatButton? = null,
    private val savedUsersScrollContainer: HorizontalScrollView? = null,
    private val burcCard: CardView? = null,
    private val timeIntervalDailySelectedBG: ImageView? = null,
    private val timeIntervalWeeklySelectedBG: ImageView? = null,
    private val timeIntervalMonthlySelectedBG: ImageView? = null,
    private val timeIntervalYearlySelectedBG: ImageView? = null,
    private val savedUsersLinearContainer: LinearLayout? = null,
    private val activity: MainActivity? = null,
    private val generalSign: CardView? = null,
    private val loveSign: CardView? = null,
    private val careerSign: CardView? = null,
    private val selectedModeTitle: TextView? = null,
) {

    fun animateBurcCardIn(
        burcCard: CardView,
        burcCardInnerLayout: RelativeLayout,
        MiraBurcCardTop: ImageView,
        MiraBurcCardTopTriangle: ImageView,
        backArrowCard: CardView,
        mainActivityGeneralLayout: RelativeLayout
    ) {
        val params = burcCard.layoutParams as RelativeLayout.LayoutParams
        controlVariables.isBurcCardOpen = true
        animateBurcCardSize(burcCard, 380, 500, {
            setViewVisibleWithAnimation(context, MiraBurcCardTop, MiraBurcCardTopTriangle, backArrowCard, burcCardInnerLayout)
            burcCard.setCardBackgroundColor(Color.parseColor("#313131"))
            mainActivityGeneralLayout.background = getDrawable(context,R.drawable.space_background)
        }, {
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 0)
            params.addRule(RelativeLayout.CENTER_IN_PARENT, 1)
        })
    }

    fun animateBurcCardOut(burcCard: CardView, MiraBurcCardTop: ImageView, MiraBurcCardTopTriangle: ImageView,
                           backArrowCard: CardView, settingsButtonCard: CardView, clickToGetHoroscopeText: TextView) {
        val params = burcCard.layoutParams as RelativeLayout.LayoutParams
        controlVariables.isBurcCardOpen = false
        controlVariables.isGeneralModeSelected = false
        controlVariables.isLoveModeSelected = false
        controlVariables.isCareerModeSelected = false

        val learnYourBurcButton = burcCard.findViewById<AppCompatButton>(R.id.learnYourBurcButton)
        val burcCardInnerLayout = burcCard.findViewById<RelativeLayout>(R.id.burcCardInnerLayout)
        val timeIntervalsScrollContainer = burcCard.findViewById<HorizontalScrollView>(R.id.savedUsersScrollContainer)

        animateBurcCardSize(burcCard, 340, 80, {
            setViewInvisible(burcCardInnerLayout, learnYourBurcButton, timeIntervalsScrollContainer, MiraBurcCardTop, MiraBurcCardTopTriangle)
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
            setViewVisible(settingsButtonCard, clickToGetHoroscopeText)
            setViewGone(backArrowCard)
        })
    }

    fun animateBurcCardSize(burcCard: CardView, newWidth: Int, newHeight: Int, onAnimationStart: () -> Unit, onAnimationEnd: () -> Unit) {
        val params = burcCard.layoutParams as RelativeLayout.LayoutParams

        val scale = context.resources.displayMetrics.density
        val newW = (newWidth * scale + 0.5f).toInt()
        val newH = (newHeight * scale + 0.5f).toInt()

        val animatorWidth = ValueAnimator.ofInt(burcCard.width, newW)
        animatorWidth.addUpdateListener { animation ->
            val value = animation.animatedValue as Int
            params.width = value
            burcCard.layoutParams = params
        }

        val animatorHeight = ValueAnimator.ofInt(burcCard.height, newH)
        animatorHeight.addUpdateListener { animation ->
            val value = animation.animatedValue as Int
            params.height = value
            burcCard.layoutParams = params
        }

        val animatorSet = AnimatorSet()
        animatorSet.playTogether(animatorWidth, animatorHeight)
        animatorSet.duration = 100

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

    fun handleIsSelected(isGeneral: Boolean, isLove: Boolean, isCareer: Boolean, isFromLove: Boolean) {
        controlVariables.isGeneralModeSelected = isGeneral
        controlVariables.isLoveModeSelected = isLove
        controlVariables.isCareerModeSelected = isCareer
        controlVariables.isFromLoveHoroscope = isFromLove
    }

    fun handleTimeIntervalSelect(time_interval: String) {
        if (controlVariables.isFromLoveHoroscope) {
            setViewInvisible(learnYourBurcButton)
            ExportLookupUserCardFunctions.deselectAllCards(cardList)
        }

        animateCardSize(context, 380, 580, burcCard!!, animationDuration = 200)
        postHoroscopeData.time_interval = time_interval

        if (!controlVariables.isTimeIntervalSelected) {
            controlVariables.isTimeIntervalSelected = true
            if (postHoroscopeData.type == "love") {
                setViewVisibleWithAnimation(context, savedUsersScrollContainer)
            }
            if (postHoroscopeData.type != "love") {
                setViewVisibleWithAnimation(context, learnYourBurcButton)
            }
        }

        when (time_interval) {
            "daily" -> {
                setViewVisibleWithAnimation(context, timeIntervalDailySelectedBG)
                setViewInvisible(timeIntervalWeeklySelectedBG, timeIntervalMonthlySelectedBG, timeIntervalYearlySelectedBG)
            }
            "weekly" -> {
                setViewVisibleWithAnimation(context, timeIntervalWeeklySelectedBG)
                setViewInvisible(timeIntervalDailySelectedBG, timeIntervalMonthlySelectedBG, timeIntervalYearlySelectedBG)
            }
            "monthly" -> {
                setViewVisibleWithAnimation(context, timeIntervalMonthlySelectedBG)
                setViewInvisible(timeIntervalDailySelectedBG, timeIntervalWeeklySelectedBG, timeIntervalYearlySelectedBG)
            }
            "yearly" -> {
                setViewVisibleWithAnimation(context, timeIntervalYearlySelectedBG)
                setViewInvisible(timeIntervalDailySelectedBG, timeIntervalWeeklySelectedBG, timeIntervalMonthlySelectedBG)
            }
        }
    }

    fun getSavedLookUpUsers(context: Context) {
        val gson = Gson()
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(urls.lookupUserURL)
            .get()
            .header("Authorization", "Bearer ${tokensDataClass.accessToken}")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                println("exception $e")
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body()?.string()
                statusCode = response.code()

                if (statusCode == 401) {
                    println("unauthorized 401, taking new access token")
                    AuthenticationFunctions.PostJsonFunctions.takeFreshTokens(
                        urls.refreshURL,
                        context
                    ) { responseBody401, exception ->
                        if (responseBody401 != null) {
                            println(tokensDataClass.accessToken)
                            getSavedLookUpUsers(context)
                        } else {
                            println(exception)
                        }
                    }
                }
                if (statusCode == 200) {
                    val userListType = object : TypeToken<List<SavedLookUpUsersDataClass>>() {}.type
                    savedLookupUserList = gson.fromJson(responseBody, userListType)

                    activity!!.runOnUiThread {
                        for (user in savedLookupUserList) {
                            val savedLookupUserCardView = SavedLookupUserCardView(context, cardList,burcCard!!,learnYourBurcButton!!)
                            savedLookupUserCardView.findViewById<TextView>(R.id.savedUsername).text = user.name
                            savedLookupUserCardView.id = user.id
                            savedUsersLinearContainer!!.addView(savedLookupUserCardView)
                            cardList.add(savedLookupUserCardView)
                            savedLookupUserCardView.setOnLongClickListener {
                                val builder = AlertDialog.Builder(context)
                                builder.setMessage("Do You Want to Delete This LookupUser Entirely?")

                                builder.setPositiveButton("Yes") { _, _ ->
                                    ExportLookupUserCardFunctions.destroySavedLookUpUser(
                                        user.id,
                                        cardList,
                                        savedUsersLinearContainer,
                                        savedLookupUserCardView,
                                        activity,
                                        context
                                    )
                                }
                                builder.setNegativeButton("No", null)
                                builder.create().show()
                                true
                            }
                        }
                        controlVariables.isSavedUsersLoaded = true
                    }
                }
            }
        })
    }

    fun handleModeSelect(type: String) {
        val learnYourBurcButtonParams = learnYourBurcButton!!.layoutParams as RelativeLayout.LayoutParams
        val scale = context.resources.displayMetrics.density
        val newTopMarginForModeCards = (30 * scale + 0.5f).toInt()
        val generalSignParams = generalSign!!.layoutParams as RelativeLayout.LayoutParams
        val loveSignParams = loveSign!!.layoutParams as RelativeLayout.LayoutParams
        val careerSignParams = careerSign!!.layoutParams as RelativeLayout.LayoutParams
        val oldTopMarginForModeCards = (45 * scale + 0.5f).toInt()
        val generalSignBackground = generalSign.findViewById<CardView>(R.id.generalSignBackground)
        val loveSignBackground = loveSign.findViewById<CardView>(R.id.loveSignBackground)
        val careerSignBackground = careerSign.findViewById<CardView>(R.id.careerSignBackground)
        controlVariables.isTimeIntervalSelected = false
        animateCardSize(context, 380, 500, burcCard!!, animationDuration = 200)
        setViewInvisible(learnYourBurcButton, timeIntervalDailySelectedBG, timeIntervalWeeklySelectedBG, timeIntervalMonthlySelectedBG, timeIntervalYearlySelectedBG, savedUsersScrollContainer)
        postHoroscopeData.time_interval = null
        learnYourBurcButtonParams.topMargin = (35 * scale + 0.5f).toInt()
        postHoroscopeData.type = type
        setViewInvisible(selectedModeTitle)
        val animation = AnimationUtils.loadAnimation(context, R.anim.activity_slide_down)
        selectedModeTitle!!.startAnimation(animation)
        selectedModeTitle.text = type.uppercase()
        setViewVisible(selectedModeTitle)

        ExportLookupUserCardFunctions.deselectAllCards(cardList)
        when (type) {
            "general" -> {
                generalSignParams.topMargin = newTopMarginForModeCards; loveSignParams.topMargin =
                    oldTopMarginForModeCards; careerSignParams.topMargin = oldTopMarginForModeCards
                setViewVisibleWithAnimation(context, generalSignBackground)
                setViewInvisible(loveSignBackground, careerSignBackground)
            }
            "love" -> {
                generalSignParams.topMargin = oldTopMarginForModeCards; loveSignParams.topMargin =
                    newTopMarginForModeCards; careerSignParams.topMargin = oldTopMarginForModeCards
                setViewVisibleWithAnimation(context, loveSignBackground)
                setViewInvisible(generalSignBackground, careerSignBackground)
                if (!controlVariables.isSavedUsersLoaded) {
                    learnYourBurcButtonParams.topMargin = (55 * scale + 0.5f).toInt()
                    getSavedLookUpUsers(context)
                }
            }
            "career" -> {
                generalSignParams.topMargin = oldTopMarginForModeCards; loveSignParams.topMargin =
                    oldTopMarginForModeCards; careerSignParams.topMargin = newTopMarginForModeCards
                setViewVisibleWithAnimation(context, careerSignBackground)
                setViewInvisible(generalSignBackground, loveSignBackground)
            }
        }
    }

}
