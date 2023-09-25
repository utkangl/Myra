package com.example.falci

import android.app.ActivityOptions
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.falci.internalClasses.TransitionToFragment.ReplaceActivityToFragment.replaceProfileActivityToFragment

class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        // directly navigate to profile fragment when this activity is created
        replaceProfileActivityToFragment(supportFragmentManager, ProfileFragment())
    }

     // when user press back button, simply go back if there are fragments in the backstack
    // else navigate user to main activity
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()

        // Check if there are fragments in the back stack
        if (supportFragmentManager.backStackEntryCount == 0) {
            val options = ActivityOptions.makeCustomAnimation(this, R.anim.activity_slide_down, 0)
            val intent = Intent(this, MainActivity::class.java);startActivity(intent,options.toBundle())
        }
    }

}