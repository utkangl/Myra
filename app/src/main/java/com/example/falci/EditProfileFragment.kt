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
import org.json.JSONObject
import com.example.falci.LoginSignupActivity.ProfileFunctions.putEditProfileJson
import com.google.android.gms.common.api.ApiException
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient

val editProfileJson = JSONObject()

class EditProfileFragment : Fragment() {

    lateinit var nameField: TextView
    lateinit var genderField: TextView
    lateinit var birthDateField: TextView
    lateinit var birthTimeField: TextView
    lateinit var locationField: TextView
    lateinit var occupationField: TextView
    lateinit var relationShipStatusField: TextView
    lateinit var savebutton: AppCompatButton
    lateinit var savedatebutton: AppCompatButton
    lateinit var backArrow: ImageView
    lateinit var backArrowCard: CardView
    lateinit var editProfileDatePicker: DatePicker
    lateinit var editProfileTimePicker: TimePicker
    lateinit var genderPickSpinner: Spinner
    lateinit var occupationSpinner: Spinner
    lateinit var maritalstatusSpinner: Spinner

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

        nameField = v.findViewById(R.id.nameedittext)
        genderField = v.findViewById(R.id.genderedittext)
        birthDateField = v.findViewById(R.id.birthdateedittext)
        birthTimeField = v.findViewById(R.id.birthtimeedittext)
        locationField = v.findViewById(R.id.locationedittext)
        occupationField = v.findViewById(R.id.occupationedittext)
        relationShipStatusField = v.findViewById(R.id.relationshipstatusedittext)
        savebutton = v.findViewById(R.id.savebutton)
        savedatebutton = v.findViewById(R.id.savedatebutton)
        backArrow = v.findViewById(R.id.back_arrow)
        backArrowCard = v.findViewById(R.id.backarrowcard)
        editProfileDatePicker = v.findViewById(R.id.editprofiledatepicker)
        editProfileTimePicker = v.findViewById(R.id.editprofiletimepicker)
        genderPickSpinner = v.findViewById(R.id.editProfile_genderpick_spinner)
        occupationSpinner = v.findViewById(R.id.editProfile_occupationpick_spinner)
        maritalstatusSpinner = v.findViewById(R.id.editProfile_marital_status_spinner)

        val nameFieldHint               = v.findViewById<TextView>(R.id.namefieldhint)
        val genderFieldHint             = v.findViewById<TextView>(R.id.genderfieldhint)
        val birthDateFieldHint          = v.findViewById<TextView>(R.id.birthdatefieldhint)
        val birthTimeFieldHint          = v.findViewById<TextView>(R.id.birthtimefieldhint)
        val locationFieldHint           = v.findViewById<TextView>(R.id.locationfieldhint)
        val occupationFieldHint         = v.findViewById<TextView>(R.id.occupationfieldhint)
        val relationshipStatusFieldHint = v.findViewById<TextView>(R.id.relationshipstatusfieldhint)
        val editProfileTitle = v.findViewById<TextView>(R.id.editprofiletitle)


        // Set initial values
        nameField.setText(profileFirst_name)
        genderField.setText(profileGender)
        birthDateField.setText(profileBirth_day)
        birthTimeField.setText(profileBirth_time)
        locationField.setText(profileBirth_place)
        occupationField.setText(profileOccupation)
        relationShipStatusField.setText(profileRelationshipStatus)

        println(profileBirth_day)
        println(profileBirth_time)

        savebutton.visibility = View.INVISIBLE

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                savebutton.visibility = View.VISIBLE
            }
        }

        // Add TextWatcher to each EditText
        nameField.addTextChangedListener(textWatcher)
        genderField.addTextChangedListener(textWatcher)
        birthDateField.addTextChangedListener(textWatcher)
        birthTimeField.addTextChangedListener(textWatcher)
        locationField.addTextChangedListener(textWatcher)
        occupationField.addTextChangedListener(textWatcher)
        relationShipStatusField.addTextChangedListener(textWatcher)

        savebutton.setOnClickListener {

            // Değişen değerleri JSON nesnesine ekle
            if (nameField.text.toString() != profileFirst_name) {
                editProfileJson.put("first_name", nameField.text.toString())
            }
            if (genderField.text.toString() != profileGender) {
                editProfileJson.put("gender", genderField.text.toString())
            }
            if (birthDateField.text.toString() != profileBirth_day) {
                editProfileJson.put("birthDay", "${birthDateField.text.toString()} ${birthTimeField.text.toString()} +0000")
            }

            if (birthTimeField.text.toString() != profileBirth_time) {
                editProfileJson.put("birthDay", "${birthDateField.text.toString()} ${birthTimeField.text.toString()} +0000")
            }

            if (locationField.text.toString() != profileBirth_place) {
                editProfileJson.put("location", locationField.text.toString())
            }
            if (occupationField.text.toString() != profileOccupation) {
                editProfileJson.put("occupation", occupationField.text.toString())
            }
            if (relationShipStatusField.text.toString() != profileRelationshipStatus) {
                editProfileJson.put("relationshipStatus", relationShipStatusField.text.toString())
            }

            // Save changes
            profileFirst_name = nameField.text.toString()
            profileGender = genderField.text.toString()
            profileBirth_day = birthDateField.text.toString()
            profileBirth_time = birthTimeField.text.toString()
            profileBirth_place = locationField.text.toString()
            profileOccupation = occupationField.text.toString()
            profileRelationshipStatus = relationShipStatusField.text.toString()

            savebutton.visibility = View.INVISIBLE

            println("yolladigim json $editProfileJson")

            putEditProfileJson(
                url = "http://31.210.43.174:1337/auth/profile/edit/",
                json = editProfileJson,
                accessToken = LoginSignupActivity.Loginfunctions.AccessToken
            )
            { responseBody, exception ->
                if (exception != null) {
                    println("Error: ${exception.message}")
                } else {
                   // println("Response: $responseBody")

                    val responseJson = responseBody?.let { it1 -> JSONObject(it1) }
                    if (responseJson != null) {
                        val detail = responseJson.optString("detail")
                        //println(detail)
                    }

                }
            }

            fragmentManager?.popBackStack()
        }

        backArrow.setOnClickListener{
            fragmentManager?.popBackStack()
        }

        birthDateField.setOnClickListener{
            editProfileDatePicker.visibility = View.VISIBLE
            nameField.visibility = View.GONE
            genderField.visibility = View.GONE
            birthDateField.visibility = View.GONE
            birthTimeField.visibility = View.GONE
            locationField.visibility = View.GONE
            occupationField.visibility = View.GONE
            relationShipStatusField.visibility = View.GONE

            nameFieldHint.visibility = View.GONE
            genderFieldHint.visibility = View.GONE
            birthDateFieldHint.visibility = View.GONE
            birthTimeFieldHint.visibility = View.GONE
            locationFieldHint.visibility = View.GONE
            occupationFieldHint.visibility = View.GONE
            relationshipStatusFieldHint.visibility = View.GONE
            editProfileTitle.visibility = View.GONE

            backArrowCard.visibility = View.GONE
            savebutton.visibility = View.GONE
            savedatebutton.visibility = View.VISIBLE



            savedatebutton.setOnClickListener{

                val selectedYear = editProfileDatePicker.year
                val selectedMonth =  editProfileDatePicker.month + 1
                val selectedDay = editProfileDatePicker.dayOfMonth
                val editProfileSelectedDate = "$selectedYear-$selectedMonth-$selectedDay"
                birthDateField.text = editProfileSelectedDate

                println("kaydete bastim $editProfileSelectedDate")

                editProfileDatePicker.visibility = View.GONE
                backArrowCard.visibility = View.VISIBLE
                savebutton.visibility = View.VISIBLE
                savedatebutton.visibility = View.GONE

                nameField.visibility = View.VISIBLE
                genderField.visibility = View.VISIBLE
                birthDateField.visibility = View.VISIBLE
                birthTimeField.visibility = View.VISIBLE
                locationField.visibility = View.VISIBLE
                occupationField.visibility = View.VISIBLE
                relationShipStatusField.visibility = View.VISIBLE

                nameFieldHint.visibility = View.VISIBLE
                genderFieldHint.visibility = View.VISIBLE
                birthDateFieldHint.visibility = View.VISIBLE
                birthTimeFieldHint.visibility = View.VISIBLE
                locationFieldHint.visibility = View.VISIBLE
                occupationFieldHint.visibility = View.VISIBLE
                relationshipStatusFieldHint.visibility = View.VISIBLE
                editProfileTitle.visibility = View.VISIBLE
            }

        }

        birthTimeField.setOnClickListener{
            editProfileTimePicker.visibility = View.VISIBLE
            nameField.visibility = View.GONE
            genderField.visibility = View.GONE
            birthDateField.visibility = View.GONE
            birthTimeField.visibility = View.GONE
            locationField.visibility = View.GONE
            occupationField.visibility = View.GONE
            relationShipStatusField.visibility = View.GONE

            nameFieldHint.visibility = View.GONE
            genderFieldHint.visibility = View.GONE
            birthDateFieldHint.visibility = View.GONE
            birthTimeFieldHint.visibility = View.GONE
            locationFieldHint.visibility = View.GONE
            occupationFieldHint.visibility = View.GONE
            relationshipStatusFieldHint.visibility = View.GONE
            editProfileTitle.visibility = View.GONE

            backArrowCard.visibility = View.GONE
            savebutton.visibility = View.GONE
            savedatebutton.visibility = View.VISIBLE



            savedatebutton.setOnClickListener{

                val selectedHour = editProfileTimePicker.hour
                val selectedMinute = editProfileTimePicker.minute
                val editProfileSelectedTime = "$selectedHour:$selectedMinute:00"
                birthTimeField.text = editProfileSelectedTime

                println("kaydete bastim $editProfileSelectedTime")

                editProfileTimePicker.visibility = View.GONE
                backArrowCard.visibility = View.VISIBLE
                savebutton.visibility = View.VISIBLE
                savedatebutton.visibility = View.GONE

                nameField.visibility = View.VISIBLE
                genderField.visibility = View.VISIBLE
                birthDateField.visibility = View.VISIBLE
                birthTimeField.visibility = View.VISIBLE
                locationField.visibility = View.VISIBLE
                occupationField.visibility = View.VISIBLE
                relationShipStatusField.visibility = View.VISIBLE

                nameFieldHint.visibility = View.VISIBLE
                genderFieldHint.visibility = View.VISIBLE
                birthDateFieldHint.visibility = View.VISIBLE
                birthTimeFieldHint.visibility = View.VISIBLE
                locationFieldHint.visibility = View.VISIBLE
                occupationFieldHint.visibility = View.VISIBLE
                relationshipStatusFieldHint.visibility = View.VISIBLE
                editProfileTitle.visibility = View.VISIBLE
            }

        }


        genderField.setOnClickListener {
            genderPickSpinner.visibility = View.VISIBLE
            genderFieldHint.visibility = View.GONE
        }

        val genderAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.genders, android.R.layout.simple_spinner_item)
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        genderPickSpinner.adapter = genderAdapter

        var isUserInteracted = false

        genderPickSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (isUserInteracted) { // Kullanıcı bir şey seçtiyse
                    val selectedGender = parent?.getItemAtPosition(position) as? String
                    genderField.text = selectedGender
                    genderPickSpinner.visibility = View.GONE
                    genderFieldHint.visibility = View.VISIBLE
                } else {
                    isUserInteracted = true
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                println("Nothing Selected for Gender")
            }
        }


        occupationField.setOnClickListener{
            occupationSpinner.visibility = View.VISIBLE
            occupationFieldHint.visibility = View.GONE
        }
        val occupationAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.occupations, android.R.layout.simple_spinner_item)
        occupationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        occupationSpinner.adapter = occupationAdapter

        var isUserInteractedOccupation = false


        occupationSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (isUserInteractedOccupation){
                    val selectedStatus = parent?.getItemAtPosition(position) as? String
                    if (selectedStatus != null) {
                        occupationField.text = selectedStatus
                    }
                    occupationSpinner.visibility = View.GONE
                    occupationFieldHint.visibility = View.VISIBLE
                }
                else{
                    isUserInteractedOccupation = true
                }

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                println("Nothing Selected for Occupation")
            }
        }


        relationShipStatusField.setOnClickListener{
            maritalstatusSpinner.visibility = View.VISIBLE
            relationshipStatusFieldHint.visibility = View.GONE
        }
        val maritalStatusAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.marital_status, android.R.layout.simple_spinner_item)
        maritalStatusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        maritalstatusSpinner.adapter = maritalStatusAdapter

        var isUserInteractedMarital = false

        maritalstatusSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (isUserInteractedMarital){
                    val selectedStatus = parent?.getItemAtPosition(position) as? String
                    if (selectedStatus != null) {
                        relationShipStatusField.text = selectedStatus
                        maritalstatusSpinner.visibility = View.GONE
                        relationshipStatusFieldHint.visibility = View.VISIBLE
                    }
                }
                else{
                    isUserInteractedMarital = true
                }

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                genderField.setText(profileGender)
                println("Nothing Selected for Marital Status")
            }
        }




        autoCompleteTextView = v.findViewById(R.id.annen)
        val editProfileLocationCardView = v.findViewById<CardView>(R.id.editProfileLocationCardView)

        locationField.setOnClickListener {
            autoCompleteTextView.visibility = View.VISIBLE
            editProfileLocationCardView.visibility = View.VISIBLE

            nameField.visibility = View.GONE
            genderField.visibility = View.GONE
            birthDateField.visibility = View.GONE
            birthTimeField.visibility = View.GONE
            locationField.visibility = View.GONE
            occupationField.visibility = View.GONE
            relationShipStatusField.visibility = View.GONE

            nameFieldHint.visibility = View.GONE
            genderFieldHint.visibility = View.GONE
            birthDateFieldHint.visibility = View.GONE
            birthTimeFieldHint.visibility = View.GONE
            locationFieldHint.visibility = View.GONE
            occupationFieldHint.visibility = View.GONE
            relationshipStatusFieldHint.visibility = View.GONE
            editProfileTitle.visibility = View.GONE
        }

        autoCompleteTextView.setOnItemClickListener { _, _, _, _ ->
            // AutoCompleteTextView'dan bir öğe seçildiğinde burada çalışacak kodu ekleyin
            nameField.visibility = View.VISIBLE
            genderField.visibility = View.VISIBLE
            birthDateField.visibility = View.VISIBLE
            birthTimeField.visibility = View.VISIBLE
            locationField.visibility = View.VISIBLE
            occupationField.visibility = View.VISIBLE
            relationShipStatusField.visibility = View.VISIBLE

            nameFieldHint.visibility = View.VISIBLE
            genderFieldHint.visibility = View.VISIBLE
            birthDateFieldHint.visibility = View.VISIBLE
            birthTimeFieldHint.visibility = View.VISIBLE
            locationFieldHint.visibility = View.VISIBLE
            occupationFieldHint.visibility = View.VISIBLE
            relationshipStatusFieldHint.visibility = View.VISIBLE
            editProfileTitle.visibility = View.VISIBLE

            autoCompleteTextView.visibility = View.GONE
            editProfileLocationCardView.visibility = View.GONE

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

                // Kullanıcı bir seçim yaptı mı kontrolü
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