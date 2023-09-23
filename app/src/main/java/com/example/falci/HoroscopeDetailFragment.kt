package com.example.falci

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import com.example.falci.internalClasses.TransitionToFragment.ReplaceActivityToFragment.replaceMainActivityToFragment
import com.example.falci.internalClasses.dataClasses.getHoroscopeData
import com.example.falci.internalClasses.dataClasses.userCompleteProfile

class HoroscopeDetailFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_horoscope_detail, container, false)
        val horoscopeText = v.findViewById<TextView>(R.id.horoscope_textView)
        val miraHoroscopeDetailBottom = v.findViewById<ImageView>(R.id.miraHoroscopeDetailBottom)

        val horoscope =
            " Welcome ${userCompleteProfile.name} \n \n " +
            "Burc Ozetin: \n\n  " +
            "${getHoroscopeData.summary}  \n\n " +
            "Pozitif Yonler: \n \n ${getHoroscopeData.good}" +
            "\n\n Negatif Yonler: \n\n ${getHoroscopeData.bad}"

        horoscopeText.text = horoscope

        miraHoroscopeDetailBottom.setOnClickListener{
            replaceMainActivityToFragment(parentFragmentManager, ChatFragment())
        }

        // if user press back button on horoscope detail fragment, load main activity
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val mainActivityIntent = Intent(requireContext(), MainActivity::class.java)
                startActivity(mainActivityIntent)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        return v
    }

}