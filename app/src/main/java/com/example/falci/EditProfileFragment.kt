package com.example.falci

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Button
import android.widget.ImageView
import org.json.JSONObject
import com.example.falci.LoginSignupActivity.ProfileFunctions.putEditProfileJson
    val editProfileJson = JSONObject()

class EditProfileFragment : Fragment() {

    lateinit var nameField: EditText
    lateinit var genderField: EditText
    lateinit var birthDateField: EditText
    lateinit var birthTimeField: EditText
    lateinit var locationField: EditText
    lateinit var occupationField: EditText
    lateinit var relationShipStatusField: EditText
    lateinit var savebutton: Button
    lateinit var backArrow: ImageView

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

        // Set initial values
        nameField.setText(profileFirst_name)
        genderField.setText(profileGender)
        birthDateField.setText(profileBirth_day)
        birthTimeField.setText(profileBirth_day)
        locationField.setText(profileBirth_place)
        occupationField.setText(profileOccupation)
        relationShipStatusField.setText(profileRelationship_status)

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
            if (relationShipStatusField.text.toString() != profileRelationship_status) {
                editProfileJson.put("relationship_status", relationShipStatusField.text.toString())
            }

            // Save changes
            profileFirst_name = nameField.text.toString()
            profileGender = genderField.text.toString()
            profileBirth_day = birthDateField.text.toString()
            profileBirth_time = birthTimeField.text.toString()
            profileBirth_place = locationField.text.toString()
            profileOccupation = occupationField.text.toString()
            profileRelationship_status = relationShipStatusField.text.toString()

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

        return v
    }
}