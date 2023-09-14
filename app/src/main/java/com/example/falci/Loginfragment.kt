package com.example.falci

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import com.example.falci.LoginSignupActivity.Loginfunctions.StatusCode
import com.example.falci.LoginSignupActivity.Loginfunctions.createLoginJSON
import com.example.falci.LoginSignupActivity.Loginfunctions.postloginJson
import com.example.falci.internalClasses.InternalFunctions.ReplaceFragmentWithAnimation.replaceFragmentWithAnimation

var isLoggedin = false

class Loginfragment : Fragment() {

    private var savedUsername: String? = null
    private var savedPassword: String? = null

    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_login, container, false)


        val changeToSignUp = v.findViewById<LinkTextView>(R.id.loginfragmentsignuplinkedtext)

        usernameEditText = v.findViewById(R.id.loginfragmentusername)
        passwordEditText = v.findViewById(R.id.loginfragmentpassword)

        val loginfragmentloginbutton = v.findViewById<AppCompatButton>(R.id.loginfragmentnextbutton)

        loginfragmentloginbutton.setOnClickListener {
            val enteredUsername = usernameEditText.text.toString()
            val enteredPassword = passwordEditText.text.toString()

            val loginJson = createLoginJSON(enteredUsername, enteredPassword)

            val loginposturl = "http://31.210.43.174:1337/auth/token/"

            postloginJson(loginposturl, loginJson) { responseBody, exception ->
                if (exception != null) {
                    println("Error: ${exception.message}")
                } else {
                    println("Response: $responseBody")

                    if (StatusCode == 200) {
                        isLoggedin = true
                        val intent = Intent(requireActivity(), MainActivity::class.java)
                        startActivity(intent)
                    } else {
                        println("Error Code: $StatusCode")
                    }
                }
            }
        }

        changeToSignUp.setOnClickListener { replaceFragmentWithAnimation(parentFragmentManager, SignUpFragment()) }

        if (!savedUsername.isNullOrEmpty() && !savedPassword.isNullOrEmpty()) {
            usernameEditText.setText(savedUsername)
            passwordEditText.setText(savedPassword)
        }

        return v
    }
}