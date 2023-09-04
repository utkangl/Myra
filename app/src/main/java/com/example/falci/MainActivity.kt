package com.example.falci

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.cardview.widget.CardView

class MainActivity : AppCompatActivity() {

    lateinit var chatwithmiraButton: ImageView
    lateinit var empty: TextView
    lateinit var burcCard: CardView
    lateinit var tarotFali: CardView
    lateinit var fortuneCookie: CardView
    lateinit var backarrowcard: CardView
    lateinit var generalSign: CardView
    lateinit var loveSign: CardView
    lateinit var careerSign: CardView
    lateinit var backArrow: ImageButton
    lateinit var dailyButton: AppCompatButton
    lateinit var monthlyButton: AppCompatButton
    lateinit var yearlyButton: AppCompatButton

    val loginfragment = Loginfragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        chatwithmiraButton = findViewById(R.id.mainMenuMira)
        empty = findViewById(R.id.empty)
        burcCard = findViewById(R.id.burcCard)
        tarotFali = findViewById(R.id.tarotFali)
        fortuneCookie = findViewById(R.id.fortuneCookie)
        backarrowcard = findViewById(R.id.backarrowcard)
        generalSign = findViewById(R.id.generalSign)
        loveSign = findViewById(R.id.loveSign)
        careerSign = findViewById(R.id.careerSign)
        backArrow = findViewById(R.id.back_arrow)
        dailyButton = findViewById(R.id.dailyButton)
        monthlyButton = findViewById(R.id.monthlyButton)
        yearlyButton = findViewById(R.id.yearlyButton)


        chatwithmiraButton.setOnClickListener {

            println(isLoggedin)

            if (isLoggedin) {
                if (savedInstanceState == null) {
                    supportFragmentManager.beginTransaction()
                        .setCustomAnimations(
                            R.anim.fade_in,
                            R.anim.fade_out,
                            R.anim.fade_in,
                            R.anim.fade_out
                        )
                        .replace(R.id.main_fragment_container, ChatFragment())
                        .addToBackStack(null)
                        .commit()

                }
            }

            if (!isLoggedin) {
                if (savedInstanceState == null) {
                    supportFragmentManager.beginTransaction()
                        .setCustomAnimations(
                            R.anim.fade_in,
                            R.anim.fade_out,
                            R.anim.fade_in,
                            R.anim.fade_out
                        )
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

        burcCard.setOnClickListener {
            // Diğer bileşenleri gizle
            chatwithmiraButton.visibility = View.GONE
            empty.visibility = View.GONE
            tarotFali.visibility = View.GONE
            fortuneCookie.visibility = View.GONE
            dailyButton.visibility = View.GONE
            monthlyButton.visibility = View.GONE
            yearlyButton.visibility = View.GONE

            backarrowcard.visibility = View.VISIBLE
            generalSign.visibility = View.VISIBLE
            loveSign.visibility = View.VISIBLE
            careerSign.visibility = View.VISIBLE

            val scale = resources.displayMetrics.density

            val dpValueWidth = 370
            val newWidth = (dpValueWidth * scale + 0.5f).toInt()

            val dpValueHeight = 550
            val newHeight = (dpValueHeight * scale + 0.5f).toInt()

            val dpValueMarginTop = 150 // Yatayda ortalanmanın yukarısında olacak şekilde ayarlayın
            val newMarginTop = (dpValueMarginTop * scale + 0.5f).toInt()

            val params = burcCard.layoutParams as RelativeLayout.LayoutParams

            // Yatayda ortalanmış
            params.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE)

            // Yatayda biraz yukarıda
            params.addRule(
                RelativeLayout.ABOVE,
                R.id.empty
            ) // Yüksekliği yukarıdaki view'a göre ayarlayın

            // Genişlik ve yüksekliği ayarla
            params.width = newWidth
            params.height = newHeight

            // MarginTop ayarla
            params.topMargin = newMarginTop

            // Layout parametrelerini güncelle
            burcCard.layoutParams = params

            val generalSignCardparams = generalSign.layoutParams as RelativeLayout.LayoutParams
            val loveSignCardparams = loveSign.layoutParams as RelativeLayout.LayoutParams
            val careerSignCardparams = careerSign.layoutParams as RelativeLayout.LayoutParams

            val dpMarginBottom = 25
            val newMarginBottom = (dpMarginBottom * scale + 0.5f).toInt()

            generalSignCardparams.bottomMargin = newMarginBottom
            loveSignCardparams.bottomMargin = newMarginBottom
            careerSignCardparams.bottomMargin = newMarginBottom

            generalSign.layoutParams = generalSignCardparams
            loveSign.layoutParams = loveSignCardparams
            careerSign.layoutParams = careerSignCardparams

        }

        backArrow.setOnClickListener {

            chatwithmiraButton.visibility = View.VISIBLE
            empty.visibility = View.VISIBLE
            tarotFali.visibility = View.VISIBLE
            fortuneCookie.visibility = View.VISIBLE

            backarrowcard.visibility = View.GONE
            generalSign.visibility = View.GONE
            loveSign.visibility = View.GONE
            careerSign.visibility = View.GONE
            dailyButton.visibility = View.GONE
            monthlyButton.visibility = View.GONE
            yearlyButton.visibility = View.GONE


            val scale = resources.displayMetrics.density
            val oldWidth = (340 * scale + 0.5f).toInt()
            val oldHeight = (250 * scale + 0.5f).toInt()

            val params = burcCard.layoutParams as RelativeLayout.LayoutParams

            params.width = oldWidth
            params.height = oldHeight


            val generalSignCardparams = generalSign.layoutParams as RelativeLayout.LayoutParams
            val loveSignCardparams = loveSign.layoutParams as RelativeLayout.LayoutParams
            val careerSignCardparams = careerSign.layoutParams as RelativeLayout.LayoutParams

            val dpMarginBottom = 25
            val newMarginBottom = (dpMarginBottom * scale + 0.5f).toInt()

            generalSignCardparams.bottomMargin = newMarginBottom
            loveSignCardparams.bottomMargin = newMarginBottom
            careerSignCardparams.bottomMargin = newMarginBottom

            generalSign.layoutParams = generalSignCardparams
            loveSign.layoutParams = loveSignCardparams
            careerSign.layoutParams = careerSignCardparams

            generalSign.setCardBackgroundColor(getColor(R.color.passivesigncard))
            loveSign.setCardBackgroundColor(getColor(R.color.passivesigncard))
            careerSign.setCardBackgroundColor(getColor(R.color.passivesigncard))

        }

        generalSign.setOnClickListener {

            dailyButton.visibility = View.VISIBLE
            monthlyButton.visibility = View.VISIBLE
            yearlyButton.visibility = View.VISIBLE

            val scale = resources.displayMetrics.density

            val dpValueHeight = 630
            val newHeight = (dpValueHeight * scale + 0.5f).toInt()

            val dpMarginBottom = 105
            val newMarginBottom = (dpMarginBottom * scale + 0.5f).toInt()

            val burcCardparams = burcCard.layoutParams as RelativeLayout.LayoutParams

            val generalSignCardparams = generalSign.layoutParams as RelativeLayout.LayoutParams
            val loveSignCardparams = loveSign.layoutParams as RelativeLayout.LayoutParams
            val careerSignCardparams = careerSign.layoutParams as RelativeLayout.LayoutParams

            burcCardparams.height = newHeight
            burcCard.layoutParams = burcCardparams

            generalSignCardparams.bottomMargin = newMarginBottom
            loveSignCardparams.bottomMargin = newMarginBottom
            careerSignCardparams.bottomMargin = newMarginBottom

            generalSign.layoutParams = generalSignCardparams
            loveSign.layoutParams = loveSignCardparams
            careerSign.layoutParams = careerSignCardparams

            generalSign.setCardBackgroundColor(getColor(R.color.nameinputbackground))
            loveSign.setCardBackgroundColor(getColor(R.color.passivesigncard))
            careerSign.setCardBackgroundColor(getColor(R.color.passivesigncard))

            dailyButton.setBackgroundResource(R.drawable.button_passive)
            monthlyButton.setBackgroundResource(R.drawable.button_passive)
            yearlyButton.setBackgroundResource(R.drawable.button_passive)


        }


        loveSign.setOnClickListener {

            dailyButton.visibility = View.VISIBLE
            monthlyButton.visibility = View.VISIBLE
            yearlyButton.visibility = View.VISIBLE

            val scale = resources.displayMetrics.density

            val dpValueHeight = 630
            val newHeight = (dpValueHeight * scale + 0.5f).toInt()

            val dpMarginBottom = 105
            val newMarginBottom = (dpMarginBottom * scale + 0.5f).toInt()

            val burcCardparams = burcCard.layoutParams as RelativeLayout.LayoutParams

            val generalSignCardparams = generalSign.layoutParams as RelativeLayout.LayoutParams
            val loveSignCardparams = loveSign.layoutParams as RelativeLayout.LayoutParams
            val careerSignCardparams = careerSign.layoutParams as RelativeLayout.LayoutParams

            burcCardparams.height = newHeight
            burcCard.layoutParams = burcCardparams

            generalSignCardparams.bottomMargin = newMarginBottom
            loveSignCardparams.bottomMargin = newMarginBottom
            careerSignCardparams.bottomMargin = newMarginBottom

            generalSign.layoutParams = generalSignCardparams
            loveSign.layoutParams = loveSignCardparams
            careerSign.layoutParams = careerSignCardparams

            generalSign.setCardBackgroundColor(getColor(R.color.passivesigncard))
            loveSign.setCardBackgroundColor(getColor(R.color.nameinputbackground))
            careerSign.setCardBackgroundColor(getColor(R.color.passivesigncard))

            dailyButton.setBackgroundResource(R.drawable.button_passive)
            monthlyButton.setBackgroundResource(R.drawable.button_passive)
            yearlyButton.setBackgroundResource(R.drawable.button_passive)


        }


        careerSign.setOnClickListener {

            dailyButton.visibility = View.VISIBLE
            monthlyButton.visibility = View.VISIBLE
            yearlyButton.visibility = View.VISIBLE

            val scale = resources.displayMetrics.density

            val dpValueHeight = 630
            val newHeight = (dpValueHeight * scale + 0.5f).toInt()

            val dpMarginBottom = 105
            val newMarginBottom = (dpMarginBottom * scale + 0.5f).toInt()

            val burcCardparams = burcCard.layoutParams as RelativeLayout.LayoutParams

            val generalSignCardparams = generalSign.layoutParams as RelativeLayout.LayoutParams
            val loveSignCardparams = loveSign.layoutParams as RelativeLayout.LayoutParams
            val careerSignCardparams = careerSign.layoutParams as RelativeLayout.LayoutParams

            burcCardparams.height = newHeight
            burcCard.layoutParams = burcCardparams

            generalSignCardparams.bottomMargin = newMarginBottom
            loveSignCardparams.bottomMargin = newMarginBottom
            careerSignCardparams.bottomMargin = newMarginBottom

            generalSign.layoutParams = generalSignCardparams
            loveSign.layoutParams = loveSignCardparams
            careerSign.layoutParams = careerSignCardparams

            generalSign.setCardBackgroundColor(getColor(R.color.passivesigncard))
            loveSign.setCardBackgroundColor(getColor(R.color.passivesigncard))
            careerSign.setCardBackgroundColor(getColor(R.color.nameinputbackground))

            dailyButton.setBackgroundResource(R.drawable.button_passive)
            monthlyButton.setBackgroundResource(R.drawable.button_passive)
            yearlyButton.setBackgroundResource(R.drawable.button_passive)

        }

        dailyButton.setOnClickListener {
            dailyButton.setBackgroundResource(R.drawable.common_next_button)
            monthlyButton.setBackgroundResource(R.drawable.button_passive)
            yearlyButton.setBackgroundResource(R.drawable.button_passive)
        }

        monthlyButton.setOnClickListener {
            monthlyButton.setBackgroundResource(R.drawable.common_next_button)
            dailyButton.setBackgroundResource(R.drawable.button_passive)
            yearlyButton.setBackgroundResource(R.drawable.button_passive)
        }

        yearlyButton.setOnClickListener {
            yearlyButton.setBackgroundResource(R.drawable.common_next_button)
            dailyButton.setBackgroundResource(R.drawable.button_passive)
            monthlyButton.setBackgroundResource(R.drawable.button_passive)
        }

    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()

        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

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