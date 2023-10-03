package com.example.falci

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import com.example.falci.internalClasses.*
import com.example.falci.internalClasses.AuthenticationFunctions.CreateJsonObject.createJsonObject
import com.example.falci.internalClasses.TransitionToFragment.ReplaceActivityToFragment.replaceLoginActivityToSignUpFragment
import com.example.falci.internalClasses.dataClasses.*
import org.json.JSONObject


class Loginfragment : Fragment() {

    // making this fields lateInit to initialize those on button click. If we were to initialize
    // them in onCreateView, they would not be re assignable when we came back with back button
    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_login, container, false)

        val changeToSignUp = v.findViewById<LinkTextView>(R.id.loginfragmentsignuplinkedtext)
        val loginfragmentloginbutton = v.findViewById<AppCompatButton>(R.id.loginFragmentNextButton)
        val remembermeCheckBox = v.findViewById<CheckBox>(R.id.remembermeCheckBox)

        usernameEditText = v.findViewById(R.id.loginFragmentUsername)
        passwordEditText = v.findViewById(R.id.loginFragmentPassword)

         // if user has came to login fragment by completing the profile,
        // fill the fields with user's registered informations to make login easier
        if (authenticated.isFromSignIn){
            usernameEditText.setText(userRegister.email)
            passwordEditText.setText(userRegister.password)
        }

        remembermeCheckBox.setOnCheckedChangeListener { _, isRememberMeTrue ->
            if (isRememberMeTrue){
                authenticated.rememberMe = true
                requireActivity().runOnUiThread { Toast.makeText(requireContext(), "remember me is true", Toast.LENGTH_SHORT).show()}
            } else requireActivity().runOnUiThread { Toast.makeText(requireContext(), "remember me is false", Toast.LENGTH_SHORT).show()}


        }

        // assign user inputs to vars, create jsonObject w/ this vars, post them, handle response
        loginfragmentloginbutton.setOnClickListener {
            val enteredUsername = usernameEditText.text.toString()
            val enteredPassword = passwordEditText.text.toString()


            // create the json object that will be posted for login process and set its fields with user input
            val loginJson = createJsonObject(
                "email" to enteredUsername,
                "password" to enteredPassword
            )

            // post login json object, and handle the response for errors and success
            AuthenticationFunctions.PostJsonFunctions.postJsonNoHeader(urls.loginURL, loginJson, requireContext()) { responseBody, _ ->
                println("response $responseBody")

                   // if response code is 200 success, toast successfully login,
                  // set isLoggedIn as true and isFromSignIn as false,
                 // because when isFromSignIn is true, main activity directly navigates to login
                // then navigate to main activity
                if (statusCode == 200)  {
                    requireActivity().runOnUiThread {
                        Toast.makeText(requireContext(), "Login Successful", Toast.LENGTH_SHORT).show()
                        authenticated.isLoggedIn = true
                        authenticated.isFromSignIn = false
                        val options = ActivityOptions.makeCustomAnimation(requireContext(), R.anim.activity_slide_down, 0)
                        val intent = Intent(requireActivity(), MainActivity::class.java);startActivity(intent, options.toBundle())

                        val sharedPreferences = requireContext().getSharedPreferences("token_prefs", Context.MODE_PRIVATE)
                        val editor = sharedPreferences.edit()

                        editor.putLong("token_creation_time", tokensDataClass.tokensCreatedAt)
                        editor.putString("refresh_token", tokensDataClass.refreshToken)

                        if (authenticated.rememberMe){
                            editor.putString("access_token", tokensDataClass.accessToken)
                            editor.putBoolean("didLogin", true)
                        }

                        editor.apply()


                    }
                }

                // if response code is 401 unauthorized, toast the error
                if (statusCode == 401){
                    val responseJson = responseBody?.let { it1 -> JSONObject(it1) }
                    val detail = responseJson?.optString("detail")
                    requireActivity().runOnUiThread { Toast.makeText(requireContext(), detail, Toast.LENGTH_LONG).show()}
                }
            }
        }

        // navigate user to signup fragment on changeToSignup click
        changeToSignUp.setOnClickListener {
            replaceLoginActivityToSignUpFragment(parentFragmentManager, SignUpFragment())
        }

        return v
    }


}

