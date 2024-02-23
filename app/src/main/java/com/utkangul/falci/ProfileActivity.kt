package com.utkangul.falci

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.utkangul.falci.internalClasses.TransitionToFragment.ReplaceActivityToFragment.replaceProfileActivityToFragment
import com.utkangul.falci.internalClasses.dataClasses.controlVariables
import java.util.*

class ProfileActivity : AppCompatActivity() {
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

        setContentView(R.layout.activity_profile)
        // directly navigate to profile fragment when this activity is created
        println("favorilere yonendir ${controlVariables.navigateToFavs}")
        if (!controlVariables.navigateToFavs) replaceProfileActivityToFragment(supportFragmentManager, ProfileFragment())
        if (controlVariables.navigateToFavs)  replaceProfileActivityToFragment(supportFragmentManager, FavouriteHoroscopesFragment()); controlVariables.navigateToFavs = false
        if (controlVariables.isFromChangePassword)  replaceProfileActivityToFragment(supportFragmentManager, SettingsFragment()); controlVariables.isFromChangePassword = false
        if (controlVariables.isFromCoinClick)  replaceProfileActivityToFragment(supportFragmentManager, PurchaseFragment()); controlVariables.isFromCoinClick = false

    }

     // when user press back button, simply go back if there are fragments in the backstack
    // else navigate user to main activity
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()

        // Check if there are fragments in the back stack
        if (supportFragmentManager.backStackEntryCount == 0) {
            val options = ActivityOptions.makeCustomAnimation(this, R.anim.activity_slide_down, R.anim.fade_out)
            val intent = Intent(this, MainActivity::class.java);startActivity(intent,options.toBundle())
        }
    }

}