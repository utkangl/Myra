package com.example.falci

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.HorizontalScrollView
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
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
import com.example.falci.internalClasses.HoroscopeFunctions.getLoveHoroscope
import com.example.falci.internalClasses.InternalFunctions.SetVisibilityFunctions.setViewGone
import com.example.falci.internalClasses.InternalFunctions.SetVisibilityFunctions.setViewInvisible
import com.example.falci.internalClasses.InternalFunctions.SetVisibilityFunctions.setViewVisibleWithAnimation
import com.example.falci.internalClasses.ProfileFunctions.ProfileFunctions.makeGetProfileRequest
import com.example.falci.internalClasses.TransitionToFragment.ReplaceActivityToFragment.replaceMainActivityToFragment
import com.example.falci.internalClasses.dataClasses.*
import okhttp3.*

var cardList: MutableList<SavedLookupUserCardView> = mutableListOf()
var savedLookupUserList: List<SavedLookUpUsersDataClass> = emptyList()

class MainActivity : AppCompatActivity() {
    private lateinit var splashViewModel: ViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        splashViewModel = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(application))[SplashViewModel::class.java]
        installSplashScreen().apply {
            setKeepOnScreenCondition {
                (splashViewModel as SplashViewModel).isLoading.value  // keep showing splash screen until launching is over
            }
        }
        setContentView(R.layout.activity_main)

        controlVariables.getProfileAgain = false
        controlVariables.isSavedUsersLoaded = false
        controlVariables.isBurcCardOpen = false
        val tokensSharedPreferences = this.getSharedPreferences("token_prefs", Context.MODE_PRIVATE)

        val currentTime = System.currentTimeMillis() / 1000
        val savedTokenCreationTime = tokensSharedPreferences.getLong(
            "token_creation_time",
            0
        ) // it is set in last login, does not matter if remember me is checked or not, saved until logout
        println(savedTokenCreationTime)
        println(currentTime)
        checkIsAccessExpired(currentTime, savedTokenCreationTime, 9000, this) // if it has been more than 15 minutes till creation, refresh
        if (currentTime - savedTokenCreationTime > 6000000) {
            authenticated.isLoggedIn = false
        } // refresh token lasts 30 minutes

        val burcCard = findViewById<CardView>(R.id.burcCard)
        val burcCardInnerLayout = findViewById<RelativeLayout>(R.id.burcCardInnerLayout)
        val miraBurcCardTop = findViewById<ImageView>(R.id.MiraBurcCardTop)
        val miraBurcCardTopTriangle = findViewById<ImageView>(R.id.MiraBurcCardTopTriangle)
        val backArrowCard = findViewById<CardView>(R.id.backArrowCard)
        val settingsButtonCard = findViewById<CardView>(R.id.settingsButtonCard)
        val generalSign = findViewById<CardView>(R.id.generalSign)
        val timeIntervalDaily = findViewById<CardView>(R.id.timeIntervalDaily)
        val timeIntervalDailySelectedBG = findViewById<ImageView>(R.id.timeIntervalDailySelectedBG)
        val timeIntervalWeekly = findViewById<CardView>(R.id.timeIntervalWeekly)
        val timeIntervalWeeklySelectedBG = findViewById<ImageView>(R.id.timeIntervalWeeklySelectedBG)
        val timeIntervalMonthly = findViewById<CardView>(R.id.timeIntervalMonthly)
        val timeIntervalMonthlySelectedBG = findViewById<ImageView>(R.id.timeIntervalMonthlySelectedBG)
        val timeIntervalYearly = findViewById<CardView>(R.id.timeIntervalYearly)
        val timeIntervalYearlySelectedBG = findViewById<ImageView>(R.id.timeIntervalYearlySelectedBG)
        val loveSign = findViewById<CardView>(R.id.loveSign)
        val careerSign = findViewById<CardView>(R.id.careerSign)
        val backArrow = findViewById<ImageButton>(R.id.back_arrow)
        val savedUsersScrollContainer = findViewById<HorizontalScrollView>(R.id.savedUsersScrollContainer)
        val savedUsersLinearContainer = findViewById<LinearLayout>(R.id.savedUsersLinearContainer)
        val addLookupUser = findViewById<CardView>(R.id.addLookupUser)
        val learnYourBurcButton = findViewById<AppCompatButton>(R.id.learnYourBurcButton)
        val settingsButton = findViewById<ImageButton>(R.id.settingsButton)
        val miraMainMenu = findViewById<ImageView>(R.id.miraMainMenu)
        val clickToGetHoroscopeText = findViewById<TextView>(R.id.ClickToGetHoroscopeText)
        val thinkingAnimation = findViewById<LottieAnimationView>(R.id.thinkingAnimation)
        val selectedModeTitle = findViewById<TextView>(R.id.selectedModeTitle)

        val burcCardFunctions = BurcCardFunctions(this, learnYourBurcButton, savedUsersScrollContainer,
            burcCard, timeIntervalDailySelectedBG, timeIntervalWeeklySelectedBG, timeIntervalMonthlySelectedBG,
            timeIntervalYearlySelectedBG, savedUsersLinearContainer, this,generalSign, loveSign, careerSign, selectedModeTitle
        )

        // if user has came to this activity from signUp fragment, then directly navigate user to
        // loginSignUp activity to login first
        if (authenticated.isFromSignIn) {
            val options = ActivityOptions.makeCustomAnimation(this, R.anim.activity_slide_down, 0)
            val intent = Intent(this, LoginSignupActivity::class.java);startActivity(intent, options.toBundle())
            setViewGone(burcCard, settingsButtonCard)
        }

        //navigate user directly to horoscope, when horoscope mode is love and when user is navigated to main screen from lookup user's complete profile
        if (controlVariables.navigateToHoroscope) {
            println("navigating to horoscope detail")
            replaceMainActivityToFragment(supportFragmentManager, HoroscopeDetailFragment())
            setViewGone(burcCard, settingsButtonCard, miraMainMenu)
            controlVariables.navigateToHoroscope = false
            controlVariables.isFromLoveHoroscope = false
        }

        val generalSignParams = generalSign.layoutParams as RelativeLayout.LayoutParams
        val loveSignParams = loveSign.layoutParams as RelativeLayout.LayoutParams
        val careerSignParams = careerSign.layoutParams as RelativeLayout.LayoutParams
        val scale = this.resources.displayMetrics.density
        val oldTopMarginForModeCards = (45 * scale + 0.5f).toInt()

        settingsButton.setOnClickListener {
            // when user clicks to profile button and if user is logged in, get profile informations
            // and set the response to UserProfileDataClass's instance
            if (authenticated.isLoggedIn) {
                makeGetProfileRequest(urls.getProfileURL, this) { _, _ -> }
//                    val options = ActivityOptions.makeCustomAnimation(this, R.anim.activity_slide_down, 0)
//                    val intent = Intent(this, ProfileActivity::class.java)
//                    ContextCompat.startActivity(this, intent, options.toBundle())
            }

            // when user clicks to profile button but did not login, navigate user to loginSignUp activity
            if (!authenticated.isLoggedIn) {
                if (savedInstanceState == null) {
                    val options = ActivityOptions.makeCustomAnimation(this, R.anim.activity_slide_down, 0)
                    val intent = Intent(this, LoginSignupActivity::class.java); startActivity(intent, options.toBundle())
                }
            }
        }

        burcCard.setOnClickListener {
            burcCard.setCardBackgroundColor(Color.parseColor("#313131"))

            if (!controlVariables.isBurcCardOpen) {
                if (authenticated.isLoggedIn) {
                    setViewInvisible(clickToGetHoroscopeText)
                    setViewGone(settingsButtonCard)
                    burcCardFunctions.animateBurcCardIn(burcCard, burcCardInnerLayout, miraBurcCardTop, miraBurcCardTopTriangle, backArrowCard)
                } else {
                    val options = ActivityOptions.makeCustomAnimation(this, R.anim.activity_slide_down, 0)
                    val intent = Intent(this, LoginSignupActivity::class.java); startActivity(intent, options.toBundle())
                }
            }
        }

        fun handleCloseBurcCard(){
            burcCard.setCardBackgroundColor(Color.parseColor("#1c1444"))
            burcCardFunctions.animateBurcCardOut(burcCard, miraBurcCardTop, miraBurcCardTopTriangle, backArrowCard, settingsButtonCard, clickToGetHoroscopeText)
            generalSignParams.topMargin = oldTopMarginForModeCards
            loveSignParams.topMargin = oldTopMarginForModeCards
            careerSignParams.topMargin = oldTopMarginForModeCards
        }

        backArrowCard.setOnClickListener { if (controlVariables.isBurcCardOpen) { handleCloseBurcCard() } }
        backArrow.setOnClickListener {
            backArrowCard.performClick()
        }

        generalSign.setOnClickListener {
            if (!controlVariables.isGeneralModeSelected) {
                burcCardFunctions.handleModeSelect("general")
                burcCardFunctions.handleIsSelected(isGeneral = true, isLove = false, isCareer = false, isFromLove = false)
            }
        }
        loveSign.setOnClickListener {
            if (!controlVariables.isLoveModeSelected) {
                burcCardFunctions.handleModeSelect("love")
                burcCardFunctions.handleIsSelected(isGeneral = false, isLove = true, isCareer = false, isFromLove = true)
            }
        }
        careerSign.setOnClickListener {
            if (!controlVariables.isCareerModeSelected) {
                burcCardFunctions.handleModeSelect("career")
                burcCardFunctions.handleIsSelected(isGeneral = false, isLove = false, isCareer = true, isFromLove = false)
            }
        }

        timeIntervalDaily.setOnClickListener { burcCardFunctions.handleTimeIntervalSelect("daily") }
        timeIntervalWeekly.setOnClickListener { burcCardFunctions.handleTimeIntervalSelect("weekly") }
        timeIntervalMonthly.setOnClickListener { burcCardFunctions.handleTimeIntervalSelect("monthly") }
        timeIntervalYearly.setOnClickListener { burcCardFunctions.handleTimeIntervalSelect("yearly") }

        addLookupUser.setOnClickListener {
            // if horoscope type is love, navigate user to complete profile screen to get lookup user's profile
            if (controlVariables.isFromLoveHoroscope) {
                setViewVisibleWithAnimation(this, addLookupUser.findViewById<ImageView>(R.id.addLookupUserBG))
                handleCloseBurcCard()
                Handler(Looper.getMainLooper()).postDelayed({
                    val options = ActivityOptions.makeCustomAnimation(this, R.anim.activity_slide_down, 0)
                    val intent = Intent(this, CompleteProfile::class.java); startActivity(intent, options.toBundle())
                }, 500)
            }
        }

        learnYourBurcButton.setOnClickListener {
            controlVariables.navigateBackToProfileActivity = false

            // if user is logged in, close burcCard and then after 300ms make the transition
            // to horoscope detail fragment, but until api response with horoscope
            // play thinking animation
            if (authenticated.isLoggedIn) {
                // if horoscope type is not love, call get horoscope function
                if (!controlVariables.isFromLoveHoroscope) {
                    burcCardFunctions.animateBurcCardOut(
                        burcCard,
                        miraBurcCardTop,
                        miraBurcCardTopTriangle,
                        backArrowCard,
                        settingsButtonCard,
                        clickToGetHoroscopeText
                    )
                    Handler(Looper.getMainLooper()).postDelayed({
                        setViewGone(burcCard, settingsButtonCard, miraMainMenu)
                        HoroscopeFunctions.getHoroscope(thinkingAnimation, supportFragmentManager, this)
                    }, 500)
                }
                if (controlVariables.isFromLoveHoroscope) {
                    burcCardFunctions.animateBurcCardOut(
                        burcCard,
                        miraBurcCardTop,
                        miraBurcCardTopTriangle,
                        backArrowCard,
                        settingsButtonCard,
                        clickToGetHoroscopeText
                    )
                    Handler(Looper.getMainLooper()).postDelayed({
                        setViewGone(burcCard, settingsButtonCard, miraMainMenu)
                        getLoveHoroscope(thinkingAnimation, this, getPartnerProfile.id)
                    }, 500)
                }
            } else {
                // if user is not logged in, close burcCard and then after 300ms make the transition
                // to login signup activity
                burcCardFunctions.animateBurcCardOut(
                    burcCard,
                    miraBurcCardTop,
                    miraBurcCardTopTriangle,
                    backArrowCard,
                    settingsButtonCard,
                    clickToGetHoroscopeText
                )
                Handler(Looper.getMainLooper()).postDelayed({
                    val options = ActivityOptions.makeCustomAnimation(this, R.anim.activity_slide_down, 0)
                    val intent = Intent(this, LoginSignupActivity::class.java); startActivity(intent, options.toBundle())
                }, 500)
            }
        }
    } // end of onCreate function


    // if back button is pressed in main activity, check if you are in main activity or,
    // a fragment that hosted by the main activity. If you are in main activity then show
    // dialog alert to user to ask if they want to quit the app. If you are in a fragment that
    // hosted by main activity then just simply go back
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.main_fragment_container)
        if (currentFragment == null) {
            if (!controlVariables.isBurcCardOpen) {
                val builder = AlertDialog.Builder(this)
                builder.setMessage("Do You Want To Exit?")
                builder.setPositiveButton("Yes") { _, _ -> moveTaskToBack(true);finishAffinity() }
                builder.setNegativeButton("No", null)
                builder.create().show()
            }
        }
        if (currentFragment != null) super.onBackPressed()
    }
} // end of class