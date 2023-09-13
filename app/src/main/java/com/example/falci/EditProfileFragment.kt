package com.example.falci

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatButton
import androidx.cardview.widget.CardView
import org.json.JSONObject
import com.example.falci.LoginSignupActivity.ProfileFunctions.putEditProfileJson
import com.example.falci.internalClasses.InternalFunctions.UpdateProfileFieldIfChanged.updateProfileFieldIfChanged
import com.google.android.gms.common.api.ApiException
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.example.falci.internalClasses.InternalFunctions.SetVisibilityFunctions.setViewVisible
import com.example.falci.internalClasses.InternalFunctions.SetVisibilityFunctions.setViewGone
import com.example.falci.internalClasses.InternalFunctions.AddTextWatcher.addTextWatcher
import com.example.falci.internalClasses.InternalFunctions.SetupFieldClickListener.setupFieldClickListener
import com.example.falci.internalClasses.InternalFunctions.SetupSpinnerAndField.setupSpinnerAndField
import com.example.falci.internalClasses.InternalFunctions.UpdateProfileFieldIfChanged.updateBirthDayIfChanged

val editProfileJson = JSONObject()

class EditProfileFragment : Fragment() {

    private lateinit var placesClient: PlacesClient
    private lateinit var autoCompleteTextView: AutoCompleteTextView
    private var currentQuery: String = ""
    private var isCitySelected: Boolean = false

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_editprofile, container, false)

        val nameField = v.findViewById<TextView>(R.id.nameedittext)
        val genderField = v.findViewById<TextView>(R.id.genderedittext)
        val birthDateField = v.findViewById<TextView>(R.id.birthdateedittext)
        val birthTimeField = v.findViewById<TextView>(R.id.birthtimeedittext)
        val locationField = v.findViewById<TextView>(R.id.locationedittext)
        val occupationField = v.findViewById<TextView>(R.id.occupationedittext)
        val relationShipStatusField = v.findViewById<TextView>(R.id.relationshipstatusedittext)
        val savebutton = v.findViewById<AppCompatButton>(R.id.savebutton)
        val savedatebutton = v.findViewById<AppCompatButton>(R.id.savedatebutton)
        val backArrow = v.findViewById<ImageView>(R.id.back_arrow)
        val editProfileDatePicker = v.findViewById<DatePicker>(R.id.editprofiledatepicker)
        val editProfileTimePicker = v.findViewById<TimePicker>(R.id.editprofiletimepicker)
        val genderPickSpinner = v.findViewById<Spinner>(R.id.editProfile_genderpick_spinner)
        val occupationSpinner = v.findViewById<Spinner>(R.id.editProfile_occupationpick_spinner)
        val maritalStatusSpinner = v.findViewById<Spinner>(R.id.editProfile_marital_status_spinner)

        val genderFieldHint = v.findViewById<TextView>(R.id.genderfieldhint)
        val occupationFieldHint = v.findViewById<TextView>(R.id.occupationfieldhint)
        val relationshipStatusFieldHint = v.findViewById<TextView>(R.id.relationshipstatusfieldhint)
        val editProfileGeneralLayout = v.findViewById<RelativeLayout>(R.id.editProfileGeneralLayout)

        nameField.text = userProfile.firstName
        genderField.text = userProfile.gender
        birthDateField.text = userProfile.birthDay
        birthTimeField.text = userProfile.birthTime
        locationField.text = userProfile.birthPlace
        occupationField.text = userProfile.occupation
        relationShipStatusField.text = userProfile.relationshipStatus

        setViewGone(savebutton)

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
            override fun afterTextChanged(s: Editable?) {
                setViewVisible(savebutton)
            }
        }

        // Add TextWatcher to each EditText
        addTextWatcher(nameField,genderField,birthDateField,birthTimeField,locationField,occupationField,relationShipStatusField, textWatcher = textWatcher)

        savebutton.setOnClickListener {

            // Add values to EditProfileJson if changed
            updateProfileFieldIfChanged("first_name", nameField, editProfileJson, userProfile.firstName)
            updateProfileFieldIfChanged("gender", genderField, editProfileJson, userProfile.gender)
            updateBirthDayIfChanged    ("birthDay", birthDateField, birthTimeField, userProfile, editProfileJson)
            updateProfileFieldIfChanged("location", locationField, editProfileJson, userProfile.birthPlace)
            updateProfileFieldIfChanged("occupation", occupationField, editProfileJson, userProfile.occupation)
            updateProfileFieldIfChanged("relationshipStatus", relationShipStatusField, editProfileJson, userProfile.relationshipStatus)

            // Save changes
            userProfile.apply {
                firstName = nameField.text.toString()
                gender = genderField.text.toString()
                birthDay = birthDateField.text.toString()
                birthTime = birthTimeField.text.toString()
                birthPlace = locationField.text.toString()
                occupation = occupationField.text.toString()
                relationshipStatus = relationShipStatusField.text.toString()
            }

            putEditProfileJson(
                url = "http://31.210.43.174:1337/auth/profile/edit/",
                json = editProfileJson,
                accessToken = LoginSignupActivity.Loginfunctions.AccessToken
            )
            {_,_->}

            setViewGone(savebutton)
            fragmentManager?.popBackStack()
        }

        backArrow.setOnClickListener { fragmentManager?.popBackStack() }

        birthDateField.setOnClickListener {
            setViewGone(editProfileGeneralLayout)
            setViewVisible(editProfileDatePicker, savedatebutton)

            savedatebutton.setOnClickListener {
                val selectedYear = editProfileDatePicker.year
                val selectedMonth = editProfileDatePicker.month + 1
                val selectedDay = editProfileDatePicker.dayOfMonth
                val editProfileSelectedDate = "$selectedYear-$selectedMonth-$selectedDay"
                birthDateField.text = editProfileSelectedDate

                setViewVisible(editProfileGeneralLayout)
                setViewGone(editProfileDatePicker, savedatebutton)
            }

        }
        birthTimeField.setOnClickListener {
            setViewGone(editProfileGeneralLayout)
            setViewVisible(editProfileTimePicker,savedatebutton)

            savedatebutton.setOnClickListener {

                val selectedHour = editProfileTimePicker.hour
                val selectedMinute = editProfileTimePicker.minute
                val editProfileSelectedTime = "$selectedHour:$selectedMinute:00"
                birthTimeField.text = editProfileSelectedTime

                setViewGone(editProfileTimePicker,savedatebutton)
                setViewVisible(editProfileGeneralLayout)
            }
        }

        setupFieldClickListener(genderField, genderPickSpinner, genderFieldHint)
        setupFieldClickListener(occupationField, occupationSpinner, occupationFieldHint)
        setupFieldClickListener(relationShipStatusField, maritalStatusSpinner, relationshipStatusFieldHint)

        val genderAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.genders, android.R.layout.simple_spinner_item)
        val isUserInteractedGender = false
        setupSpinnerAndField(genderPickSpinner, genderFieldHint,genderAdapter,isUserInteractedGender){selectedGender -> genderField.text = selectedGender}

        val occupationAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.occupations, android.R.layout.simple_spinner_item)
        val isUserInteractedOccupation = false
        setupSpinnerAndField(occupationSpinner, occupationFieldHint,occupationAdapter,isUserInteractedOccupation){selectedOccupation -> occupationField.text = selectedOccupation}

        val maritalStatusAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.marital_status, android.R.layout.simple_spinner_item)
        val isUserInteractedMarital = false
        setupSpinnerAndField(maritalStatusSpinner, relationshipStatusFieldHint,maritalStatusAdapter,isUserInteractedMarital){selectedMaritalStatus -> occupationField.text = selectedMaritalStatus}


        autoCompleteTextView = v.findViewById(R.id.searchCity)
        val editProfileLocationCardView = v.findViewById<CardView>(R.id.editProfileLocationCardView)

        locationField.setOnClickListener {
            setViewGone(editProfileGeneralLayout)
            setViewVisible(editProfileLocationCardView)
        }

        autoCompleteTextView.setOnItemClickListener { _, _, _, _ ->

            setViewGone(editProfileLocationCardView)
            setViewVisible(editProfileGeneralLayout)

            locationField.text = autoCompleteTextView.text.toString()

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




        return v
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