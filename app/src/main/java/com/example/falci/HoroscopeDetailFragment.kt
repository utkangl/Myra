package com.example.falci

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.falci.internalClasses.HoroscopeFunctions
import com.example.falci.internalClasses.dataClasses.horoscopeChatMessageData
import com.example.falci.internalClasses.dataClasses.horoscopeMessage

class HoroscopeDetailFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_horoscope_detail, container, false)
        val getfortune = v.findViewById<ImageView>(R.id.mira_horoscopedetailtop)
        val horoscopeText = v.findViewById<TextView>(R.id.horoscope_textView)

        getfortune.setOnClickListener{
            horoscopeText.text = horoscopeMessage.message
        }

        return v
    }

}