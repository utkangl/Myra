package com.example.falci

import android.app.ActivityOptions
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.widget.AppCompatButton
import com.airbnb.lottie.LottieAnimationView
import com.example.falci.internalClasses.*
import com.example.falci.internalClasses.AuthenticationFunctions.CreateJsonObject.createJsonObject
import com.example.falci.internalClasses.AuthenticationFunctions.PostJsonFunctions.postJsonWithHeader
import com.example.falci.internalClasses.HoroscopeFunctions.getLoveHoroscope
import com.example.falci.internalClasses.InternalFunctions.SetVisibilityFunctions.setViewGone
import com.example.falci.internalClasses.InternalFunctions.SetVisibilityFunctions.setViewGoneWithAnimation
import com.example.falci.internalClasses.InternalFunctions.SetVisibilityFunctions.setViewVisible
import com.example.falci.internalClasses.InternalFunctions.SetVisibilityFunctions.setViewVisibleWithAnimation
import com.example.falci.internalClasses.dataClasses.*
import org.json.JSONObject

class CompleteProfile : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_complete_profile)

        val miraSpeechBubbleContainer = findViewById<RelativeLayout>(R.id.mira_speech_bubble_container)
        val completeProfilePickersContainer = findViewById<RelativeLayout>(R.id.completeProfilePickersContainer)

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

        val thinkingAnimation = findViewById<LottieAnimationView>(R.id.thinkingAnimation)

        val namePick = findViewById<EditText>(R.id.namePick)
        val genderPick = findViewById<Spinner>(R.id.genderPick)
        val datePick = findViewById<DatePicker>(R.id.datePick)
        val timePick = findViewById<TimePicker>(R.id.timePick)
        val locationPick= findViewById<AppCompatButton>(R.id.locationPick)
        val occupationPick= findViewById<Spinner>(R.id.occupationPick)
        val relationPick= findViewById<Spinner>(R.id.relationPick)

        val cityInput= findViewById<AutoCompleteTextView>(R.id.cityInput)


        var isNextName = false
        var isNextGender = false
        var isNextLocation = false
        var isNextRelation = false
        var isNextOccupation = false

        // multiple spinner will be used in that screen, this function aims to reduce repeating code
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

         // let user to go on to genderpick if input lasts longer than 2 characters else toast error
        // and  set name field of CompleteProfileUserDataClass's instance w/ user's name input
        fun setName(){
             if (isFromLoveHoroscope && namePick.text.length >= 2){
                 partnerProfile.partnerName = namePick.text.toString()
                 isNextName = true
             }else{
                 if (namePick.text.length >= 2) {
                     userCompleteProfile.name = namePick.text.toString()
                     isNextName = true
                 } else {
                     this.runOnUiThread {
                         Toast.makeText(this, "Your name must be at least 2 characters long", Toast.LENGTH_SHORT).show()
                     }
                 }
             }


        }

        // set gender field of CompleteProfileUserDataClass's instance w/ user's gender input
        fun setGender(){
            setSpinner(genderPick, R.array.genders, "Pick your gender") { selectedGender ->
                if (selectedGender != "Pick your gender") {
                    if (isFromLoveHoroscope){
                        partnerProfile.partnerGender = selectedGender
                    }else{
                        userCompleteProfile.gender = selectedGender
                    }
                    isNextGender = true
                }
            }
        }

        // set date field of CompleteProfileUserDataClass's instance w/ user's date input
        fun setDate(){
            val selectedYear = datePick.year
            val selectedMonth =  datePick.month + 1
            val selectedDay = datePick.dayOfMonth
            val selectedDate = "$selectedYear-$selectedMonth-$selectedDay"
            if (isFromLoveHoroscope){
                partnerProfile.partnerDate = selectedDate
            }else{
                userCompleteProfile.date = selectedDate
            }
            setViewGoneWithAnimation(this@CompleteProfile,datePickContainer)
            setViewVisibleWithAnimation(this@CompleteProfile,timePickContainer)
        }

        // set time field of CompleteProfileUserDataClass's instance w/ user's time input
        fun setTime(){
            val selectedHour = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                timePick.hour
            } else {
                TODO("VERSION.SDK_INT < M")
            }
            val selectedMinute = timePick.minute
            val selectedTime = "$selectedHour:$selectedMinute:00"
            if (isFromLoveHoroscope)partnerProfile.partnerTime = selectedTime; else userCompleteProfile.time = selectedTime

            setViewGoneWithAnimation(this@CompleteProfile,timePickContainer)
            setViewVisibleWithAnimation(this@CompleteProfile,locationPickContainer)
        }

        // set location field of CompleteProfileUserDataClass's instance w/ user's location input
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

        // set occupation field of CompleteProfileUserDataClass's instance w/ user's occupation input
        fun setOccupation(){
            setSpinner(occupationPick, R.array.occupations, "Pick your occupation") { selectedOccupation ->
                if (isFromLoveHoroscope) {partnerProfile.partnerOccupation = selectedOccupation}
                else {userCompleteProfile.occupation = selectedOccupation}
                isNextOccupation = true
            }
        }

        // set relation field of CompleteProfileUserDataClass's instance w/ user's relation input
        fun setRelation(){
            setSpinner(relationPick, R.array.marital_status, "Medeni durumunuzu Seciniz") { selectedRelation ->
                userCompleteProfile.relation = selectedRelation
                isNextRelation = true
            }
        }

        // this function will put date and time inputs in one variable
        fun formatDateAndTime(date: String, time: String): String {
            return ("$date $time +0000")
        }


        // create Json, fill with inputs, post it, handle response with response code,toast detail
        fun completeProfile(){

             // create the json object that will be posted as completeProfileJson
            //fill json with the user inputs, format date and time into one variable before fill
            val formattedDate = formatDateAndTime(userCompleteProfile.date, userCompleteProfile.time)
            val zodiacInfoJson = createJsonObject(
                "name" to userCompleteProfile.name,
                "location" to userCompleteProfile.location,
                "birthDay" to formattedDate,
                "gender" to userCompleteProfile.gender,
                "occupation" to userCompleteProfile.occupation
            )

            val infoJson = createJsonObject(
                "zodiacInfo" to zodiacInfoJson,
                "relationshipStatus" to userCompleteProfile.relation
            )

            val completeProfileJSON = createJsonObject(
                "info" to infoJson
            )

               // post complete profile json, handle response with response codes, toast details
              // if response code is success set isFromSignin true and navigate to main activity
             // main activity will directly navigate user to login screen when isFromSignIn is true
            // if response code is error just toast the error
            postJsonWithHeader(urls.completeProfileURL, completeProfileJSON, this)
            { responseBody, _ ->
                val responseJson = responseBody?.let { it1 -> JSONObject(it1) }
                val detail = responseJson?.optString("detail")

                if (statusCode == 200){
                    this.runOnUiThread{
                        Toast.makeText(this, detail, Toast.LENGTH_SHORT).show()
                        val options = ActivityOptions.makeCustomAnimation(this, R.anim.activity_slide_down, 0)
                        val intent = Intent(this, MainActivity::class.java); startActivity(intent,options.toBundle())
                        authenticated.isFromSignIn = true
                    }
                }
                if (statusCode == 208){
                    this.runOnUiThread{
                        Toast.makeText(this, detail, Toast.LENGTH_SHORT).show()
                    }
                }
                if (statusCode == 400){
                    this.runOnUiThread{
                        Toast.makeText(this, detail, Toast.LENGTH_SHORT).show()
                    }
                }
                if (statusCode == 503){
                    this.runOnUiThread{
                        Toast.makeText(this, detail, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        // fill partner profile json with partner's data class
        fun createLookupUserJson(){
            val formattedDate = formatDateAndTime(partnerProfile.partnerDate, partnerProfile.partnerTime)
            lookupUserJson = createJsonObject(
                "name" to partnerProfile.partnerName,
                "gender" to partnerProfile.partnerGender,
                "birth_day" to formattedDate,
                "birth_place" to partnerProfile.partnerLocation,
                "occupation" to partnerProfile.partnerOccupation
            )
        }

        // when activity is created directly set namePick view visible
        setViewVisibleWithAnimation(this,namePickContainer)

        // call setName if success change to genderPick, error state is handled by setName function
        nameNextButton.setOnClickListener{
            setName()
            if (isNextName){
                setViewGoneWithAnimation(this,namePickContainer)
                setViewVisibleWithAnimation(this,genderPickContainer)
                setGender()
            }
        }

        // allow user to go on to datePick if gender has chosen, else wise toast error
        genderNextButton.setOnClickListener{
            if (isNextGender){
                setViewGoneWithAnimation(this@CompleteProfile,genderPickContainer)
                setViewVisibleWithAnimation(this@CompleteProfile,datePickContainer)
            } else {
                this.runOnUiThread {
                    Toast.makeText(this, "You should pick a gender before you go on", Toast.LENGTH_SHORT).show()
                    setViewGoneWithAnimation(this,genderPick)
                    setViewVisibleWithAnimation(this,genderPick)
                }
            }
        }

        // call setDate function to set the date as user has chosen
        dateNextButton.setOnClickListener{
            setDate()
        }

        // set time and location by calling functions
        timeNextButton.setOnClickListener{
            setTime()
            setLocation()
        }

        // allow user to go on to occupationPick if location has chosen, else wise toast error
        locationNextButton.setOnClickListener{
            if (isNextLocation){
                setViewGoneWithAnimation(this,locationPickContainer)
                setViewVisibleWithAnimation(this,occupationPickContainer)
                setOccupation()
            }else {
                this.runOnUiThread {
                    Toast.makeText(this, "You should choose your birth location before you go on", Toast.LENGTH_SHORT).show()
                    setViewGoneWithAnimation(this,locationPick)
                    setViewVisibleWithAnimation(this,locationPick)
                }
            }
        }

        // allow user to go on to relationship status pick if occupation has chosen else toast error
        occupationNextButton.setOnClickListener{
            // if completing the profile of lookup user, call get horoscope function for love
            if (isFromLoveHoroscope && isNextOccupation){
                isNextOccupation = false
                createLookupUserJson()
                println(lookupUserJson)
                setViewGone(completeProfilePickersContainer, miraSpeechBubbleContainer)
                getLoveHoroscope(thinkingAnimation, this)

            // if completing the user's profile
            }else{
                // if occupation is picked, go on to relationship status
                if (isNextOccupation){
                    setViewGoneWithAnimation(this@CompleteProfile,occupationPickContainer)
                    setViewVisibleWithAnimation(this@CompleteProfile,relationPickContainer)
                    setRelation()
                }else {
                    // if occupation is not picked, toast choose your occupation
                    this.runOnUiThread {
                        Toast.makeText(this, "You should choose your occupation before you go on", Toast.LENGTH_SHORT).show()
                        setViewGoneWithAnimation(this,occupationPick)
                        setViewVisibleWithAnimation(this,occupationPick)
                    }
                }
            }

        }

        // if relation has chosen, call completeProfile function else wise toast error
        relationNextButton.setOnClickListener{
            println(userCompleteProfile)
            if (isNextRelation){
                completeProfile()
            }else {
                this.runOnUiThread {
                    Toast.makeText(this, "You should choose your marital status before you go on", Toast.LENGTH_SHORT).show()
                    setViewGoneWithAnimation(this,relationPick)
                    setViewVisibleWithAnimation(this,relationPick)
                }
            }
        }
    }
}
