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

class RelationshipStatusPickFragment : Fragment() {

    private lateinit var selectedMaritalStatus: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_relationship_status_pick, container, false)


        val maritalStatusSpinner = v.findViewById<Spinner>(R.id.marital_status_spinner)
        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.marital_status,
            android.R.layout.simple_spinner_item
        )

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        maritalStatusSpinner.adapter = adapter

        maritalStatusSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedStatus = parent?.getItemAtPosition(position) as? String
                if (selectedStatus != null) {
                    selectedMaritalStatus = selectedStatus
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                println("Nothing Selected for Marital Status")
            }
        }

        val relationshipStatusPickFragmentnextbutton =
            v.findViewById<AppCompatButton>(R.id.relationshipstatuspickfragmentnextbutton)
        val occupationPickFragment = OccupationPickFragment()


        relationshipStatusPickFragmentnextbutton.setOnClickListener {

            println("marital statu: $selectedMaritalStatus")

            if (selectedMaritalStatus != "Medeni durumunuzu Seciniz") {
                MaritalStatusObject.maritalStatu = selectedMaritalStatus

                parentFragmentManager.beginTransaction().apply {
                    setCustomAnimations(
                        R.anim.slide_down,
                        R.anim.slide_up,
                        R.anim.slide_down,
                        R.anim.slide_up
                    )
                    replace(R.id.main_fragment_container, occupationPickFragment)
                    addToBackStack(null)
                    commit()
                }

            } else {
                println("you must select your marital status before go on")
            }

        }

        return v
    }

    object MaritalStatusObject {
        lateinit var maritalStatu: String
    }

}