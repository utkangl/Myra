package com.example.falci

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.cardview.widget.CardView
import com.example.falci.internalClasses.AuthenticationFunctions.CreateJsonObject.createJsonObject
import com.example.falci.internalClasses.AuthenticationFunctions.PostJsonFunctions.postJsonWithHeader
import com.example.falci.internalClasses.InternalFunctions.AnimateCardSize.animateCardSize
import com.example.falci.internalClasses.InternalFunctions.SetVisibilityFunctions.setViewGone
import com.example.falci.internalClasses.InternalFunctions.SetVisibilityFunctions.setViewVisible
import com.example.falci.internalClasses.TransitionToFragment.ReplaceFragmentWithAnimation.replaceFragmentWithAnimation
import com.example.falci.internalClasses.urls

class ProfileFragment : Fragment() {

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_profile, container, false)

        val firstLetterView = v.findViewById<TextView>(R.id.profileFragmentFirstLetter)
        val profileFragmentName = v.findViewById<TextView>(R.id.profileFragmentName)
        val editprofilebutton = v.findViewById<AppCompatButton>(R.id.profilefragmenteditprofilebutton)

        val firstLetter = userProfile.firstName.first().toString().uppercase()
        firstLetterView.text = firstLetter
        profileFragmentName.text = userProfile.firstName


        val jupyterlayout = v.findViewById<RelativeLayout>(R.id.scrollviewjupyterlayout)
        val marslayout = v.findViewById<RelativeLayout>(R.id.marslayout)
        val mercurylayout = v.findViewById<RelativeLayout>(R.id.mercurylayout)
        val neptunlayout = v.findViewById<RelativeLayout>(R.id.neptunlayout)
        val saturnlayout = v.findViewById<RelativeLayout>(R.id.saturnlayout)
        val sunlayout = v.findViewById<RelativeLayout>(R.id.sunlayout)
        val uranuslayout = v.findViewById<RelativeLayout>(R.id.uranuslayout)
        val venuslayout = v.findViewById<RelativeLayout>(R.id.venuslayout)
        val moonlayout = v.findViewById<RelativeLayout>(R.id.moonlayout)
        val zodiacCard = v.findViewById<CardView>(R.id.zodiaccard)
        val profileCard = v.findViewById<CardView>(R.id.profilecard)
        val profileViewBottomCard = v.findViewById<CardView>(R.id.profileviewbottomcard)
        val profileTitle = v.findViewById<TextView>(R.id.profileTitle)
        val changeAccountButton = v.findViewById<AppCompatButton>(R.id.changeaccountbutton)
        val backArrowCard = v.findViewById<CardView>(R.id.backarrowcard)
        val backArrow = v.findViewById<ImageView>(R.id.back_arrow)
        val burcexplanationplanet = v.findViewById<CardView>(R.id.burcexplanationplanet)
        val zodiacexplanationcard = v.findViewById<RelativeLayout>(R.id.zodiacexplanationcard)
        val burcexplanationplanetimage = burcexplanationplanet.findViewById<ImageView>(R.id.burcexplanationplanetimage)
        val burcexplanationplanettext = zodiacexplanationcard.findViewById<TextView>(R.id.burcexplanationplanettext)


        zodiacCard.setOnClickListener {
            setViewGone(profileCard, profileViewBottomCard, profileTitle, changeAccountButton)
            setViewVisible(backArrowCard)
            animateCardSize(requireContext(), 370, 550, zodiacCard)
        }

        backArrow.setOnClickListener {
            setViewVisible(profileCard, profileViewBottomCard, profileTitle, changeAccountButton)
            setViewGone(backArrowCard)
            animateCardSize(requireContext(), 370, 330, zodiacCard)
        }

        editprofilebutton.setOnClickListener {
            // change to editProfile screen if user is logged in
            if (isLoggedin){ replaceFragmentWithAnimation(parentFragmentManager, EditProfileFragment()) }

            // change to Login screen if user is not logged in
            if (!isLoggedin){ replaceFragmentWithAnimation(parentFragmentManager, Loginfragment()) }
        }

        val planetLayouts = listOf(
            jupyterlayout, marslayout, mercurylayout, neptunlayout,
            saturnlayout, sunlayout, uranuslayout, venuslayout, moonlayout
        )

        val planetImages = listOf(
            R.drawable.jupiter, R.drawable.mars, R.drawable.mercury,
            R.drawable.neptun, R.drawable.saturn, R.drawable.sun,
            R.drawable.uranus, R.drawable.venus, R.drawable.moon
        )

        val planetNames = listOf(
            "Jupiter", "Mars", "Merkur", "Neptun",
            "Saturn", "Sun", "Uranus", "Venus", "Moon")

        planetLayouts.forEachIndexed { index, layout ->
            layout.setOnClickListener {
                burcexplanationplanetimage.setImageResource(planetImages[index])
                burcexplanationplanettext.text = planetNames[index]
            }
        }

        changeAccountButton.setOnClickListener{
            if (isLoggedin){
                val refreshTokenJSON = createJsonObject("refresh_token" to loginTokens.loginRefreshToken)
                postJsonWithHeader(urls.logoutURL,refreshTokenJSON, loginTokens.loginAccessToken)
                { _, exception ->
                    if (exception == null) {
                            isLoggedin = false
                            replaceFragmentWithAnimation(parentFragmentManager, Loginfragment())
                    }
                    else println(exception)
                }

            } else{ println("Hele once bi giris yap sonra cikis yaparsin") }
        }

        return v

    }

}