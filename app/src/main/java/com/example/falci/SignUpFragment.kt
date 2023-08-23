package com.example.falci
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.widget.AppCompatButton
import com.example.falci.LoginSignupActivity.RegistrationFunction as regobject
import com.example.falci.LoginSignupActivity.PostRegistrationJSONFunction as postjsonobject


class SignUpFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_sign_up, container, false)


        val username = v.findViewById<EditText>(R.id.signupfragmentusernametext).text
        val password = v.findViewById<EditText>(R.id.signupfragmentpasswordtext).text

        val signupfragmentsignupbutton = v.findViewById<AppCompatButton>(R.id.signupfragmentsignupbutton)

        val registerposturl = "http://31.210.43.174:1337/auth/register/"
        val loginposturl = "http://31.210.43.174:1337/auth/"

        signupfragmentsignupbutton.setOnClickListener{

            val registrationJSON = regobject.createRegistrationJSON(username.toString(), password.toString())

            postjsonobject.postJson(registerposturl, registrationJSON) { responseBody, exception ->
                if (exception != null) {
                    println("Error: ${exception.message}")
                } else {
                    println("Response: $responseBody")
                }
            }


        }

        return v
    }


}


























