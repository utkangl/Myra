package com.example.falci

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.falci.internalClasses.TransitionToFragment.ReplaceActivityToFragment.replaceLoginActivityToFragment


class LoginSignupActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_signup)
        replaceLoginActivityToFragment(supportFragmentManager, Loginfragment())
    }


}