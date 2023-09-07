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

class ProfileFragment : Fragment() {


    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_profile, container, false)

        val editprofilebutton = v.findViewById<AppCompatButton>(R.id.profilefragmenteditprofilebutton)
        val jupyterlayout = v.findViewById<RelativeLayout>(R.id.scrollviewjupyterlayout)
        val burcexplanationplanet = v.findViewById<CardView>(R.id.burcexplanationplanet)



        editprofilebutton.setOnClickListener{
            parentFragmentManager.beginTransaction().apply {
                setCustomAnimations(
                    R.anim.fade_in,
                    R.anim.fade_out,
                    R.anim.fade_in,
                    R.anim.fade_out
                )
                replace(R.id.main_fragment_container, EditProfileFragment())
                addToBackStack(null)
                commit()
            }
        }

        jupyterlayout.setOnClickListener{
            val burcexplanationplanetimage = burcexplanationplanet.findViewById<ImageView>(R.id.burcexplanationplanetimage)
            val burcexplanationplanettext = burcexplanationplanet.findViewById<TextView>(R.id.burcexplanationplanettext)
            burcexplanationplanetimage.setImageResource(R.drawable.jupiter)
        }





        return v

    }



}