package com.utkangul.falci

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.AppCompatButton
import androidx.cardview.widget.CardView
import org.json.JSONObject
import com.utkangul.falci.internalClasses.InternalFunctions.UpdateProfileFieldIfChanged.updateProfileFieldIfChanged
import com.utkangul.falci.internalClasses.InternalFunctions.SetVisibilityFunctions.setViewVisible
import com.utkangul.falci.internalClasses.InternalFunctions.SetVisibilityFunctions.setViewGone
import com.utkangul.falci.internalClasses.InternalFunctions.AddTextWatcher.addTextWatcher
 import com.utkangul.falci.internalClasses.InternalFunctions.SetVisibilityFunctions.setViewInvisible
import com.utkangul.falci.internalClasses.InternalFunctions.SetVisibilityFunctions.setViewVisibleWithAnimation
import com.utkangul.falci.internalClasses.InternalFunctions.SpinnerFunctions.setSpinner
import com.utkangul.falci.internalClasses.InternalFunctions.TimeFormatFunctions.convertTimestampToDateTime
import com.utkangul.falci.internalClasses.InternalFunctions.UpdateProfileFieldIfChanged.updateBirthDayIfChanged
import com.utkangul.falci.internalClasses.InternalFunctions.SpinnerFunctions.setupFieldClickListener
import com.utkangul.falci.internalClasses.LocationService
import com.utkangul.falci.internalClasses.ProfileFunctions.ProfileFunctions.putEditProfileJson
import com.utkangul.falci.internalClasses.dataClasses.controlVariables
import com.utkangul.falci.internalClasses.dataClasses.urls
import com.utkangul.falci.internalClasses.dataClasses.userProfile
import com.utkangul.falci.internalClasses.statusCode


val editProfileJson = JSONObject()
class EditProfileFragment : Fragment() {

    private lateinit var locationService: LocationService

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

        println(userProfile)



        val (formattedDate, formattedTime) = convertTimestampToDateTime(userProfile.birth_day!!.toLong())
        nameField.text = userProfile.first_name
        genderField.text = userProfile.gender
        birthDateField.text = formattedDate
        birthTimeField.text = formattedTime
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
            userProfile.first_name?.let { it1 -> updateProfileFieldIfChanged("first_name", nameField, editProfileJson, it1) }
            userProfile.gender?.let { it1 -> updateProfileFieldIfChanged("gender", genderField, editProfileJson, it1) }
            updateBirthDayIfChanged ("birthDay", birthDateField, birthTimeField, userProfile, editProfileJson,requireActivity(),requireContext())
            userProfile.birth_place?.let { it1 -> updateProfileFieldIfChanged("location", locationField, editProfileJson, it1) }
            userProfile.occupation?.let { it1 -> updateProfileFieldIfChanged("occupation", occupationField, editProfileJson, it1) }
            userProfile.relationship_status?.let { it1 -> updateProfileFieldIfChanged("relationshipStatus", relationShipStatusField, editProfileJson, it1)
            }

            // Save changes
            userProfile.apply {
                first_name = nameField.text.toString()
                gender = genderField.text.toString()
               // birth_day = birthDateField.text.toString()
                birth_place = locationField.text.toString()
                occupation = occupationField.text.toString()
                relationship_status = relationShipStatusField.text.toString()
            }

            println("yolladigim json $editProfileJson")
            putEditProfileJson(urls.editProfileURL, editProfileJson, requireContext()) {_,_->
                if (statusCode == 200){
                    requireActivity().runOnUiThread { setViewGone(savebutton) }
                    parentFragmentManager.popBackStack()
                    controlVariables.getProfileAgain = true
                }
                else println(statusCode)
            }
        }

        backArrow.setOnClickListener { parentFragmentManager.popBackStack() }

        birthDateField.setOnClickListener {
            setViewGone(editProfileGeneralLayout)
            setViewVisible(editProfileDatePicker, savedatebutton)
            controlVariables.inPicker = true
            savedatebutton.setOnClickListener {
                val selectedYear = editProfileDatePicker.year
                val selectedMonth = editProfileDatePicker.month + 1
                val selectedDay = editProfileDatePicker.dayOfMonth
                val editProfileSelectedDate = "$selectedYear-$selectedMonth-$selectedDay"
                birthDateField.text = editProfileSelectedDate
                println(birthDateField.text)
                setViewVisible(editProfileGeneralLayout)
                setViewGone(editProfileDatePicker, savedatebutton)
                controlVariables.inPicker = false
            }
        }
        birthTimeField.setOnClickListener {
            setViewGone(editProfileGeneralLayout)
            setViewVisible(editProfileTimePicker,savedatebutton)
            controlVariables.inPicker = true

            savedatebutton.setOnClickListener {
                val selectedHour = editProfileTimePicker.hour
                val selectedMinute = editProfileTimePicker.minute
                val editProfileSelectedTime = "$selectedHour:$selectedMinute:00"
                birthTimeField.text = editProfileSelectedTime
                println(birthTimeField.text)
                setViewGone(editProfileTimePicker,savedatebutton)
                setViewVisible(editProfileGeneralLayout)
                controlVariables.inPicker = false
            }
        }

        setupFieldClickListener(genderField, genderPickSpinner, genderFieldHint)
        setupFieldClickListener(occupationField, occupationSpinner, occupationFieldHint)
        setupFieldClickListener(relationShipStatusField, maritalStatusSpinner, relationshipStatusFieldHint)


        setSpinner(requireContext(),genderPickSpinner, genderFieldHint, R.array.genders, "Pick your gender")
        {selectedGender -> genderField.text = selectedGender}

        setSpinner(requireContext(),occupationSpinner, occupationFieldHint, R.array.occupations, "Pick your occupation")
        {selectedOccupation -> occupationField.text = selectedOccupation}

        setSpinner(requireContext(),maritalStatusSpinner, relationshipStatusFieldHint, R.array.marital_status, "Medeni durumunuzu Seciniz")
        {selectedMaritalStatus -> relationShipStatusField.text = selectedMaritalStatus}

        val cityInput = v.findViewById<AutoCompleteTextView>(R.id.searchCity)
        val editProfileLocationCardView = v.findViewById<CardView>(R.id.editProfileLocationCardView)

        this.locationService = LocationService(requireContext())
        locationService.initializeAutoCompleteTextView(cityInput)

        locationField.setOnClickListener {
            controlVariables.inLocationPickCard = true
            setViewGone(editProfileGeneralLayout)
            setViewVisible(editProfileLocationCardView)
        }

        cityInput.setOnItemClickListener { _, _, _, _ ->
            setViewGone(editProfileLocationCardView)
            setViewVisible(editProfileGeneralLayout)
            locationField.text = cityInput.text.toString()
            controlVariables.inLocationPickCard = false
            locationService.hideKeyboard(requireView())
        }

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (controlVariables.inLocationPickCard) {
                    setViewInvisible(editProfileLocationCardView)
                    setViewVisibleWithAnimation(requireContext(),editProfileGeneralLayout)
                    controlVariables.inLocationPickCard = false
                }
                else if (controlVariables.inPicker) {
                    setViewInvisible(editProfileDatePicker, editProfileTimePicker,savedatebutton)
                    setViewVisibleWithAnimation(requireContext(),editProfileGeneralLayout)
                    controlVariables.inPicker = false
                } else {
                    remove() // Geri çağrıyı kaldır
                    isEnabled = false // Geri çağrıyı devre dışı bırak
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                    controlVariables.inLocationPickCard = false
                }
            }
        }
        callback.isEnabled = true
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        return v
    }
}