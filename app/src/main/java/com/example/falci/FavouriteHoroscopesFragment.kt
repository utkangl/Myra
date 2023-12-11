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
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.activity.OnBackPressedCallback
import com.airbnb.lottie.LottieAnimationView
import com.example.falci.internalClasses.GetFavsFuncs
import com.example.falci.internalClasses.dataClasses.controlVariables
import com.example.falci.internalClasses.dataClasses.listOfFavouriteHoroscopes
import com.example.falci.internalClasses.dataClasses.urls
import com.example.falci.internalClasses.listOfFavCards
import com.example.falci.internalClasses.InternalFunctions.SetVisibilityFunctions.setViewGoneWithAnimation
import com.example.falci.internalClasses.InternalFunctions.SetVisibilityFunctions.setViewVisibleWithAnimation


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
        val favHoroscopesContainer = v.findViewById<RelativeLayout>(R.id.favHoroscopesContainer)

        val getFavsFuncs = GetFavsFuncs()


        controlVariables.swipeBack = false

        getFavsFuncs.getFavouriteHoroscopes(favHoroscopeLoadingAnimation, requireContext(), searchFavHoroscope, cancelFavSearchFilter, favHoroscopeLinearLayout, urls.favouriteHoroscopeURL, favouriteHoroscopesScrollview)

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

        favouriteHoroscopesScrollview.setOnTouchListener { _, _ ->
            val inputMethodManager = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(cancelFavSearchFilter.windowToken, 0)
        }

        favouriteHoroscopesScrollview.viewTreeObserver.addOnScrollChangedListener {

            var lastScrollPosition = 0

                val scrollY = favouriteHoroscopesScrollview.scrollY
                val scrollDirection = if (scrollY > lastScrollPosition) {
                    ScrollDirection.DOWN
                } else {
                    ScrollDirection.UP
                }

                when {
                    scrollDirection == ScrollDirection.DOWN && scrollY > 400 -> {
                        println("suraya girdim ama değişken false değil")
                        println("degisken ${controlVariables.favsSwipedDown}")
                        if (!controlVariables.favsSwipedDown) {
                            setViewGoneWithAnimation(requireContext(), favHoroscopesContainer)
                            controlVariables.favsSwipedDown = true
                        }
                    }
                    scrollDirection == ScrollDirection.UP && scrollY > -150 -> {
                        println("buraya girdim ama değişken true değil")
                        println("degisken ${controlVariables.favsSwipedDown} ve yukarı kaydırdın")
                        if (controlVariables.favsSwipedDown) {
                            println("su an visible yapmam lazm aq")
                            setViewVisibleWithAnimation(requireContext(), favHoroscopesContainer)
                            controlVariables.favsSwipedDown = false
                        }
                    }
                }
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
    enum class ScrollDirection {
        UP, DOWN
    }

}