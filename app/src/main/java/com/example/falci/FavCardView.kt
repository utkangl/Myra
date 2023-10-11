package com.example.falci

import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.animation.TranslateAnimation
import android.widget.ImageButton
import android.widget.RelativeLayout
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.example.falci.internalClasses.InternalFunctions.SetVisibilityFunctions.setViewGone
import com.example.falci.internalClasses.InternalFunctions.SetVisibilityFunctions.setViewVisible
import com.example.falci.internalClasses.dataClasses.favouriteHoroscope
import com.example.falci.internalClasses.dataClasses.getHoroscopeData
import okhttp3.*

@SuppressLint("ClickableViewAccessibility")
class FavCardView constructor(
    context: Context,
): RelativeLayout(context){

    private val cardView: CardView
    private val swipeDeleteButton: ImageButton
    private var isSwiped = false
    private var startX: Float = 0f
    private var swipeDistance: Float = 0f
    private val maxSwipeDistance = 160f

    init {
        LayoutInflater.from(context).inflate(R.layout.fav_card_view, this, true)
        cardView = findViewById(R.id.favCard)
        swipeDeleteButton = findViewById(R.id.swipe_delete_image_button)

        favouriteHoroscope.id = getHoroscopeData.favourite_id
        println(getHoroscopeData.favourite_id)

        setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    startX = event.x
                }

                MotionEvent.ACTION_MOVE -> {
                    val dx = event.x - startX
                    if (dx < 0 && !isSwiped) {
                        isSwiped = true
                        val animation = TranslateAnimation(0f, -maxSwipeDistance, 0f, 0f)
                        animation.duration = 200
                        animation.fillAfter = true
                        setViewVisible(swipeDeleteButton)
                        cardView.startAnimation(animation)
                    }
                    swipeDistance = dx
                }

                MotionEvent.ACTION_UP -> {
                    if (!isSwiped){
                        println("click click")
                        performClick()
                        val options = ActivityOptions.makeCustomAnimation(context, R.anim.activity_slide_down, 0)
                        val intent = Intent(context, MainActivity::class.java)
                        ContextCompat.startActivity(context, intent, options.toBundle())
                    }
                    if (isSwiped){
                        isSwiped = false
                        val animation = TranslateAnimation(0f, 0f, 0f, 0f)
                        animation.duration = 500
                        animation.fillAfter = true
                        cardView.startAnimation(animation)
                        setViewGone(swipeDeleteButton)
                    }
                }
            }
            true
        }
    }

}