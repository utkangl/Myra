package com.example.falci

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import androidx.appcompat.widget.AppCompatButton
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.gms.common.api.ApiException
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest

class BirthLocationPickFragment : Fragment() {

    private lateinit var placesClient: PlacesClient
    private lateinit var autoCompleteTextView: AutoCompleteTextView
    private var currentQuery: String = ""
    private var isCitySelected: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_birth_location_pick, container, false)

        autoCompleteTextView = v.findViewById(R.id.cityInput)

        // Google Places API başlatma
        Places.initialize(requireContext(), "AIzaSyA5EjVol_is8EPaAprlzCmp20_gEK9X9vo")
        placesClient = Places.createClient(requireContext())

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, mutableListOf<String>())
        autoCompleteTextView.setAdapter(adapter)

        autoCompleteTextView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                currentQuery = s.toString()
                fetchPredictions()
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        val birthLocationPickFragmentnextbutton = v.findViewById<AppCompatButton>(R.id.birthlocationpickfragmentnextbutton)
        val relationshipStatusPickFragment = RelationshipStatusPickFragment()

        birthLocationPickFragmentnextbutton.setOnClickListener{

            if (isCitySelected){

                parentFragmentManager.beginTransaction().apply {
                    replace(R.id.fragment_container, relationshipStatusPickFragment)
                    addToBackStack(null)
                    commit()
                }

            }

        }

        return v
    }

    private fun fetchPredictions() {
        val token = AutocompleteSessionToken.newInstance()
        val request = FindAutocompletePredictionsRequest.builder()
            .setSessionToken(token)
            .setQuery(currentQuery)
            .build()

        placesClient.findAutocompletePredictions(request)
            .addOnSuccessListener { response ->
                val adapter = autoCompleteTextView.adapter as? ArrayAdapter<String>
                adapter?.clear()
                for (prediction in response.autocompletePredictions) {
                    adapter?.add(prediction.getFullText(null).toString())
                }

                // Kullanıcı bir seçim yaptı mı kontrolü
                val selectedCity = autoCompleteTextView.text.toString()
                isCitySelected =
                    selectedCity in response.autocompletePredictions.map { it.getFullText(null).toString() }
            }
            .addOnFailureListener { exception ->
                val statusCode = (exception as? ApiException)?.statusCode
                if (statusCode != null) {
                    println(statusCode)
                }
            }
    }








}