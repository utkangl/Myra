package com.utkangul.falci

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.utkangul.falci.internalClasses.TransitionToFragment.ReplaceActivityToFragment.replaceLoginActivityToFragment
import com.utkangul.falci.internalClasses.dataClasses.controlVariables
import java.util.*


class LoginSignupActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.runOnUiThread{
            val languageSharedPreferences: SharedPreferences = application.getSharedPreferences("language_choice", Context.MODE_PRIVATE)
            val languageChoice = languageSharedPreferences.getString("language",null)
            if (!languageChoice.isNullOrEmpty()){
                val locale = Locale(languageChoice)
                Locale.setDefault(locale)
                val config = Configuration()
                config.locale = locale
                resources.updateConfiguration(config, resources.displayMetrics)
            }
        }

        setContentView(R.layout.activity_login_signup)
        // directly navigate to login fragment when this activity is created
        if (!controlVariables.navigateToSignUp) replaceLoginActivityToFragment(supportFragmentManager, LoginFragment())


        if (controlVariables.navigateToSignUp){
            replaceLoginActivityToFragment(supportFragmentManager, SignUpFragment())
            controlVariables.navigateToSignUp = false
        }


    }

}