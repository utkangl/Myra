package com.example.falci

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.cardview.widget.CardView
import com.example.falci.LoginSignupActivity.ProfileFunctions.makeGetProfileRequest as ProfileRequestFunc
import com.example.falci.LoginSignupActivity.Loginfunctions.AccessToken
import com.example.falci.internalClasses.AnimationHelper
import com.example.falci.internalClasses.InternalFunctions.SetVisibilityFunctions.setViewGone
import com.example.falci.internalClasses.InternalFunctions.SetVisibilityFunctions.setViewVisible
import com.example.falci.internalClasses.InternalFunctions.TimeFormatFunctions.separateBirthDate
import com.example.falci.internalClasses.InternalFunctions.TimeFormatFunctions.separateBirthTime
import com.example.falci.internalClasses.PeriodButtonViewUpdater
import com.example.falci.internalClasses.UserProfileDataClass
import com.example.falci.internalClasses.InternalFunctions.ReplaceActivityToFragment.replaceActivityToFragment
import org.json.JSONObject
import kotlin.math.log10

lateinit var userProfile: UserProfileDataClass

class MainActivity : AppCompatActivity() {

    lateinit var chatwithmiraButton: ImageView
    lateinit var empty: TextView
    lateinit var burcCard: CardView
    lateinit var tarotFali: CardView
    lateinit var fortuneCookie: CardView
    lateinit var settingsbuttoncard: CardView

    val loginfragment = Loginfragment()
    val chatfragment = ChatFragment()
    val profileFragment = ProfileFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        chatwithmiraButton = findViewById<ImageView>(R.id.mainMenuMira)
        empty = findViewById<TextView>(R.id.empty)
        burcCard = findViewById<CardView>(R.id.burcCard)
        tarotFali = findViewById<CardView>(R.id.tarotFali)
        fortuneCookie = findViewById<CardView>(R.id.fortuneCookie)
        val backarrowcard = findViewById<CardView>(R.id.backarrowcard)
        val generalSign = findViewById<CardView>(R.id.generalSign)
        val loveSign = findViewById<CardView>(R.id.loveSign)
        val careerSign = findViewById<CardView>(R.id.careerSign)
        val backArrow = findViewById<ImageButton>(R.id.back_arrow)
        val dailyButton = findViewById<AppCompatButton>(R.id.dailyButton)
        val monthlyButton = findViewById<AppCompatButton>(R.id.monthlyButton)
        val yearlyButton = findViewById<AppCompatButton>(R.id.yearlyButton)
        val learnyourburcButton = findViewById<AppCompatButton>(R.id.learnyourburcButton)
        settingsbuttoncard = findViewById<CardView>(R.id.settingsbuttoncard)
        val settingsbutton = findViewById<ImageButton>(R.id.settingsbutton)

        val animationHelper = AnimationHelper(this)
        val signCardViewUpdater = SignCardViewUpdater(this)

        animationHelper.initializeViews(
            burcCard, generalSign, loveSign, careerSign, dailyButton,
            monthlyButton, yearlyButton, learnyourburcButton, backarrowcard, fortuneCookie,
            tarotFali, settingsbuttoncard, chatwithmiraButton, empty)


        signCardViewUpdater.initializeViews(
            burcCard, generalSign, loveSign, careerSign, dailyButton,
            monthlyButton, yearlyButton, learnyourburcButton)

        chatwithmiraButton.setOnClickListener {

            // change to chat screen if loggedin
            if (savedInstanceState == null){
                if (isLoggedin) { replaceActivityToFragment(supportFragmentManager, chatfragment)}
            }

            // change to login screen if not loggedin
            if (savedInstanceState == null){
                if (!isLoggedin) { replaceActivityToFragment(supportFragmentManager, loginfragment)}
            }

            setViewGone(chatwithmiraButton, empty, burcCard, tarotFali, fortuneCookie, settingsbuttoncard)
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

        settingsbutton.setOnClickListener{

            if(isLoggedin){

                ProfileRequestFunc(url = "http://31.210.43.174:1337/auth/profile/", accessToken = AccessToken)
                { responseBody, exception ->
                    if (exception != null) {
                        println("Error: ${exception.message}")
                    } else {
                        println("Response: $responseBody")

                        val responseJson = JSONObject(responseBody)

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

                // change to profile screen if loggedin
                if (savedInstanceState == null) {
                    replaceActivityToFragment(supportFragmentManager, profileFragment)
                }

                setViewGone(chatwithmiraButton, empty, burcCard, tarotFali, fortuneCookie, settingsbuttoncard)
            }

            if (!isLoggedin) {
                if (savedInstanceState == null) {
                    replaceActivityToFragment(supportFragmentManager, loginfragment)
                }

                setViewGone(chatwithmiraButton, empty, burcCard, tarotFali, fortuneCookie, settingsbuttoncard)
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()

        // Check if there are fragments in the back stack
        if (supportFragmentManager.backStackEntryCount == 0) {
            overridePendingTransition(R.anim.slide_up, R.anim.slide_down)

            setViewVisible(chatwithmiraButton, empty, burcCard, tarotFali, fortuneCookie, settingsbuttoncard)

        }
    }

}