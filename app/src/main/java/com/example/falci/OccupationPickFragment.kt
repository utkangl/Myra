package com.example.falci

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment


class OccupationPickFragment : Fragment() {

    private lateinit var selectedOccupation: String


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_occupation_pick, container, false)


        val occupationPickFragmentnextbutton = v.findViewById<AppCompatButton>(R.id.occupationpickfragmentnextbutton)

        val occupationspinner = v.findViewById<Spinner>(R.id.occupation_spinner)
        val adapter = ArrayAdapter.createFromResource(requireContext(), R.array.occupations, android.R.layout.simple_spinner_item)

        adapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item)
        occupationspinner.adapter = adapter

        occupationspinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedStatus = parent?.getItemAtPosition(position) as? String
                if (selectedStatus != null) {
                    selectedOccupation = selectedStatus
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                println("Nothing Selected for Marital Status")
            }
        }


        occupationPickFragmentnextbutton.setOnClickListener{

            val intent = Intent(activity, MainPage::class.java)


            if (selectedOccupation.isNotEmpty()){
                startActivity(intent)
            }else{
                println("choose an occupation to go on")
            }

        }




        return v

    }


}