package com.utkangul.falci

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.utkangul.falci.internalClasses.TransitionToFragment.ReplaceActivityToFragment.replaceLoginActivityToFragment
import com.utkangul.falci.internalClasses.dataClasses.controlVariables


class LoginSignupActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_signup)
        // directly navigate to login fragment when this activity is created
        if (!controlVariables.navigateToSignUp) replaceLoginActivityToFragment(supportFragmentManager, Loginfragment())


        if (controlVariables.navigateToSignUp){
            replaceLoginActivityToFragment(supportFragmentManager, SignUpFragment())
            controlVariables.navigateToSignUp = false
        }


    }

}