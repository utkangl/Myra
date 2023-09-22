package com.example.falci

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.cardview.widget.CardView
import com.airbnb.lottie.LottieAnimationView
import com.example.falci.internalClasses.*
import com.example.falci.internalClasses.InternalFunctions.SetVisibilityFunctions.setViewGone
import com.example.falci.internalClasses.InternalFunctions.TimeFormatFunctions.separateBirthDate
import com.example.falci.internalClasses.InternalFunctions.TimeFormatFunctions.separateBirthTime
import com.example.falci.internalClasses.ProfileFunctions.ProfileFunctions.makeGetProfileRequest
import com.example.falci.internalClasses.TransitionToFragment.ReplaceFragmentWithAnimation.replaceFragmentWithAnimation
import com.example.falci.internalClasses.dataClasses.*
import org.json.JSONObject

lateinit var userProfile: UserProfileDataClass

class MainActivity : AppCompatActivity() {

    private lateinit var burcCard: CardView
    private lateinit var settingsButtonCard: CardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        burcCard = findViewById<CardView>(R.id.burcCard)
        val backArrowCard = findViewById<CardView>(R.id.backArrowCard)
        val generalSign = findViewById<CardView>(R.id.generalSign)
        val loveSign = findViewById<CardView>(R.id.loveSign)
        val careerSign = findViewById<CardView>(R.id.careerSign)
        val backArrow = findViewById<ImageButton>(R.id.back_arrow)
        val dailyButton = findViewById<AppCompatButton>(R.id.dailyButton)
        val monthlyButton = findViewById<AppCompatButton>(R.id.monthlyButton)
        val yearlyButton = findViewById<AppCompatButton>(R.id.yearlyButton)
        val learnYourBurcButton = findViewById<AppCompatButton>(R.id.learnYourBurcButton)
        settingsButtonCard = findViewById<CardView>(R.id.settingsButtonCard)
        val settingsButton = findViewById<ImageButton>(R.id.settingsButton)
        val miraMainMenu = findViewById<ImageView>(R.id.miraMainMenu)


        val animationHelper = AnimationHelper(this)
        val signCardViewUpdater = SignCardViewUpdater(this)

        if (authenticated.isFromSignIn){
            val intent = Intent(this, LoginSignupActivity::class.java);startActivity(intent)
            setViewGone(burcCard, settingsButtonCard)
        }

        animationHelper.initializeViews(
            burcCard, generalSign, loveSign, careerSign, dailyButton,
            monthlyButton, yearlyButton, learnYourBurcButton, backArrowCard, settingsButtonCard)


        signCardViewUpdater.initializeViews(
            burcCard, generalSign, loveSign, careerSign, dailyButton,
            monthlyButton, yearlyButton, learnYourBurcButton)

        burcCard.setOnClickListener { animationHelper.animateBurcCardIn() }

        backArrow.setOnClickListener { animationHelper.animateBurcCardOut()}

        generalSign.setOnClickListener { signCardViewUpdater.updateUIForCardClick(generalSign); postHoroscopeData.type ="general"}
        loveSign.setOnClickListener { signCardViewUpdater.updateUIForCardClick(loveSign); postHoroscopeData.type ="love"}
        careerSign.setOnClickListener { signCardViewUpdater.updateUIForCardClick(careerSign); postHoroscopeData.type ="career"}

        val periodButtonViewUpdater = PeriodButtonViewUpdater(this)
        dailyButton.setOnClickListener { periodButtonViewUpdater.updateButtonView(dailyButton); postHoroscopeData.time_interval ="daily" }
        monthlyButton.setOnClickListener { periodButtonViewUpdater.updateButtonView(monthlyButton); postHoroscopeData.time_interval ="monthly"  }
        yearlyButton.setOnClickListener { periodButtonViewUpdater.updateButtonView(yearlyButton); postHoroscopeData.time_interval ="yearly"  }

        settingsButton.setOnClickListener{

            if(authenticated.isLoggedIn){

                makeGetProfileRequest(urls.getProfileURL, loginTokens.loginAccessToken)
                { responseBody, exception ->
                    if (exception != null) {
                        println("Error: ${exception.message}")
                    } else {
                        println("Response: $responseBody")

                        val responseJson = responseBody?.let { it1 -> JSONObject(it1) }

                        if (responseJson != null) {
                            userProfile = UserProfileDataClass(
                                username = responseJson.optString("username"),
                                firstName = responseJson.optString("first_name"),
                                birthPlace = responseJson.optString("birth_place"),
                                birthDay = separateBirthDate(responseJson.optString("birth_day")),
                                birthTime = separateBirthTime(responseJson.optString("birth_day")),
                                relationshipStatus = responseJson.optString("relationship_status"),
                                gender = responseJson.optString("gender"),
                                occupation = responseJson.optString("occupation")
                            )
                        }
                    }
                }

                // change to profile screen if loggedin
                if (savedInstanceState == null) {
//                    replaceMainActivityToFragment(supportFragmentManager, profileFragment)
                    val intent = Intent(this, ProfileActivity::class.java);startActivity(intent)

                }

                setViewGone(burcCard,settingsButtonCard)
            }

            if (!authenticated.isLoggedIn) {
                if (savedInstanceState == null) {
                    val intent = Intent(this, LoginSignupActivity::class.java); startActivity(intent)

                }

                setViewGone(burcCard, settingsButtonCard)
            }
        }

        val thinkingAnimation = findViewById<LottieAnimationView>(R.id.thinkingAnimation)

        learnYourBurcButton.setOnClickListener{
            animationHelper.animateBurcCardOutt()
            if (authenticated.isLoggedIn){
                setViewGone(burcCard,settingsButtonCard, miraMainMenu)
                HoroscopeFunctions.getHoroscope(thinkingAnimation, supportFragmentManager)

            }else{
                val intent = Intent(this, LoginSignupActivity::class.java); startActivity(intent)
                setViewGone(burcCard,  settingsButtonCard)
            }
        }

        miraMainMenu.setOnClickListener{
            if (authenticated.isLoggedIn){
                replaceFragmentWithAnimation(supportFragmentManager, ChatFragment())
                setViewGone(miraMainMenu, burcCard, settingsButtonCard)
            }
        }
    }


    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.main_fragment_container)
        if (currentFragment == null) {
            val builder = AlertDialog.Builder(this)
            builder.setMessage("Uygulamadan çıkmak istediğinize emin misiniz?")
            builder.setPositiveButton("Evet") { dialogInterface: DialogInterface, i: Int ->
                moveTaskToBack(true)
                finish()
            }
            builder.setNegativeButton("Hayır", null)
            val dialog = builder.create()
            dialog.show()
        } else {
            super.onBackPressed()
        }
    }

}