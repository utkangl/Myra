package com.utkangul.falci

import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.OnBackPressedCallback
import com.airbnb.lottie.LottieAnimationView
import com.utkangul.falci.internalClasses.GetFavsFuncs
import com.utkangul.falci.internalClasses.UserStatusFunctions
import com.utkangul.falci.internalClasses.dataClasses.authenticated
import com.utkangul.falci.internalClasses.dataClasses.controlVariables
import com.utkangul.falci.internalClasses.dataClasses.urls
import com.utkangul.falci.internalClasses.listOfFavCards
import okhttp3.OkHttpClient


var numOfCards = 0

class FavouriteHoroscopesFragment : Fragment() {

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_favourite_horoscopes, container, false)
        controlVariables.favsSwipedDown = false
        numOfCards = 0

        val favHoroscopeLinearLayout = v.findViewById<LinearLayout>(R.id.favourite_horoscopes_linearlayout)
        val favHoroscopeLoadingAnimation = v.findViewById<LottieAnimationView>(R.id.favHoroscopeLoadingAnimation)
        val searchFavHoroscope = v.findViewById<EditText>(R.id.searchFavHoroscope)
        val cancelFavSearchFilter = v.findViewById<ImageButton>(R.id.cancelFavSearchFilter)
        val favouriteHoroscopesScrollview = v.findViewById<ScrollView>(R.id.favourite_horoscopes_scrollview)
        val editFavourites = v.findViewById<TextView>(R.id.editFavourites)

        val getFavsFuncs = GetFavsFuncs(requireActivity())


        controlVariables.swipeBack = false

        Handler(Looper.getMainLooper()).postDelayed({
            getFavsFuncs.getFavouriteHoroscopes(favHoroscopeLoadingAnimation, requireContext(), searchFavHoroscope, cancelFavSearchFilter,
                favHoroscopeLinearLayout, urls.favouriteHoroscopeURL, favouriteHoroscopesScrollview)
        },1500)


        editFavourites.setOnClickListener{
            for (card in listOfFavCards){
                if (card.isEditing){
                    card.invisibleClickDeleteButtons(requireContext())
                    editFavourites.text = "Edit Favourites"
                }
                else{
                    editFavourites.text = "Done"
                    card.visibleClickDeleteButtons(requireContext())
                }
            }
        }

//        var startY : Float = 0F
//        var swipeDistance: Float = 0f
//        favouriteHoroscopesScrollview.setOnTouchListener { _, event ->
//            val inputMethodManager = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//            inputMethodManager.hideSoftInputFromWindow(cancelFavSearchFilter.windowToken, 0)
//            when (event.action) {
//                MotionEvent.ACTION_DOWN -> {
//                    // Save the initial Y position
//                    startY = event.y
//                }
//                MotionEvent.ACTION_MOVE -> {
//                    // Calculate the vertical distance from the initial position
//                    val dy = event.y - startY
//                    println(startY)
//                    println(event.y)
//
//                    if (dy <= -220 && !controlVariables.favsSwipedDown) {
//                        setViewGoneWithAnimation(requireContext(), favHoroscopesContainer)
//                        controlVariables.favsSwipedDown = true
//                    } else if (dy >= 220 && controlVariables.favsSwipedDown) {
//                        setViewVisibleWithAnimation(requireContext(), favHoroscopesContainer)
//                        controlVariables.favsSwipedDown = false
//                    }
//                    swipeDistance = dy
//                }
//                MotionEvent.ACTION_UP -> {
//                    // Handle release if needed
//                }
//            }
//
//            // Return true to indicate that the event has been consumed
//            true
//        }

//        favouriteHoroscopesScrollview.viewTreeObserver.addOnScrollChangedListener {
//            favouriteHoroscopesScrollview.setOnScrollChangeListener { _, _, scrollY, _, oldScrollY ->
//                val scrollDifference = scrollY - oldScrollY
//                println(scrollDifference)
//                when {
//                    scrollDifference > 100 -> {
//                        // Aşağı kaydırma işlemi
//                        if (!controlVariables.favsSwipedDown) {
//                            setViewGoneWithAnimation(requireContext(), favHoroscopesContainer)
//                            controlVariables.favsSwipedDown = true
//                        }
//                    }
//                    scrollDifference < -60 -> {
//                        // Yukarı kaydırma işlemi
//                        if (controlVariables.favsSwipedDown) {
//                            setViewVisibleWithAnimation(requireContext(), favHoroscopesContainer)
//                            controlVariables.favsSwipedDown = false
//                        }
//                    }
//                }
//            }
//        }


        // create callback variable which will handle onBackPressed and navigate to main activity
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val options = ActivityOptions.makeCustomAnimation(requireContext(), R.anim.activity_slide_down, 0)
                val intent = Intent(requireContext(), ProfileActivity::class.java)
                startActivity(intent, options.toBundle())
            }
        }
        // call callback to navigate user to main activity when back button is pressed
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
        return v

    }
//    enum class ScrollDirection {
//        UP, DOWN
//    }

}