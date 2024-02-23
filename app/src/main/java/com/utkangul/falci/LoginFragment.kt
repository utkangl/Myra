package com.utkangul.falci

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import com.utkangul.falci.internalClasses.*
import com.utkangul.falci.internalClasses.AuthenticationFunctions.CreateJsonObject.createJsonObject
import com.utkangul.falci.internalClasses.TransitionToFragment.ReplaceActivityToFragment.replaceLoginActivityToSignUpFragment
import com.utkangul.falci.internalClasses.dataClasses.*
import org.json.JSONObject


class LoginFragment : Fragment() {

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
        val loginBackButton= v.findViewById<ImageButton>(R.id.loginBackButton)

        usernameEditText = v.findViewById(R.id.loginFragmentUsername)
        passwordEditText = v.findViewById(R.id.loginFragmentPassword)

         // if user has came to login fragment by completing the profile,
        // fill the fields with user's registered informations to make login easier
        if (authenticated.isFromSignIn){
            usernameEditText.setText(userRegister.email)
            passwordEditText.setText(userRegister.password)
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
            AuthenticationFunctions.PostJsonFunctions.postJsonNoHeader(requireContext(),requireActivity(),urls.loginURL, loginJson) { responseBody, _ ->
                println("response $responseBody")

                if (statusCode == 200)  {
                    requireActivity().runOnUiThread {
                        Toast.makeText(requireContext(), requireActivity().resources.getString(R.string.login_success), Toast.LENGTH_SHORT).show()
                        authenticated.isLoggedIn = true
                        authenticated.isFromSignIn = false
                        val options = ActivityOptions.makeCustomAnimation(requireContext(), R.anim.activity_slide_down, 0)
                        val intent = Intent(requireActivity(), MainActivity::class.java);startActivity(intent, options.toBundle())
                        val sharedPreferences = requireContext().getSharedPreferences("token_prefs", Context.MODE_PRIVATE)
                        val editor = sharedPreferences.edit()
                        editor.putLong("token_creation_time", tokensDataClass.tokensCreatedAt)
                        editor.putString("refresh_token", tokensDataClass.refreshToken)
                        editor.putString("access_token", tokensDataClass.accessToken)
                        editor.putBoolean("didLogin", true)
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

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val options = ActivityOptions.makeCustomAnimation(requireContext(), R.anim.activity_slide_down, 0)
                val mainActivityIntent = Intent(requireContext(), MainActivity::class.java)
                startActivity(mainActivityIntent, options.toBundle())
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        loginBackButton.setOnClickListener{
            callback.handleOnBackPressed()
        }
        return v
    }


}

