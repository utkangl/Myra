package com.example.falci

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.appcompat.widget.AppCompatButton
import java.util.*

class BirthdatePickFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_birthdate_pick, container, false)

        val datepicker = v.findViewById<DatePicker>(R.id.datepicker)
        val timepicker = v.findViewById<TimePicker>(R.id.timepicker)

        val datepickfragmentnextbutton = v.findViewById<AppCompatButton>(R.id.datepickfragmentnextbutton)
        val birthLocationPickFragment = BirthLocationPickFragment()

        datepickfragmentnextbutton.setOnClickListener {
            val selectedYear = datepicker.year
            val selectedMonth = datepicker.month + 1 // DatePicker'da aylar 0-11 arasında indekslenir
            val selectedDay = datepicker.dayOfMonth

            val selectedHour = timepicker.hour
            val selectedMinute = timepicker.minute

            val selectedDateTimeText = "Tarih: $selectedDay/$selectedMonth/$selectedYear, Saat: $selectedHour:$selectedMinute"
            println("Seçili Tarih ve Saat: $selectedDateTimeText")

            parentFragmentManager.beginTransaction().apply {
                replace(R.id.fragment_container, birthLocationPickFragment)
                addToBackStack(null)
                commit()
            }
        }

        return v
    }
}