package com.example.falci

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.falci.internalClasses.TransitionToFragment.ReplaceActivityToFragment.replaceProfileActivityToFragment

class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        replaceProfileActivityToFragment(supportFragmentManager, ProfileFragment())
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()

        // Check if there are fragments in the back stack
        if (supportFragmentManager.backStackEntryCount == 0) {
            //overridePendingTransition(R.anim.slide_up, R.anim.slide_down)
            val intent = Intent(this, MainActivity::class.java);startActivity(intent)
        }
    }

}