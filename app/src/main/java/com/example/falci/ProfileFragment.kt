package com.example.falci

import android.animation.AnimatorSet
import android.animation.ValueAnimator
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
import com.example.falci.LoginSignupActivity.logoutFunctions


var firstLetter = "A"

var profileUsername = ""
var profileFirst_name = ""
var profileBirth_place = ""
var profileBirth_day = ""
var profileBirth_time = ""
var profileRelationshipStatus = ""
var profileGender = ""
var profileOccupation = ""


class ProfileFragment : Fragment() {

    private lateinit var jupyterlayout: RelativeLayout
    private lateinit var marslayout: RelativeLayout
    private lateinit var mercurylayout: RelativeLayout
    private lateinit var neptunlayout: RelativeLayout
    private lateinit var saturnlayout: RelativeLayout
    private lateinit var sunlayout: RelativeLayout
    private lateinit var uranuslayout: RelativeLayout
    private lateinit var venuslayout: RelativeLayout
    private lateinit var moonlayout : RelativeLayout
    private lateinit var zodiacCard: CardView
    private lateinit var profileCard: CardView
    private lateinit var profileViewBottomCard: CardView
    private lateinit var profileTitle: TextView
    private lateinit var changeAccountButton: AppCompatButton
    private lateinit var backArrowCard: CardView
    private lateinit var backArrow: ImageView


    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_profile, container, false)


        firstLetter = profileFirst_name.first().toString().uppercase()

        val firstletter = v.findViewById<TextView>(R.id.profileFragmentFirstLetter)
        val profilefragmentName = v.findViewById<TextView>(R.id.profileFragmentName)

        firstletter.text = firstLetter
        profilefragmentName.text = profileFirst_name

        val editprofilebutton =
            v.findViewById<AppCompatButton>(R.id.profilefragmenteditprofilebutton)

        jupyterlayout = v.findViewById(R.id.scrollviewjupyterlayout)
        marslayout = v.findViewById(R.id.marslayout)
        mercurylayout = v.findViewById(R.id.mercurylayout)
        neptunlayout = v.findViewById(R.id.neptunlayout)
        saturnlayout = v.findViewById(R.id.saturnlayout)
        sunlayout = v.findViewById(R.id.sunlayout)
        uranuslayout = v.findViewById(R.id.uranuslayout)
        venuslayout = v.findViewById(R.id.venuslayout)
        moonlayout = v.findViewById(R.id.moonlayout)
        zodiacCard = v.findViewById(R.id.zodiaccard)
        profileCard = v.findViewById(R.id.profilecard)
        profileViewBottomCard = v.findViewById(R.id.profileviewbottomcard)
        profileTitle = v.findViewById(R.id.profileTitle)
        changeAccountButton = v.findViewById(R.id.changeaccountbutton)
        backArrowCard = v.findViewById(R.id.backarrowcard)
        backArrow = v.findViewById(R.id.back_arrow)


        val burcexplanationplanet = v.findViewById<CardView>(R.id.burcexplanationplanet)
        val zodiacexplanationcard = v.findViewById<RelativeLayout>(R.id.zodiacexplanationcard)



        zodiacCard.setOnClickListener {

            profileCard.visibility = View.GONE
            profileViewBottomCard.visibility = View.GONE
            profileTitle.visibility = View.GONE
            changeAccountButton.visibility = View.GONE

            backArrowCard.visibility = View.VISIBLE

            val scale = resources.displayMetrics.density
            val newWidth = (370 * scale + 0.5f).toInt()
            val newHeight = (550 * scale + 0.5f).toInt()
            val params = zodiacCard.layoutParams as RelativeLayout.LayoutParams


            val animatorWidth = ValueAnimator.ofInt(zodiacCard.width, newWidth)
            animatorWidth.addUpdateListener { animation ->
                val value = animation.animatedValue as Int
                params.width = value
                zodiacCard.layoutParams = params
            }

            val animatorHeight = ValueAnimator.ofInt(zodiacCard.height, newHeight)
            animatorHeight.addUpdateListener { animation ->
                val value = animation.animatedValue as Int
                params.height = value
                zodiacCard.layoutParams = params
            }


            val animatorSet = AnimatorSet()
            animatorSet.playTogether(animatorWidth, animatorHeight)
            animatorSet.duration = 300
            animatorSet.start()
        }

        backArrow.setOnClickListener {

            profileCard.visibility = View.VISIBLE
            profileViewBottomCard.visibility = View.VISIBLE
            profileTitle.visibility = View.VISIBLE
            changeAccountButton.visibility = View.VISIBLE
            backArrowCard.visibility = View.GONE

            val scale = resources.displayMetrics.density
            val newWidth = (370 * scale + 0.5f).toInt()
            val newHeight = (330 * scale + 0.5f).toInt()
            val params = zodiacCard.layoutParams as RelativeLayout.LayoutParams

            val animatorWidth = ValueAnimator.ofInt(zodiacCard.width, newWidth)
            animatorWidth.addUpdateListener { animation ->
                val value = animation.animatedValue as Int
                params.width = value
                zodiacCard.layoutParams = params
            }

            val animatorHeight = ValueAnimator.ofInt(zodiacCard.height, newHeight)
            animatorHeight.addUpdateListener { animation ->
                val value = animation.animatedValue as Int
                params.height = value
                zodiacCard.layoutParams = params
            }


            val animatorSet = AnimatorSet()
            animatorSet.playTogether(animatorWidth, animatorHeight)
            animatorSet.duration = 300

            animatorSet.start()

        }

        editprofilebutton.setOnClickListener {

            if (isLoggedin){
                println("name $profileFirst_name")
                println("locate $profileBirth_place")

                parentFragmentManager.beginTransaction().apply {
                    setCustomAnimations(
                        R.anim.slide_down,
                        R.anim.slide_up,
                        R.anim.slide_down,
                        R.anim.slide_up
                    )
                    replace(R.id.main_fragment_container, EditProfileFragment())
                    addToBackStack(null)
                    commit()
                }
            }

        }

        jupyterlayout.setOnClickListener {
            val burcexplanationplanetimage =
                burcexplanationplanet.findViewById<ImageView>(R.id.burcexplanationplanetimage)
            val burcexplanationplanettext =
                zodiacexplanationcard.findViewById<TextView>(R.id.burcexplanationplanettext)
            burcexplanationplanetimage.setImageResource(R.drawable.jupiter)
            burcexplanationplanettext.text = "Jupiter"
        }

        marslayout.setOnClickListener {
            val burcexplanationplanetimage =
                burcexplanationplanet.findViewById<ImageView>(R.id.burcexplanationplanetimage)
            val burcexplanationplanettext =
                zodiacexplanationcard.findViewById<TextView>(R.id.burcexplanationplanettext)
            burcexplanationplanetimage.setImageResource(R.drawable.mars)
            burcexplanationplanettext.text = "Mars"
        }

        mercurylayout.setOnClickListener {
            val burcexplanationplanetimage =
                burcexplanationplanet.findViewById<ImageView>(R.id.burcexplanationplanetimage)
            val burcexplanationplanettext =
                zodiacexplanationcard.findViewById<TextView>(R.id.burcexplanationplanettext)
            burcexplanationplanetimage.setImageResource(R.drawable.mercury)
            burcexplanationplanettext.text = "Merkur"
        }

        neptunlayout.setOnClickListener {
            val burcexplanationplanetimage =
                burcexplanationplanet.findViewById<ImageView>(R.id.burcexplanationplanetimage)
            val burcexplanationplanettext =
                zodiacexplanationcard.findViewById<TextView>(R.id.burcexplanationplanettext)
            burcexplanationplanetimage.setImageResource(R.drawable.neptun)
            burcexplanationplanettext.text = "Neptun"
        }

        saturnlayout.setOnClickListener {
            val burcexplanationplanetimage =
                burcexplanationplanet.findViewById<ImageView>(R.id.burcexplanationplanetimage)
            val burcexplanationplanettext =
                zodiacexplanationcard.findViewById<TextView>(R.id.burcexplanationplanettext)
            burcexplanationplanetimage.setImageResource(R.drawable.saturn)
            burcexplanationplanettext.text = "Saturn"
        }

        sunlayout.setOnClickListener {
            val burcexplanationplanetimage =
                burcexplanationplanet.findViewById<ImageView>(R.id.burcexplanationplanetimage)
            val burcexplanationplanettext =
                zodiacexplanationcard.findViewById<TextView>(R.id.burcexplanationplanettext)
            burcexplanationplanetimage.setImageResource(R.drawable.sun)
            burcexplanationplanettext.text = "Sun"
        }

        uranuslayout.setOnClickListener {
            val burcexplanationplanetimage =
                burcexplanationplanet.findViewById<ImageView>(R.id.burcexplanationplanetimage)
            val burcexplanationplanettext =
                zodiacexplanationcard.findViewById<TextView>(R.id.burcexplanationplanettext)
            burcexplanationplanetimage.setImageResource(R.drawable.uranus)
            burcexplanationplanettext.text = "Uranus"
        }

        venuslayout.setOnClickListener {
            val burcexplanationplanetimage =
                burcexplanationplanet.findViewById<ImageView>(R.id.burcexplanationplanetimage)
            val burcexplanationplanettext =
                zodiacexplanationcard.findViewById<TextView>(R.id.burcexplanationplanettext)
            burcexplanationplanetimage.setImageResource(R.drawable.venus)
            burcexplanationplanettext.text = "Venus"
        }
        moonlayout.setOnClickListener {
            val burcexplanationplanetimage =
                burcexplanationplanet.findViewById<ImageView>(R.id.burcexplanationplanetimage)
            val burcexplanationplanettext =
                zodiacexplanationcard.findViewById<TextView>(R.id.burcexplanationplanettext)
            burcexplanationplanetimage.setImageResource(R.drawable.moon)
            burcexplanationplanettext.text = "Moon"
        }

        changeAccountButton.setOnClickListener{

            if (isLoggedin){

                val refreshTokenJSON = logoutFunctions.createLogoutJSON(LoginSignupActivity.Loginfunctions.RefreshToken)

                logoutFunctions.makeLogoutRequest("http://31.210.43.174:1337/auth/logout/", LoginSignupActivity.Loginfunctions.AccessToken, refreshTokenJSON)
                { responseBody, exception ->
                    if (exception != null) {
                        println("Error: ${exception.message}")
                    } else {

                        println("Response: $responseBody")
                        isLoggedin = false

                        parentFragmentManager.beginTransaction().apply {
                            setCustomAnimations(
                                R.anim.slide_down,
                                R.anim.slide_up,
                                R.anim.slide_down,
                                R.anim.slide_up
                            )
                            replace(R.id.main_fragment_container, Loginfragment())
                            addToBackStack(null)
                            commit()
                        }


                    }
                }

            }

            else{
                println("Hele once bi giris yap sonra cikis yaparsin")
            }

        }

        return v

    }

}