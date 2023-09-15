package com.example.falci

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.widget.AppCompatButton
import com.example.falci.internalClasses.*
import com.example.falci.internalClasses.InternalFunctions.SetVisibilityFunctions.setViewGone
import com.example.falci.internalClasses.InternalFunctions.SetVisibilityFunctions.setViewGoneWithAnimation
import com.example.falci.internalClasses.InternalFunctions.SetVisibilityFunctions.setViewVisible
import com.example.falci.internalClasses.InternalFunctions.SetVisibilityFunctions.setViewVisibleWithAnimation
import org.json.JSONObject

class CompleteProfile : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_complete_profile)


            var containerIndex = 1

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


            var selectedGender = "Pick your gender"
            var selectedRelation = "Medeni durumunuzu Seciniz"

            var isNextName = false
            var isNextGender = false
            var isNextLocation = false
            var isNextRelation = false
            var isNextOccupation = false

            fun setName(){
                if (namePick.text.isNotEmpty()) {
                    com.example.falci.internalClasses.userCompleteProfile.name = namePick.text.toString()
                    isNextName = true
                }
            }

            fun setGender(){
                val adapter = ArrayAdapter.createFromResource(this, R.array.genders, android.R.layout.simple_spinner_item)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                genderPick.adapter = adapter

                genderPick.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long)
                    {
                        val selectedgender = parent?.getItemAtPosition(position) as? String
                        if (selectedgender != null) {
                            selectedGender = selectedgender
                            if (selectedgender != "Pick your gender"){
                                com.example.falci.internalClasses.userCompleteProfile.gender = selectedGender
                                isNextGender = true
                            }
                        }
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        println("Nothing Selected for Gender")
                    }
                }

            }

            fun setDate(){
                val selectedYear = datePick.year
                val selectedMonth =  datePick.month + 1
                val selectedDay = datePick.dayOfMonth
                val selectedDate = "$selectedYear-$selectedMonth-$selectedDay"
                com.example.falci.internalClasses.userCompleteProfile.date = selectedDate
                setViewGoneWithAnimation(this@CompleteProfile,datePickContainer)
                setViewVisibleWithAnimation(this@CompleteProfile,timePickContainer)
            }

            fun setTime(){
                val selectedHour = timePick.hour
                val selectedMinute = timePick.minute
                val selectedTime = "$selectedHour:$selectedMinute:00"
                com.example.falci.internalClasses.userCompleteProfile.time = selectedTime
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


            fun setOccupation(){

                val adapter = ArrayAdapter.createFromResource(this, R.array.occupations, android.R.layout.simple_spinner_item)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                occupationPick.adapter = adapter

                occupationPick.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        val selectedStatus = parent?.getItemAtPosition(position) as? String
                        if (selectedStatus != null) {
                            com.example.falci.internalClasses.userCompleteProfile.occupation = selectedStatus
                            isNextOccupation = true
                        }
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        println("Nothing Selected for Occupation")
                    }

                }
            }


            fun setRelation(){
                val adapter = ArrayAdapter.createFromResource(this, R.array.marital_status, android.R.layout.simple_spinner_item)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

                relationPick.adapter = adapter

                relationPick.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        val selectedStatus = parent?.getItemAtPosition(position) as? String
                        if (selectedStatus != null && selectedStatus != "Medeni durumunuzu Seciniz") {
                            selectedRelation = selectedStatus
                            com.example.falci.internalClasses.userCompleteProfile.relation = selectedRelation
                            isNextRelation = true
                        }

                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        println("Nothing Selected for Marital Status")
                    }
                }
            }

        fun formatDateAndTime(date: String, time: String): String {
            return ("$date $time +0000")
        }
        fun completeProfile(){
            val formattedDate = formatDateAndTime(com.example.falci.internalClasses.userCompleteProfile.date, com.example.falci.internalClasses.userCompleteProfile.time)

            val zodiacInfoJson = AuthenticationFunctions.CreateJsonObject.createJsonObject(
                "name" to com.example.falci.internalClasses.userCompleteProfile.name,
                "location" to com.example.falci.internalClasses.userCompleteProfile.location,
                "birthDay" to formattedDate,
                "gender" to com.example.falci.internalClasses.userCompleteProfile.gender,
                "occupation" to com.example.falci.internalClasses.userCompleteProfile.occupation
            )

            val infoJson = AuthenticationFunctions.CreateJsonObject.createJsonObject(
                "zodiacInfo" to zodiacInfoJson,
                "relationshipStatus" to com.example.falci.internalClasses.userCompleteProfile.relation
            )

            val completeProfileJSON = AuthenticationFunctions.CreateJsonObject.createJsonObject(
                "info" to infoJson
            )

            println("complete profile bilgileri jsonu: $completeProfileJSON")

            AuthenticationFunctions.PostJsonFunctions.postJsonWithHeader(
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
            }
        }

        occupationNextButton.setOnClickListener{
            if (isNextOccupation){
                setViewGoneWithAnimation(this@CompleteProfile,occupationPickContainer)
                setViewVisibleWithAnimation(this@CompleteProfile,relationPickContainer)
                setRelation()
            }
        }

        relationNextButton.setOnClickListener{
            println(com.example.falci.internalClasses.userCompleteProfile)
            if (isNextRelation){
                completeProfile()
                val loginFragment = Loginfragment()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                authenticated.isFromSignIn = true
            }
        }
    }
}