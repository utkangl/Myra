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

import com.example.falci.NamePickFragment.NameObject.name
import com.example.falci.GenderPickFragment.GenderObject.gender
import com.example.falci.BirthdatePickFragment.DateObject.date
import com.example.falci.BirthTimePickFragment.TimeObject.time
import com.example.falci.BirthLocationPickFragment.LocationObject.location
import com.example.falci.RelationshipStatusPickFragment.MaritalStatusObject.maritalStatu
import com.example.falci.internalClasses.AuthenticationFunctions.CreateJsonObject.createJsonObject
import com.example.falci.internalClasses.AuthenticationFunctions.PostJsonFunctions.postJsonWithHeader
import com.example.falci.internalClasses.TransitionToFragment.ReplaceFragmentWithAnimation.replaceFragmentWithAnimation
import com.example.falci.internalClasses.urls
import org.json.JSONObject


class OccupationPickFragment : Fragment() {

    private lateinit var selectedOccupation: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_occupation_pick, container, false)

        val occupationPickFragmentnextbutton =
            v.findViewById<AppCompatButton>(R.id.occupationpickfragmentnextbutton)

        val occupationspinner = v.findViewById<Spinner>(R.id.occupation_spinner)
        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.occupations,
            android.R.layout.simple_spinner_item
        )

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

        occupationPickFragmentnextbutton.setOnClickListener {

            val formattedDate = formatDateAndTime(date, time)

            val zodiacInfoJson = createJsonObject(
                "name" to name,
                "location" to location,
                "birthDay" to formattedDate,
                "gender" to gender,
                "occupation" to selectedOccupation
            )

            val infoJson = createJsonObject(
                "zodiacInfo" to zodiacInfoJson,
                "relationshipStatus" to maritalStatu
            )

            val completeProfileJSON = createJsonObject(
                "info" to infoJson
            )

            println("complete profile bilgileri jsonu: $completeProfileJSON")

            postJsonWithHeader(urls.completeProfileURL, completeProfileJSON, registerTokensDataClass.registerAccessToken)
            { responseBody, exception ->
                if (exception != null) {
                    println("Error: ${exception.message}")
                } else {
                    val responseJson = responseBody?.let { it1 -> JSONObject(it1) }
                    val detail = responseJson?.optString("detail")
                    println(detail)
                    replaceFragmentWithAnimation(parentFragmentManager, Loginfragment())
                }
            }
        }

        return v

    }

    private fun formatDateAndTime(date: String, time: String): String {
        return ("$date $time +0000")
    }

}



