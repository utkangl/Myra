package com.example.falci

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.cardview.widget.CardView
import com.example.falci.internalClasses.AnimationHelper
import com.example.falci.internalClasses.InternalFunctions.SetVisibilityFunctions.setViewGone
import com.example.falci.internalClasses.InternalFunctions.SetVisibilityFunctions.setViewVisible
import com.example.falci.internalClasses.InternalFunctions.TimeFormatFunctions.separateBirthDate
import com.example.falci.internalClasses.InternalFunctions.TimeFormatFunctions.separateBirthTime
import com.example.falci.internalClasses.PeriodButtonViewUpdater
import com.example.falci.internalClasses.ProfileFunctions.ProfileFunctions.makeGetProfileRequest
import com.example.falci.internalClasses.UserProfileDataClass
import com.example.falci.internalClasses.TransitionToFragment.ReplaceActivityToFragment.replaceActivityToFragment
import com.example.falci.internalClasses.urls
import org.json.JSONObject

lateinit var userProfile: UserProfileDataClass

class MainActivity : AppCompatActivity() {

    lateinit var chatWithMiraButton: ImageView
    lateinit var empty: TextView
    lateinit var burcCard: CardView
    lateinit var tarotFali: CardView
    lateinit var fortuneCookie: CardView
    lateinit var settingsButtonCard: CardView

    private val loginFragment = Loginfragment()
    private val chatFragment = ChatFragment()
    private val profileFragment = ProfileFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        chatWithMiraButton = findViewById<ImageView>(R.id.mainMenuMira)
        empty = findViewById<TextView>(R.id.empty)
        burcCard = findViewById<CardView>(R.id.burcCard)
        tarotFali = findViewById<CardView>(R.id.tarotFali)
        fortuneCookie = findViewById<CardView>(R.id.fortuneCookie)
        val backArrowCard = findViewById<CardView>(R.id.backarrowcard)
        val generalSign = findViewById<CardView>(R.id.generalSign)
        val loveSign = findViewById<CardView>(R.id.loveSign)
        val careerSign = findViewById<CardView>(R.id.careerSign)
        val backArrow = findViewById<ImageButton>(R.id.back_arrow)
        val dailyButton = findViewById<AppCompatButton>(R.id.dailyButton)
        val monthlyButton = findViewById<AppCompatButton>(R.id.monthlyButton)
        val yearlyButton = findViewById<AppCompatButton>(R.id.yearlyButton)
        val learnYourBurcButton = findViewById<AppCompatButton>(R.id.learnyourburcButton)
        settingsButtonCard = findViewById<CardView>(R.id.settingsbuttoncard)
        val settingsButton = findViewById<ImageButton>(R.id.settingsbutton)

        val animationHelper = AnimationHelper(this)
        val signCardViewUpdater = SignCardViewUpdater(this)

        animationHelper.initializeViews(
            burcCard, generalSign, loveSign, careerSign, dailyButton,
            monthlyButton, yearlyButton, learnYourBurcButton, backArrowCard, fortuneCookie,
            tarotFali, settingsButtonCard, chatWithMiraButton, empty)


        signCardViewUpdater.initializeViews(
            burcCard, generalSign, loveSign, careerSign, dailyButton,
            monthlyButton, yearlyButton, learnYourBurcButton)

        chatWithMiraButton.setOnClickListener {

            // change to chat screen if loggedin
            if (savedInstanceState == null){
                if (isLoggedin) { replaceActivityToFragment(supportFragmentManager, chatFragment)}
            }

            // change to login screen if not loggedin
            if (savedInstanceState == null){
                if (!isLoggedin) { replaceActivityToFragment(supportFragmentManager, loginFragment)}
            }

            setViewGone(chatWithMiraButton, empty, burcCard, tarotFali, fortuneCookie, settingsButtonCard)
        }

        burcCard.setOnClickListener { animationHelper.animateBurcCardIn() }

        backArrow.setOnClickListener { animationHelper.animateBurcCardOut() }

        generalSign.setOnClickListener { signCardViewUpdater.updateUIForCardClick(generalSign) }
        loveSign.setOnClickListener { signCardViewUpdater.updateUIForCardClick(loveSign) }
        careerSign.setOnClickListener { signCardViewUpdater.updateUIForCardClick(careerSign) }

        val periodButtonViewUpdater = PeriodButtonViewUpdater(this)
        dailyButton.setOnClickListener { periodButtonViewUpdater.updateButtonView(dailyButton) }
        monthlyButton.setOnClickListener { periodButtonViewUpdater.updateButtonView(monthlyButton) }
        yearlyButton.setOnClickListener { periodButtonViewUpdater.updateButtonView(yearlyButton) }

        settingsButton.setOnClickListener{

            if(isLoggedin){

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
                    replaceActivityToFragment(supportFragmentManager, profileFragment)
                }

                setViewGone(chatWithMiraButton, empty, burcCard, tarotFali, fortuneCookie, settingsButtonCard)
            }

            if (!isLoggedin) {
                if (savedInstanceState == null) {
                    replaceActivityToFragment(supportFragmentManager, loginFragment)
                }

                setViewGone(chatWithMiraButton, empty, burcCard, tarotFali, fortuneCookie, settingsButtonCard)
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()

        // Check if there are fragments in the back stack
        if (supportFragmentManager.backStackEntryCount == 0) {
            overridePendingTransition(R.anim.slide_up, R.anim.slide_down)

            setViewVisible(chatWithMiraButton, empty, burcCard, tarotFali, fortuneCookie, settingsButtonCard)

        }
    }

}