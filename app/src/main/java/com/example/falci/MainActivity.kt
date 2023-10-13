package com.example.falci

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.HorizontalScrollView
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.cardview.widget.CardView
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.airbnb.lottie.LottieAnimationView
import com.example.falci.internalClasses.*
import com.example.falci.internalClasses.AuthenticationFunctions.PostJsonFunctions.checkIsAccessExpired
import com.example.falci.internalClasses.InternalFunctions.AnimateCardSize.animateCardSize
import com.example.falci.internalClasses.InternalFunctions.SetVisibilityFunctions.setViewGone
import com.example.falci.internalClasses.InternalFunctions.SetVisibilityFunctions.setViewInvisible
import com.example.falci.internalClasses.InternalFunctions.SetVisibilityFunctions.setViewVisible
import com.example.falci.internalClasses.InternalFunctions.SetVisibilityFunctions.setViewVisibleWithAnimation
import com.example.falci.internalClasses.ProfileFunctions.ProfileFunctions.makeGetProfileRequest
import com.example.falci.internalClasses.TransitionToFragment.ReplaceActivityToFragment.replaceMainActivityToFragment
import com.example.falci.internalClasses.dataClasses.*

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

        val learnYourBurcButton = findViewById<AppCompatButton>(R.id.learnYourBurcButton)
        val settingsButton = findViewById<ImageButton>(R.id.settingsButton)
        val miraMainMenu = findViewById<ImageView>(R.id.miraMainMenu)
        val burcCardExplanationTextScroll = findViewById<ScrollView>(R.id.burcCardExplanationTextScroll)
        val clickToGetHoroscopeText = findViewById<TextView>(R.id.ClickToGetHoroscopeText)
        val animationHelper = AnimationHelper(this)
//        val periodButtonViewUpdater = PeriodButtonViewUpdater(this)
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
            if(authenticated.isLoggedIn) {
                clickToGetHoroscopeText.visibility = View.INVISIBLE
                setViewGone(learnYourBurcButton, settingsButtonCard)
                animationHelper.animateBurcCardIn(burcCard, burcCardInnerLayout, miraBurcCardTop, miraBurcCardTopTriangle,backArrowCard,clickToGetHoroscopeText)
            }
            else {
                val options = ActivityOptions.makeCustomAnimation(this, R.anim.activity_slide_down, 0)
                val intent = Intent(this, LoginSignupActivity::class.java); startActivity(intent, options.toBundle())
            }
        }

        val generalSignParams = generalSign.layoutParams as RelativeLayout.LayoutParams
        val loveSignParams = loveSign.layoutParams as RelativeLayout.LayoutParams
        val careerSignParams = careerSign.layoutParams as RelativeLayout.LayoutParams
        val scale = this.resources.displayMetrics.density
        val newTopMarginForModeCards = (30 * scale + 0.5f).toInt()
        val oldTopMarginForModeCards = (45 * scale + 0.5f).toInt()

        generalSign.setOnClickListener{
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
        }

        loveSign.setOnClickListener{
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
        }

        careerSign.setOnClickListener{
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


        // if burcCard is clicked and user is logged in open the card and make the clickToGetHoroscopeText invisible, if not logged in navigate user to login screen
//        burcCard.setOnClickListener {
//            if(authenticated.isLoggedIn) {
//                setViewGone(clickToGetHoroscopeText); animationHelper.animateBurcCardIn()
//            }
//            else {
//                val options = ActivityOptions.makeCustomAnimation(this, R.anim.activity_slide_down, 0)
//                val intent = Intent(this, LoginSignupActivity::class.java); startActivity(intent, options.toBundle())
//            }
//        }
//        backArrow.setOnClickListener { setViewVisible(clickToGetHoroscopeText); animationHelper.animateBurcCardOut()}                                             // if backArrow is clicked close the card and make the clickToGetHoroscopeText visible
//        generalSign.setOnClickListener { signCardViewUpdater.updateUIForCardClick(generalSign, animationHelper); postHoroscopeData.type ="general"; isFromLoveHoroscope = false}  //make burcCard larger and set postHoroscopeData's type to general
//        loveSign.setOnClickListener { signCardViewUpdater.updateUIForCardClick(loveSign, animationHelper); postHoroscopeData.type ="love"; isFromLoveHoroscope = true}           //make burcCard larger and set postHoroscopeData's type to love
//        careerSign.setOnClickListener { signCardViewUpdater.updateUIForCardClick(careerSign, animationHelper); postHoroscopeData.type ="career"; isFromLoveHoroscope = false}   //make burcCard larger and set postHoroscopeData's type to career
//        dailyButton.setOnClickListener { periodButtonViewUpdater.updateButtonView(dailyButton, animationHelper); postHoroscopeData.time_interval ="daily" }                    //make burcCard larger and set postHoroscopeData's time interval to daily
//        monthlyButton.setOnClickListener { periodButtonViewUpdater.updateButtonView(monthlyButton,animationHelper); postHoroscopeData.time_interval ="monthly"}              //make burcCard larger and set postHoroscopeData's time interval to monthly
//        yearlyButton.setOnClickListener { periodButtonViewUpdater.updateButtonView(yearlyButton,animationHelper); postHoroscopeData.time_interval ="yearly"}                //make burcCard larger and set postHoroscopeData's time interval to yearly

        settingsButton.setOnClickListener{
            // when user clicks to profile button and if user is logged in, get profile informations
            // and set the response to UserProfileDataClass's instance
            if(authenticated.isLoggedIn){
                makeGetProfileRequest(urls.getProfileURL,this) { _, _ -> }
            }

            // when user clicks to profile button but did not login, navigate user to loginSignUp activity
            if (!authenticated.isLoggedIn) {
                if (savedInstanceState == null) {
                    val options = ActivityOptions.makeCustomAnimation(this, R.anim.activity_slide_down, 0)
                    val intent = Intent(this, LoginSignupActivity::class.java); startActivity(intent, options.toBundle())
                }
            }
        }


//        learnYourBurcButton.setOnClickListener{
//            navigateBackToProfileActivity = false
//
//            // if user is logged in, close burcCard and then after 300ms make the transition
//            // to horoscope detail fragment, but until api response with horoscope
//            // play thinking animation
//            if (authenticated.isLoggedIn){
//                // if horoscope type is love, navigate user to complete profile screen to get lookup user's profile
//                if (isFromLoveHoroscope){
//                    val options = ActivityOptions.makeCustomAnimation(this, R.anim.activity_slide_down, 0)
//                    val intent = Intent(this, CompleteProfile::class.java); startActivity(intent,options.toBundle())
//                }
//
//                // if horoscope type is not love, call get horoscope function
//                if (!isFromLoveHoroscope){
//                    animationHelper.animateBurcCardOut()
//                    Handler(Looper.getMainLooper()).postDelayed({
//                        setViewGone(burcCard,settingsButtonCard, miraMainMenu)
//                        HoroscopeFunctions.getHoroscope(thinkingAnimation, supportFragmentManager,this)
//                    }, 300)
//                }
//
//
//            }else{
//                // if user is not logged in, close burcCard and then after 300ms make the transition
//                // to login signup activity
//                animationHelper.animateBurcCardOut()
//                Handler(Looper.getMainLooper()).postDelayed({
//                    val options = ActivityOptions.makeCustomAnimation(this, R.anim.activity_slide_down, 0)
//                    val intent = Intent(this, LoginSignupActivity::class.java); startActivity(intent,options.toBundle())
//                }, 300)
//            }
//        }
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