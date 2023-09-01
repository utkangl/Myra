package com.example.falci

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.AppCompatButton

class MainActivity : AppCompatActivity() {

    lateinit var chatwithmiraButton: AppCompatButton
    val loginfragment = Loginfragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        chatwithmiraButton = findViewById(R.id.chatwithmira)

        chatwithmiraButton.setOnClickListener {

            println(isLoggedin)

            if (isLoggedin) {
                if (savedInstanceState == null) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_fragment_container, ChatFragment())
                        .addToBackStack(null)
                        .commit()

                }
            }

            if (!isLoggedin) {
                if (savedInstanceState == null) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_fragment_container, Loginfragment())
                        .addToBackStack(null)
                        .commit()

                }
            }

            chatwithmiraButton.visibility = View.GONE

        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()

        // Check if there are fragments in the back stack
        if (supportFragmentManager.backStackEntryCount == 0) {
            // No fragments in the back stack, show the button again
            chatwithmiraButton.visibility = View.VISIBLE
        }
    }

}