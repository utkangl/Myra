package com.example.falci

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import com.example.falci.internalClasses.*
import com.example.falci.internalClasses.AuthenticationFunctions.CreateJsonObject.createJsonObject
import com.example.falci.internalClasses.TransitionToFragment.ReplaceFragmentWithAnimation.replaceFragmentWithAnimation

lateinit var loginTokens: LoginTokensDataClass

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

        if (authenticated.isFromSignIn){
            usernameEditText.setText(userRegister.email)
            passwordEditText.setText(userRegister.password)
        }

        val loginfragmentloginbutton = v.findViewById<AppCompatButton>(R.id.loginfragmentnextbutton)

        loginfragmentloginbutton.setOnClickListener {
            val enteredUsername = usernameEditText.text.toString()
            val enteredPassword = passwordEditText.text.toString()


            val loginJson = createJsonObject(
                "email" to enteredUsername,
                "password" to enteredPassword
            )

            AuthenticationFunctions.PostJsonFunctions.postJsonNoHeader(urls.loginURL, loginJson, "login") { responseBody, exception ->
                println(responseBody)
                if (exception != null) { println("Error: ${exception.message}") }
                if (statusCode == 200) {
                    authenticated.isLoggedIn = true
                    authenticated.isFromSignIn = false
                    val intent = Intent(requireActivity(), MainActivity::class.java)
                    startActivity(intent)
                } else {
                    println("Error Code: $statusCode")
                }
            }
        }


        changeToSignUp.setOnClickListener {
            replaceFragmentWithAnimation(parentFragmentManager, SignUpFragment())
        }

        if (!savedUsername.isNullOrEmpty() && !savedPassword.isNullOrEmpty()) {
            usernameEditText.setText(savedUsername)
            passwordEditText.setText(savedPassword)
        }

        return v
    }
}