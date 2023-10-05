package com.example.falci

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
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.cardview.widget.CardView
import com.example.falci.internalClasses.AuthenticationFunctions.CreateJsonObject.createJsonObject
import com.example.falci.internalClasses.AuthenticationFunctions.PostJsonFunctions.postJsonWithHeader
import com.example.falci.internalClasses.InternalFunctions.AnimateCardSize.animateCardSize
import com.example.falci.internalClasses.InternalFunctions.SetVisibilityFunctions.setViewGone
import com.example.falci.internalClasses.InternalFunctions.SetVisibilityFunctions.setViewVisible
import com.example.falci.internalClasses.TransitionToFragment
import com.example.falci.internalClasses.TransitionToFragment.ReplaceFragmentWithAnimation.replaceProfileFragmentWithAnimation
import com.example.falci.internalClasses.dataClasses.authenticated
import com.example.falci.internalClasses.dataClasses.tokensDataClass
import com.example.falci.internalClasses.dataClasses.urls
import com.example.falci.internalClasses.dataClasses.userProfile
import com.example.falci.internalClasses.statusCode
import org.json.JSONObject
import java.io.InputStream

@Suppress("DEPRECATION")
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

        println("profile içinden yazıyorum ${userProfile.first_name}")
        // set firstLetter variable with the first letter of UserProfileDataClass's name field
        val firstLetter = userProfile.first_name!!.first().toString().uppercase()
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
        val burcexplanationplanetimage = burcexplanationplanet.findViewById<ImageView>(R.id.burcexplanationplanetimage)
        val planetHoroscopeImage = v.findViewById<ImageView>(R.id.planet_horoscope_image)
        val burcexplanationplanettext = zodiacexplanationcard.findViewById<TextView>(R.id.burcexplanationplanettext)
        val planetHoroscopeText = zodiacexplanationcard.findViewById<TextView>(R.id.planet_horoscope_text)
        val burcExplanationText = zodiacexplanationcard.findViewById<TextView>(R.id.burcExplanationText)
        val burcExplanationTextScroll = zodiacexplanationcard.findViewById<ScrollView>(R.id.burcExplanationTextScroll)


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
            if (authenticated.isLoggedIn){ replaceProfileFragmentWithAnimation(parentFragmentManager, EditProfileFragment()) }

            // change to Login screen if user is not logged in
            if (!authenticated.isLoggedIn){
                val intent = Intent(requireContext(), LoginSignupActivity::class.java); startActivity(intent)
            }
        }

        burcExplanationTextScroll.setOnTouchListener { _,_ ->
            zodiacCard.performClick()
            false
        }

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

        val planetToColorMap = mutableMapOf<String, Int>()
        planetToColorMap["Jupiter"] = resources.getColor(R.color.jupiter)
        planetToColorMap["Mars"] = resources.getColor(R.color.mars)
        planetToColorMap["Mercury"] = resources.getColor(R.color.mercury)
        planetToColorMap["Neptune"] = resources.getColor(R.color.neptune)
        planetToColorMap["Saturn"] = resources.getColor(R.color.saturn)
        planetToColorMap["Sun"] = resources.getColor(R.color.sun)
        planetToColorMap["Uranus"] = resources.getColor(R.color.uranus)
        planetToColorMap["Venus"] = resources.getColor(R.color.venus)
        planetToColorMap["Moon"] = resources.getColor(R.color.moon)


        burcexplanationplanetimage.setImageResource(planetImages[0])
        burcexplanationplanettext.text = planetNames[0]
        var planetName = planetNames[0]
        var sign = planetSignMap[planetName]
        planetHoroscopeText.text = sign
        planetHoroscopeImage.setImageResource(planetSignToImageMap[sign]!!)
        planetHoroscopeImageCard.setCardBackgroundColor(planetToColorMap[planetName]!!)
        profileCardZodiacName.text = planetSignMap["Sun"]

        val fadeOut = AlphaAnimation(1f, 0f)
        fadeOut.duration = 150
        val fadeIn = AlphaAnimation(0f, 1f)
        fadeIn.duration = 150


        planetLayouts.forEachIndexed { index, layout ->
            layout.setOnClickListener {
                burcexplanationplanetimage.startAnimation(fadeOut)
                burcexplanationplanettext.startAnimation(fadeOut)
                planetHoroscopeImageCard.startAnimation(fadeOut)
                planetHoroscopeText.startAnimation(fadeOut)

                fadeOut.setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationStart(animation: Animation?) {
                    }

                    override fun onAnimationEnd(animation: Animation?) {
                        burcexplanationplanetimage.setImageResource(planetImages[index])
                        burcexplanationplanettext.text = planetNames[index]

                        planetName = planetNames[index]
                        sign = planetSignMap[planetName]
                        planetHoroscopeText.text = sign

                        planetHoroscopeImage.setImageResource(planetSignToImageMap[sign]!!)
                        planetHoroscopeImageCard.setCardBackgroundColor(planetToColorMap[planetName]!!)

                        burcexplanationplanetimage.startAnimation(fadeIn)
                        burcexplanationplanettext.startAnimation(fadeIn)
                        planetHoroscopeImageCard.startAnimation(fadeIn)
                        planetHoroscopeText.startAnimation(fadeIn)
                    }

                    override fun onAnimationRepeat(animation: Animation?) {
                        // do nothing
                    }
                })
            }
        }

          // check if user is logged in, if so create json, post it,
         // handle the response with response codes. If 205 navigate user to login and toast success
        //  else if 400, toast the error, if user is not logged in, toast error
        logoutButton.setOnClickListener{
            if (authenticated.isLoggedIn){
                val refreshTokenJSON = createJsonObject("refresh_token" to tokensDataClass.refreshToken)
                postJsonWithHeader(urls.logoutURL,refreshTokenJSON, tokensDataClass.accessToken,requireContext())
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










        showFavHoroscopesLayout.setOnClickListener{
            TransitionToFragment.ReplaceActivityToFragment.replaceProfileActivityToFragment(parentFragmentManager, FavouriteHoroscopesFragment())
        }



        return v
    }
}