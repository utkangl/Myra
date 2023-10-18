package com.example.falci.internalClasses

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import com.example.falci.internalClasses.dataClasses.controlVariables
import com.example.falci.internalClasses.dataClasses.postPartnerProfile
import com.example.falci.internalClasses.dataClasses.userCompleteProfile
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.gms.common.api.ApiException

class LocationService(private val context: Context) {

    private lateinit var placesClient: PlacesClient
    private lateinit var autoCompleteTextView: AutoCompleteTextView
    private var isCitySelected = false

    private var currentQuery: String = ""

    fun initializeAutoCompleteTextView(autoCompleteTextView: AutoCompleteTextView) {
        this.autoCompleteTextView = autoCompleteTextView

        Places.initialize(
            context,
            "AIzaSyA5EjVol_is8EPaAprlzCmp20_gEK9X9vo"
        )
        placesClient = Places.createClient(context)

        val adapter = ArrayAdapter(
            context,
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

        autoCompleteTextView.setOnItemClickListener { _, _, _, _ ->
            // AutoCompleteTextView'dan bir öğe seçildiğinde burada çalışacak kodu ekleyin
            hideKeyboard(autoCompleteTextView)
        }
    }

    fun fetchPredictions() {
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

                val selectedCity = autoCompleteTextView.text.toString()

                if (controlVariables.isFromLoveHoroscope) {
                    postPartnerProfile.partnerLocation = selectedCity
                } else userCompleteProfile.location = selectedCity

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

     fun hideKeyboard(view: View) {
        val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }
}