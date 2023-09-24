package com.example.falci

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatButton
import androidx.cardview.widget.CardView
import org.json.JSONObject
import com.example.falci.internalClasses.InternalFunctions.UpdateProfileFieldIfChanged.updateProfileFieldIfChanged
import com.example.falci.internalClasses.InternalFunctions.SetVisibilityFunctions.setViewVisible
import com.example.falci.internalClasses.InternalFunctions.SetVisibilityFunctions.setViewGone
import com.example.falci.internalClasses.InternalFunctions.AddTextWatcher.addTextWatcher
import com.example.falci.internalClasses.InternalFunctions.SetupFieldClickListener.setupFieldClickListener
import com.example.falci.internalClasses.InternalFunctions.SetupSpinnerAndField.setupSpinnerAndField
import com.example.falci.internalClasses.InternalFunctions.TimeFormatFunctions.separateBirthDate
import com.example.falci.internalClasses.InternalFunctions.TimeFormatFunctions.separateBirthTime
import com.example.falci.internalClasses.InternalFunctions.UpdateProfileFieldIfChanged.updateBirthDayIfChanged
import com.example.falci.internalClasses.LocationService
import com.example.falci.internalClasses.ProfileFunctions.ProfileFunctions.putEditProfileJson
import com.example.falci.internalClasses.dataClasses.urls

val editProfileJson = JSONObject()

class EditProfileFragment : Fragment() {

    private lateinit var locationService: LocationService

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_editprofile, container, false)

        val nameField = v.findViewById<TextView>(R.id.nameEditText)
        val genderField = v.findViewById<TextView>(R.id.genderEditText)
        val birthDateField = v.findViewById<TextView>(R.id.birthDateEditText)
        val birthTimeField = v.findViewById<TextView>(R.id.birthTimeEditText)
        val locationField = v.findViewById<TextView>(R.id.locationEditText)
        val occupationField = v.findViewById<TextView>(R.id.occupationEditText)
        val relationShipStatusField = v.findViewById<TextView>(R.id.relationshipStatusEditText)
        val savebutton = v.findViewById<AppCompatButton>(R.id.savebutton)
        val savedatebutton = v.findViewById<AppCompatButton>(R.id.savedatebutton)
        val backArrow = v.findViewById<ImageView>(R.id.back_arrow)
        val editProfileDatePicker = v.findViewById<DatePicker>(R.id.editProfileDatePicker)
        val editProfileTimePicker = v.findViewById<TimePicker>(R.id.editProfileTimePicker)
        val genderPickSpinner = v.findViewById<Spinner>(R.id.editProfile_genderpick_spinner)
        val occupationSpinner = v.findViewById<Spinner>(R.id.editProfile_OccupationPickSpinner)
        val maritalStatusSpinner = v.findViewById<Spinner>(R.id.editProfile_marital_status_spinner)

        val genderFieldHint = v.findViewById<TextView>(R.id.genderFieldHint)
        val occupationFieldHint = v.findViewById<TextView>(R.id.occupationFieldHint)
        val relationshipStatusFieldHint = v.findViewById<TextView>(R.id.relationshipStatusFieldHint)
        val editProfileGeneralLayout = v.findViewById<RelativeLayout>(R.id.editProfileGeneralLayout)

        nameField.text = userProfile.first_name
        genderField.text = userProfile.gender
        birthDateField.text = separateBirthDate(userProfile.birth_day)
        birthTimeField.text = separateBirthTime(userProfile.birth_day)
        locationField.text = userProfile.birth_place
        occupationField.text = userProfile.occupation
        relationShipStatusField.text = userProfile.relationship_status

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
            updateProfileFieldIfChanged("first_name", nameField, editProfileJson, userProfile.first_name)
            updateProfileFieldIfChanged("gender", genderField, editProfileJson, userProfile.gender)
            updateBirthDayIfChanged    ("birthDay", birthDateField, birthTimeField, userProfile, editProfileJson)
            updateProfileFieldIfChanged("location", locationField, editProfileJson, userProfile.birth_place)
            updateProfileFieldIfChanged("occupation", occupationField, editProfileJson, userProfile.occupation)
            updateProfileFieldIfChanged("relationshipStatus", relationShipStatusField, editProfileJson, userProfile.relationship_status)

            // Save changes
            userProfile.apply {
                first_name = nameField.text.toString()
                gender = genderField.text.toString()
                birth_day = birthDateField.text.toString()
                birth_place = locationField.text.toString()
                occupation = occupationField.text.toString()
                relationship_status = relationShipStatusField.text.toString()
            }

            putEditProfileJson(
                url = urls.editProfileURL,
                json = editProfileJson,
                accessToken = loginTokens.loginAccessToken
            )
            {_,_->}

            setViewGone(savebutton)
            parentFragmentManager.popBackStack()
        }

        backArrow.setOnClickListener { parentFragmentManager.popBackStack() }

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

        val genderAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.genders, R.layout.custom_spinner_item)
        val isUserInteractedGender = false
        setupSpinnerAndField(genderPickSpinner, genderFieldHint,genderAdapter,isUserInteractedGender){selectedGender -> genderField.text = selectedGender}

        val occupationAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.occupations, R.layout.custom_spinner_item)
        val isUserInteractedOccupation = false
        setupSpinnerAndField(occupationSpinner, occupationFieldHint,occupationAdapter,isUserInteractedOccupation){selectedOccupation -> occupationField.text = selectedOccupation}

        val maritalStatusAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.marital_status, R.layout.custom_spinner_item)
        val isUserInteractedMarital = false
        setupSpinnerAndField(maritalStatusSpinner, relationshipStatusFieldHint,maritalStatusAdapter,isUserInteractedMarital){selectedMaritalStatus -> occupationField.text = selectedMaritalStatus}


        val cityInput = v.findViewById<AutoCompleteTextView>(R.id.searchCity)
        val editProfileLocationCardView = v.findViewById<CardView>(R.id.editProfileLocationCardView)

        this.locationService = LocationService(requireContext())
        locationService.initializeAutoCompleteTextView(cityInput)

        locationField.setOnClickListener {
            setViewGone(editProfileGeneralLayout)
            setViewVisible(editProfileLocationCardView)
        }

        cityInput.setOnItemClickListener { _, _, _, _ ->
            setViewGone(editProfileLocationCardView)
            setViewVisible(editProfileGeneralLayout)
            locationField.text = cityInput.text.toString()
            locationService.hideKeyboard(requireView())
        }

        return v
    }
}