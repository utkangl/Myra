package com.utkangul.falci

import android.app.ActivityOptions
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.cardview.widget.CardView
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.airbnb.lottie.LottieAnimationView
import com.utkangul.falci.internalClasses.*
import com.utkangul.falci.internalClasses.AuthenticationFunctions.PostJsonFunctions.checkIsAccessExpired
import com.utkangul.falci.internalClasses.HoroscopeFunctions.getLoveHoroscope
import com.utkangul.falci.internalClasses.InternalFunctions.SetVisibilityFunctions.setViewGone
import com.utkangul.falci.internalClasses.InternalFunctions.SetVisibilityFunctions.setViewGoneWithAnimation
import com.utkangul.falci.internalClasses.InternalFunctions.SetVisibilityFunctions.setViewInvisible
import com.utkangul.falci.internalClasses.InternalFunctions.SetVisibilityFunctions.setViewVisibleWithAnimation
import com.utkangul.falci.internalClasses.TransitionToFragment.ReplaceActivityToFragment.replaceMainActivityToFragment
import com.utkangul.falci.internalClasses.dataClasses.*
import com.google.android.gms.ads.*
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.android.gms.ads.rewarded.ServerSideVerificationOptions
import com.utkangul.falci.internalClasses.ProfileFunctions.ProfileFunctions.makeGetProfileRequest
import com.utkangul.falci.internalClasses.UserStatusFunctions.UserStatusFunctionsObject.getUserStatus
import okhttp3.*
import java.util.*


var cardList: MutableList<SavedLookupUserCardView> = mutableListOf()
var savedLookupUserList: List<SavedLookUpUsersDataClass> = emptyList()

class MainActivity : AppCompatActivity() {
    private lateinit var splashViewModel: ViewModel
    private var rewardedAd: RewardedAd? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.runOnUiThread{
            val languageSharedPreferences: SharedPreferences = application.getSharedPreferences("language_choice", Context.MODE_PRIVATE)
            val languageChoice = languageSharedPreferences.getString("language",null)
            if (!languageChoice.isNullOrEmpty()){
                val locale = Locale(languageChoice)
                Locale.setDefault(locale)
                val config = Configuration()
                config.locale = locale
                resources.updateConfiguration(config, resources.displayMetrics)
            }
        }


        splashViewModel = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(application))[SplashViewModel::class.java]
        installSplashScreen().apply {
            setKeepOnScreenCondition { (splashViewModel as SplashViewModel).isLoading.value }  // keep showing splash screen until launching is over
        }
        setContentView(R.layout.activity_main)

        println(userProfile)
        val adRequest = AdRequest.Builder().build()
        RewardedAd.load(this, "ca-app-pub-9194768212989464/2985888856", adRequest, object : RewardedAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                println("onAdFailedToLoad'a dustum")
                println(adError.toString())
                rewardedAd = null
            }

            override fun onAdLoaded(ad: RewardedAd) {
                Log.d(TAG, "Ad was loaded.")
                rewardedAd = ad
                val options = ServerSideVerificationOptions.Builder()
                    .setCustomData(tokensDataClass.accessToken)
                    .build()
                rewardedAd?.setServerSideVerificationOptions(options)
            }
        })

        rewardedAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdClicked() {
                // Called when a click is recorded for an ad.
                Log.d(TAG, "Ad was clicked.")
            }

            override fun onAdDismissedFullScreenContent() {
                // Called when ad is dismissed.
                // Set the ad reference to null so you don't show the ad a second time.
                Log.d(TAG, "Ad dismissed fullscreen content.")
                val options = ActivityOptions.makeCustomAnimation(this@MainActivity, R.anim.activity_slide_down, 0)
                val intent = Intent(this@MainActivity, CompleteProfile::class.java); startActivity(intent, options.toBundle())
                rewardedAd = null
            }

            override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                // Called when ad fails to show.
                Log.e(TAG, "Ad failed to show fullscreen content.")
                rewardedAd = null
            }

            override fun onAdImpression() {
                // Called when an impression is recorded for an ad.
                Log.d(TAG, "Ad recorded an impression.")
            }

            override fun onAdShowedFullScreenContent() {
                // Called when ad is shown.
                Log.d(TAG, "Ad showed fullscreen content.")
            }
        }

        val mainActivityGeneralLayout = findViewById<RelativeLayout>(R.id.mainActivityGeneralLayout)
        mainActivityGeneralLayout.background = resources.getDrawable(R.drawable.main_menu_background, theme)



        controlVariables.getProfileAgain = false
        controlVariables.isSavedUsersLoaded = false
        controlVariables.isBurcCardOpen = false
        val tokensSharedPreferences = this.getSharedPreferences("token_prefs", Context.MODE_PRIVATE)
        val didLogin = tokensSharedPreferences.getBoolean("didLogin",false)

        val currentTime = System.currentTimeMillis() / 1000
        val savedTokenCreationTime = tokensSharedPreferences.getLong("token_creation_time", 0)
        println(" token creation time $savedTokenCreationTime")
        val isCreationTimeZero: Boolean = savedTokenCreationTime.toInt()==0

        when(isCreationTimeZero to didLogin){
            true to true ->{
                println("token creation time 0 ama didlogin true, didlogini false yapıyorum")
                authenticated.isLoggedIn = false
                val editor = tokensSharedPreferences.edit()
                editor.putBoolean("didLogin", false)
                editor.apply()
                authenticated.isLoggedIn = false
            }
            false to true -> {
                println("didlogin true, creation time sıfır değil, expire kontrolü yapıyorum")
                println("it has been ${currentTime - savedTokenCreationTime} seconds since last token refresh")
                controlVariables.isInExpireControl = true
                checkIsAccessExpired(this,currentTime, savedTokenCreationTime, 900, this) // if it has been more than 15 minutes till creation, refresh
            }
            true to false -> {
                println("token creation time 0, ve didlogin false. logout yapılmış")
                authenticated.isLoggedIn = false
            }
        }

        if ((currentTime - savedTokenCreationTime > 600000) && savedTokenCreationTime.toInt() != 0) {
            println("refresh de dolmus")
            authenticated.isLoggedIn = false
        } // refresh token lasts 30 minutes


        val burcCard = findViewById<CardView>(R.id.burcCard)
        val burcCardInnerLayout = findViewById<RelativeLayout>(R.id.burcCardInnerLayout)
        val miraBurcCardTop = findViewById<ImageView>(R.id.MiraBurcCardTop)
        val miraBurcCardTopTriangle = findViewById<ImageView>(R.id.MiraBurcCardTopTriangle)
        val mainActivityBackButton = findViewById<ImageButton>(R.id.mainActivityBackButton)
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
        val savedUsersScrollContainer = findViewById<HorizontalScrollView>(R.id.savedUsersScrollContainer)
        val savedUsersLinearContainer = findViewById<LinearLayout>(R.id.savedUsersLinearContainer)
        val addLookupUser = findViewById<CardView>(R.id.addLookupUser)
        val learnYourBurcButton = findViewById<AppCompatButton>(R.id.learnYourBurcButton)
        val settingsButton = findViewById<ImageButton>(R.id.settingsButton)
        val miraMainMenu = findViewById<ImageView>(R.id.miraMainMenu)
        val clickToGetHoroscopeText = findViewById<TextView>(R.id.ClickToGetHoroscopeText)
        val thinkingAnimation = findViewById<LottieAnimationView>(R.id.thinkingAnimation)
        val selectedModeTitle = findViewById<TextView>(R.id.selectedModeTitle)

        val useOrGainCoinMenuCard = findViewById<CardView>(R.id.useOrGainCoinMenuCard)
        val closeUseOrGainCoinMenuCard = findViewById<ImageButton>(R.id.closeUseOrGainCoinMenuCardButton)
        val useOrGainCoinMenuCurrentCoinText = findViewById<TextView>(R.id.useOrGainCoinMenuCurrentCoinText)
        val useOrGainCoinMenuTitle = findViewById<TextView>(R.id.useOrGainCoinMenuTitle)
        val watchAdToEarnCoinButton = findViewById<AppCompatButton>(R.id.WatchAdToEarnCoinButton)
        val useCoinButton = findViewById<AppCompatButton>(R.id.UseCoinButton)
        val coinAmountContainerLayout = findViewById<RelativeLayout>(R.id.coinAmountContainerLayout)
        val coinAmountText = findViewById<TextView>(R.id.coinAmountText)

        val claimCampaignCard = findViewById<CardView>(R.id.claimCampaignCard)
        val claimCampaignClaimButton = findViewById<AppCompatButton>(R.id.claimCampaignClaimButton)

        val generalSignParams = generalSign.layoutParams as RelativeLayout.LayoutParams
        val loveSignParams = loveSign.layoutParams as RelativeLayout.LayoutParams
        val careerSignParams = careerSign.layoutParams as RelativeLayout.LayoutParams
        val scale = this.resources.displayMetrics.density
        val oldTopMarginForModeCards = (45 * scale + 0.5f).toInt()

        val burcCardFunctions = BurcCardFunctions(
            this, learnYourBurcButton, savedUsersScrollContainer,
            burcCard, timeIntervalDailySelectedBG, timeIntervalWeeklySelectedBG, timeIntervalMonthlySelectedBG,
            timeIntervalYearlySelectedBG, savedUsersLinearContainer, this, generalSign, loveSign, careerSign, selectedModeTitle
        )

        controlVariables.isInDelay = false

        // if user has came to this activity from signUp fragment, then directly navigate user to
        // loginSignUp activity to login first
        if (authenticated.isFromSignIn) {
            val options = ActivityOptions.makeCustomAnimation(this, R.anim.activity_slide_down, 0)
            val intent = Intent(this, LoginSignupActivity::class.java);startActivity(intent, options.toBundle())
            setViewGone(burcCard, settingsButtonCard)
        }

        if (authenticated.isLoggedIn){
            setViewVisibleWithAnimation(this,coinAmountContainerLayout)
        }


        //navigate user directly to horoscope, when horoscope mode is love and when user is navigated to main screen from lookup user's complete profile
        if (controlVariables.navigateToHoroscope) {
            println("navigate to horoscope ile geldim ")
            println("${controlVariables.isFromCompleteLookup} controlVariables.isFromCompleteLookup")
            println("navigating to horoscope detail")
            replaceMainActivityToFragment(supportFragmentManager, HoroscopeDetailFragment())
            setViewGone(burcCard, settingsButtonCard, miraMainMenu,coinAmountContainerLayout)
            controlVariables.navigateToHoroscope = false
            controlVariables.isFromLoveHoroscope = false
        }

        println("is in expire ${controlVariables.isInExpireControl}")
        //get user status for coin, premium and campaign
        if (controlVariables.isInExpireControl){
            println("expire kontrolü bitsin diye get user statusu gecikmeli çagiriyorm")
            Handler(Looper.getMainLooper()).postDelayed({
            if (authenticated.isLoggedIn){
                getUserStatus(this, this,useOrGainCoinMenuCurrentCoinText,coinAmountText,claimCampaignCard,claimCampaignClaimButton)
            }
            },1000)
        } else {
            if (authenticated.isLoggedIn){
                getUserStatus(this, this,useOrGainCoinMenuCurrentCoinText,coinAmountText,claimCampaignCard,claimCampaignClaimButton)
            }
        }

        settingsButton.setOnClickListener {
            if (settingsButton.isEnabled) {
                settingsButton.isEnabled = false
                if (authenticated.isLoggedIn) {
                    makeGetProfileRequest(urls.getProfileURL, this, this, settingsButton) { _, _ -> }
                } else {
                    if (savedInstanceState == null) {
                        val options = ActivityOptions.makeCustomAnimation(this, R.anim.activity_slide_down, 0)
                        val intent = Intent(this, LoginSignupActivity::class.java); startActivity(intent, options.toBundle())
                        settingsButton.isEnabled = true
                    }
                }
            }
        }

        burcCard.setOnClickListener {
            if (!controlVariables.isInDelay) {
                if (!controlVariables.isBurcCardOpen) {
                    if (authenticated.isLoggedIn) {
                        setViewInvisible(clickToGetHoroscopeText)
                        setViewGoneWithAnimation(this, settingsButtonCard, miraMainMenu,coinAmountContainerLayout)
                        burcCardFunctions.animateBurcCardIn(
                            burcCard,
                            burcCardInnerLayout,
                            miraBurcCardTop,
                            miraBurcCardTopTriangle,
                            mainActivityBackButton,
                            mainActivityGeneralLayout
                        )
                    } else {
                        val options = ActivityOptions.makeCustomAnimation(this, R.anim.activity_slide_down, 0)
                        val intent = Intent(this, LoginSignupActivity::class.java); startActivity(intent, options.toBundle())
                    }
                } else {
                    println("controlVariables.isBurcCardOpen is ${controlVariables.isBurcCardOpen}")
                }
            } else {
                println("controlVariables.isInDelay is ${controlVariables.isInDelay}")

            }
        }

        fun handleCloseBurcCard() {
            burcCard.setCardBackgroundColor(Color.parseColor("#1c1444"))
            setViewVisibleWithAnimation(this, miraMainMenu, settingsButtonCard,coinAmountContainerLayout)
            mainActivityGeneralLayout.background = resources.getDrawable(R.drawable.main_menu_background, theme)
            selectedModeTitle.text = resources.getString(R.string.select_mode)
            burcCardFunctions.animateBurcCardOut(
                burcCard,
                miraBurcCardTop,
                miraBurcCardTopTriangle,
                mainActivityBackButton,
                settingsButtonCard,
                clickToGetHoroscopeText
            )
            generalSignParams.topMargin = oldTopMarginForModeCards
            loveSignParams.topMargin = oldTopMarginForModeCards
            careerSignParams.topMargin = oldTopMarginForModeCards
            controlVariables.selectedTimeInterval = null
        }

        mainActivityBackButton.setOnClickListener {
            if (controlVariables.isBurcCardOpen) {
                handleCloseBurcCard()
                closeUseOrGainCoinMenuCard.performClick()
            }
        }

        mainActivityGeneralLayout.setOnClickListener{
            if (controlVariables.isBurcCardOpen){
                handleCloseBurcCard()
                closeUseOrGainCoinMenuCard.performClick()
            }
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
                controlVariables.isFromAddLookupUser = false
            }
        }
        careerSign.setOnClickListener {
            if (!controlVariables.isCareerModeSelected) {
                burcCardFunctions.handleModeSelect("career")
                burcCardFunctions.handleIsSelected(isGeneral = false, isLove = false, isCareer = true, isFromLove = false)
            }
        }

        timeIntervalDaily.setOnClickListener {
            burcCardFunctions.handleTimeIntervalSelect("daily")
            burcCardFunctions.setSelectedFortuneFields(learnYourBurcButton,postHoroscopeData.type!!, postHoroscopeData.time_interval!!, getPartnerProfile.id) }

        timeIntervalWeekly.setOnClickListener {
            burcCardFunctions.handleTimeIntervalSelect("weekly")
            burcCardFunctions.setSelectedFortuneFields(learnYourBurcButton,postHoroscopeData.type!!, postHoroscopeData.time_interval!!, getPartnerProfile.id)
        }

        timeIntervalMonthly.setOnClickListener {
            burcCardFunctions.handleTimeIntervalSelect("monthly")
            burcCardFunctions.setSelectedFortuneFields(learnYourBurcButton,postHoroscopeData.type!!, postHoroscopeData.time_interval!!, getPartnerProfile.id)
        }
        timeIntervalYearly.setOnClickListener {
            burcCardFunctions.handleTimeIntervalSelect("yearly")
            burcCardFunctions.setSelectedFortuneFields(learnYourBurcButton,postHoroscopeData.type!!, postHoroscopeData.time_interval!!, getPartnerProfile.id)
        }

        addLookupUser.setOnClickListener {
            // if horoscope type is love, navigate user to complete profile screen to get lookup user's profile
            if (controlVariables.isFromLoveHoroscope) {
                setViewVisibleWithAnimation(this, addLookupUser.findViewById<ImageView>(R.id.addLookupUserBG))
                useOrGainCoinMenuTitle.text = "${postHoroscopeData.type.toString().uppercase()} / ${postHoroscopeData.time_interval.toString().uppercase()}"
                setViewVisibleWithAnimation(this, useOrGainCoinMenuCard)
                controlVariables.isFromAddLookupUser = true
            }
        }

        closeUseOrGainCoinMenuCard.setOnClickListener {
            setViewGoneWithAnimation(this, useOrGainCoinMenuCard)
            controlVariables.isInDelay = false
        }

        useCoinButton.setOnClickListener {
            controlVariables.navigateBackToProfileActivity = false
            if (userStatusDataClass.coin >= 10){
                coin.current_coin -= 10
                setViewGoneWithAnimation(this,useOrGainCoinMenuCard)
                controlVariables.isInDelay = true
                // if horoscope type is not love, call get horoscope function
                if (!controlVariables.isFromLoveHoroscope) {
                    handleCloseBurcCard()
                    Handler(Looper.getMainLooper()).postDelayed({
                        setViewGone(burcCard, settingsButtonCard, miraMainMenu,coinAmountContainerLayout)
                        HoroscopeFunctions.getHoroscope(thinkingAnimation, supportFragmentManager, this,this)
                    }, 350)
                    setViewGone(miraBurcCardTop, miraBurcCardTopTriangle)
                }
                if (controlVariables.isFromLoveHoroscope && !controlVariables.isFromAddLookupUser) {
                    handleCloseBurcCard()
                    Handler(Looper.getMainLooper()).postDelayed({
                        setViewGone(burcCard, settingsButtonCard, miraMainMenu,coinAmountContainerLayout)
                        getLoveHoroscope(thinkingAnimation, this, getPartnerProfile.id,supportFragmentManager,this)
                    }, 350)
                    setViewGone(miraBurcCardTop, miraBurcCardTopTriangle)
                }
                if (controlVariables.isFromLoveHoroscope && controlVariables.isFromAddLookupUser) {
                    handleCloseBurcCard()
                    Handler(Looper.getMainLooper()).postDelayed({
                        val options = ActivityOptions.makeCustomAnimation(this, R.anim.activity_slide_down, 0)
                        val intent = Intent(this, CompleteProfile::class.java); startActivity(intent, options.toBundle())
                        controlVariables.isFromAddLookupUser = false
                    }, 370)
                }
            }
        }

            watchAdToEarnCoinButton.setOnClickListener {
            controlVariables.navigateBackToProfileActivity = false

            rewardedAd?.let { ad ->
                Handler(Looper.getMainLooper()).postDelayed({
                    ad.show(this, OnUserEarnedRewardListener { _ ->
                        // Handle the reward.
                        Log.d(TAG, "User earned the reward.")
                        getUserStatus(this, this,useOrGainCoinMenuCurrentCoinText,coinAmountText,claimCampaignCard,claimCampaignClaimButton)
//                        val options = ActivityOptions.makeCustomAnimation(this, R.anim.activity_slide_down, 0)
//                        val intent = Intent(this, MainActivity::class.java); startActivity(intent, options.toBundle())
                    })
                }, 190)
//                handleCloseBurcCard()
//                setViewGoneWithAnimation(this,useOrGainCoinMenuCard)

                controlVariables.isInDelay = true
                // if horoscope type is not love, call get horoscope function
//                if (!controlVariables.isFromLoveHoroscope) {
//                    coin.current_coin -= 10
//                    Handler(Looper.getMainLooper()).postDelayed({
//                        setViewGone(burcCard, settingsButtonCard, miraMainMenu,coinAmountContainerLayout)
//                        HoroscopeFunctions.getHoroscope(thinkingAnimation, supportFragmentManager, this,this)
//                    }, 350)
//                    setViewGone(miraBurcCardTop, miraBurcCardTopTriangle)
//                }
//
//                if (controlVariables.isFromLoveHoroscope && !controlVariables.isFromAddLookupUser) {
//                    coin.current_coin -= 10
//                    Handler(Looper.getMainLooper()).postDelayed({
//                        setViewGone(burcCard, settingsButtonCard, miraMainMenu,coinAmountContainerLayout)
//                        getLoveHoroscope(thinkingAnimation, this, getPartnerProfile.id,supportFragmentManager,this)
//                    }, 350)
//                    setViewGone(miraBurcCardTop, miraBurcCardTopTriangle)
//                }

            } ?: run {
                Log.d(TAG, "The rewarded ad wasn't ready yet.")
                this.runOnUiThread{
                    Toast.makeText(this, this.resources.getString(R.string.error_loading_ad), Toast.LENGTH_SHORT).show()
                }
            }
        }

        learnYourBurcButton.setOnClickListener {
            controlVariables.navigateBackToProfileActivity = false
            if (authenticated.isLoggedIn) {
                if (!controlVariables.isSameFortune && !controlVariables.isLoveSameFortune){
                    if (controlVariables.isCareerModeSelected or controlVariables.isLoveModeSelected or controlVariables.isGeneralModeSelected) {
                        controlVariables.isInDelay = true
                        setViewVisibleWithAnimation(this, useOrGainCoinMenuCard)
                        useOrGainCoinMenuTitle.text = "${postHoroscopeData.type.toString().uppercase()} / ${postHoroscopeData.time_interval.toString().uppercase()}"
                    }
                }
                else if(controlVariables.isSameFortune && !controlVariables.isLoveSameFortune){
                    // get daily general/career horoscope again
//                    burcCardFunctions.setSelectedFortuneFields(postHoroscopeData.type!!, postHoroscopeData.time_interval!!, getPartnerProfile.id)
                    handleCloseBurcCard()
                    Handler(Looper.getMainLooper()).postDelayed({
                        setViewGone(burcCard, settingsButtonCard, miraMainMenu,coinAmountContainerLayout)
                        HoroscopeFunctions.getHoroscope(thinkingAnimation, supportFragmentManager, this,this)
                        controlVariables.isSameFortune = false
                        controlVariables.isLoveSameFortune = false
                    }, 350)
                    setViewGone(miraBurcCardTop, miraBurcCardTopTriangle)
                }
                else if (controlVariables.isLoveSameFortune && !controlVariables.isSameFortune){
                    // get daily love horoscope again
//                    burcCardFunctions.setSelectedFortuneFields(postHoroscopeData.type!!, postHoroscopeData.time_interval!!, getPartnerProfile.id)
                    handleCloseBurcCard()
                    Handler(Looper.getMainLooper()).postDelayed({
                        setViewGone(burcCard, settingsButtonCard, miraMainMenu,coinAmountContainerLayout)
                        getLoveHoroscope(thinkingAnimation, this, getPartnerProfile.id,supportFragmentManager,this)
                        controlVariables.isSameFortune = false
                        controlVariables.isLoveSameFortune = false
                    }, 350)
                    setViewGone(miraBurcCardTop, miraBurcCardTopTriangle)
                }
            }

            // if user is not logged in
            else {
                // if user is not logged in, close burcCard and then after 300ms make the transition
                // to login signup activity
                burcCardFunctions.animateBurcCardOut(burcCard, miraBurcCardTop, miraBurcCardTopTriangle, mainActivityBackButton, settingsButtonCard, clickToGetHoroscopeText)
                Handler(Looper.getMainLooper()).postDelayed({
                    val options = ActivityOptions.makeCustomAnimation(this, R.anim.activity_slide_down, 0)
                    val intent = Intent(this, LoginSignupActivity::class.java); startActivity(intent, options.toBundle())
                }, 250)
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