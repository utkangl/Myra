package com.example.falci

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.falci.internalClasses.TransitionToFragment.ReplaceActivityToFragment.replaceLoginActivityToFragment


class LoginSignupActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_signup)
        // directly navigate to login fragment when this activity is created
        if (!navigateToSignUp) replaceLoginActivityToFragment(supportFragmentManager, Loginfragment())


        if (navigateToSignUp)
            replaceLoginActivityToFragment(supportFragmentManager, SignUpFragment())
            navigateToSignUp = false

    }

}