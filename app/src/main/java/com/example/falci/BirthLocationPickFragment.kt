package com.example.falci

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import com.example.falci.internalClasses.InternalFunctions.SetVisibilityFunctions.setViewGone
import com.example.falci.internalClasses.InternalFunctions.SetVisibilityFunctions.setViewVisible
import com.example.falci.internalClasses.LocationService
import com.example.falci.internalClasses.TransitionToFragment.ReplaceFragmentWithAnimation.replaceFragmentWithAnimation
import com.example.falci.internalClasses.userRegister

class BirthLocationPickFragment : Fragment() {

    private lateinit var locationService: LocationService

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_birth_location_pick, container, false)

        val cityInput = v.findViewById<AutoCompleteTextView>(R.id.cityInput)
        val chooseyourcity = v.findViewById<AppCompatButton>(R.id.chooseyourcity)
        val locationFragmentFirstLayout = v.findViewById<RelativeLayout>(R.id.locationFragmentFirstLayout)
        val locationFragmentSecondLayout = v.findViewById<RelativeLayout>(R.id.locationFragmentSecondLayout)
        val birthLocationPickFragmentnextbutton = v.findViewById<AppCompatButton>(R.id.birthLocationPickFragmentnextbutton)

        this.locationService = LocationService(requireContext())
        locationService.initializeAutoCompleteTextView(cityInput)

        chooseyourcity.setOnClickListener {
            setViewVisible(locationFragmentSecondLayout)
            setViewGone(locationFragmentFirstLayout)
        }

        cityInput.setOnItemClickListener { _, _, _, _ ->
            setViewVisible(locationFragmentFirstLayout)
            setViewGone(locationFragmentSecondLayout)
            locationService.hideKeyboard(requireView())
            chooseyourcity.text = cityInput.text.toString()
        }

        birthLocationPickFragmentnextbutton.setOnClickListener {
            print("location: ${userRegister.location}")
            if (locationService.isCitySelected) {
                replaceFragmentWithAnimation(parentFragmentManager, RelationshipStatusPickFragment())
            }
        }

        return v

    }
}

