package com.example.falci


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.widget.AppCompatButton
import com.example.falci.internalClasses.TransitionToFragment.ReplaceFragmentWithAnimation.replaceFragmentWithAnimation

class GenderPickFragment : Fragment() {

    private lateinit var selectedGender: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_gender_pick, container, false)
        val genderpickfragmentnextbutton = v.findViewById<AppCompatButton>(R.id.genderpickfragmentnextbutton)
        val genderpickspinner = v.findViewById<Spinner>(R.id.genderpick_spinner)

        val adapter = ArrayAdapter.createFromResource(requireContext(), R.array.genders, android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        genderpickspinner.adapter = adapter

        genderpickspinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long)
            {
                val selectedgender = parent?.getItemAtPosition(position) as? String
                if (selectedgender != null) { selectedGender = selectedgender; GenderObject.gender = selectedGender }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                println("Nothing Selected for Gender")
            }
        }

        // when next button clicked; if gender is chose, change to birth date screen
        genderpickfragmentnextbutton.setOnClickListener {
            if (selectedGender != "Pick your gender") { replaceFragmentWithAnimation(parentFragmentManager, BirthdatePickFragment()) }
            else { println("you must select your gender before go on") }
        }

        return v
    }
    object GenderObject { lateinit var gender: String }
}