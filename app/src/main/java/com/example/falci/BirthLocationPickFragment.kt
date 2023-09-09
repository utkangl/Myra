package com.example.falci

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.cardview.widget.CardView
import com.google.android.libraries.places.api.Places
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
        val birthLocationPickFragmentnextbutton =
            v.findViewById<AppCompatButton>(R.id.birthLocationPickFragmentnextbutton)
        val birthlocationpickfragmenttitle =
            v.findViewById<TextView>(R.id.birthlocationpickfragmenttitle)
        val birthlocationpickfragmentstep5of7 =
            v.findViewById<TextView>(R.id.birthlocationpickfragmentstep5of7)
        val star1 = v.findViewById<StarShape>(R.id.star1)
        val star2 = v.findViewById<StarShape>(R.id.star2)
        val star3 = v.findViewById<StarShape>(R.id.star3)
        val star4 = v.findViewById<StarShape>(R.id.star4)
        val star5 = v.findViewById<StarShape>(R.id.star5)
        val star6 = v.findViewById<StarShape>(R.id.star6)
        val star7 = v.findViewById<StarShape>(R.id.star7)

        chooseyourcity.setOnClickListener {
            autoCompleteTextView.visibility = View.VISIBLE
            locationfragmentcardview.visibility = View.VISIBLE
            chooseyourcity.visibility = View.INVISIBLE
            birthLocationPickFragmentnextbutton.visibility = View.INVISIBLE
            birthlocationpickfragmenttitle.visibility = View.INVISIBLE
            birthlocationpickfragmentstep5of7.visibility = View.INVISIBLE
            star1.visibility = View.INVISIBLE
            star2.visibility = View.INVISIBLE
            star3.visibility = View.INVISIBLE
            star4.visibility = View.INVISIBLE
            star5.visibility = View.INVISIBLE
            star6.visibility = View.INVISIBLE
            star7.visibility = View.INVISIBLE
        }

        autoCompleteTextView.setOnItemClickListener { _, _, _, _ ->
            // AutoCompleteTextView'dan bir öğe seçildiğinde burada çalışacak kodu ekleyin
            star1.visibility = View.VISIBLE
            star2.visibility = View.VISIBLE
            star3.visibility = View.VISIBLE
            star4.visibility = View.VISIBLE
            star5.visibility = View.VISIBLE
            star6.visibility = View.VISIBLE
            star7.visibility = View.VISIBLE
            locationfragmentcardview.visibility = View.INVISIBLE
            chooseyourcity.visibility = View.VISIBLE
            birthLocationPickFragmentnextbutton.visibility = View.VISIBLE
            birthlocationpickfragmenttitle.visibility = View.VISIBLE
            birthlocationpickfragmentstep5of7.visibility = View.VISIBLE

            chooseyourcity.text = autoCompleteTextView.text.toString()

            val inputMethodManager =
                requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(autoCompleteTextView.windowToken, 0)
        }


        Places.initialize(
            requireContext(),
            "AIzaSyA5EjVol_is8EPaAprlzCmp20_gEK9X9vo"
        ) // Google Places API başlatma TODO/apikeyi boyle aciktan verme
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
                    setCustomAnimations(
                        R.anim.slide_down,
                        R.anim.slide_up,
                        R.anim.slide_down,
                        R.anim.slide_up
                    )
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