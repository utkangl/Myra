package com.example.falci

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.animation.TranslateAnimation
import android.widget.ImageButton
import android.widget.RelativeLayout
import androidx.cardview.widget.CardView
import com.example.falci.internalClasses.InternalFunctions.SetVisibilityFunctions.setViewGone
import com.example.falci.internalClasses.InternalFunctions.SetVisibilityFunctions.setViewGoneWithAnimation
import com.example.falci.internalClasses.InternalFunctions.SetVisibilityFunctions.setViewVisible
import com.example.falci.internalClasses.InternalFunctions.SetVisibilityFunctions.setViewVisibleWithAnimation
import com.example.falci.internalClasses.dataClasses.controlVariables
import com.example.falci.internalClasses.dataClasses.favouriteHoroscope
import com.example.falci.internalClasses.dataClasses.getHoroscopeData

var timeWhenSwiped: Long = 0

@SuppressLint("ClickableViewAccessibility")
class FavCardView constructor(
    context: Context,
): RelativeLayout(context) {
    private val cardView: CardView
    private val swipeDeleteButton: ImageButton
    private val clickDeleteButton: ImageButton
    private var startX: Float = 0f
    private var swipeDistance: Float = 0f
    private val maxSwipeDistance = 160f
    private val swipeThreshold = 10
    var isSwiped = false
    var isEditing = false

    init {
        LayoutInflater.from(context).inflate(R.layout.fav_card_view, this, true)
        cardView = findViewById(R.id.favCard)
        swipeDeleteButton = findViewById(R.id.swipe_delete_image_button)
        clickDeleteButton = findViewById(R.id.click_delete_image_button)

        favouriteHoroscope.id = getHoroscopeData.favourite_id
        println(getHoroscopeData.favourite_id)


        setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    startX = event.x
                }

                // on swipe left
                MotionEvent.ACTION_MOVE -> {
                    val dx = event.x - startX
                    if (dx < 0 && !isSwiped && !controlVariables.swipeBack && kotlin.math.abs(dx) >= swipeThreshold && !isEditing) {
                        isSwiped = true
                        controlVariables.swipeBack = true
                        val animation = TranslateAnimation(0f, -maxSwipeDistance, 0f, 0f)
                        animation.duration = 200
                        animation.fillAfter = true
                        setViewVisible(swipeDeleteButton)
                        cardView.startAnimation(animation)
                        timeWhenSwiped = System.currentTimeMillis()
                    }
                    swipeDistance = dx
                }

                // on click
                MotionEvent.ACTION_UP -> {
                    if (!isEditing) {
                        performClick()
                    }
                }
            }
            true
        }
    }


    fun visibleClickDeleteButtons(context: Context){
        if (!isEditing){
            setViewVisibleWithAnimation(context, clickDeleteButton)
            isEditing = true
        }
    }

    fun invisibleClickDeleteButtons(context: Context){
        if (isEditing){
            setViewGoneWithAnimation(context,clickDeleteButton)
            isEditing = false
        }
    }
}

object SwipeBack {
    fun swipeBack(favCardView: FavCardView) {
        if (favCardView.isSwiped && controlVariables.swipeBack) {
            setViewGone(favCardView.findViewById<ImageButton>(R.id.swipe_delete_image_button))
            controlVariables.swipeBack = false
            favCardView.isSwiped = false
            val animation = TranslateAnimation(0f, 0f, 0f, 0f)
            animation.duration = 500
            animation.fillAfter = true
            favCardView.findViewById<CardView>(R.id.favCard).startAnimation(animation)
        }
    }
}