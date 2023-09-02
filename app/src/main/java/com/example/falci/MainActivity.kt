package com.example.falci

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.cardview.widget.CardView

class MainActivity : AppCompatActivity() {

    lateinit var chatwithmiraButton: ImageView
    lateinit var empty: TextView
    lateinit var burcCard: CardView
    lateinit var tarotFali: CardView
    lateinit var fortuneCookie: CardView

    val loginfragment = Loginfragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        chatwithmiraButton = findViewById(R.id.mainMenuMira)
        empty = findViewById(R.id.empty)
        burcCard = findViewById(R.id.burcCard)
        tarotFali = findViewById(R.id.tarotFali)
        fortuneCookie = findViewById(R.id.fortuneCookie)


        chatwithmiraButton.setOnClickListener {

            println(isLoggedin)

            if (isLoggedin) {
                if (savedInstanceState == null) {
                    supportFragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                        .replace(R.id.main_fragment_container, ChatFragment())
                        .addToBackStack(null)
                        .commit()

                }
            }

            if (!isLoggedin) {
                if (savedInstanceState == null) {
                    supportFragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                        .replace(R.id.main_fragment_container, Loginfragment())
                        .addToBackStack(null)
                        .commit()

                }
            }

            chatwithmiraButton.visibility = View.GONE
            empty.visibility = View.GONE
            burcCard.visibility = View.GONE
            tarotFali.visibility = View.GONE
            fortuneCookie.visibility = View.GONE

        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()

        // Check if there are fragments in the back stack
        if (supportFragmentManager.backStackEntryCount == 0) {
            // No fragments in the back stack, show the button again
            chatwithmiraButton.visibility = View.VISIBLE
            empty.visibility = View.VISIBLE
            burcCard.visibility = View.VISIBLE
            tarotFali.visibility = View.VISIBLE
            fortuneCookie.visibility = View.VISIBLE
        }
    }

}