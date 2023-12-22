package com.utkangul.falci

import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.cardview.widget.CardView
import com.utkangul.falci.internalClasses.AuthenticationFunctions.CreateJsonObject.createJsonObject
import com.utkangul.falci.internalClasses.AuthenticationFunctions.PostJsonFunctions.postJsonWithHeader
import com.utkangul.falci.internalClasses.InternalFunctions.AnimateCardSize.animateCardSize
import com.utkangul.falci.internalClasses.InternalFunctions.SetVisibilityFunctions.setViewGone
import com.utkangul.falci.internalClasses.InternalFunctions.SetVisibilityFunctions.setViewVisible
import com.utkangul.falci.internalClasses.ProfileFunctions.ProfileFunctions.getPlanetExplanationJsonValue
import com.utkangul.falci.internalClasses.ProfileFunctions.ProfileFunctions.getPlanetZodiacExplanationJsonValue
import com.utkangul.falci.internalClasses.ProfileFunctions.ProfileFunctions.makeGetProfileRequest
import com.utkangul.falci.internalClasses.TransitionToFragment.ReplaceActivityToFragment.replaceProfileActivityToFragment
import com.utkangul.falci.internalClasses.TransitionToFragment.ReplaceFragmentWithAnimation.replaceProfileFragmentWithAnimation
import com.utkangul.falci.internalClasses.dataClasses.*
import com.utkangul.falci.internalClasses.statusCode
import org.json.JSONObject

class ProfileFragment : Fragment() {
    private lateinit var sharedPreferences: SharedPreferences

    @SuppressLint("SetTextI18n", "ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_profile, container, false)

        val firstLetterView = v.findViewById<TextView>(R.id.profileFragmentFirstLetter)
        val profileFragmentName = v.findViewById<TextView>(R.id.profileFragmentName)
        val editprofilebutton = v.findViewById<AppCompatButton>(R.id.profilefragmenteditprofilebutton)

        println("profile içinden yazıyorum $userProfile")
        // set firstLetter variable with the first letter of UserProfileDataClass's name field
        var firstLetter: String = "?"
        if (!userProfile.first_name.isNullOrEmpty()){
            firstLetter = userProfile.first_name!!.first().toString().uppercase()
        }
        firstLetterView.text = firstLetter
        profileFragmentName.text = userProfile.first_name


        val jupyterlayout = v.findViewById<RelativeLayout>(R.id.scrollViewJupyterLayout)
        val marslayout = v.findViewById<RelativeLayout>(R.id.marslayout)
        val mercurylayout = v.findViewById<RelativeLayout>(R.id.mercurylayout)
        val neptuneLayout = v.findViewById<RelativeLayout>(R.id.neptuneLayout)
        val saturnlayout = v.findViewById<RelativeLayout>(R.id.saturnlayout)
        val sunlayout = v.findViewById<RelativeLayout>(R.id.sunlayout)
        val uranuslayout = v.findViewById<RelativeLayout>(R.id.uranuslayout)
        val venuslayout = v.findViewById<RelativeLayout>(R.id.venuslayout)
        val moonlayout = v.findViewById<RelativeLayout>(R.id.moonlayout)
        val zodiacCard = v.findViewById<CardView>(R.id.zodiaccard)
        val profileCard = v.findViewById<CardView>(R.id.profilecard)
        val profileViewBottomCard = v.findViewById<CardView>(R.id.profileViewBottomCard)
        val profileTitle = v.findViewById<TextView>(R.id.profileTitle)
        val profileCardZodiacName = v.findViewById<TextView>(R.id.profileCardZodiacName)
        val logoutButton = v.findViewById<AppCompatButton>(R.id.logoutButton)
        val backArrowCard = v.findViewById<CardView>(R.id.backArrowCard)
        val backArrow = v.findViewById<ImageView>(R.id.back_arrow)
        val burcexplanationplanet = v.findViewById<CardView>(R.id.burcexplanationplanet)
        val planetHoroscopeImageCard = v.findViewById<CardView>(R.id.planet_horoscope_image_card)
        val zodiacexplanationcard = v.findViewById<RelativeLayout>(R.id.zodiacexplanationcard)
        val showFavHoroscopesLayout = v.findViewById<RelativeLayout>(R.id.showFavHoroscopesLayout)
        val navigateToSettingsContainerLayout = v.findViewById<RelativeLayout>(R.id.navigateToSettingsContainerLayout)
        val burcexplanationplanetimage = burcexplanationplanet.findViewById<ImageView>(R.id.burcexplanationplanetimage)
        val planetHoroscopeImage = v.findViewById<ImageView>(R.id.planet_horoscope_image)
        val burcexplanationplanettext = zodiacexplanationcard.findViewById<TextView>(R.id.burcexplanationplanettext)
        val planetHoroscopeText = zodiacexplanationcard.findViewById<TextView>(R.id.planet_horoscope_text)
        val burcExplanationText = zodiacexplanationcard.findViewById<TextView>(R.id.burcExplanationText)
        val burcExplanationTextScroll = zodiacexplanationcard.findViewById<ScrollView>(R.id.burcExplanationTextScroll)
        val navigateToPurchaseContainerLayout = v.findViewById<RelativeLayout>(R.id.navigateToPurchaseContainerLayout)

        getHoroscopeData.apply {
            id = null
            thread = null
            summary = null
            good = null
            bad =  null
            time_remaining = null
            score = null
            is_favourite = false
            favourite_id = null
        }

        // if user click to zodiac card, make the card larger and set other components invisible
        zodiacCard.setOnClickListener {
            setViewGone(profileCard, profileViewBottomCard, profileTitle, logoutButton)
            setViewVisible(backArrowCard)
            animateCardSize(requireContext(), 370, 550, zodiacCard,burcExplanationTextScroll, 90 )
        }

        // if user click to back arrow, make the zodiac card smaller and set components visible
        backArrow.setOnClickListener {
            setViewVisible(profileCard, profileViewBottomCard, profileTitle, logoutButton)
            setViewGone(backArrowCard)
            animateCardSize(requireContext(), 370, 330, zodiacCard,burcExplanationTextScroll, 40)
        }

        editprofilebutton.setOnClickListener {
            // change to editProfile screen if user is logged in
            if (authenticated.isLoggedIn){
                if (controlVariables.getProfileAgain){ makeGetProfileRequest(urls.getProfileURL, requireContext(),null,null) { _, _ -> } }
                replaceProfileFragmentWithAnimation(parentFragmentManager, EditProfileFragment())
            }

            // change to Login screen if user is not logged in
            if (!authenticated.isLoggedIn){
                val intent = Intent(requireContext(), LoginSignupActivity::class.java); startActivity(intent)
            }
        }

        burcExplanationTextScroll.setOnTouchListener { _,_ ->
            zodiacCard.performClick()
            false
        }


        activity!!.runOnUiThread {
            val planetLayouts = listOf(
                jupyterlayout, marslayout, mercurylayout, neptuneLayout,
                saturnlayout, sunlayout, uranuslayout, venuslayout, moonlayout
            )

            val planetImages = listOf(
                R.drawable.jupiter, R.drawable.mars, R.drawable.mercury,
                R.drawable.neptune, R.drawable.saturn, R.drawable.sun,
                R.drawable.uranus, R.drawable.venus, R.drawable.moon
            )

            val planetNames = listOf(
                "Jupiter", "Mars", "Mercury", "Neptune",
                "Saturn", "Sun", "Uranus", "Venus", "Moon")

            val planetSignMap = mutableMapOf<String, String>()

            for (sign in userProfile.signs) {
                planetSignMap[sign.planet] = sign.sign
            }

            val planetSignToImageMap = mutableMapOf<String, Int>()
            planetSignToImageMap["Cap"] = R.drawable.capricorn
            planetSignToImageMap["Aqu"] = R.drawable.aquarius
            planetSignToImageMap["Pis"] = R.drawable.pisces
            planetSignToImageMap["Ari"] = R.drawable.aries
            planetSignToImageMap["Tau"] = R.drawable.taurus
            planetSignToImageMap["Gem"] = R.drawable.gemini
            planetSignToImageMap["Can"] = R.drawable.cancer
            planetSignToImageMap["Leo"] = R.drawable.leo
            planetSignToImageMap["Vir"] = R.drawable.virgo
            planetSignToImageMap["Lib"] = R.drawable.libra
            planetSignToImageMap["Sco"] = R.drawable.scorpio
            planetSignToImageMap["Sag"] = R.drawable.sagittarius

            burcexplanationplanetimage.setImageResource(planetImages[0])
            burcexplanationplanettext.text = planetNames[0]
            var planetName = planetNames[0]
            var sign = planetSignMap[planetName]
            planetHoroscopeText.text = sign
            planetHoroscopeImage.setImageResource(planetSignToImageMap[sign]!!)
            profileCardZodiacName.text = planetSignMap["Sun"]

            val fadeOut = AlphaAnimation(1f, 0f)
            fadeOut.duration = 300
            val fadeIn = AlphaAnimation(0f, 1f)
            fadeIn.duration = 300


            val planetZodiacJsonFileName = "planet_sign_exp"
            val planetJsonFileName = "planet_exp"


            fun updateUIWithResult(result: String) {
                requireActivity().runOnUiThread {
                    burcExplanationText.text = result
                }
            }

            burcExplanationText.text = " $planetName ; ${getPlanetExplanationJsonValue(requireContext(), planetJsonFileName, planetName)}  $planetName ve $sign birlikteliği ${getPlanetZodiacExplanationJsonValue(requireContext(), planetZodiacJsonFileName, planetName, sign!!)}"

            val planetExplanation = getPlanetExplanationJsonValue(requireContext(), planetJsonFileName, planetName)
            val zodiacExplanation = getPlanetZodiacExplanationJsonValue(requireContext(), planetZodiacJsonFileName, planetName, sign)
            val result = " $planetName ; $planetExplanation $planetName ve $sign birlikteliği $zodiacExplanation"
            updateUIWithResult(result)



            var currentIndex = 0

            planetLayouts.forEachIndexed { index, layout ->
                layout.setOnClickListener {
                    if (index != currentIndex){
                        currentIndex = index
                        burcexplanationplanetimage.startAnimation(fadeOut)
                        burcexplanationplanettext.startAnimation(fadeOut)
                        planetHoroscopeImageCard.startAnimation(fadeOut)
                        planetHoroscopeText.startAnimation(fadeOut)

                        val upAnimation = AnimationUtils.loadAnimation(context, R.anim.zodiacexp_slide_up)
                        val downAnimation = AnimationUtils.loadAnimation(context, R.anim.activity_slide_down)
                        burcExplanationText.startAnimation(upAnimation)

                        fadeOut.setAnimationListener(object : Animation.AnimationListener {
                            override fun onAnimationStart(animation: Animation?) {
                            }

                            override fun onAnimationEnd(animation: Animation?) {
                                burcexplanationplanetimage.setImageResource(planetImages[index])
                                burcexplanationplanettext.text = planetNames[index]

                                planetName = planetNames[index]
                                sign = planetSignMap[planetName]
                                planetHoroscopeText.text = sign
                                burcExplanationText.text = " $planetName ; ${getPlanetExplanationJsonValue(requireContext(), planetJsonFileName, planetName)}  $planetName ve $sign birlikteliği ${getPlanetZodiacExplanationJsonValue(requireContext(), planetZodiacJsonFileName, planetName, sign!!)}"

                                planetHoroscopeImage.setImageResource(planetSignToImageMap[sign]!!)

                                burcexplanationplanetimage.startAnimation(fadeIn)
                                burcexplanationplanettext.startAnimation(fadeIn)
                                planetHoroscopeImageCard.startAnimation(fadeIn)
                                planetHoroscopeText.startAnimation(fadeIn)
                                burcExplanationText.startAnimation(downAnimation)
                            }

                            override fun onAnimationRepeat(animation: Animation?) {
                                // do nothing
                            }
                        })
                    }
                }
            }

        }

          // check if user is logged in, if so create json, post it,
         // handle the response with response codes. If 205 navigate user to login and toast success
        //  else if 400, toast the error, if user is not logged in, toast error
        logoutButton.setOnClickListener{
            if (authenticated.isLoggedIn){
                val refreshTokenJSON = createJsonObject("refresh_token" to tokensDataClass.refreshToken)
                postJsonWithHeader(urls.logoutURL,refreshTokenJSON, requireContext())
                { responseBody, _ ->
                    if (statusCode == 205){
                        requireActivity().runOnUiThread { Toast.makeText(requireContext(), "Logout Successful", Toast.LENGTH_LONG).show()}
                        val options = ActivityOptions.makeCustomAnimation(requireContext(), R.anim.activity_slide_down, 0)
                        val intent = Intent(requireContext(), MainActivity::class.java);startActivity(intent,options.toBundle())
                        authenticated.isLoggedIn = false

                        sharedPreferences = requireContext().getSharedPreferences("token_prefs", Context.MODE_PRIVATE)
                        val editor = sharedPreferences.edit()
                        editor.putString("access_token", "")
                        editor.putString("refresh_token", "")
                        editor.putLong("token_creation_time", 0)
                        editor.putBoolean("didLogin", false)
                        editor.apply()
                    } else if (statusCode == 400){
                        val responseJson = responseBody?.let { it1 -> JSONObject(it1) }
                        val detail = responseJson?.optString("detail")
                        requireActivity().runOnUiThread { Toast.makeText(requireContext(), detail, Toast.LENGTH_LONG).show()}
                    }
                }
            } else{requireActivity().runOnUiThread { Toast.makeText(requireContext(), "can not logout without logging in", Toast.LENGTH_LONG).show()} }
        }
        showFavHoroscopesLayout.setOnClickListener{ replaceProfileActivityToFragment(parentFragmentManager, FavouriteHoroscopesFragment())}
        navigateToSettingsContainerLayout.setOnClickListener{replaceProfileActivityToFragment(parentFragmentManager, SettingsFragment())}
        navigateToPurchaseContainerLayout.setOnClickListener{replaceProfileFragmentWithAnimation(parentFragmentManager, PurchaseFragment())}
        return v
    }
}