package com.example.falci

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import com.example.falci.LoginSignupActivity.Loginfunctions.StatusCode
import com.example.falci.LoginSignupActivity.Loginfunctions.createLoginJSON
import com.example.falci.LoginSignupActivity.Loginfunctions.postloginJson
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException


var isLoggedin = false

class Loginfragment : Fragment() {

    private lateinit var gso: GoogleSignInOptions
    private lateinit var gsc: GoogleSignInClient

    private var savedUsername: String? = null
    private var savedPassword: String? = null

    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText


    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val resultCode = result.resultCode
            val data: Intent? = result.data
            if (resultCode == AppCompatActivity.RESULT_OK) {
                handleSignInResult(data)
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_login, container, false)


        val loginfragmentsignuplinkedtext =
            v.findViewById<LinkTextView>(R.id.loginfragmentsignuplinkedtext)
        val signUpFragment = SignUpFragment()

        usernameEditText = v.findViewById(R.id.loginfragmentusername)
        passwordEditText = v.findViewById(R.id.loginfragmentpassword)

        val loginfragmentloginbutton = v.findViewById<AppCompatButton>(R.id.loginfragmentnextbutton)
        val loginfragmentloginwithgooglebutton =
            v.findViewById<AppCompatButton>(R.id.loginfragmentloginwithgooglebutton)
        val logoutfromgooglebutton = v.findViewById<Button>(R.id.logoutfromgoogle)

        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        gsc = GoogleSignIn.getClient(requireContext(), gso)

//        val acct = GoogleSignIn.getLastSignedInAccount(requireContext())
//        if (acct != null) {
//            navigateToSecondActivity()
//        }


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

        loginfragmentloginwithgooglebutton.setOnClickListener {
            signIn()
        }

        loginfragmentsignuplinkedtext.setOnClickListener {
            parentFragmentManager.beginTransaction().apply {
                replace(R.id.main_fragment_container, signUpFragment)
                addToBackStack(null)
                commit()
            }
        }


        logoutfromgooglebutton.setOnClickListener {
            signOut()
        }


        if (!savedUsername.isNullOrEmpty() && !savedPassword.isNullOrEmpty()) {
            usernameEditText.setText(savedUsername)
            passwordEditText.setText(savedPassword)
        }

        return v
    }


    private fun signIn() {
        val signInIntent = gsc.signInIntent
        resultLauncher.launch(signInIntent)
    }

    private fun signOut() {
        gsc.signOut().addOnCompleteListener {
            Toast.makeText(requireContext(), "Signed out from Google", Toast.LENGTH_SHORT).show()
        }
    }


    private fun navigateToMainActivity() {
        requireActivity().finish()
        val intent = Intent(requireContext(), MainActivity::class.java)
        startActivity(intent)
    }

    override fun onPause() {
        super.onPause()

        savedUsername = usernameEditText.text.toString()
        savedPassword = passwordEditText.text.toString()
    }

    private fun handleSignInResult(data: Intent?) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        try {
            val account = task.getResult(ApiException::class.java)

            if (account != null) {
                val idToken = account.idToken
                println("AL SANA TOKEN $idToken")
            }
            navigateToMainActivity()
        } catch (e: ApiException) {
            Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT).show()
        }
    }
}