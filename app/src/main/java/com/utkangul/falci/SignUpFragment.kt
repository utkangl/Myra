package com.utkangul.falci

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.AppCompatButton
import androidx.cardview.widget.CardView
import com.utkangul.falci.internalClasses.*
import com.utkangul.falci.internalClasses.AuthenticationFunctions.CreateJsonObject.createJsonObject
import com.utkangul.falci.internalClasses.AuthenticationFunctions.PostJsonFunctions.postJsonNoHeader
import com.utkangul.falci.internalClasses.InternalFunctions.SetVisibilityFunctions.setViewGoneWithAnimation
import com.utkangul.falci.internalClasses.InternalFunctions.SetVisibilityFunctions.setViewVisibleWithAnimation
import com.utkangul.falci.internalClasses.TransitionToFragment.ReplaceActivityToFragment.replaceLoginActivityToSignUpFragment
import com.utkangul.falci.internalClasses.dataClasses.controlVariables
import com.utkangul.falci.internalClasses.dataClasses.urls
import com.utkangul.falci.internalClasses.dataClasses.userRegister
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
        val kvkkPopupCard = v.findViewById<CardView>(R.id.kvkkPopupCard)
        val acceptKvkkButton = v.findViewById<AppCompatButton>(R.id.acceptKvkkButton)

        signupfragmentsignupbutton.setOnClickListener {
            emailField = v.findViewById(R.id.signUpFragmentUsernameText)
            passwordField = v.findViewById(R.id.signUpFragmentPasswordText)
            passwordAgainField = v.findViewById(R.id.signUpFragmentPasswordAgainText)
            passwordAgainField = v.findViewById(R.id.signUpFragmentPasswordAgainText)

            //creating the json object that will be posted
            val registerJSON = createJsonObject("email" to emailField.text.toString(), "password" to passwordField.text.toString())

            //setting userRegister's (an instance of UserRegisterDataClass) fields as user inputs
            userRegister.email = emailField.text.toString()
            userRegister.password = passwordField.text.toString()

            // if the passwords match each other
            if (passwordField.text.contentEquals(passwordAgainField.text) && !passwordField.text.isNullOrEmpty()){
                setViewVisibleWithAnimation(requireContext(),kvkkPopupCard)
                setViewGoneWithAnimation(requireContext(),signupfragmentsignupbutton)
                acceptKvkkButton.setOnClickListener{
                    setViewVisibleWithAnimation(requireContext(),signupfragmentsignupbutton)
                    setViewGoneWithAnimation(requireContext(),kvkkPopupCard)
                    //post registerJson and let user know the error if there is one, else wise toast success
                    postJsonNoHeader(requireContext(),requireActivity(),urls.signUpURL, registerJSON, ) { responseBody, _ ->

                        // if response code is 201, toast success and  navigate user to CompleteProfile activity
                        if (statusCode == 201){
                            println("status code is $statusCode sign up successful")
                            requireActivity().runOnUiThread {
                                Toast.makeText(requireContext(), "Basari ile kayit olundu", Toast.LENGTH_SHORT).show()
                                controlVariables.isFromLoveHoroscope = false
                                replaceLoginActivityToSignUpFragment(parentFragmentManager,EmailVerificationFragment())
    //                            val intent = Intent(requireActivity(), CompleteProfile::class.java)
    //                            startActivity(intent)
                            }
                        }
                        // if response code is not success
                        else{
                            val responseJson = responseBody?.let { it1 -> JSONObject(it1) }
                            val detail = responseJson?.optString("detail")
                            requireActivity().runOnUiThread {
                                Toast.makeText(requireContext(), "$detail", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            } else{ // if passwords do not match
                Toast.makeText(requireContext(), "Passwords do not match or empty", Toast.LENGTH_SHORT).show()
            }

        }

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                replaceLoginActivityToSignUpFragment(
                    parentFragmentManager,
                    Loginfragment()
                )
            }
        };requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)


        return v
    }

}