package com.example.falci

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.widget.AppCompatButton
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
        val namePickFragment = NamePickFragment()


        signupfragmentsignupbutton.setOnClickListener {

            usernameField = v.findViewById(R.id.signupfragmentusernametext)
            passwordField = v.findViewById(R.id.signupfragmentpasswordtext)

            val registerposturl = "http://31.210.43.174:1337/auth/register/"

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
                        val responseJson = JSONObject(responseBody)
                        val detail = responseJson.optString("detail")

                        if (detail != null && detail.isNotEmpty()) {
                            // API yanıtında bir hata ayrıntısı var, burada işlem yapabilirsiniz.
                            println("API Hata: $detail")
                        } else {
                            // Hata ayrıntısı yok, başarılı işlem
                            println("Başarılı İşlem")
                            println(responseBody)
                            parentFragmentManager.beginTransaction().apply {
                                setCustomAnimations(
                                    R.anim.fade_in,
                                    R.anim.fade_out,
                                    R.anim.fade_in,
                                    R.anim.fade_out
                                )
                                replace(R.id.main_fragment_container, namePickFragment)
                                addToBackStack(null)
                                commit()
                            }
                        }
                    } catch (e: JSONException) {
                        println("JSON Analiz Hatası: ${e.message}")
                    }
                }
            }


        }

        return v
    }



}


























