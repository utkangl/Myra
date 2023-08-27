package com.example.falci

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText


class OccupationPickFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_occupation_pick, container, false)


        val occupationPickFragmentnextbutton = v.findViewById<AppCompatButton>(R.id.occupationpickfragmentnextbutton)
        val occupationpickfragmentoccupationinputtext = v.findViewById<AppCompatEditText>(R.id.occupationpickfragmentoccupationinputtext).text



        occupationPickFragmentnextbutton.setOnClickListener{

            if (occupationpickfragmentoccupationinputtext.toString().isNotEmpty()){
                val intent = Intent(activity, MainPage::class.java)
                startActivity(intent)
                }
            }












        return v

    }


}