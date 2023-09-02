package com.example.falci

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import androidx.appcompat.widget.AppCompatButton
import androidx.cardview.widget.CardView
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

        val locationfragmentcardview = v.findViewById<CardView>(R.id.locationFragmentCardView)
        val chooseyourcity = v.findViewById<AppCompatButton>(R.id.chooseyourcity)
        val relationshipStatusPickFragment = RelationshipStatusPickFragment()
        val birthLocationPickFragmentnextbutton = v.findViewById<AppCompatButton>(R.id.birthLocationPickFragmentnextbutton)

        chooseyourcity.setOnClickListener{
            autoCompleteTextView.visibility = View.VISIBLE
            locationfragmentcardview.visibility = View.VISIBLE
            chooseyourcity.visibility = View.INVISIBLE
            birthLocationPickFragmentnextbutton.visibility = View.INVISIBLE
        }

        autoCompleteTextView.setOnItemClickListener { _, _, _, _ ->
            // AutoCompleteTextView'dan bir öğe seçildiğinde burada çalışacak kodu ekleyin
            locationfragmentcardview.visibility = View.INVISIBLE
            chooseyourcity.visibility = View.VISIBLE
            birthLocationPickFragmentnextbutton.visibility = View.VISIBLE

            chooseyourcity.text = autoCompleteTextView.text.toString()
        }


        Places.initialize(requireContext(), "AIzaSyA5EjVol_is8EPaAprlzCmp20_gEK9X9vo") // Google Places API başlatma TODO/apikeyi boyle aciktan verme
        placesClient = Places.createClient(requireContext())

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            mutableListOf<String>()
        )
        autoCompleteTextView.setAdapter(adapter)

        autoCompleteTextView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                currentQuery = s.toString()
                fetchPredictions()
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        birthLocationPickFragmentnextbutton.setOnClickListener {

            print("location: ${LocationObject.location}")

            if (isCitySelected) {

                parentFragmentManager.beginTransaction().apply {
                    setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out)
                    replace(R.id.main_fragment_container, relationshipStatusPickFragment)
                    addToBackStack(null)
                    commit()
                }
            }
        }

        return v
    }

    object LocationObject {
        var location: String = ""
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

                LocationObject.location = selectedCity
                isCitySelected = selectedCity in response.autocompletePredictions.map {
                    it.getFullText(null).toString()
                }

            }
            .addOnFailureListener { exception ->
                val statusCode = (exception as? ApiException)?.statusCode
                if (statusCode != null) {
                    println(statusCode)
                }
            }
    }

}