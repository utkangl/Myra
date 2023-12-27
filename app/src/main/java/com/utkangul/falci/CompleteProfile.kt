package com.utkangul.falci

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.airbnb.lottie.LottieAnimationView
import com.utkangul.falci.internalClasses.*
import com.utkangul.falci.internalClasses.AuthenticationFunctions.CreateJsonObject.createJsonObject
import com.utkangul.falci.internalClasses.AuthenticationFunctions.PostJsonFunctions.postJsonWithHeader
import com.utkangul.falci.internalClasses.HoroscopeFunctions.getLoveHoroscope
import com.utkangul.falci.internalClasses.InternalFunctions.SetVisibilityFunctions.setViewGone
import com.utkangul.falci.internalClasses.InternalFunctions.SetVisibilityFunctions.setViewGoneWithAnimation
import com.utkangul.falci.internalClasses.InternalFunctions.SetVisibilityFunctions.setViewVisible
import com.utkangul.falci.internalClasses.InternalFunctions.SetVisibilityFunctions.setViewVisibleWithAnimation
import com.utkangul.falci.internalClasses.dataClasses.*
import com.google.gson.Gson
import org.json.JSONObject
import java.util.*


class CompleteProfile : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.runOnUiThread{
            val languageSharedPreferences: SharedPreferences = application.getSharedPreferences("language_choice", Context.MODE_PRIVATE)
            val languageChoice = languageSharedPreferences.getString("language",null)
            if (!languageChoice.isNullOrEmpty()){
                val locale = Locale(languageChoice)
                Locale.setDefault(locale)
                val config = Configuration()
                config.locale = locale
                resources.updateConfiguration(config, resources.displayMetrics)
            }
        }

        setContentView(R.layout.activity_complete_profile)

        val miraSpeechBubbleContainer = findViewById<RelativeLayout>(R.id.mira_speech_bubble_container)
        val completeProfilePickersContainer = findViewById<RelativeLayout>(R.id.completeProfilePickersContainer)

        val nextButton = findViewById<AppCompatButton>(R.id.next_button)
        val timePickNextButton = findViewById<AppCompatButton>(R.id.timePickNextButton)
        val datePickNextButton = findViewById<AppCompatButton>(R.id.datePickNextButton)

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

        val locationPick= findViewById<AppCompatButton>(R.id.locationPick)
        val occupationPick= findViewById<Spinner>(R.id.occupationPick)
        val relationPick= findViewById<Spinner>(R.id.relationPick)

        val cityInput= findViewById<AutoCompleteTextView>(R.id.cityInput)

        val star1 = findViewById<ImageView>(R.id.star1)
        val star2 = findViewById<ImageView>(R.id.star2)
        val star3 = findViewById<ImageView>(R.id.star3)
        val star4 = findViewById<ImageView>(R.id.star4)
        val star5 = findViewById<ImageView>(R.id.star5)
        val star6 = findViewById<ImageView>(R.id.star6)
        val star7 = findViewById<ImageView>(R.id.star7)
        
        val timePick = findViewById<TimePicker>(R.id.timePick)
        val datePick = findViewById<DatePicker>(R.id.datePick)


        val completeProfileBackButton = findViewById<ImageButton>(R.id.completeProfileBackButton)


        val calendarForMaxDate = Calendar.getInstance()
        val maxDate = calendarForMaxDate.timeInMillis

        var isFromSetName = false
        var isFromSetLocation = false
        var isFromSetTime = false
        var isGender = false
        var isLocation = false
        var isOccupation = false

        datePick.maxDate = maxDate


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
        
        completeProfilePickersContainer.setOnClickListener{
            println(step)
            println("isFromSetLocation ${isFromSetLocation}")
        }

        // create Json, fill with inputs, post it, handle response with response code,toast detail
        fun completeProfile(){

            // create the json object that will be posted as completeProfileJson
            //fill json with the user inputs, format date and time into one variable before fill
            val calendar = Calendar.getInstance()
            calendar.set(completeProfileTimeStamp.year, completeProfileTimeStamp.month, completeProfileTimeStamp.day, completeProfileTimeStamp.hour, completeProfileTimeStamp.minute)
            val completeProfileBirthday = calendar.timeInMillis / 1000

            val zodiacInfoJson = createJsonObject(
                "name" to userCompleteProfile.name,
                "location" to userCompleteProfile.location,
                "birthDay" to completeProfileBirthday.toString(),
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
            println(completeProfileJSON)
            postJsonWithHeader(this,urls.completeProfileURL, completeProfileJSON, this)
            { responseBody, _ ->
                val responseJson = responseBody?.let { it1 -> JSONObject(it1) }
                val detail = responseJson?.optString("detail")
                val errorCode = responseJson?.optString("error_code")
                println(errorCode)

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
            val calendar = Calendar.getInstance()
            calendar.set(partnerProfileTimeStamp.year, partnerProfileTimeStamp.month, partnerProfileTimeStamp.day, partnerProfileTimeStamp.hour, partnerProfileTimeStamp.minute)
            val partnerBirthDay = calendar.timeInMillis / 1000
            postLookupUserJson = createJsonObject(
                "name" to postPartnerProfile.partnerName,
                "gender" to postPartnerProfile.partnerGender,
                "birth_day" to partnerBirthDay.toString(),
                "birth_place" to postPartnerProfile.partnerLocation,
                "occupation" to postPartnerProfile.partnerOccupation,
                "relationship_status" to postPartnerProfile.partnerRelationStatus
            )
        }

         // let user to go on to genderpick if input lasts longer than 2 characters else toast error
        // and  set name field of CompleteProfileUserDataClass's instance w/ user's name input
         fun setName(){

             if (controlVariables.isFromLoveHoroscope && namePick.text.length >= 2){
                 postPartnerProfile.partnerName = namePick.text.toString()
                 step = 1; isFromSetName = true
             }else{
                 if (namePick.text.length >= 2) {
                     userCompleteProfile.name = namePick.text.toString()
                      step = 1 ; isFromSetName = true
                 } else {
                     this.runOnUiThread {
                         Toast.makeText(this, this.resources.getString(R.string.name_character_warning), Toast.LENGTH_SHORT).show();step = 0
                     }
                 }
             }
         }

        // set gender field of CompleteProfileUserDataClass's instance w/ user's gender input
        fun setGender(){
            println(resources.getString(R.string.defaultTextOfGender))
            if (genderPick.selectedItem == null || genderPick.selectedItem == resources.getString(R.string.defaultTextOfGender)){
                isGender = false
            }
            println(genderPick.selectedItem)
            if (step == 1 && genderPick.selectedItem != null && genderPick.selectedItem != resources.getString(R.string.defaultTextOfGender) && !isFromSetName) step=2
            setSpinner(genderPick, R.array.genders, resources.getString(R.string.defaultTextOfGender)) { selectedGender ->
                if (controlVariables.isFromLoveHoroscope) postPartnerProfile.partnerGender = selectedGender; step =2;isGender = true
                if (!controlVariables.isFromLoveHoroscope) userCompleteProfile.gender = selectedGender; step = 2; isGender = true
            }
        }
        // set date field of CompleteProfileUserDataClass's instance w/ user's date input
        fun setDate(){
            val year = datePick.year
            val month =  datePick.month + 1
            val day = datePick.dayOfMonth
            if (controlVariables.isFromLoveHoroscope)  {
                partnerProfileTimeStamp.year = year
                partnerProfileTimeStamp.month = month
                partnerProfileTimeStamp.day = day
            }
            if (!controlVariables.isFromLoveHoroscope) {
                completeProfileTimeStamp.year = year
                completeProfileTimeStamp.month = month
                completeProfileTimeStamp.day = day
            }
        }

        // set time field of CompleteProfileUserDataClass's instance w/ user's time input
        fun setTime(){
            val hour = timePick.hour
            val minute = timePick.minute
            if (controlVariables.isFromLoveHoroscope) {
                partnerProfileTimeStamp.hour = hour
                partnerProfileTimeStamp.minute = minute
            }
            if (!controlVariables.isFromLoveHoroscope) {
                completeProfileTimeStamp.hour = hour
                completeProfileTimeStamp.minute = minute
            }
        }

        // set location field of CompleteProfileUserDataClass's instance w/ user's location input
        fun setLocation(){
            if (step==4 && locationPick.text != resources.getString(R.string.defaultTextOfLocation) ){
                step = 5
                isFromSetLocation = true
                isLocation = true
                if (!isFromSetTime){
                    nextButton.performClick()
                    isLocation = false
                }
            }
            locationPick.setOnClickListener{
                val locationService = LocationService(this)
                locationService.initializeAutoCompleteTextView(cityInput)
                setViewGone(locationPickFirstLayout)
                setViewVisible(locationPickSecondLayout)
                controlVariables.inLocationPickCard = true
                cityInput.setOnItemClickListener { _, _, _, _ ->
                    setViewGoneWithAnimation(this,locationPickSecondLayout)
                    setViewVisibleWithAnimation(this,locationPickFirstLayout)
                    controlVariables.inLocationPickCard = false
                    locationService.hideKeyboard(cityInput)
                    locationPick.text = cityInput.text.toString()
                        isFromSetLocation = true
                        step=5
                        isLocation = true
                }
            }
        }

        // set occupation field of CompleteProfileUserDataClass's instance w/ user's occupation input
        fun setOccupation(){
            isFromSetTime = false
            if (step == 5 && occupationPick.selectedItem != resources.getString(R.string.defaultTextOfOccupation) && occupationPick.selectedItem != null && !isFromSetLocation){
                step = 6
            }
            setSpinner(occupationPick, R.array.occupations, resources.getString(R.string.defaultTextOfOccupation)) { selectedOccupation ->
                if (selectedOccupation != resources.getString(R.string.defaultTextOfOccupation) && isFromSetLocation){
                    if (controlVariables.isFromLoveHoroscope) {postPartnerProfile.partnerOccupation = selectedOccupation}
                    if (!controlVariables.isFromLoveHoroscope) {userCompleteProfile.occupation = selectedOccupation}
                    step = 6
                    isOccupation = true
                    isFromSetLocation = false
                } else step = 5
            }
        }

        // set relation field of CompleteProfileUserDataClass's instance w/ user's relation input
        fun setRelation(){
            val gson = Gson()

            setSpinner(relationPick, R.array.marital_status, resources.getString(R.string.defaultTextOfRelation)) { selectedRelation ->
                if (selectedRelation != resources.getString(R.string.defaultTextOfRelation)) {

                    if (controlVariables.isFromLoveHoroscope) {
                        postPartnerProfile.partnerRelationStatus = selectedRelation
                        createLookupUserJson()
                        println(postLookupUserJson)
                        setViewGone(completeProfilePickersContainer, miraSpeechBubbleContainer)
                        postJsonWithHeader(this,urls.lookupUserURL, postLookupUserJson, this) { responseBody, _ ->
                            val responseJson = responseBody?.let { it1 -> JSONObject(it1) }
                            val detail = responseJson?.optString("detail")
                            val errorCode = responseJson?.optString("error_code")
                            println("detail bu $detail")
                            println("errorCode bu $errorCode")
                            getPartnerProfile = gson.fromJson(responseBody, GetPartnerProfileDataClass::class.java)
                            if (statusCode == 201) {
                                println("responseBody is $responseBody")
                                controlVariables.isFromCompleteLookup = true
                                getLoveHoroscope(thinkingAnimation, this, getPartnerProfile.id,supportFragmentManager,this)
                            }
                        }
                    }
                    if (!controlVariables.isFromLoveHoroscope){ userCompleteProfile.relation = selectedRelation     }
                    step = 7; star7.setBackgroundResource(R.drawable.star_white)
                } else step = 6
            }
        }

        // when activity is created directly set namePick view visible
        setViewVisibleWithAnimation(this,namePickContainer)

        nextButton.setOnClickListener{
            println(step)
            if (step == 0){
                setName()
            }

            if (step == 1){
                star1.setBackgroundResource(R.drawable.star_white)
                setGender()
                setViewGoneWithAnimation(this,namePickContainer)
                setViewVisibleWithAnimation(this,genderPickContainer)
            }

            if (step == 2){
                star2.setBackgroundResource(R.drawable.star_white)
                setViewGoneWithAnimation(this,genderPickContainer, nextButton)
                setViewVisibleWithAnimation(this,datePickContainer,datePickNextButton)
                isGender = false
                setDate()
                datePickNextButton.setOnClickListener{
                    star3.setBackgroundResource(R.drawable.star_white)
                    step = 3
                    setViewVisibleWithAnimation(this,nextButton)
                    setViewGoneWithAnimation(this,datePickContainer)
                    nextButton.performClick()
                }
            }

            if (step == 5){
                star5.setBackgroundResource(R.drawable.star_white)
                setViewGoneWithAnimation(this,locationPickContainer)
                setViewVisibleWithAnimation(this,occupationPickContainer)
                isLocation = false
                setOccupation()
            }

            if (step == 6){
                star6.setBackgroundResource(R.drawable.star_white)
                isOccupation = false
                setViewGoneWithAnimation(this,occupationPickContainer)
                setViewVisibleWithAnimation(this,relationPickContainer)
                setRelation()
            }

            when(step){

                3 -> {
                    setViewGoneWithAnimation(this,datePickContainer, nextButton)
                    setViewVisibleWithAnimation(this,timePickContainer,timePickNextButton)
                    setTime()
                    timePickNextButton.setOnClickListener{
                        step = 4
                        isFromSetTime = true
                        setViewVisibleWithAnimation(this,nextButton)
                        setViewGoneWithAnimation(this,timePickNextButton)
                        nextButton.performClick()
                    }

                }

                4 -> {
                    star4.setBackgroundResource(R.drawable.star_white)
                    setViewGoneWithAnimation(this,timePickContainer)
                    setViewVisibleWithAnimation(this,locationPickContainer)
                    isFromSetLocation = true
                    setLocation()
                }

                7-> completeProfile()
            }
        }

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {

                if (controlVariables.inLocationPickCard){
                    setViewVisible(locationPickFirstLayout)
                    setViewGone(locationPickSecondLayout)
                    controlVariables.inLocationPickCard = false
                }

                if (step == 0){
                    controlVariables.navigateToSignUp = if (controlVariables.isFromLoveHoroscope){
                        val options = ActivityOptions.makeCustomAnimation(applicationContext, R.anim.activity_slide_down, 0)
                        val intent = Intent(applicationContext, MainActivity::class.java);startActivity(intent, options.toBundle())
                        false
                    } else {
                        val options = ActivityOptions.makeCustomAnimation(applicationContext, R.anim.activity_slide_down, 0)
                        val intent = Intent(applicationContext, LoginSignupActivity::class.java);startActivity(intent, options.toBundle())
                        true
                    }
                }

                if (step == 1){
                    step = 0
                    setViewGoneWithAnimation(applicationContext, genderPickContainer)
                    setViewVisibleWithAnimation(applicationContext, namePickContainer)
                    star1.setBackgroundResource(R.drawable.star)
                }

                if (step == 2){
                    isFromSetName = false
                    if (isGender){
                        step = 0
                        setViewGoneWithAnimation(applicationContext, genderPickContainer)
                        setViewVisibleWithAnimation(applicationContext, namePickContainer,nextButton)
                        star2.setBackgroundResource(R.drawable.star)
                    }
                    else{
                        step = 1
                        setViewGoneWithAnimation(applicationContext, datePickContainer,datePickNextButton)
                        setViewVisibleWithAnimation(applicationContext, genderPickContainer,nextButton)
                        star2.setBackgroundResource(R.drawable.star)
                    }
                }

                if (step == 3){
                    step = 2
                    setViewGoneWithAnimation(applicationContext, timePickContainer, timePickNextButton)
                    setViewVisibleWithAnimation(applicationContext, datePickContainer, datePickNextButton)
                    star3.setBackgroundResource(R.drawable.star)
                }

                if (step == 4){
                    if (!controlVariables.inLocationPickCard){
                        step = 3
                        setViewGoneWithAnimation(applicationContext, locationPickContainer, nextButton)
                        setViewVisibleWithAnimation(applicationContext, timePickContainer, timePickNextButton)
                        star4.setBackgroundResource(R.drawable.star)
                    }
                }

                if (step == 5){
                    if (isLocation){
                        step = 3
                        nextButton.performClick()
                        setViewGoneWithAnimation(applicationContext, locationPickContainer)
                        setViewVisibleWithAnimation(applicationContext, timePickContainer)
                        star5.setBackgroundResource(R.drawable.star)
                    }
                    else{
                        step = 4
                        setViewGoneWithAnimation(applicationContext, occupationPickContainer)
                        setViewVisibleWithAnimation(applicationContext, locationPickContainer)
                        star5.setBackgroundResource(R.drawable.star)
                    }
                }

                if (step == 6){
                    if (isOccupation){
                        step = 4
                        isFromSetLocation = true
                        setViewGoneWithAnimation(applicationContext, occupationPickContainer)
                        setViewVisibleWithAnimation(applicationContext, locationPickContainer)
                        star6.setBackgroundResource(R.drawable.star)
                    }
                    else{
                        step = 5
                        isFromSetLocation = false
                        setViewGoneWithAnimation(applicationContext, relationPickContainer)
                        setViewVisibleWithAnimation(applicationContext, occupationPickContainer)
                        star6.setBackgroundResource(R.drawable.star)
                    }
                }

                if (step == 7){
                    step = 5
                    setViewGoneWithAnimation(applicationContext, relationPickContainer)
                    setViewVisibleWithAnimation(applicationContext, occupationPickContainer)
                    star6.setBackgroundResource(R.drawable.star)
                    star7.setBackgroundResource(R.drawable.star)
                }

            }
        }
        this.onBackPressedDispatcher.addCallback(this, callback)

        completeProfileBackButton.setOnClickListener{
            this.onBackPressed()
        }
    }
}
