package com.example.falci

import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat


class GenderPickFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_gender_pick, container, false)

        val malecircle = v.findViewById<ImageView>(R.id.malecircle)
        val femalecircle = v.findViewById<ImageView>(R.id.femalecircle)

        val fadeIn = AnimationUtils.loadAnimation(context, R.anim.fade_in)
        val fadeOut = AnimationUtils.loadAnimation(context, R.anim.fade_out)

        var maleisClicked = false
        var femaleisClicked = false

        val genderpickfragmentnextbutton = v.findViewById<AppCompatButton>(R.id.genderpickfragmentnextbutton)

        val birthdatepickfragment = BirthdatePickFragment()

        malecircle.setOnClickListener{

            maleisClicked != maleisClicked

            if (!femaleisClicked){
                maleisClicked = if (maleisClicked){
                    malecircle.startAnimation(fadeOut)
                    malecircle.setBackgroundResource(R.drawable.overlay_drawable_male)
                    false
                }else{
                    malecircle.startAnimation(fadeOut)
                    malecircle.setBackgroundResource(R.drawable.overlay_drawable_male_onclicked)
                    true
                }
            }

        }


        femalecircle.setOnClickListener {

            femaleisClicked != femaleisClicked

            if (!maleisClicked) {

                femaleisClicked = if (femaleisClicked) {
                    femalecircle.startAnimation(fadeOut)
                    femalecircle.setBackgroundResource(R.drawable.overlay_drawable_female)
                    false
                } else {
                    femalecircle.startAnimation(fadeOut)
                    femalecircle.setBackgroundResource(R.drawable.overlay_drawable_female_onclicked)
                    true
                }
            }
        }


        genderpickfragmentnextbutton.setOnClickListener{

            if (maleisClicked or femaleisClicked){

                parentFragmentManager.beginTransaction().apply {
                    replace(R.id.fragment_container, birthdatepickfragment)
                    addToBackStack(null)
                    commit()

                }


            }

        }


        return v
    }


}