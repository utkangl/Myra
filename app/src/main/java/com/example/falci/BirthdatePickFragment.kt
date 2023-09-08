package com.example.falci

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import androidx.appcompat.widget.AppCompatButton

class BirthdatePickFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_birthdate_pick, container, false)

        val datepicker = v.findViewById<DatePicker>(R.id.datepicker)

        val datepickfragmentnextbutton =
            v.findViewById<AppCompatButton>(R.id.datepickfragmentnextbutton)

        val birthTimePickFragment = BirthTimePickFragment()

        datepickfragmentnextbutton.setOnClickListener {
            val selectedYear = datepicker.year
            val selectedMonth =  datepicker.month + 1
            val selectedDay = datepicker.dayOfMonth


            val selectedDate = "$selectedYear-$selectedMonth-$selectedDay"
            DateObject.date = selectedDate

            println(selectedDate)

            parentFragmentManager.beginTransaction().apply {
                setCustomAnimations(
                    R.anim.fade_in,
                    R.anim.fade_out,
                    R.anim.fade_in,
                    R.anim.fade_out
                )
                replace(R.id.main_fragment_container, birthTimePickFragment)
                addToBackStack(null)
                commit()
            }
        }

        return v
    }

    object DateObject {
        lateinit var date: String
    }

}