package com.example.falci

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment

import com.example.falci.LoginSignupActivity.RegistrationFunctions

import com.example.falci.NamePickFragment.NameObject.name
import com.example.falci.GenderPickFragment.GenderObject.gender
import com.example.falci.BirthdatePickFragment.DateObject.date
import com.example.falci.BirthTimePickFragment.TimeObject.time
import com.example.falci.BirthLocationPickFragment.LocationObject.location
import com.example.falci.RelationshipStatusPickFragment.MaritalStatusObject.maritalStatu



class OccupationPickFragment : Fragment() {

    private lateinit var selectedOccupation: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_occupation_pick, container, false)

        val loginFragment = Loginfragment()

        val occupationPickFragmentnextbutton =
            v.findViewById<AppCompatButton>(R.id.occupationpickfragmentnextbutton)

        val occupationspinner = v.findViewById<Spinner>(R.id.occupation_spinner)
        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.occupations,
            android.R.layout.simple_spinner_item
        )

        //adapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item)
        occupationspinner.adapter = adapter

        occupationspinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedStatus = parent?.getItemAtPosition(position) as? String
                if (selectedStatus != null) {
                    selectedOccupation = selectedStatus
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                println("Nothing Selected for Occupation")
            }
        }

        val registerposturl = "http://31.210.43.174:1337/auth/profile/complete/"

        occupationPickFragmentnextbutton.setOnClickListener {

            val formattedDate = formatDateAndTime(date, time)

            val completeProfileJSON = RegistrationFunctions.createCompleteProfileJSON(
                name.toString(),
                gender,
                formattedDate,
                location,
                maritalStatu,
                selectedOccupation
            )

            println("complete profile bilgileri jsonu: $completeProfileJSON")

            RegistrationFunctions.postCompleteProfileJson(
                registerposturl,
                completeProfileJSON
            ) { responseBody, exception ->
                if (exception != null) {
                    println("Error: ${exception.message}")
                } else {
                    println("Response: $responseBody")

                    parentFragmentManager.beginTransaction().apply {
                        setCustomAnimations(
                            R.anim.slide_down,
                            R.anim.slide_up,
                            R.anim.slide_down,
                            R.anim.slide_up
                        )
                        replace(R.id.main_fragment_container, loginFragment)
                        addToBackStack(null)
                        commit()
                    }


                }
            }

        }

        return v

    }

    private fun formatDateAndTime(date: String, time: String): String {
        return ("$date $time +0000")
    }

}



