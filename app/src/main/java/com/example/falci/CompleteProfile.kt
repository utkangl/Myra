package com.example.falci

import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.OnBackPressedCallback
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

var navigateToSignUp = false

class CompleteProfile : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_complete_profile)

        val miraSpeechBubbleContainer = findViewById<RelativeLayout>(R.id.mira_speech_bubble_container)
        val completeProfilePickersContainer = findViewById<RelativeLayout>(R.id.completeProfilePickersContainer)

        val nextButton = findViewById<AppCompatButton>(R.id.next_button)
        val timePickNextButton = findViewById<AppCompatButton>(R.id.timePickNextButton)

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

        val star1 = findViewById<StarShape>(R.id.star1)
        val star2 = findViewById<StarShape>(R.id.star2)
        val star3 = findViewById<StarShape>(R.id.star3)
        val star4 = findViewById<StarShape>(R.id.star4)
        val star5 = findViewById<StarShape>(R.id.star5)
        val star6 = findViewById<StarShape>(R.id.star6)
        val star7 = findViewById<StarShape>(R.id.star7)


        var allowNext = false


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

        var step = 0

        completeProfilePickersContainer.setOnClickListener{ println(step); println(allowNext) }
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

         // let user to go on to genderpick if input lasts longer than 2 characters else toast error
        // and  set name field of CompleteProfileUserDataClass's instance w/ user's name input
         fun setName(){
             allowNext = false
             if (isFromLoveHoroscope && namePick.text.length >= 2){
                 partnerProfile.partnerName = namePick.text.toString()
                 allowNext = true; star1.startColorAnimation()
             }else{
                 if (namePick.text.length >= 2) {
                     userCompleteProfile.name = namePick.text.toString()
                     allowNext = true; star1.startColorAnimation()
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
                    if (isFromLoveHoroscope) partnerProfile.partnerGender = selectedGender; allowNext = true; star2.startColorAnimation()
                    if (!isFromLoveHoroscope) userCompleteProfile.gender = selectedGender; allowNext = true; star2.startColorAnimation()

                } else this.runOnUiThread { Toast.makeText(this, "pick your gender", Toast.LENGTH_SHORT).show(); allowNext = false
                }
            }
        }
        // set date field of CompleteProfileUserDataClass's instance w/ user's date input
        fun setDate(){
            val selectedYear = datePick.year
            val selectedMonth =  datePick.month + 1
            val selectedDay = datePick.dayOfMonth
            val selectedDate = "$selectedYear-$selectedMonth-$selectedDay"
            println(selectedYear)
            allowNext = (selectedYear < 2020)
            if (isFromLoveHoroscope)  partnerProfile.partnerDate = selectedDate; star3.startColorAnimation()
            if (!isFromLoveHoroscope) userCompleteProfile.date = selectedDate; star3.startColorAnimation()
        }

        // set time field of CompleteProfileUserDataClass's instance w/ user's time input
        @SuppressLint("StopShip")
        fun setTime(){
            val selectedHour = timePick.hour
            val selectedMinute = timePick.minute
            val selectedTime = "$selectedHour:$selectedMinute:00"
            if (isFromLoveHoroscope)  partnerProfile.partnerTime = selectedTime; star4.startColorAnimation()
            if (!isFromLoveHoroscope) userCompleteProfile.time = selectedTime; star4.startColorAnimation()
        }

        // set location field of CompleteProfileUserDataClass's instance w/ user's location input
        fun setLocation(){
            if (locationPick.text != "sehrini_sec"){
                step = 5; allowNext = true
            }
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
                    step = 5; allowNext = true; star5.startColorAnimation()
                }
            }
        }

        // set occupation field of CompleteProfileUserDataClass's instance w/ user's occupation input
        fun setOccupation(){
            setSpinner(occupationPick, R.array.occupations, "Pick your occupation") { selectedOccupation ->
                if (isFromLoveHoroscope) {
                    partnerProfile.partnerOccupation = selectedOccupation
                    createLookupUserJson()
                    println(lookupUserJson)
                    setViewGone(completeProfilePickersContainer, miraSpeechBubbleContainer)
                    getLoveHoroscope(thinkingAnimation, this)
                }
                else{
                    if (selectedOccupation != "Pick your occupation" ){
                        userCompleteProfile.occupation = selectedOccupation
                        step = 6; star6.startColorAnimation()
                    } else{
                        step = 5
                    }
                }
            }
        }

        // set relation field of CompleteProfileUserDataClass's instance w/ user's relation input
        fun setRelation(){
            allowNext = false
            setSpinner(relationPick, R.array.marital_status, "Medeni durumunuzu Seciniz") { selectedRelation ->
                if (selectedRelation != "Medeni durumunuzu Seciniz") {
                    userCompleteProfile.relation = selectedRelation
                    step = 7; star7.startColorAnimation()
                } else step = 6
            }
        }

        // when activity is created directly set namePick view visible
        setViewVisibleWithAnimation(this,namePickContainer)

        nextButton.setOnClickListener{
            if (step == 0){
                setName()
                if (step == 0 && allowNext){
                    allowNext = false; step = 1; nextButton.performClick()
                }
            }

            if (step == 1){
                setGender()
                setViewGoneWithAnimation(this,namePickContainer)
                setViewVisibleWithAnimation(this,genderPickContainer)
                if (step==1 && allowNext){
                    allowNext = false; step = 2
                }
            }

            if (step == 2){
                setViewGoneWithAnimation(this,genderPickContainer)
                setViewVisibleWithAnimation(this,datePickContainer)
                setDate()
                if (step == 2 && allowNext){
                    allowNext = false; step = 3
                }
            }

            if (step == 5){
                setViewGoneWithAnimation(this,locationPickContainer)
                setViewVisibleWithAnimation(this,occupationPickContainer)
                setOccupation()

            }

            if (step == 6){
                setViewGoneWithAnimation(this,occupationPickContainer)
                setViewVisibleWithAnimation(this,relationPickContainer)
                setRelation()
            }

            when(step){

                3 -> {
                    allowNext = false
                    setViewGoneWithAnimation(this,datePickContainer, nextButton)
                    setViewVisibleWithAnimation(this,timePickContainer,timePickNextButton)
                    setTime()
                    timePickNextButton.setOnClickListener{
                        allowNext = true
                        step = 4
                        setViewVisibleWithAnimation(this,nextButton)
                        setViewGoneWithAnimation(this,timePickNextButton)
                        nextButton.performClick()
                    }

                }

                4 -> {
                    allowNext = false
                    setViewGoneWithAnimation(this,timePickContainer)
                    setViewVisibleWithAnimation(this,locationPickContainer)
                    println("allow next?: $step")
                    setLocation()
                    println("allow next?: $step")
                }

                7-> completeProfile()
            }
        }

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (step == 0){
                    val options = ActivityOptions.makeCustomAnimation(applicationContext, R.anim.activity_slide_down, 0)
                    val intent = Intent(applicationContext, LoginSignupActivity::class.java);startActivity(intent, options.toBundle())
                    navigateToSignUp = true
                }

                if (step == 1){
                    step = 0
                    setViewGoneWithAnimation(applicationContext, genderPickContainer)
                    setViewVisibleWithAnimation(applicationContext, namePickContainer)
                }

                if (step == 2){
                    step = 1
                    setViewGoneWithAnimation(applicationContext, datePickContainer)
                    setViewVisibleWithAnimation(applicationContext, genderPickContainer)
                }

                if (step == 3){
                    step = 2
                    setViewGoneWithAnimation(applicationContext, timePickContainer)
                    setViewVisibleWithAnimation(applicationContext, datePickContainer, nextButton)
                }

                if (step == 4){
                    step = 3
                    setViewGoneWithAnimation(applicationContext, locationPickContainer, nextButton)
                    setViewVisibleWithAnimation(applicationContext, timePickContainer, timePickNextButton)
                }

                if (step == 5){
                    step = 4
                    allowNext = false
                    setLocation()
                    setViewGoneWithAnimation(applicationContext, occupationPickContainer)
                    setViewVisibleWithAnimation(applicationContext, locationPickContainer)
                }

                if (step == 6){
                    step = 5
                    setViewGoneWithAnimation(applicationContext, relationPickContainer)
                    setViewVisibleWithAnimation(applicationContext, occupationPickContainer)
                }

                if (step == 7){
                    step = 5
                    setViewGoneWithAnimation(applicationContext, relationPickContainer)
                    setViewVisibleWithAnimation(applicationContext, occupationPickContainer)
                }

            }
        }
        this.onBackPressedDispatcher.addCallback(this, callback)

    }
}
