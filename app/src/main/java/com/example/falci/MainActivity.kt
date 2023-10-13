package com.example.falci

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.HorizontalScrollView
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.airbnb.lottie.LottieAnimationView
import com.example.falci.internalClasses.*
import com.example.falci.internalClasses.AuthenticationFunctions.PostJsonFunctions.checkIsAccessExpired
import com.example.falci.internalClasses.InternalFunctions.AnimateCardSize.animateCardSize
import com.example.falci.internalClasses.InternalFunctions.SetVisibilityFunctions.setViewGone
import com.example.falci.internalClasses.InternalFunctions.SetVisibilityFunctions.setViewInvisible
import com.example.falci.internalClasses.InternalFunctions.SetVisibilityFunctions.setViewVisibleWithAnimation
import com.example.falci.internalClasses.ProfileFunctions.ProfileFunctions.makeGetProfileRequest
import com.example.falci.internalClasses.TransitionToFragment.ReplaceActivityToFragment.replaceMainActivityToFragment
import com.example.falci.internalClasses.dataClasses.*


var isBurcCardOpen = false
var isGeneralModeSelected = false
var isLoveModeSelected = false
var isCareerModeSelected = false

class MainActivity : AppCompatActivity() {
   private lateinit var splashViewModel: ViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        splashViewModel = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(application))[SplashViewModel::class.java]
        installSplashScreen().apply {
            setKeepOnScreenCondition{
                (splashViewModel as SplashViewModel).isLoading.value  // keep showing splash screen until launching is over
            }
        }
        setContentView(R.layout.activity_main)

        getProfileAgain = false

        val tokensSharedPreferences = this.getSharedPreferences("token_prefs", Context.MODE_PRIVATE)

        val currentTime = System.currentTimeMillis() / 1000
        val savedTokenCreationTime = tokensSharedPreferences.getLong("token_creation_time",0) // it is set in last login, does not matter if remember me is checked or not, saved until logout
        println(savedTokenCreationTime)
        println(currentTime)
        checkIsAccessExpired(currentTime, savedTokenCreationTime, 9000,  this) // if it has been more than 15 minutes till creation, refresh
        if(currentTime - savedTokenCreationTime > 6000000){ authenticated.isLoggedIn = false } // refresh token lasts 30 minutes

        val burcCard = findViewById<CardView>(R.id.burcCard)
        val burcCardInnerLayout = findViewById<RelativeLayout>(R.id.burcCardInnerLayout)
        val miraBurcCardTop = findViewById<ImageView>(R.id.MiraBurcCardTop)
        val miraBurcCardTopTriangle = findViewById<ImageView>(R.id.MiraBurcCardTopTriangle)
        val backArrowCard = findViewById<CardView>(R.id.backArrowCard)
        val settingsButtonCard = findViewById<CardView>(R.id.settingsButtonCard)
        val generalSign = findViewById<CardView>(R.id.generalSign)
        val generalSignBackground = findViewById<CardView>(R.id.generalSignBackground)
        val timeIntervalDaily = findViewById<CardView>(R.id.timeIntervalDaily)
        val timeIntervalDailySelectedBG = findViewById<ImageView>(R.id.timeIntervalDailySelectedBG)
        val timeIntervalWeekly = findViewById<CardView>(R.id.timeIntervalWeekly)
        val timeIntervalWeeklySelectedBG = findViewById<ImageView>(R.id.timeIntervalWeeklySelectedBG)
        val timeIntervalMonthly= findViewById<CardView>(R.id.timeIntervalMonthly)
        val timeIntervalMonthlySelectedBG= findViewById<ImageView>(R.id.timeIntervalMonthlySelectedBG)
        val timeIntervalYearly = findViewById<CardView>(R.id.timeIntervalYearly)
        val timeIntervalYearlySelectedBG = findViewById<ImageView>(R.id.timeIntervalYearlySelectedBG)
        val loveSign = findViewById<CardView>(R.id.loveSign)
        val loveSignBackground = findViewById<CardView>(R.id.loveSignBackground)
        val careerSign = findViewById<CardView>(R.id.careerSign)
        val careerSignBackground = findViewById<CardView>(R.id.careerSignBackground)
        val backArrow = findViewById<ImageButton>(R.id.back_arrow)
        val timeIntervalsScrollContainer = findViewById<HorizontalScrollView>(R.id.timeIntervalsScrollContainer)
        val addLookupUser = findViewById<CardView>(R.id.addLookupUser)

        val learnYourBurcButton = findViewById<AppCompatButton>(R.id.learnYourBurcButton)
        val settingsButton = findViewById<ImageButton>(R.id.settingsButton)
        val miraMainMenu = findViewById<ImageView>(R.id.miraMainMenu)
        val clickToGetHoroscopeText = findViewById<TextView>(R.id.ClickToGetHoroscopeText)
        val animationHelper = AnimationHelper(this)
        val thinkingAnimation = findViewById<LottieAnimationView>(R.id.thinkingAnimation)
        // if user has came to this activity from signUp fragment, then directly navigate user to
        // loginSignUp activity to login first
        if (authenticated.isFromSignIn){
            val options = ActivityOptions.makeCustomAnimation(this, R.anim.activity_slide_down, 0)
            val intent = Intent(this, LoginSignupActivity::class.java);startActivity(intent, options.toBundle())
            setViewGone(burcCard, settingsButtonCard)
        }

        //navigate user directly to horoscope, when horoscope mode is love and when user is navigated to main screen from lookup user's complete profile
        if (navigateToHoroscope){
            println("navigating to horoscope detail")
            replaceMainActivityToFragment(supportFragmentManager, HoroscopeDetailFragment())
            setViewGone(burcCard,settingsButtonCard, miraMainMenu)
            navigateToHoroscope = false
            isFromLoveHoroscope = false
        }


        burcCard.setOnClickListener {
            if (!isBurcCardOpen){
                if(authenticated.isLoggedIn) {
                    setViewInvisible(clickToGetHoroscopeText)
                    setViewGone(settingsButtonCard)
                    animationHelper.animateBurcCardIn(burcCard, burcCardInnerLayout, miraBurcCardTop, miraBurcCardTopTriangle,backArrowCard)
                }
                else {
                    val options = ActivityOptions.makeCustomAnimation(this, R.anim.activity_slide_down, 0)
                    val intent = Intent(this, LoginSignupActivity::class.java); startActivity(intent, options.toBundle())
                }
            }
        }


        val generalSignParams = generalSign.layoutParams as RelativeLayout.LayoutParams
        val loveSignParams = loveSign.layoutParams as RelativeLayout.LayoutParams
        val careerSignParams = careerSign.layoutParams as RelativeLayout.LayoutParams
        val scale = this.resources.displayMetrics.density
        val newTopMarginForModeCards = (30 * scale + 0.5f).toInt()
        val oldTopMarginForModeCards = (45 * scale + 0.5f).toInt()


        backArrow.setOnClickListener{
            if (isBurcCardOpen){
                animationHelper.animateBurcCardOut(burcCard, miraBurcCardTop,miraBurcCardTopTriangle,backArrowCard,settingsButtonCard,clickToGetHoroscopeText)
                generalSignParams.topMargin = oldTopMarginForModeCards
                loveSignParams.topMargin = oldTopMarginForModeCards
                careerSignParams.topMargin = oldTopMarginForModeCards
                generalSign.layoutParams = generalSignParams
                loveSign.layoutParams = loveSignParams
                careerSign.layoutParams = careerSignParams
            }
        }

        generalSign.setOnClickListener{
            if (!isGeneralModeSelected){
                setViewVisibleWithAnimation(this,generalSignBackground)
                setViewInvisible(loveSignBackground, careerSignBackground)
                generalSignParams.topMargin = newTopMarginForModeCards
                loveSignParams.topMargin = oldTopMarginForModeCards
                careerSignParams.topMargin = oldTopMarginForModeCards
                generalSign.layoutParams = generalSignParams
                loveSign.layoutParams = loveSignParams
                careerSign.layoutParams = careerSignParams
                postHoroscopeData.type = "general"
                animateCardSize(this,370,450, burcCard, animationDuration = 200)
                setViewInvisible(learnYourBurcButton, timeIntervalDailySelectedBG,timeIntervalWeeklySelectedBG,timeIntervalMonthlySelectedBG,timeIntervalYearlySelectedBG,timeIntervalsScrollContainer)
                postHoroscopeData.time_interval = null
                isGeneralModeSelected = true
                isLoveModeSelected = false
                isCareerModeSelected = false
                isFromLoveHoroscope = false
            }
        }

        loveSign.setOnClickListener{
            if (!isLoveModeSelected){
                setViewVisibleWithAnimation(this,loveSignBackground)
                setViewInvisible(generalSignBackground, careerSignBackground)
                generalSignParams.topMargin = oldTopMarginForModeCards
                loveSignParams.topMargin = newTopMarginForModeCards
                careerSignParams.topMargin = oldTopMarginForModeCards
                generalSign.layoutParams = generalSignParams
                loveSign.layoutParams = loveSignParams
                careerSign.layoutParams = careerSignParams
                postHoroscopeData.type = "love"
                animateCardSize(this,370,450, burcCard, animationDuration = 200)
                setViewInvisible(learnYourBurcButton, timeIntervalDailySelectedBG,timeIntervalWeeklySelectedBG,timeIntervalMonthlySelectedBG,timeIntervalYearlySelectedBG,timeIntervalsScrollContainer)
                postHoroscopeData.time_interval = null
                isGeneralModeSelected = false
                isLoveModeSelected = true
                isCareerModeSelected = false
                isFromLoveHoroscope = true
            }

        }

        careerSign.setOnClickListener{
            if (!isCareerModeSelected){
                setViewVisibleWithAnimation(this,careerSignBackground)
                setViewInvisible(generalSignBackground, loveSignBackground)
                generalSignParams.topMargin = oldTopMarginForModeCards
                loveSignParams.topMargin = oldTopMarginForModeCards
                careerSignParams.topMargin = newTopMarginForModeCards
                generalSign.layoutParams = generalSignParams
                loveSign.layoutParams = loveSignParams
                careerSign.layoutParams = careerSignParams
                postHoroscopeData.type = "career"
                animateCardSize(this,370,450, burcCard, animationDuration = 200)
                setViewInvisible(learnYourBurcButton, timeIntervalDailySelectedBG,timeIntervalWeeklySelectedBG,timeIntervalMonthlySelectedBG,timeIntervalYearlySelectedBG,timeIntervalsScrollContainer)
                postHoroscopeData.time_interval = null
                isGeneralModeSelected = false
                isLoveModeSelected = false
                isCareerModeSelected = true
                isFromLoveHoroscope = false
            }
        }

        timeIntervalDaily.setOnClickListener{
            if (postHoroscopeData.type != "love"){
                animateCardSize(this,370,550, burcCard, animationDuration = 200)
                setViewVisibleWithAnimation(this, learnYourBurcButton, timeIntervalDailySelectedBG)
                setViewInvisible(timeIntervalWeeklySelectedBG,timeIntervalMonthlySelectedBG,timeIntervalYearlySelectedBG)
                postHoroscopeData.time_interval = "daily"
            }
            else{
                animateCardSize(this,370,550, burcCard, animationDuration = 200)
                setViewVisibleWithAnimation(this, timeIntervalsScrollContainer , timeIntervalDailySelectedBG)
                setViewInvisible(timeIntervalWeeklySelectedBG,timeIntervalMonthlySelectedBG,timeIntervalYearlySelectedBG)
                postHoroscopeData.time_interval = "yearly"
            }
        }

        timeIntervalWeekly.setOnClickListener{
            if (postHoroscopeData.type != "love") {
                animateCardSize(this, 370, 550, burcCard, animationDuration = 200)
                setViewVisibleWithAnimation(this, learnYourBurcButton, timeIntervalWeeklySelectedBG)
                setViewInvisible(timeIntervalDailySelectedBG, timeIntervalMonthlySelectedBG, timeIntervalYearlySelectedBG)
                postHoroscopeData.time_interval = "weekly"
            }
            else{
                animateCardSize(this,370,550, burcCard, animationDuration = 200)
                setViewVisibleWithAnimation(this, timeIntervalsScrollContainer , timeIntervalWeeklySelectedBG)
                setViewInvisible(timeIntervalDailySelectedBG,timeIntervalMonthlySelectedBG,timeIntervalYearlySelectedBG)
                postHoroscopeData.time_interval = "yearly"
            }
        }

        timeIntervalMonthly.setOnClickListener{
            if (postHoroscopeData.type != "love") {
                animateCardSize(this, 370, 550, burcCard, animationDuration = 200)
                setViewVisibleWithAnimation(this, learnYourBurcButton, timeIntervalMonthlySelectedBG)
                setViewInvisible(timeIntervalDailySelectedBG, timeIntervalWeeklySelectedBG, timeIntervalYearlySelectedBG)
                postHoroscopeData.time_interval = "monthly"
            }
            else{
                animateCardSize(this,370,550, burcCard, animationDuration = 200)
                setViewVisibleWithAnimation(this, timeIntervalsScrollContainer , timeIntervalMonthlySelectedBG)
                setViewInvisible(timeIntervalDailySelectedBG,timeIntervalWeeklySelectedBG,timeIntervalYearlySelectedBG)
                postHoroscopeData.time_interval = "yearly"
            }
        }

        timeIntervalYearly.setOnClickListener{
            if (postHoroscopeData.type != "love"){
                animateCardSize(this,370,550, burcCard, animationDuration = 200)
                setViewVisibleWithAnimation(this, learnYourBurcButton, timeIntervalYearlySelectedBG)
                setViewInvisible(timeIntervalDailySelectedBG,timeIntervalWeeklySelectedBG,timeIntervalMonthlySelectedBG)
                postHoroscopeData.time_interval = "yearly"
            } else{
                animateCardSize(this,370,550, burcCard, animationDuration = 200)
                setViewVisibleWithAnimation(this, timeIntervalsScrollContainer , timeIntervalYearlySelectedBG)
                setViewInvisible(timeIntervalDailySelectedBG,timeIntervalWeeklySelectedBG,timeIntervalMonthlySelectedBG)
                postHoroscopeData.time_interval = "yearly"
            }
        }

        settingsButton.setOnClickListener{
            // when user clicks to profile button and if user is logged in, get profile informations
            // and set the response to UserProfileDataClass's instance
            if(authenticated.isLoggedIn){
                makeGetProfileRequest(urls.getProfileURL,this) { _, _ -> }
                val options = ActivityOptions.makeCustomAnimation(this, R.anim.activity_slide_down, 0)
                val intent = Intent(this, ProfileActivity::class.java)
                ContextCompat.startActivity(this, intent, options.toBundle())
            }

            // when user clicks to profile button but did not login, navigate user to loginSignUp activity
            if (!authenticated.isLoggedIn) {
                if (savedInstanceState == null) {
                    val options = ActivityOptions.makeCustomAnimation(this, R.anim.activity_slide_down, 0)
                    val intent = Intent(this, LoginSignupActivity::class.java); startActivity(intent, options.toBundle())
                }
            }
        }

        addLookupUser.setOnClickListener{
            // if horoscope type is love, navigate user to complete profile screen to get lookup user's profile
            if (isFromLoveHoroscope){
                animationHelper.animateBurcCardOut(burcCard, miraBurcCardTop,miraBurcCardTopTriangle,backArrowCard,settingsButtonCard,clickToGetHoroscopeText)
                generalSignParams.topMargin = oldTopMarginForModeCards
                loveSignParams.topMargin = oldTopMarginForModeCards
                careerSignParams.topMargin = oldTopMarginForModeCards
                generalSign.layoutParams = generalSignParams
                loveSign.layoutParams = loveSignParams
                careerSign.layoutParams = careerSignParams
                Handler(Looper.getMainLooper()).postDelayed({
                    val options = ActivityOptions.makeCustomAnimation(this, R.anim.activity_slide_down, 0)
                    val intent = Intent(this, CompleteProfile::class.java); startActivity(intent,options.toBundle())
                }, 500)
            }
        }

        learnYourBurcButton.setOnClickListener{
            navigateBackToProfileActivity = false

            // if user is logged in, close burcCard and then after 300ms make the transition
            // to horoscope detail fragment, but until api response with horoscope
            // play thinking animation
            if (authenticated.isLoggedIn){
                // if horoscope type is not love, call get horoscope function
                if (!isFromLoveHoroscope){
                    animationHelper.animateBurcCardOut(burcCard,miraBurcCardTop,miraBurcCardTopTriangle,backArrowCard,settingsButtonCard,clickToGetHoroscopeText)
                    Handler(Looper.getMainLooper()).postDelayed({
                        setViewGone(burcCard,settingsButtonCard, miraMainMenu)
                        HoroscopeFunctions.getHoroscope(thinkingAnimation, supportFragmentManager,this)
                    }, 500)
                }
            }else{
                // if user is not logged in, close burcCard and then after 300ms make the transition
                // to login signup activity
                animationHelper.animateBurcCardOut(burcCard,miraBurcCardTop,miraBurcCardTopTriangle,backArrowCard,settingsButtonCard,clickToGetHoroscopeText)
                Handler(Looper.getMainLooper()).postDelayed({
                    val options = ActivityOptions.makeCustomAnimation(this, R.anim.activity_slide_down, 0)
                    val intent = Intent(this, LoginSignupActivity::class.java); startActivity(intent,options.toBundle())
                }, 500)
            }
        }
    } // end of onCreate function



    // if back button is pressed in main activity, check if you are in main activity or,
    // a fragment which gets host by main fragment. If you are in main activity then show
    // dialog alert to user to ask if they want to quit the app. If you are in a fragment which
    // hosted by main activity then just simply go back
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.main_fragment_container)
        if (currentFragment == null) {
            val builder = AlertDialog.Builder(this)
            builder.setMessage("Do You Want To Exit?")

            builder.setPositiveButton("Yes") { _ , _ ->
                moveTaskToBack(true)
                finishAffinity( )
            }
            builder.setNegativeButton("No", null)

            val dialog = builder.create()
            dialog.show()
        }
        else {
            super.onBackPressed()
        }
    }


} // end of class