package com.example.falci

import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.activity.OnBackPressedCallback
import com.airbnb.lottie.LottieAnimationView
import com.example.falci.internalClasses.GetFavsFuncs

var navigateBackToProfileActivity = false
var navigateToFavs = false

class FavouriteHoroscopesFragment : Fragment() {

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v =  inflater.inflate(R.layout.fragment_favourite_horoscopes, container, false)

        val favHoroscopeLinearLayout = v.findViewById<LinearLayout>(R.id.favourite_horoscopes_linearlayout)
        val favHoroscopeLoadingAnimation = v.findViewById<LottieAnimationView>(R.id.favHoroscopeLoadingAnimation)
        val searchFavHoroscope = v.findViewById<EditText>(R.id.searchFavHoroscope)
        val cancelFavSearchFilter = v.findViewById< ImageButton>(R.id.cancelFavSearchFilter)
        val favouriteHoroscopesScrollview = v.findViewById< ScrollView>(R.id.favourite_horoscopes_scrollview)

        val getFavsFuncs = GetFavsFuncs()

        activity!!.runOnUiThread {
            getFavsFuncs.getFavouriteHoroscopes(favHoroscopeLoadingAnimation, requireContext(), searchFavHoroscope, cancelFavSearchFilter, favHoroscopeLinearLayout )
        }

        favouriteHoroscopesScrollview.setOnTouchListener { _, _ ->
            val inputMethodManager = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(cancelFavSearchFilter.windowToken, 0)
            false
        }

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


}