package com.example.falci

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.cardview.widget.CardView
import com.example.falci.LoginSignupActivity.ProfileFunctions.makeGetProfileRequest as ProfileRequestFunc
import com.example.falci.LoginSignupActivity.Loginfunctions.AccessToken
import com.example.falci.internalClasses.InternalFunctions.SetVisibilityFunctions.setViewGone
import com.example.falci.internalClasses.InternalFunctions.SetVisibilityFunctions.setViewVisible
import com.example.falci.internalClasses.InternalFunctions.TimeFormatFunctions.separateBirthDate
import com.example.falci.internalClasses.InternalFunctions.TimeFormatFunctions.separateBirthTime
import com.example.falci.internalClasses.UserProfileDataClass
import org.json.JSONObject

    lateinit var userProfile: UserProfileDataClass

class MainActivity : AppCompatActivity() {

    lateinit var chatwithmiraButton: ImageView
    lateinit var empty: TextView
    lateinit var burcCard: CardView
    lateinit var tarotFali: CardView
    lateinit var fortuneCookie: CardView
    lateinit var backarrowcard: CardView
    lateinit var generalSign: CardView
    lateinit var loveSign: CardView
    lateinit var careerSign: CardView
    lateinit var backArrow: ImageButton
    lateinit var dailyButton: AppCompatButton
    lateinit var monthlyButton: AppCompatButton
    lateinit var yearlyButton: AppCompatButton
    lateinit var learnyourburcButton: AppCompatButton
    lateinit var settingsbuttoncard: CardView
    lateinit var settingsbutton: ImageButton

    val loginfragment = Loginfragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        chatwithmiraButton = findViewById(R.id.mainMenuMira)
        empty = findViewById(R.id.empty)
        burcCard = findViewById(R.id.burcCard)
        tarotFali = findViewById(R.id.tarotFali)
        fortuneCookie = findViewById(R.id.fortuneCookie)
        backarrowcard = findViewById(R.id.backarrowcard)
        generalSign = findViewById(R.id.generalSign)
        loveSign = findViewById(R.id.loveSign)
        careerSign = findViewById(R.id.careerSign)
        backArrow = findViewById(R.id.back_arrow)
        dailyButton = findViewById(R.id.dailyButton)
        monthlyButton = findViewById(R.id.monthlyButton)
        yearlyButton = findViewById(R.id.yearlyButton)
        learnyourburcButton = findViewById(R.id.learnyourburcButton)
        settingsbuttoncard = findViewById(R.id.settingsbuttoncard)
        settingsbutton = findViewById(R.id.settingsbutton)


        chatwithmiraButton.setOnClickListener {

            println(isLoggedin)

            if (isLoggedin) {
                if (savedInstanceState == null) {
                    supportFragmentManager.beginTransaction()
                        .setCustomAnimations(
                            R.anim.slide_down,
                            R.anim.slide_up,
                            R.anim.slide_down,
                            R.anim.slide_up
                        )
                        .replace(R.id.main_fragment_container, ChatFragment())
                        .addToBackStack(null)
                        .commit()

                }
            }

            if (!isLoggedin) {
                if (savedInstanceState == null) {
                    supportFragmentManager.beginTransaction()
                        .setCustomAnimations(
                            R.anim.slide_down,
                            R.anim.slide_up,
                            R.anim.slide_down,
                            R.anim.slide_up
                        )
                        .replace(R.id.main_fragment_container, Loginfragment())
                        .addToBackStack(null)
                        .commit()

                }
            }

            setViewGone(chatwithmiraButton, empty, burcCard, tarotFali, fortuneCookie, settingsbuttoncard)
        }

        burcCard.setOnClickListener {

            val scale = resources.displayMetrics.density
            val newWidth = (370 * scale + 0.5f).toInt()
            val newHeight = (550 * scale + 0.5f).toInt()
            val burcCardMarginBottom = (-190 * scale + 0.5f).toInt()

            val params = burcCard.layoutParams as RelativeLayout.LayoutParams

            val animatorWidth = ValueAnimator.ofInt(burcCard.width, newWidth)
            animatorWidth.addUpdateListener { animation ->
                val value = animation.animatedValue as Int
                params.width = value
                burcCard.layoutParams = params
            }

            val animatorHeight = ValueAnimator.ofInt(burcCard.height, newHeight)
            animatorHeight.addUpdateListener { animation ->
                val value = animation.animatedValue as Int
                params.height = value
                params.bottomMargin = burcCardMarginBottom
                burcCard.layoutParams = params
            }

            val animatorSet = AnimatorSet()
            animatorSet.playTogether(animatorWidth, animatorHeight)
            animatorSet.duration = 500 // Animasyon süresi (ms)

            // Animasyon tamamlandığında çalışacak bir Listener ekleyin
            animatorSet.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    setViewVisible(backarrowcard, generalSign, loveSign, careerSign)
                }

                override fun onAnimationStart(animation: Animator) {
                    setViewGone(chatwithmiraButton, tarotFali, fortuneCookie, dailyButton, monthlyButton, yearlyButton, learnyourburcButton, settingsbuttoncard)
                    params.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE)
                }

            })

            animatorSet.start()

            val generalSignCardparams = generalSign.layoutParams as RelativeLayout.LayoutParams
            val loveSignCardparams = loveSign.layoutParams as RelativeLayout.LayoutParams
            val careerSignCardparams = careerSign.layoutParams as RelativeLayout.LayoutParams

            val dpMarginBottom = 25
            val newMarginBottom = (dpMarginBottom * scale + 0.5f).toInt()

            generalSignCardparams.bottomMargin = newMarginBottom
            loveSignCardparams.bottomMargin = newMarginBottom
            careerSignCardparams.bottomMargin = newMarginBottom

            generalSign.layoutParams = generalSignCardparams
            loveSign.layoutParams = loveSignCardparams
            careerSign.layoutParams = careerSignCardparams
        }

        backArrow.setOnClickListener {

            val scale = resources.displayMetrics.density
            val oldWidth = (340 * scale + 0.5f).toInt()
            val oldHeight = (200 * scale + 0.5f).toInt()
            val burcCardMarginBottom = (-190 * scale + 0.5f).toInt()

            val params = burcCard.layoutParams as RelativeLayout.LayoutParams

            // Genişlik ve yüksekliği animasyonlu olarak değiştirin
            val animatorWidth = ValueAnimator.ofInt(params.width, oldWidth)
            animatorWidth.addUpdateListener { animation ->
                val value = animation.animatedValue as Int
                params.width = value
                params.bottomMargin = burcCardMarginBottom
                burcCard.layoutParams = params
            }

            val animatorHeight = ValueAnimator.ofInt(params.height, oldHeight)
            animatorHeight.addUpdateListener { animation ->
                val value = animation.animatedValue as Int
                params.height = value
                burcCard.layoutParams = params
            }

            val animatorSet = AnimatorSet()
            animatorSet.playTogether(animatorWidth, animatorHeight)
            animatorSet.duration = 500 // Animasyon süresi (ms)

            // Animasyon tamamlandığında çalışacak bir Listener ekleyin
            animatorSet.addListener(object : AnimatorListenerAdapter() {

                override fun onAnimationStart(animation: Animator) {

                    setViewGone(generalSign, loveSign, careerSign, dailyButton, monthlyButton, yearlyButton, learnyourburcButton)
                    setViewVisible(empty, settingsbuttoncard)

                }

                override fun onAnimationEnd(animation: Animator) {
                    // Animasyon tamamlandığında görünür öğeleri etkinleştirin
                    chatwithmiraButton.visibility = View.VISIBLE
                    tarotFali.visibility = View.VISIBLE
                    fortuneCookie.visibility = View.VISIBLE
                    backarrowcard.visibility = View.GONE
                }
            })

            animatorSet.start()

            val generalSignCardparams = generalSign.layoutParams as RelativeLayout.LayoutParams
            val loveSignCardparams = loveSign.layoutParams as RelativeLayout.LayoutParams
            val careerSignCardparams = careerSign.layoutParams as RelativeLayout.LayoutParams

            val dpMarginBottom = 25
            val newMarginBottom = (dpMarginBottom * scale + 0.5f).toInt()

            generalSignCardparams.bottomMargin = newMarginBottom
            loveSignCardparams.bottomMargin = newMarginBottom
            careerSignCardparams.bottomMargin = newMarginBottom

            generalSign.layoutParams = generalSignCardparams
            loveSign.layoutParams = loveSignCardparams
            careerSign.layoutParams = careerSignCardparams

            generalSign.setCardBackgroundColor(getColor(R.color.passivesigncard))
            loveSign.setCardBackgroundColor(getColor(R.color.passivesigncard))
            careerSign.setCardBackgroundColor(getColor(R.color.passivesigncard))
        }

        generalSign.setOnClickListener {

            setViewVisible(dailyButton, monthlyButton, yearlyButton)
            setViewGone(learnyourburcButton)


            val scale = resources.displayMetrics.density

            val newHeight = (630 * scale + 0.5f).toInt()
            val newMarginBottom = (105 * scale + 0.5f).toInt()
            val burcCardMarginBottom = (-270 * scale + 0.5f).toInt()
            val dailyButtonMarginBottom = (25 * scale + 0.5f).toInt()

            val burcCardparams = burcCard.layoutParams as RelativeLayout.LayoutParams

            val generalSignCardparams = generalSign.layoutParams as RelativeLayout.LayoutParams
            val loveSignCardparams = loveSign.layoutParams as RelativeLayout.LayoutParams
            val careerSignCardparams = careerSign.layoutParams as RelativeLayout.LayoutParams
            val dailyButtonParams = dailyButton.layoutParams as RelativeLayout.LayoutParams
            val monthlyButtonParams = monthlyButton.layoutParams as RelativeLayout.LayoutParams
            val yearlyButtonParams = yearlyButton.layoutParams as RelativeLayout.LayoutParams

            burcCardparams.bottomMargin = burcCardMarginBottom

            dailyButtonParams.bottomMargin = dailyButtonMarginBottom
            monthlyButtonParams.bottomMargin = dailyButtonMarginBottom
            yearlyButtonParams.bottomMargin = dailyButtonMarginBottom
            burcCardparams.height = newHeight

            burcCard.layoutParams = burcCardparams

            generalSignCardparams.bottomMargin = newMarginBottom
            loveSignCardparams.bottomMargin = newMarginBottom
            careerSignCardparams.bottomMargin = newMarginBottom

            generalSign.layoutParams = generalSignCardparams
            loveSign.layoutParams = loveSignCardparams
            careerSign.layoutParams = careerSignCardparams
            dailyButton.layoutParams = dailyButtonParams
            monthlyButton.layoutParams = monthlyButtonParams
            yearlyButton.layoutParams = yearlyButtonParams

            generalSign.setCardBackgroundColor(getColor(R.color.nameinputbackground))
            loveSign.setCardBackgroundColor(getColor(R.color.passivesigncard))
            careerSign.setCardBackgroundColor(getColor(R.color.passivesigncard))

            dailyButton.setBackgroundResource(R.drawable.button_passive)
            monthlyButton.setBackgroundResource(R.drawable.button_passive)
            yearlyButton.setBackgroundResource(R.drawable.button_passive)
        }

        loveSign.setOnClickListener {

            setViewVisible(dailyButton, monthlyButton, yearlyButton)
            setViewGone(learnyourburcButton)

            val scale = resources.displayMetrics.density

            val newHeight = (630 * scale + 0.5f).toInt()

            val newMarginBottom = (105 * scale + 0.5f).toInt()
            val burcCardMarginBottom = (-270 * scale + 0.5f).toInt()
            val dailyButtonMarginBottom = (25 * scale + 0.5f).toInt()

            val burcCardparams = burcCard.layoutParams as RelativeLayout.LayoutParams

            val generalSignCardparams = generalSign.layoutParams as RelativeLayout.LayoutParams
            val loveSignCardparams = loveSign.layoutParams as RelativeLayout.LayoutParams
            val careerSignCardparams = careerSign.layoutParams as RelativeLayout.LayoutParams
            val dailyButtonParams = dailyButton.layoutParams as RelativeLayout.LayoutParams
            val monthlyButtonParams = monthlyButton.layoutParams as RelativeLayout.LayoutParams
            val yearlyButtonParams = yearlyButton.layoutParams as RelativeLayout.LayoutParams

            burcCardparams.height = newHeight

            dailyButtonParams.bottomMargin = dailyButtonMarginBottom
            monthlyButtonParams.bottomMargin = dailyButtonMarginBottom
            yearlyButtonParams.bottomMargin = dailyButtonMarginBottom


            burcCard.layoutParams = burcCardparams

            burcCardparams.bottomMargin = burcCardMarginBottom

            generalSignCardparams.bottomMargin = newMarginBottom
            loveSignCardparams.bottomMargin = newMarginBottom
            careerSignCardparams.bottomMargin = newMarginBottom

            generalSign.layoutParams = generalSignCardparams
            loveSign.layoutParams = loveSignCardparams
            careerSign.layoutParams = careerSignCardparams
            dailyButton.layoutParams = dailyButtonParams
            monthlyButton.layoutParams = monthlyButtonParams
            yearlyButton.layoutParams = yearlyButtonParams

            generalSign.setCardBackgroundColor(getColor(R.color.passivesigncard))
            loveSign.setCardBackgroundColor(getColor(R.color.nameinputbackground))
            careerSign.setCardBackgroundColor(getColor(R.color.passivesigncard))

            dailyButton.setBackgroundResource(R.drawable.button_passive)
            monthlyButton.setBackgroundResource(R.drawable.button_passive)
            yearlyButton.setBackgroundResource(R.drawable.button_passive)
        }

        careerSign.setOnClickListener {

            setViewVisible(dailyButton, monthlyButton, yearlyButton)
            setViewGone(learnyourburcButton)

            val scale = resources.displayMetrics.density

            val newHeight = (630 * scale + 0.5f).toInt()
            val newMarginBottom = (105 * scale + 0.5f).toInt()
            val burcCardMarginBottom = (-270 * scale + 0.5f).toInt()
            val dailyButtonMarginBottom = (25 * scale + 0.5f).toInt()

            val burcCardparams = burcCard.layoutParams as RelativeLayout.LayoutParams

            val generalSignCardparams = generalSign.layoutParams as RelativeLayout.LayoutParams
            val loveSignCardparams = loveSign.layoutParams as RelativeLayout.LayoutParams
            val careerSignCardparams = careerSign.layoutParams as RelativeLayout.LayoutParams
            val dailyButtonParams = dailyButton.layoutParams as RelativeLayout.LayoutParams
            val monthlyButtonParams = monthlyButton.layoutParams as RelativeLayout.LayoutParams
            val yearlyButtonParams = yearlyButton.layoutParams as RelativeLayout.LayoutParams

            burcCardparams.bottomMargin = burcCardMarginBottom

            burcCardparams.height = newHeight
            burcCard.layoutParams = burcCardparams

            generalSignCardparams.bottomMargin = newMarginBottom
            loveSignCardparams.bottomMargin = newMarginBottom
            careerSignCardparams.bottomMargin = newMarginBottom

            dailyButtonParams.bottomMargin = dailyButtonMarginBottom
            monthlyButtonParams.bottomMargin = dailyButtonMarginBottom
            yearlyButtonParams.bottomMargin = dailyButtonMarginBottom

            generalSign.layoutParams = generalSignCardparams
            loveSign.layoutParams = loveSignCardparams
            careerSign.layoutParams = careerSignCardparams
            dailyButton.layoutParams = dailyButtonParams
            monthlyButton.layoutParams = monthlyButtonParams
            yearlyButton.layoutParams = yearlyButtonParams

            generalSign.setCardBackgroundColor(getColor(R.color.passivesigncard))
            loveSign.setCardBackgroundColor(getColor(R.color.passivesigncard))
            careerSign.setCardBackgroundColor(getColor(R.color.nameinputbackground))

            dailyButton.setBackgroundResource(R.drawable.button_passive)
            monthlyButton.setBackgroundResource(R.drawable.button_passive)
            yearlyButton.setBackgroundResource(R.drawable.button_passive)
        }

        dailyButton.setOnClickListener {

            val scale = resources.displayMetrics.density

            val newHeight =               (730 * scale + 0.5f).toInt()
            val burcCardMarginBottom =    (-370 * scale + 0.5f).toInt()
            val dailyButtonMarginBottom = (125 * scale + 0.5f).toInt()
            val signsMarginBottom =       (205 * scale + 0.5f).toInt()

            val burcCardparams = burcCard.layoutParams as RelativeLayout.LayoutParams
            val dailyButtonParams = dailyButton.layoutParams as RelativeLayout.LayoutParams
            val monthlyButtonParams = monthlyButton.layoutParams as RelativeLayout.LayoutParams
            val yearlyButtonParams = yearlyButton.layoutParams as RelativeLayout.LayoutParams
            val generalSignParams = generalSign.layoutParams as RelativeLayout.LayoutParams
            val loveSignParams = loveSign.layoutParams as RelativeLayout.LayoutParams
            val careerSignParams = careerSign.layoutParams as RelativeLayout.LayoutParams

            burcCardparams.height = newHeight
            burcCardparams.bottomMargin = burcCardMarginBottom

            dailyButtonParams.bottomMargin = dailyButtonMarginBottom
            monthlyButtonParams.bottomMargin = dailyButtonMarginBottom
            yearlyButtonParams.bottomMargin = dailyButtonMarginBottom
            generalSignParams.bottomMargin = signsMarginBottom
            loveSignParams.bottomMargin = signsMarginBottom
            careerSignParams.bottomMargin = signsMarginBottom

            burcCard.layoutParams = burcCardparams
            dailyButton.layoutParams = dailyButtonParams
            generalSign.layoutParams = generalSignParams
            loveSign.layoutParams = loveSignParams
            careerSign.layoutParams = careerSignParams

            dailyButton.setBackgroundResource(R.drawable.common_next_button)
            monthlyButton.setBackgroundResource(R.drawable.button_passive)
            yearlyButton.setBackgroundResource(R.drawable.button_passive)

            setViewVisible(learnyourburcButton)

        }

        monthlyButton.setOnClickListener {

            val scale = resources.displayMetrics.density

            val newHeight =               (730 * scale + 0.5f).toInt()
            val burcCardMarginBottom =    (-370 * scale + 0.5f).toInt()
            val dailyButtonMarginBottom = (125 * scale + 0.5f).toInt()
            val signsMarginBottom =       (205 * scale + 0.5f).toInt()

            val burcCardparams = burcCard.layoutParams as RelativeLayout.LayoutParams
            val dailyButtonParams = dailyButton.layoutParams as RelativeLayout.LayoutParams
            val monthlyButtonParams = monthlyButton.layoutParams as RelativeLayout.LayoutParams
            val yearlyButtonParams = yearlyButton.layoutParams as RelativeLayout.LayoutParams
            val generalSignParams = generalSign.layoutParams as RelativeLayout.LayoutParams
            val loveSignParams = loveSign.layoutParams as RelativeLayout.LayoutParams
            val careerSignParams = careerSign.layoutParams as RelativeLayout.LayoutParams

            burcCardparams.height = newHeight
            burcCardparams.bottomMargin = burcCardMarginBottom

            dailyButtonParams.bottomMargin = dailyButtonMarginBottom
            monthlyButtonParams.bottomMargin = dailyButtonMarginBottom
            yearlyButtonParams.bottomMargin = dailyButtonMarginBottom
            generalSignParams.bottomMargin = signsMarginBottom
            loveSignParams.bottomMargin = signsMarginBottom
            careerSignParams.bottomMargin = signsMarginBottom

            burcCard.layoutParams = burcCardparams
            dailyButton.layoutParams = dailyButtonParams
            generalSign.layoutParams = generalSignParams
            loveSign.layoutParams = loveSignParams
            careerSign.layoutParams = careerSignParams

            dailyButton.setBackgroundResource(R.drawable.button_passive)
            monthlyButton.setBackgroundResource(R.drawable.common_next_button)
            yearlyButton.setBackgroundResource(R.drawable.button_passive)

            setViewVisible(learnyourburcButton)


        }

        yearlyButton.setOnClickListener {

            val scale = resources.displayMetrics.density

            val newHeight =               (730 * scale + 0.5f).toInt()
            val burcCardMarginBottom =    (-370 * scale + 0.5f).toInt()
            val dailyButtonMarginBottom = (125 * scale + 0.5f).toInt()
            val signsMarginBottom =       (205 * scale + 0.5f).toInt()

            val burcCardparams = burcCard.layoutParams as RelativeLayout.LayoutParams
            val dailyButtonParams = dailyButton.layoutParams as RelativeLayout.LayoutParams
            val monthlyButtonParams = monthlyButton.layoutParams as RelativeLayout.LayoutParams
            val yearlyButtonParams = yearlyButton.layoutParams as RelativeLayout.LayoutParams
            val generalSignParams = generalSign.layoutParams as RelativeLayout.LayoutParams
            val loveSignParams = loveSign.layoutParams as RelativeLayout.LayoutParams
            val careerSignParams = careerSign.layoutParams as RelativeLayout.LayoutParams

            burcCardparams.height = newHeight
            burcCardparams.bottomMargin = burcCardMarginBottom

            dailyButtonParams.bottomMargin = dailyButtonMarginBottom
            monthlyButtonParams.bottomMargin = dailyButtonMarginBottom
            yearlyButtonParams.bottomMargin = dailyButtonMarginBottom
            generalSignParams.bottomMargin = signsMarginBottom
            loveSignParams.bottomMargin = signsMarginBottom
            careerSignParams.bottomMargin = signsMarginBottom

            burcCard.layoutParams = burcCardparams
            dailyButton.layoutParams = dailyButtonParams
            generalSign.layoutParams = generalSignParams
            loveSign.layoutParams = loveSignParams
            careerSign.layoutParams = careerSignParams

            dailyButton.setBackgroundResource(R.drawable.button_passive)
            monthlyButton.setBackgroundResource(R.drawable.button_passive)
            yearlyButton.setBackgroundResource(R.drawable.common_next_button)

            //learnyourburcButton.visibility = View.VISIBLE
            setViewVisible(learnyourburcButton)

        }

        val profileFragment = ProfileFragment()

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

                if (savedInstanceState == null) {
                    supportFragmentManager.beginTransaction()
                        .setCustomAnimations(
                            R.anim.slide_down,
                            R.anim.slide_up,
                            R.anim.slide_down,
                            R.anim.slide_up
                        )
                        .replace(R.id.main_fragment_container, profileFragment)
                        .addToBackStack(null)
                        .commit()

                }

                setViewGone(chatwithmiraButton, empty, burcCard, tarotFali, fortuneCookie, settingsbuttoncard)
            }

            if (!isLoggedin) {
                if (savedInstanceState == null) {
                    supportFragmentManager.beginTransaction()
                        .setCustomAnimations(
                            R.anim.slide_down,
                            R.anim.slide_up,
                            R.anim.slide_down,
                            R.anim.slide_up
                        )
                        .replace(R.id.main_fragment_container, Loginfragment())
                        .addToBackStack(null)
                        .commit()

                }
                chatwithmiraButton.visibility = View.GONE
                empty.visibility = View.GONE
                burcCard.visibility = View.GONE
                tarotFali.visibility = View.GONE
                fortuneCookie.visibility = View.GONE
                settingsbuttoncard.visibility = View.GONE
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