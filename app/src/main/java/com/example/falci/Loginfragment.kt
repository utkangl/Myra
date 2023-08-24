package com.example.falci

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.widget.AppCompatButton
import com.example.falci.LoginSignupActivity.Loginfunctions.createLoginJSON
import com.example.falci.LoginSignupActivity.Loginfunctions.postloginJson


class Loginfragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_login, container, false)


        val loginfragmentsignuplinkedtext = v.findViewById<LinkTextView>(R.id.loginfragmentsignuplinkedtext)
        val signUpFragment = SignUpFragment()

        val username = v.findViewById<EditText>(R.id.loginfragmentusernametext).text
        val password = v.findViewById<EditText>(R.id.loginfragmentpasswordtext).text

        val loginfragmentloginbutton = v.findViewById<AppCompatButton>(R.id.loginfragmentloginbutton)

        val loginposturl = "http://31.210.43.174:1337/auth/token/"


        loginfragmentloginbutton.setOnClickListener{

            val loginJson = createLoginJSON(username.toString(), password.toString())

            postloginJson(loginposturl, loginJson) { responseBody, exception ->
                if (exception != null) {
                    println("Error: ${exception.message}")
                } else {
                    println("Response: $responseBody")
                }
            }

        }

        loginfragmentsignuplinkedtext.setOnClickListener{

            parentFragmentManager.beginTransaction().apply{
                replace(R.id.container, signUpFragment)
                addToBackStack(null)
                commit()
            }

        }

        return v

    }

}