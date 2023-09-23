package com.example.falci

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.widget.AppCompatButton
import com.example.falci.internalClasses.*
import com.example.falci.internalClasses.AuthenticationFunctions.PostJsonFunctions.postJsonWithHeader
import com.example.falci.internalClasses.dataClasses.authenticated
import com.example.falci.internalClasses.dataClasses.urls
import com.example.falci.internalClasses.dataClasses.userCompleteProfile
import com.example.falci.internalClasses.InternalFunctions.SetVisibilityFunctions.setViewGone
import com.example.falci.internalClasses.InternalFunctions.SetVisibilityFunctions.setViewGoneWithAnimation
import com.example.falci.internalClasses.InternalFunctions.SetVisibilityFunctions.setViewVisible
import com.example.falci.internalClasses.InternalFunctions.SetVisibilityFunctions.setViewVisibleWithAnimation
import org.json.JSONObject

class CompleteProfile : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_complete_profile)

        val nameNextButton = findViewById<AppCompatButton>(R.id.namePick_next_button)
        val genderNextButton = findViewById<AppCompatButton>(R.id.genderPick_next_button)
        val dateNextButton = findViewById<AppCompatButton>(R.id.datePick_next_button)
        val timeNextButton = findViewById<AppCompatButton>(R.id.timePick_next_button)
        val locationNextButton = findViewById<AppCompatButton>(R.id.locationPick_next_button)
        val relationNextButton = findViewById<AppCompatButton>(R.id.relationPick_next_button)
        val occupationNextButton = findViewById<AppCompatButton>(R.id.occupationPick_next_button)

        val namePickContainer = findViewById<RelativeLayout>(R.id.namePickContainer)
        val genderPickContainer = findViewById<RelativeLayout>(R.id.genderPickContainer)
        val datePickContainer = findViewById<RelativeLayout>(R.id.birthDatePickContainer)
        val timePickContainer = findViewById<RelativeLayout>(R.id.birthTimePickContainer)
        val locationPickContainer = findViewById<RelativeLayout>(R.id.locationPickContainer)
        val locationPickFirstLayout = findViewById<RelativeLayout>(R.id.locationPickFirstLayout)
        val locationPickSecondLayout = findViewById<RelativeLayout>(R.id.locationPickSecondLayout)
        val occupationPickContainer = findViewById<RelativeLayout>(R.id.occupationPickContainer)
        val relationPickContainer= findViewById<RelativeLayout>(R.id.relationPickContainer)

        val namePick = findViewById<EditText>(R.id.namePick)
        val genderPick = findViewById<Spinner>(R.id.genderPick)
        val datePick = findViewById<DatePicker>(R.id.datePick)
        val timePick = findViewById<TimePicker>(R.id.timePick)
        val locationPick= findViewById<AppCompatButton>(R.id.locationPick)
        val occupationPick= findViewById<Spinner>(R.id.occupationPick)
        val relationPick= findViewById<Spinner>(R.id.relationPick)

        val cityInput= findViewById<AutoCompleteTextView>(R.id.cityInput)

        var selectedRelation: String

        var isNextName = false
        var isNextGender = false
        var isNextLocation = false
        var isNextRelation = false
        var isNextOccupation = false

        fun setSpinner(spinner: Spinner, dataResId: Int, defaultText: String, onItemSelectedAction: (String) -> Unit) {
            val adapter = ArrayAdapter.createFromResource(this, dataResId, R.layout.custom_spinner_item)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter

            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    val selectedValue = parent?.getItemAtPosition(position) as? String
                    if (selectedValue != null && selectedValue != defaultText) {
                        onItemSelectedAction(selectedValue)
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    println("Nothing Selected")
                }
            }
        }

        fun setName(){
            if (namePick.text.isNotEmpty()) {
                userCompleteProfile.name = namePick.text.toString()
                isNextName = true
            }
        }

        fun setGender(){
            setSpinner(genderPick, R.array.genders, "Pick your gender") { selectedGender ->
                if (selectedGender != "Pick your gender") {
                    userCompleteProfile.gender = selectedGender
                    isNextGender = true
                }
            }
        }

        fun setDate(){
            val selectedYear = datePick.year
            val selectedMonth =  datePick.month + 1
            val selectedDay = datePick.dayOfMonth
            val selectedDate = "$selectedYear-$selectedMonth-$selectedDay"
            userCompleteProfile.date = selectedDate
            setViewGoneWithAnimation(this@CompleteProfile,datePickContainer)
            setViewVisibleWithAnimation(this@CompleteProfile,timePickContainer)
        }

        fun setTime(){
            val selectedHour = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                timePick.hour
            } else {
                TODO("VERSION.SDK_INT < M")
            }
            val selectedMinute = timePick.minute
            val selectedTime = "$selectedHour:$selectedMinute:00"
            userCompleteProfile.time = selectedTime
            setViewGoneWithAnimation(this@CompleteProfile,timePickContainer)
            setViewVisibleWithAnimation(this@CompleteProfile,locationPickContainer)
        }

        fun setLocation(){

            locationPick.setOnClickListener{
                val locationService = LocationService(this)
                locationService.initializeAutoCompleteTextView(cityInput)
                setViewGone(locationPickFirstLayout)
                setViewVisible(locationPickSecondLayout)

                cityInput.setOnItemClickListener { _, _, _, _ ->
                    setViewGoneWithAnimation(this,locationPickSecondLayout)
                    setViewVisibleWithAnimation(this,locationPickFirstLayout)
                    locationService.hideKeyboard(cityInput)
                    locationPick.text = cityInput.text.toString()
                    isNextLocation = true
                }

            }
        }

        setSpinner(relationPick, R.array.marital_status, "Medeni durumunuzu Seciniz") { selectedStatus ->
            selectedRelation = selectedStatus
            userCompleteProfile.relation = selectedRelation
            isNextRelation = true
        }

        fun setOccupation(){
            setSpinner(occupationPick, R.array.occupations, "Pick your occupation") { selectedStatus ->
                userCompleteProfile.occupation = selectedStatus
                isNextOccupation = true
            }
        }

        fun setRelation(){
            setSpinner(relationPick, R.array.marital_status, "Medeni durumunuzu Seciniz") { selectedStatus ->
                selectedRelation = selectedStatus
                userCompleteProfile.relation = selectedRelation
                isNextRelation = true
            }
        }

        fun formatDateAndTime(date: String, time: String): String {
            return ("$date $time +0000")
        }

        fun completeProfile(){
            val formattedDate = formatDateAndTime(userCompleteProfile.date, userCompleteProfile.time)

            val zodiacInfoJson = AuthenticationFunctions.CreateJsonObject.createJsonObject(
                "name" to userCompleteProfile.name,
                "location" to userCompleteProfile.location,
                "birthDay" to formattedDate,
                "gender" to userCompleteProfile.gender,
                "occupation" to userCompleteProfile.occupation
            )

            val infoJson = AuthenticationFunctions.CreateJsonObject.createJsonObject(
                "zodiacInfo" to zodiacInfoJson,
                "relationshipStatus" to userCompleteProfile.relation
            )

            val completeProfileJSON = AuthenticationFunctions.CreateJsonObject.createJsonObject(
                "info" to infoJson
            )

            println("complete profile bilgileri jsonu: $completeProfileJSON")

            postJsonWithHeader(
                urls.completeProfileURL,
                completeProfileJSON,
                registerTokensDataClass.registerAccessToken
            )
            { responseBody, exception ->
                if (exception != null) {
                    println("Error: ${exception.message}")
                } else {
                    val responseJson = responseBody?.let { it1 -> JSONObject(it1) }
                    val detail = responseJson?.optString("detail")
                    println(detail)
                }
            }
        }

        setViewVisibleWithAnimation(this,namePickContainer)

        nameNextButton.setOnClickListener{
            setName()
            if (isNextName){
                setViewGoneWithAnimation(this,namePickContainer)
                setViewVisibleWithAnimation(this,genderPickContainer)
                setGender()
            }

        }

        genderNextButton.setOnClickListener{
            if (isNextGender){
                setViewGoneWithAnimation(this@CompleteProfile,genderPickContainer)
                setViewVisibleWithAnimation(this@CompleteProfile,datePickContainer)
            }else  {
                setViewGoneWithAnimation(this,genderPick)
                setViewVisibleWithAnimation(this,genderPick)
            }
        }

        dateNextButton.setOnClickListener{
            setDate()
        }

        timeNextButton.setOnClickListener{
            setTime()
            setLocation()
        }

        locationNextButton.setOnClickListener{
            if (isNextLocation){
                setViewGoneWithAnimation(this,locationPickContainer)
                setViewVisibleWithAnimation(this,occupationPickContainer)
                setOccupation()
            }else {
                setViewGoneWithAnimation(this,locationPick)
                setViewVisibleWithAnimation(this,locationPick)
            }
        }

        occupationNextButton.setOnClickListener{
            if (isNextOccupation){
                setViewGoneWithAnimation(this@CompleteProfile,occupationPickContainer)
                setViewVisibleWithAnimation(this@CompleteProfile,relationPickContainer)
                setRelation()
            }else  {
                setViewGoneWithAnimation(this,occupationPick)
                setViewVisibleWithAnimation(this,occupationPick)
            }
        }

        relationNextButton.setOnClickListener{
            println(userCompleteProfile)
            if (isNextRelation){
                completeProfile()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                authenticated.isFromSignIn = true
            }else  {
                setViewGoneWithAnimation(this,relationPick)
                setViewVisibleWithAnimation(this,relationPick)
            }
        }
    }
}
