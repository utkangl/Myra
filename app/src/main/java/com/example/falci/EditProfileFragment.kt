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
import android.widget.*
import androidx.appcompat.widget.AppCompatButton
import androidx.cardview.widget.CardView
import androidx.core.content.ContentProviderCompat
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
import com.example.falci.internalClasses.InternalFunctions.SetupSpinnerAndField.setupSpinnerAndField
import com.example.falci.internalClasses.InternalFunctions.UpdateProfileFieldIfChanged.updateBirthDayIfChanged

val editProfileJson = JSONObject()

class EditProfileFragment : Fragment() {

    private lateinit var placesClient: PlacesClient
    private lateinit var autoCompleteTextView: AutoCompleteTextView
    private var currentQuery: String = ""
    private var isCitySelected: Boolean = false

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
        val backArrowCard = v.findViewById<CardView>(R.id.backarrowcard)
        val editProfileDatePicker = v.findViewById<DatePicker>(R.id.editprofiledatepicker)
        val editProfileTimePicker = v.findViewById<TimePicker>(R.id.editprofiletimepicker)
        val genderPickSpinner = v.findViewById<Spinner>(R.id.editProfile_genderpick_spinner)
        val occupationSpinner = v.findViewById<Spinner>(R.id.editProfile_occupationpick_spinner)
        val maritalStatusSpinner = v.findViewById<Spinner>(R.id.editProfile_marital_status_spinner)

        val genderFieldHint = v.findViewById<TextView>(R.id.genderfieldhint)
        val occupationFieldHint = v.findViewById<TextView>(R.id.occupationfieldhint)
        val relationshipStatusFieldHint = v.findViewById<TextView>(R.id.relationshipstatusfieldhint)
        val editProfileGeneralLayout = v.findViewById<RelativeLayout>(R.id.editProfileGeneralLayout)

        nameField.text = userProfile.profileFirst_name
        genderField.text = userProfile.profileGender
        birthDateField.text = userProfile.profileBirth_day
        birthTimeField.text = userProfile.profileBirth_time
        locationField.text = userProfile.profileBirth_place
        occupationField.text = userProfile.profileOccupation
        relationShipStatusField.text = userProfile.profileRelationshipStatus

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
            updateProfileFieldIfChanged("first_name", nameField, editProfileJson, userProfile.profileFirst_name)
            updateProfileFieldIfChanged("gender", genderField, editProfileJson, userProfile.profileGender)
            updateBirthDayIfChanged    ("birthDay", birthDateField, birthTimeField, userProfile, editProfileJson)
            updateProfileFieldIfChanged("location", locationField, editProfileJson, userProfile.profileBirth_place)
            updateProfileFieldIfChanged("occupation", occupationField, editProfileJson, userProfile.profileOccupation)
            updateProfileFieldIfChanged("relationshipStatus", relationShipStatusField, editProfileJson, userProfile.profileRelationshipStatus)



            // Save changes
            userProfile.profileFirst_name = nameField.text.toString()
            userProfile.profileGender = genderField.text.toString()
            userProfile.profileBirth_day = birthDateField.text.toString()
            userProfile.profileBirth_time = birthTimeField.text.toString()
            userProfile.profileBirth_place = locationField.text.toString()
            userProfile.profileOccupation = occupationField.text.toString()
            userProfile.profileRelationshipStatus = relationShipStatusField.text.toString()

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

        genderField.setOnClickListener {
            setViewVisible(genderPickSpinner)
            setViewGone(genderFieldHint)
        }

        val genderAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.genders, android.R.layout.simple_spinner_item)
        val isUserInteractedGender = false
        setupSpinnerAndField(genderPickSpinner, genderFieldHint,genderAdapter,isUserInteractedGender){selectedGender -> genderField.text = selectedGender}


        occupationField.setOnClickListener {
            setViewVisible(occupationSpinner)
            setViewGone(occupationFieldHint)
        }

        val occupationAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.occupations, android.R.layout.simple_spinner_item)
        val isUserInteractedOccupation = false
        setupSpinnerAndField(occupationSpinner, occupationFieldHint,occupationAdapter,isUserInteractedOccupation){selectedOccupation -> occupationField.text = selectedOccupation}


        relationShipStatusField.setOnClickListener {
            setViewVisible(maritalStatusSpinner)
            setViewGone(relationshipStatusFieldHint)
        }
        val maritalStatusAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.marital_status, android.R.layout.simple_spinner_item)
        val isUserInteractedMarital = false
        setupSpinnerAndField(maritalStatusSpinner, relationshipStatusFieldHint,maritalStatusAdapter,isUserInteractedMarital){selectedMaritalStatus -> occupationField.text = selectedMaritalStatus}



        autoCompleteTextView = v.findViewById(R.id.annen)
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