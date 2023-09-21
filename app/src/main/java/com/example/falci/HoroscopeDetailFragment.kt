package com.example.falci

import android.os.Bundle
import android.text.Html
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.falci.internalClasses.dataClasses.GetHoroscopeData
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

        val horoscope = " Welcome ${userCompleteProfile.name} \n \n Burc Ozetin: \n\n  ${getHoroscopeData.summary}  \n\n Pozitif Yonler: \n \n ${getHoroscopeData.good} \n\n Negatif Yonler: \n\n ${getHoroscopeData.bad}"

        horoscopeText.text = horoscope

        return v
    }

}