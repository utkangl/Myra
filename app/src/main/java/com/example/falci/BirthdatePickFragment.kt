package com.example.falci

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import androidx.appcompat.widget.AppCompatButton
import com.example.falci.internalClasses.TransitionToFragment.ReplaceFragmentWithAnimation.replaceFragmentWithAnimation
import com.example.falci.internalClasses.userRegister

class BirthdatePickFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_birthdate_pick, container, false)

        val datepicker = v.findViewById<DatePicker>(R.id.datepicker)
        val datepickfragmentnextbutton = v.findViewById<AppCompatButton>(R.id.datepickfragmentnextbutton)

        datepickfragmentnextbutton.setOnClickListener {
            val selectedYear = datepicker.year
            val selectedMonth =  datepicker.month + 1
            val selectedDay = datepicker.dayOfMonth
            val selectedDate = "$selectedYear-$selectedMonth-$selectedDay"
            userRegister.date = selectedDate
            replaceFragmentWithAnimation(parentFragmentManager, BirthTimePickFragment())
        }

        return v
    }

}