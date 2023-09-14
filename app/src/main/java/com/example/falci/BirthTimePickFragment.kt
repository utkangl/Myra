package com.example.falci

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TimePicker
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatButton
import com.example.falci.internalClasses.TransitionToFragment.ReplaceFragmentWithAnimation.replaceFragmentWithAnimation

class BirthTimePickFragment : Fragment() {

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_birth_time_pick, container, false)

        val timepicker = v.findViewById<TimePicker>(R.id.timepicker)

        val timepickfragmentnextbutton =
            v.findViewById<AppCompatButton>(R.id.timepickfragmentnextbutton)


        timepickfragmentnextbutton.setOnClickListener {
            val selectedHour = timepicker.hour
            val selectedMinute = timepicker.minute
            val selectedTime = "$selectedHour:$selectedMinute:00"
            TimeObject.time = selectedTime
            replaceFragmentWithAnimation(parentFragmentManager, BirthLocationPickFragment())
        }

        return v
    }


    object TimeObject {
        lateinit var time: String
    }

}