package com.example.falci

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.widget.AppCompatButton
import com.example.falci.internalClasses.TransitionToFragment.ReplaceFragmentWithAnimation.replaceFragmentWithAnimation
import org.json.JSONException
import org.json.JSONObject

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

            usernameField = v.findViewById(R.id.signupfragmentusernametext)
            passwordField = v.findViewById(R.id.signupfragmentpasswordtext)

            val registerposturl = "http://192.168.1.22:8000/auth/register/"

            val registerJSON = LoginSignupActivity.RegistrationFunctions.createRegisterJSON(
                usernameField.text.toString(),
                passwordField.text.toString(),
            )

            LoginSignupActivity.RegistrationFunctions.postsignupJson(
                registerposturl,
                registerJSON
            ) { responseBody, exception ->
                if (exception != null) {
                    println("Error: ${exception.message}")
                } else {
                    try {
                        val responseJson = responseBody?.let { it1 -> JSONObject(it1) }
                        val detail = responseJson?.optString("detail")

                        if (detail != null && detail.isNotEmpty()) {
                            println("Hata: $detail")
                        } else {

                            println(responseBody)
                            replaceFragmentWithAnimation(parentFragmentManager, NamePickFragment())
                        }
                    } catch (e: JSONException) {
                        println("JSON Analiz Hatasi: ${e.message}")
                    }
                }
            }

        }

        return v
    }

}


























