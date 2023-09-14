package com.example.falci

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle


class LoginSignupActivity : AppCompatActivity() {

    private val loginfragment = Loginfragment()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_signup)

        supportFragmentManager.beginTransaction()
            .replace(R.id.login_signup_container, loginfragment).commit()
    }

}