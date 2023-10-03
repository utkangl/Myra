package com.example.falci

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageButton
import android.widget.ImageView
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
import com.example.falci.internalClasses.InternalFunctions.SetVisibilityFunctions.setViewGone
import com.example.falci.internalClasses.InternalFunctions.SetVisibilityFunctions.setViewVisible
import com.example.falci.internalClasses.ProfileFunctions.ProfileFunctions.makeGetProfileRequest
import com.example.falci.internalClasses.TransitionToFragment.ReplaceActivityToFragment.replaceMainActivityToFragment
import com.example.falci.internalClasses.dataClasses.*
import com.google.gson.Gson
import org.json.JSONObject

//late init var userProfile: UserProfileDataClass

class MainActivity : AppCompatActivity() {

   private lateinit var splashViewModel: ViewModel

//   private val splashViewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        splashViewModel = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(application))[SplashViewModel::class.java]
        installSplashScreen().apply {
            setKeepOnScreenCondition{
                (splashViewModel as SplashViewModel).isLoading.value  // keep showing splash screen until launching is over
            }
        }
        setContentView(R.layout.activity_main)

        val burcCard = findViewById<CardView>(R.id.burcCard)
        val settingsButtonCard = findViewById<CardView>(R.id.settingsButtonCard)
        val backArrowCard = findViewById<CardView>(R.id.backArrowCard)
        val generalSign = findViewById<CardView>(R.id.generalSign)
        val loveSign = findViewById<CardView>(R.id.loveSign)
        val careerSign = findViewById<CardView>(R.id.careerSign)
        val backArrow = findViewById<ImageButton>(R.id.back_arrow)
        val dailyButton = findViewById<AppCompatButton>(R.id.dailyButton)
        val monthlyButton = findViewById<AppCompatButton>(R.id.monthlyButton)
        val yearlyButton = findViewById<AppCompatButton>(R.id.yearlyButton)
        val learnYourBurcButton = findViewById<AppCompatButton>(R.id.learnYourBurcButton)
        val settingsButton = findViewById<ImageButton>(R.id.settingsButton)
        val miraMainMenu = findViewById<ImageView>(R.id.miraMainMenu)
        val zodiacSign = findViewById<CardView>(R.id.planet_horoscope_image_card)
        val burcCardExplanationTextScroll = findViewById<ScrollView>(R.id.burcCardExplanationTextScroll)
        val clickToGetHoroscopeText = findViewById<TextView>(R.id.ClickToGetHoroscopeText)
        val animationHelper = AnimationHelper(this)
        val signCardViewUpdater = SignCardViewUpdater(this)
        val periodButtonViewUpdater = PeriodButtonViewUpdater(this)
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
            replaceMainActivityToFragment(supportFragmentManager, HoroscopeDetailFragment())
            setViewGone(burcCard,settingsButtonCard, miraMainMenu)
            navigateToHoroscope = false
            isFromLoveHoroscope = false
        }

        // initialize the views that will animation helper will use. Animation helper functions
        // use some views to set their visibility or size, and this views are declared as late init
        // so we initialize views before calling the functions that uses this views
        animationHelper.initializeViews(
            burcCard, generalSign, loveSign, careerSign, dailyButton,
            monthlyButton, yearlyButton, learnYourBurcButton, backArrowCard,
            settingsButtonCard, zodiacSign,  burcCardExplanationTextScroll
        )
        signCardViewUpdater.initializeViews(
            burcCard, generalSign, loveSign, careerSign, dailyButton,
            monthlyButton, yearlyButton, learnYourBurcButton)


        // if burcCard is clicked and user is logged in open the card and make the clickToGetHoroscopeText invisible, if not logged in navigate user to login screen
        burcCard.setOnClickListener {
            if(authenticated.isLoggedIn) {
                setViewGone(clickToGetHoroscopeText); animationHelper.animateBurcCardIn()
            }
            else {
                val options = ActivityOptions.makeCustomAnimation(this, R.anim.activity_slide_down, 0)
                val intent = Intent(this, LoginSignupActivity::class.java); startActivity(intent, options.toBundle())
            }
        }
        backArrow.setOnClickListener { setViewVisible(clickToGetHoroscopeText); animationHelper.animateBurcCardOut()}                                             // if backArrow is clicked close the card and make the clickToGetHoroscopeText visible
        generalSign.setOnClickListener { signCardViewUpdater.updateUIForCardClick(generalSign); postHoroscopeData.type ="general"; isFromLoveHoroscope = false}  //make burcCard larger and set postHoroscopeData's type to general
        loveSign.setOnClickListener { signCardViewUpdater.updateUIForCardClick(loveSign); postHoroscopeData.type ="love"; isFromLoveHoroscope = true}           //make burcCard larger and set postHoroscopeData's type to love
        careerSign.setOnClickListener { signCardViewUpdater.updateUIForCardClick(careerSign); postHoroscopeData.type ="career"; isFromLoveHoroscope = false}   //make burcCard larger and set postHoroscopeData's type to career
        dailyButton.setOnClickListener { periodButtonViewUpdater.updateButtonView(dailyButton); postHoroscopeData.time_interval ="daily" }                    //make burcCard larger and set postHoroscopeData's time interval to daily
        monthlyButton.setOnClickListener { periodButtonViewUpdater.updateButtonView(monthlyButton); postHoroscopeData.time_interval ="monthly"}              //make burcCard larger and set postHoroscopeData's time interval to monthly
        yearlyButton.setOnClickListener { periodButtonViewUpdater.updateButtonView(yearlyButton); postHoroscopeData.time_interval ="yearly"}                //make burcCard larger and set postHoroscopeData's time interval to yearly

        settingsButton.setOnClickListener{
            // when user clicks to profile button and if user is logged in, get profile informations
            // and set the response to UserProfileDataClass's instance
            if(authenticated.isLoggedIn){
                val gson = Gson()
                makeGetProfileRequest(urls.getProfileURL, tokensDataClass.accessToken)
                { responseBody, exception ->
                    if (exception != null) {
                        println("Error var : ${exception.message}")
                    } else {
                        val responseJson = responseBody?.let { it1 -> JSONObject(it1) }
                        userProfile =  gson.fromJson(responseBody, UserProfileDataClass::class.java)

                        if (responseJson != null) {
                            //and navigate user to ProfileActivity
                            if (savedInstanceState == null) {
                                val options = ActivityOptions.makeCustomAnimation(this, R.anim.activity_slide_down, 0)
                                val intent = Intent(this, ProfileActivity::class.java); startActivity(intent,options.toBundle())
                            }
                        }
                    }
                }
            }

            // when user clicks to profile button but did not login, navigate user to loginSignUp activity
            if (!authenticated.isLoggedIn) {
                if (savedInstanceState == null) {
                    val options = ActivityOptions.makeCustomAnimation(this, R.anim.activity_slide_down, 0)
                    val intent = Intent(this, LoginSignupActivity::class.java); startActivity(intent, options.toBundle())
                }
            }
        }


        learnYourBurcButton.setOnClickListener{
            // if user is logged in, close burcCard and then after 300ms make the transition
            // to horoscope detail fragment, but until api response with horoscope
            // play thinking animation
            if (authenticated.isLoggedIn){

                // if horoscope type is love, navigate user to complete profile screen to get lookup user's profile
                if (isFromLoveHoroscope){
                    val options = ActivityOptions.makeCustomAnimation(this, R.anim.activity_slide_down, 0)
                    val intent = Intent(this, CompleteProfile::class.java); startActivity(intent,options.toBundle())
                }

                // if horoscope type is not love, call get horoscope function
                if (!isFromLoveHoroscope){
                    animationHelper.animateBurcCardOut()
                    Handler(Looper.getMainLooper()).postDelayed({
                        setViewGone(burcCard,settingsButtonCard, miraMainMenu)
                        HoroscopeFunctions.getHoroscope(thinkingAnimation, supportFragmentManager)
                    }, 300)
                }


            }else{
                // if user is not logged in, close burcCard and then after 300ms make the transition
                // to login signup activity
                animationHelper.animateBurcCardOut()
                Handler(Looper.getMainLooper()).postDelayed({
                    val options = ActivityOptions.makeCustomAnimation(this, R.anim.activity_slide_down, 0)
                    val intent = Intent(this, LoginSignupActivity::class.java); startActivity(intent,options.toBundle())
                }, 300)
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
                finish()
            }
            builder.setNegativeButton("No", null)
            val dialog = builder.create()
            dialog.show()
        } else {
            super.onBackPressed()
        }
    }


} // end of class