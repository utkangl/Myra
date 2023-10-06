package com.example.falci

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import com.example.falci.internalClasses.*
import com.example.falci.internalClasses.AuthenticationFunctions.CreateJsonObject.createJsonObject
import com.example.falci.internalClasses.AuthenticationFunctions.PostJsonFunctions.postJsonNoHeader
import com.example.falci.internalClasses.dataClasses.isFromLoveHoroscope
import com.example.falci.internalClasses.dataClasses.urls
import com.example.falci.internalClasses.dataClasses.userRegister
import org.json.JSONObject

class SignUpFragment : Fragment() {

     // making this fields lateInit to initialize those on button click. If we were to initialize
    // them in onCreateView, they would not be re assignable when we came back with back button
    private lateinit var emailField: EditText
    private lateinit var passwordField: EditText
    private lateinit var passwordAgainField: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_sign_up, container, false)

        val signupfragmentsignupbutton = v.findViewById<AppCompatButton>(R.id.signupfragmentsignupbutton)

        signupfragmentsignupbutton.setOnClickListener {

            emailField = v.findViewById(R.id.signUpFragmentUsernameText)
            passwordField = v.findViewById(R.id.signUpFragmentPasswordText)
            passwordAgainField = v.findViewById(R.id.signUpFragmentPasswordAgainText)

            //creating the json object that will be posted
            val registerJSON = createJsonObject("email" to emailField.text.toString(), "password" to passwordField.text.toString())

            //setting userRegister's (an instance of UserRegisterDataClass) fields as user inputs
            userRegister.email = emailField.text.toString()
            userRegister.password = passwordField.text.toString()

            // if the passwords match each other
            if (passwordField.text.contentEquals(passwordAgainField.text)){
                //post registerJson and let user know the error if there is one, else wise toast success
                postJsonNoHeader(urls.signUpURL, registerJSON, ) { responseBody, _ ->

                    // if response code is 201, toast success and  navigate user to CompleteProfile activity
                    if (statusCode == 201){
                        println("status code is $statusCode sign up successful")
                        requireActivity().runOnUiThread {
                            Toast.makeText(requireContext(), "Basari ile kayit olundu", Toast.LENGTH_SHORT).show()
                            isFromLoveHoroscope = false
                            val intent = Intent(requireActivity(), CompleteProfile::class.java)
                            startActivity(intent)
                        }
                    }

                    // if response code is 400, toast the error
                    if (statusCode == 400){
                        val responseJson = responseBody?.let { it1 -> JSONObject(it1) }
                        val detail = responseJson?.optString("detail")
                        requireActivity().runOnUiThread {
                            Toast.makeText(requireContext(), "$detail", Toast.LENGTH_SHORT).show()
                        }
                    }

                }
            } else{ // if passwords do not match
                Toast.makeText(requireContext(), "Passwords do not match", Toast.LENGTH_SHORT).show()
            }

        }
        return v
    }

}