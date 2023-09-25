package com.example.falci

import android.app.ActivityOptions
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

        // create and format horoscope string with the instance of GetHoroscopeData's field
        val horoscope =
            " Welcome ${userCompleteProfile.name} \n \n " +
            "Burc Ozetin: \n\n  " +
            "${getHoroscopeData.summary}  \n\n " +
            "Pozitif Yonler: \n \n ${getHoroscopeData.good}" +
            "\n\n Negatif Yonler: \n\n ${getHoroscopeData.bad}"

        // to show user the text we created above, set TextView's text as the text we created
        horoscopeText.text = horoscope

        // is user clicks to mira image at the bottom, navigate user to chat w/ mira screen
        miraHoroscopeDetailBottom.setOnClickListener{
            replaceMainActivityToFragment(parentFragmentManager, ChatFragment())
        }

        // create callback variable which will handle onBackPressed and navigate to main activity
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val options = ActivityOptions.makeCustomAnimation(requireContext(), R.anim.activity_slide_down, 0)
                val mainActivityIntent = Intent(requireContext(), MainActivity::class.java)
                startActivity(mainActivityIntent, options.toBundle())
            }
        }
        // call callback to navigate user to main activity when back button is pressed
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        return v
    }

}