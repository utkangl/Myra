package com.example.falci

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import org.json.JSONObject
import com.example.falci.LoginSignupActivity.ProfileFunctions.putEditProfileJson
    val editProfileJson = JSONObject()

class EditProfileFragment : Fragment() {

    lateinit var nameField: TextView
    lateinit var genderField: TextView
    lateinit var birthDateField: TextView
    lateinit var birthTimeField: TextView
    lateinit var locationField: TextView
    lateinit var occupationField: TextView
    lateinit var relationShipStatusField: TextView
    lateinit var savebutton: Button
    lateinit var backArrow: ImageView
    lateinit var editProfileDatePicker: DatePicker
    lateinit var editProfileTimePicker: TimePicker
    lateinit var genderPickSpinner: Spinner
    lateinit var occupationSpinner: Spinner
    lateinit var maritalstatusSpinner: Spinner

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
        backArrow = v.findViewById(R.id.back_arrow)
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
        birthTimeField.setText(profileBirth_day)
        locationField.setText(profileBirth_place)
        occupationField.setText(profileOccupation)
        relationShipStatusField.setText(profileRelationshipStatus)

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
                editProfileJson.put("birthDay", birthDateField.text.toString())
            }
            if (birthTimeField.text.toString() != profileBirth_time) {
                val edittedDate = birthTimeField.text.toString().replace("Z", " ").replace("T", " ")
                val edittedSik = "$edittedDate +0000"
                editProfileJson.put("birthDay", edittedSik)
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
        }

        genderField.setOnClickListener{
            genderPickSpinner.visibility = View.VISIBLE
            genderFieldHint.visibility = View.GONE
        }
        val genderAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.genders, android.R.layout.simple_spinner_item)
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        genderPickSpinner.adapter = genderAdapter
        genderPickSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedgender = parent?.getItemAtPosition(position) as? String
                genderField.text = selectedgender
                genderPickSpinner.visibility = View.GONE
                genderFieldHint.visibility = View.VISIBLE
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
        occupationSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedStatus = parent?.getItemAtPosition(position) as? String
                if (selectedStatus != null) {
                    occupationField.text = selectedStatus
                }
                occupationSpinner.visibility = View.GONE
                occupationFieldHint.visibility = View.VISIBLE
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
        maritalstatusSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedStatus = parent?.getItemAtPosition(position) as? String
                if (selectedStatus != null) {
                    relationShipStatusField.text = selectedStatus
                    maritalstatusSpinner.visibility = View.GONE
                    relationshipStatusFieldHint.visibility = View.VISIBLE
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                genderField.setText(profileGender)
                println("Nothing Selected for Marital Status")
            }
        }


        editProfileDatePicker.setOnClickListener {
            val selectedYear = editProfileDatePicker.year
            val selectedMonth =  editProfileDatePicker.month + 1
            val selectedDay = editProfileDatePicker.dayOfMonth

            val editProfileSelectedDate = "$selectedYear-$selectedMonth-$selectedDay"
            birthDateField.setText(editProfileSelectedDate)

            println(editProfileSelectedDate)

        }

        return v
    }
}