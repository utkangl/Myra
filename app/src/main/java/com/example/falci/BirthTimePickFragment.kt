package com.example.falci

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.appcompat.widget.AppCompatButton


class BirthTimePickFragment : Fragment() {



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_birth_time_pick, container, false)

        val timepicker = v.findViewById<TimePicker>(R.id.timepicker)

        val timepickfragmentnextbutton = v.findViewById<AppCompatButton>(R.id.timepickfragmentnextbutton)

        val birthLocationPickFragment = BirthLocationPickFragment()

        timepickfragmentnextbutton.setOnClickListener {
            val selectedHour = timepicker.hour
            val selectedMinute = timepicker.minute

            val selectedTime = "$selectedHour:$selectedMinute:00"

            println(selectedTime)

            TimeObject.time = selectedTime

            parentFragmentManager.beginTransaction().apply {
                replace(R.id.main_fragment_container, birthLocationPickFragment)
                addToBackStack(null)
                commit()
            }
        }
        
        return v
    }


    object TimeObject{
        lateinit var time: String
    }

}