package com.utkangul.falci.internalClasses

import android.app.Activity
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.core.content.ContextCompat.startActivity
import com.airbnb.lottie.LottieAnimationView
import com.utkangul.falci.*
import com.utkangul.falci.HoroscopeDetailFragment.DestroyFavHoroscope.destroyFavHoroscope
import com.utkangul.falci.internalClasses.AuthenticationFunctions.PostJsonFunctions.takeFreshTokens
import com.utkangul.falci.internalClasses.InternalFunctions.SetVisibilityFunctions.setViewGone
import com.utkangul.falci.internalClasses.dataClasses.*
import com.google.gson.Gson
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.util.concurrent.TimeUnit

var listOfFavCards = mutableListOf<FavCardView>()


class GetFavsFuncs {
    private var loadMore = true
    fun getFavouriteHoroscopes(
        animationView: LottieAnimationView,
        context: Context,
        searchFavHoroscope: EditText,
        cancelFavSearchFilter: ImageButton,
        favHoroscopeLinearLayout: LinearLayout,
        getFavsUrl: String,
        favouriteHoroscopesScrollview: ScrollView
    ) {
        val gson = Gson()

         val client = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()

        val request = createFavouriteHoroscopeRequest(getFavsUrl)

        CoroutineScope(Dispatchers.IO).launch {
            showLoadingAnimation(animationView)
            delay(2500)
            val response = client.newCall(request).execute()

            when (val getFavsStatusCode = response.code) {
                401 -> handleUnauthorized(animationView, context, searchFavHoroscope, cancelFavSearchFilter, favHoroscopeLinearLayout, getFavsUrl, favouriteHoroscopesScrollview)
                200 -> {
                    handleSuccessfulResponse(response, gson, animationView, searchFavHoroscope, cancelFavSearchFilter, favHoroscopeLinearLayout, context,favouriteHoroscopesScrollview)

                    var lastScrollY = 0
                    favouriteHoroscopesScrollview.viewTreeObserver.addOnScrollChangedListener {
                        val scrollY = favouriteHoroscopesScrollview.scrollY
                        val scrollViewHeight = favouriteHoroscopesScrollview.height
                        val contentViewHeight = favouriteHoroscopesScrollview.getChildAt(0).height
                        if (contentViewHeight - scrollY - scrollViewHeight <= 100) {
                            if (listOfFavouriteHoroscopes.next.isNullOrEmpty()) println("this is the last page")
                            if (!listOfFavouriteHoroscopes.next.isNullOrEmpty() && numOfCards < listOfFavouriteHoroscopes.count && loadMore) {
                                println(listOfFavouriteHoroscopes.next)
                                getFavouriteHoroscopes(animationView, context, searchFavHoroscope, cancelFavSearchFilter, favHoroscopeLinearLayout, listOfFavouriteHoroscopes.next!!, favouriteHoroscopesScrollview)
                                loadMore = false
                            }
                        }

                        for (i in 0 until favHoroscopeLinearLayout.childCount) {
                            val childView: View = favHoroscopeLinearLayout.getChildAt(i)
                            if (childView is FavCardView) {
                                if (scrollY - lastScrollY > 8 ||  scrollY - lastScrollY < -8){
                                    if (controlVariables.swipeBack) {
                                        SwipeBack.swipeBack(childView)
                                    }
                                }
                            }
                        }
                        lastScrollY = scrollY
                    }
                }
                else ->{
                    println("Response code was: $getFavsStatusCode")
                    Toast.makeText(context, "An unexpected error occured, you are being redirected to main page", Toast.LENGTH_SHORT).show()
                    val options = ActivityOptions.makeCustomAnimation(context, R.anim.activity_slide_down, 0)
                    val intent = Intent(context, ProfileActivity::class.java)
                    startActivity(context,intent,options.toBundle())
                }

            }
        }
    }


    private fun createFavouriteHoroscopeRequest(getFavsUrl: String): Request {
        return Request.Builder()
            .url(getFavsUrl)
            .get()
            .header("Authorization", "Bearer ${tokensDataClass.accessToken}")
            .build()
    }

    private fun showLoadingAnimation(animationView: LottieAnimationView) {
        animationView.post {
            InternalFunctions.SetVisibilityFunctions.setViewVisible(animationView)
            animationView.playAnimation()
        }
    }

    private fun handleUnauthorized(
        animationView: LottieAnimationView,
        context: Context,
        searchFavHoroscope: EditText,
        cancelFavSearchFilter: ImageButton,
        favHoroscopeLinearLayout: LinearLayout,
        getFavsUrl: String,
        favouriteHoroscopesScrollview: ScrollView
    ) {
        takeFreshTokens(urls.refreshURL, context) { responseBody401, exception ->
            if (responseBody401 != null) {
                println(tokensDataClass.accessToken)
                getFavouriteHoroscopes(
                    animationView,
                    context,
                    searchFavHoroscope,
                    cancelFavSearchFilter,
                    favHoroscopeLinearLayout,
                    getFavsUrl,
                    favouriteHoroscopesScrollview
                )
            } else {
                println(exception)
            }
        }
    }

    private fun handleSuccessfulResponse(response: Response, gson: Gson, animationView: LottieAnimationView, searchFavHoroscope: EditText, cancelFavSearchFilter: ImageButton, favHoroscopeLinearLayout: LinearLayout, context: Context,favouriteHoroscopesScrollview: ScrollView) {
        val responseBody = response.body?.string()
        animationView.post {
            setViewGone(animationView)
            animationView.cancelAnimation()
        }

        // Parse the response and update the listOfFavouriteHoroscopes
        listOfFavouriteHoroscopes = gson.fromJson(responseBody, ListOfFavouriteHoroscopesDataClass::class.java)
        for (item in listOfFavouriteHoroscopes.results){
            println(item.id)
        }
//        allFavouriteHoroscopes.addAll(listOfFavouriteHoroscopes.results)
//        println(" size bu: ${allFavouriteHoroscopes.size}")
        loadMore = true

        searchAndDisplayFilteredResults(
            listOfFavouriteHoroscopes,
            searchFavHoroscope,
            cancelFavSearchFilter,
            favHoroscopeLinearLayout,
            context,
            favouriteHoroscopesScrollview,
            animationView,

        )
    }

    private fun searchAndDisplayFilteredResults(
        listOfFavouriteHoroscopes: ListOfFavouriteHoroscopesDataClass,
        searchFavHoroscope: EditText,
        cancelFavSearchFilter: ImageButton,
        favHoroscopeLinearLayout: LinearLayout,
        context: Context,
        favouriteHoroscopesScrollview:ScrollView,
        animationView: LottieAnimationView
    ) {

        CoroutineScope(Dispatchers.Main).launch {
            for (i in listOfFavouriteHoroscopes.results.indices) {
                val fortuneItem = listOfFavouriteHoroscopes.results[i]
                createAndAddFavCardView(context, favHoroscopeLinearLayout, fortuneItem)
                numOfCards += 1
            }
        }

        searchFavHoroscope.setOnEditorActionListener { _, actionId, _ ->
            for (i in 0 until favHoroscopeLinearLayout.childCount) {
                val childView: View = favHoroscopeLinearLayout.getChildAt(i)
                if (childView is FavCardView) {
                    if (controlVariables.swipeBack) {
                        SwipeBack.swipeBack(childView)
                        controlVariables.swipeBack = false
                    }
                }
            }

            if (actionId == EditorInfo.IME_ACTION_SEND) {
                val searchText = searchFavHoroscope.text.toString()
                favHoroscopeLinearLayout.removeAllViews()
                CoroutineScope(Dispatchers.Main).launch {
                    for (fortuneItem in listOfFavouriteHoroscopes.results) {
                        val summary = fortuneItem.fortune?.prompt?.summary
                        val title = fortuneItem.title
                        if (summary?.contains(searchText, ignoreCase = true) == true || title.contains(searchText, ignoreCase = true)) {
                            createAndAddFavCardView(context, favHoroscopeLinearLayout, fortuneItem)
                            val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                            inputMethodManager.hideSoftInputFromWindow(searchFavHoroscope.windowToken, 0)
                        }
                    }
                }
                true
            } else {
                false
            }
        }

        cancelFavSearchFilter.setOnClickListener {
            println("yazdırıyom bak ${searchFavHoroscope.text}")
            if (searchFavHoroscope.text.toString() != "") controlVariables.isSearchTextChanged = true

            if (controlVariables.isSearchTextChanged){
                listOfFavCards = mutableListOf()
                for (i in 0 until favHoroscopeLinearLayout.childCount) {
                    val childView: View = favHoroscopeLinearLayout.getChildAt(i)
                    if (childView is FavCardView) {
                        if (controlVariables.swipeBack) {
                            SwipeBack.swipeBack(childView)
                            controlVariables.swipeBack = false
                        }
                    }
                }
                favHoroscopeLinearLayout.removeAllViews()
                searchFavHoroscope.setText("")
                val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(cancelFavSearchFilter.windowToken, 0)
                CoroutineScope(Dispatchers.Main).launch {
                    getFavouriteHoroscopes(animationView, context, searchFavHoroscope, cancelFavSearchFilter, favHoroscopeLinearLayout, urls.favouriteHoroscopeURL, favouriteHoroscopesScrollview)
                    numOfCards = 0
                }
                controlVariables.isSearchTextChanged = false
            }
        }

    }

    private fun createAndAddFavCardView(
        context: Context,
        favHoroscopeLinearLayout: LinearLayout,
        fortuneItem: FortuneItem,
    ) {
        val summary = fortuneItem.fortune?.prompt?.summary
        val favCardView = FavCardView(context)
        val swipeDeleteButton = favCardView.findViewById<ImageButton>(R.id.swipe_delete_image_button)
        val clickDeleteButton = favCardView.findViewById<ImageButton>(R.id.click_delete_image_button)
        val favCardTitle = favCardView.findViewById<TextView>(R.id.favCardTitle)
        val favCardExplanation = favCardView.findViewById<TextView>(R.id.favCardExplanation)
        val favCardImageBackground = favCardView.findViewById<ImageView>(R.id.favCardImageBackground)


        favCardImageBackground.setBackgroundResource(R.drawable.fav_card_background_career)
        favCardTitle.text = fortuneItem.title
        val fortuneType = fortuneItem.fortune?.type
        println(fortuneType)
        when(fortuneType){
            "career" -> favCardImageBackground.setBackgroundResource(R.drawable.fav_card_background_career)
            "love" -> favCardImageBackground.setBackgroundResource(R.drawable.fav_card_background_love)
            "general" -> favCardImageBackground.setBackgroundResource(R.drawable.fav_card_background_general)
        }

        favCardExplanation.text = summary

        favCardView.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        val animation = AnimationUtils.loadAnimation(context, R.anim.fragment_slide_down)
        favCardView.startAnimation(animation)
        listOfFavCards.add(favCardView)
        favHoroscopeLinearLayout.addView(favCardView)

        favCardView.setOnClickListener {
            println(controlVariables.swipeBack)
            if (!controlVariables.swipeBack) {
                getHoroscopeData.id = fortuneItem.fortune?.id
                getHoroscopeData.thread = fortuneItem.fortune?.prompt?.thread
                getHoroscopeData.good = fortuneItem.fortune?.prompt?.good
                getHoroscopeData.bad = fortuneItem.fortune?.prompt?.bad
                getHoroscopeData.summary = fortuneItem.fortune?.prompt?.summary
                getHoroscopeData.is_favourite = true
                getHoroscopeData.favourite_id = fortuneItem.id
                controlVariables.navigateToHoroscope = true
                controlVariables.navigateBackToProfileActivity = true
                controlVariables.swipeBack = false
                val options = ActivityOptions.makeCustomAnimation(context, R.anim.activity_slide_down, 0)
                val intent = Intent(context, MainActivity::class.java)
                startActivity(context, intent, options.toBundle())
            } else {
                if (System.currentTimeMillis() - timeWhenSwiped > 350){
                    SwipeBack.swipeBack(favCardView)
                } else println(System.currentTimeMillis() - timeWhenSwiped )
            }
        }

        swipeDeleteButton.setOnClickListener {
            val favHoroscopeId = fortuneItem.id
            println(favHoroscopeId)
            destroyFavHoroscope(context = context, activity = Activity(), favouriteThisHoroscope = null, favHoroscopeId = favHoroscopeId)
            favHoroscopeLinearLayout.removeView(favCardView)
            controlVariables.swipeBack = false
        }

        clickDeleteButton.setOnClickListener {
            val favHoroscopeId = fortuneItem.id
            println(favHoroscopeId)
            destroyFavHoroscope(context = context, activity = Activity(), favouriteThisHoroscope = null, favHoroscopeId = favHoroscopeId)
            favHoroscopeLinearLayout.removeView(favCardView)
        }

    }
}