package com.example.falci

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.widget.AppCompatButton
import com.example.falci.internalClasses.*
import com.example.falci.internalClasses.AuthenticationFunctions.CreateJsonObject.createJsonObject
import com.example.falci.internalClasses.AuthenticationFunctions.PostJsonFunctions.postJsonNoHeader
import com.example.falci.internalClasses.dataClasses.RegisterTokensDataClass
import com.example.falci.internalClasses.dataClasses.urls
import com.example.falci.internalClasses.dataClasses.userRegister

lateinit var registerTokensDataClass: RegisterTokensDataClass
class SignUpFragment : Fragment() {

    private lateinit var usernameField: EditText
    private lateinit var passwordField: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_sign_up, container, false)

        val signupfragmentsignupbutton =
            v.findViewById<AppCompatButton>(R.id.signupfragmentsignupbutton)

        signupfragmentsignupbutton.setOnClickListener {

            usernameField = v.findViewById(R.id.signUpFragmentUsernameText)
            passwordField = v.findViewById(R.id.signUpFragmentPasswordText)

            val registerJSON = createJsonObject(
                "email" to usernameField.text.toString(),
                "password" to passwordField.text.toString()
            )

            userRegister.email = usernameField.text.toString()
            userRegister.password = passwordField.text.toString()

            postJsonNoHeader(urls.signUpURL, registerJSON, "register") { responseBody, exception ->
                println(responseBody)
                if (statusCode == 201){val intent = Intent(requireActivity(), CompleteProfile::class.java); startActivity(intent)}
                else { println("Error: ${exception?.message}") }
            }
        }

        return v
    }

}