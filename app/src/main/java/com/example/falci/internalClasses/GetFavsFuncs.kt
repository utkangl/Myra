package com.example.falci.internalClasses

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.core.content.ContextCompat.startActivity
import com.airbnb.lottie.LottieAnimationView
import com.example.falci.*
import com.example.falci.internalClasses.AuthenticationFunctions.PostJsonFunctions.takeFreshTokens
import com.example.falci.internalClasses.InternalFunctions.SetVisibilityFunctions.setViewGone
import com.example.falci.internalClasses.dataClasses.*
import com.google.gson.Gson
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

class GetFavsFuncs {
var loadMore = true
private var allFavouriteHoroscopes = mutableListOf<FortuneItem>()
     fun getFavouriteHoroscopes(animationView: LottieAnimationView,context: Context, searchFavHoroscope: EditText, cancelFavSearchFilter: ImageButton,favHoroscopeLinearLayout: LinearLayout, getFavsUrl: String, favouriteHoroscopesScrollview: ScrollView) {
        val gson = Gson()
        val client = OkHttpClient()
        val request = createFavouriteHoroscopeRequest(getFavsUrl)

        CoroutineScope(Dispatchers.IO).launch {
            showLoadingAnimation(animationView)
            val response = client.newCall(request).execute()
            val getFavsStatusCode = response.code()

            when (getFavsStatusCode) {
                401 -> handleUnauthorized(animationView,context,searchFavHoroscope,cancelFavSearchFilter,favHoroscopeLinearLayout, getFavsUrl,favouriteHoroscopesScrollview)
                200 -> {
                    handleSuccessfulResponse(response, gson, animationView, searchFavHoroscope, cancelFavSearchFilter, favHoroscopeLinearLayout, context)
                    favouriteHoroscopesScrollview.viewTreeObserver.addOnScrollChangedListener {
                        val scrollY = favouriteHoroscopesScrollview.scrollY
                        val scrollViewHeight = favouriteHoroscopesScrollview.height
                        val contentViewHeight = favouriteHoroscopesScrollview.getChildAt(0).height
                        if (contentViewHeight - scrollY - scrollViewHeight <= 100) {
                            if (listOfFavouriteHoroscopes.next.isNullOrEmpty()) println("this is the last page")
                            if (!listOfFavouriteHoroscopes.next.isNullOrEmpty() && numOfCards < listOfFavouriteHoroscopes.count && loadMore){
                                println(listOfFavouriteHoroscopes.next)
                                getFavouriteHoroscopes(animationView, context, searchFavHoroscope, cancelFavSearchFilter, favHoroscopeLinearLayout, listOfFavouriteHoroscopes.next!!, favouriteHoroscopesScrollview)
                                loadMore = false
                            }
                        }
                    }
                }
                else -> println("Response code was: $getFavsStatusCode")
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

    private  fun showLoadingAnimation(animationView: LottieAnimationView) {
        animationView.post {
            InternalFunctions.SetVisibilityFunctions.setViewVisible(animationView)
            animationView.playAnimation()
        }
    }

    private  fun handleUnauthorized(animationView: LottieAnimationView, context: Context,searchFavHoroscope: EditText, cancelFavSearchFilter: ImageButton, favHoroscopeLinearLayout: LinearLayout, getFavsUrl: String,favouriteHoroscopesScrollview: ScrollView) {
        takeFreshTokens(urls.refreshURL, context) { responseBody401, exception ->
            if (responseBody401 != null) {
                println(tokensDataClass.accessToken)
                getFavouriteHoroscopes(animationView,context, searchFavHoroscope,cancelFavSearchFilter,favHoroscopeLinearLayout,getFavsUrl, favouriteHoroscopesScrollview)
            } else {
                println(exception)
            }
        }
    }

    private suspend fun handleSuccessfulResponse(
        response: Response,
        gson: Gson,
        animationView: LottieAnimationView,
        searchFavHoroscope: EditText,
        cancelFavSearchFilter: ImageButton,
        favHoroscopeLinearLayout: LinearLayout,
        context: Context
    ) {
        val responseBody = response.body()?.string()
        delay(1000)

        animationView.post {
            setViewGone(animationView)
            animationView.cancelAnimation()
        }

        // Parse the response and update the listOfFavouriteHoroscopes
        listOfFavouriteHoroscopes = gson.fromJson(responseBody, ListOfFavouriteHoroscopesDataClass::class.java)
        allFavouriteHoroscopes.addAll(listOfFavouriteHoroscopes.results)
        println(" size bu: ${allFavouriteHoroscopes.size}")
        loadMore = true

        searchAndDisplayFilteredResults(
            listOfFavouriteHoroscopes,
            searchFavHoroscope,
            cancelFavSearchFilter,
            favHoroscopeLinearLayout,
            context
        )
    }

    private  fun searchAndDisplayFilteredResults(
        listOfFavouriteHoroscopes: ListOfFavouriteHoroscopesDataClass,
        searchFavHoroscope: EditText,
        cancelFavSearchFilter: ImageButton,
        favHoroscopeLinearLayout: LinearLayout,
        context: Context
    ) {
        searchFavHoroscope.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                val searchText = searchFavHoroscope.text.toString()
                favHoroscopeLinearLayout.removeAllViews()
                CoroutineScope(Dispatchers.Main).launch {
                    for (fortuneItem in listOfFavouriteHoroscopes.results) {
                        val summary = fortuneItem.fortune?.prompt?.summary

                        if (summary?.contains(searchText, ignoreCase = true) == true) {
                            createAndAddFavCardView(context, favHoroscopeLinearLayout, fortuneItem)
                        }
                    }
                }
                true
            } else {
                false
            }
        }
        cancelFavSearchFilter.setOnClickListener {
            if (searchFavHoroscope.text.toString() != "") {
                favHoroscopeLinearLayout.removeAllViews()
                searchFavHoroscope.setText("")
                val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(cancelFavSearchFilter.windowToken, 0)
                CoroutineScope(Dispatchers.Main).launch {
                    for (fortuneItem in allFavouriteHoroscopes.reversed()) {
                        createAndAddFavCardView(context, favHoroscopeLinearLayout, fortuneItem)
                    }
                }
            } else {
                val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(cancelFavSearchFilter.windowToken, 0)
                println("no keyword was given")
            }
        }
        CoroutineScope(Dispatchers.Main).launch {
                for (i in listOfFavouriteHoroscopes.results.indices.reversed()) {
                    val fortuneItem = listOfFavouriteHoroscopes.results[i]
                    createAndAddFavCardView(context, favHoroscopeLinearLayout, fortuneItem)
                    println(fortuneItem.id)
                    numOfCards += 1
                }
        }
    }

    private  fun createAndAddFavCardView(
        context: Context,
        favHoroscopeLinearLayout: LinearLayout,
        fortuneItem: FortuneItem,
    ) {
        val summary = fortuneItem.fortune?.prompt?.summary
        val favCardView = FavCardView(context)
        val favCardTitle = favCardView.findViewById<TextView>(R.id.favCardTitle)
        val favCardExplanation = favCardView.findViewById<TextView>(R.id.favCardExplanation)

        favCardTitle.text = fortuneItem.title
        favCardExplanation.text = summary

        favCardView.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        val animation = AnimationUtils.loadAnimation(context, R.anim.fragment_slide_down)
        favCardView.startAnimation(animation)
        favHoroscopeLinearLayout.addView(favCardView)

        favCardView.setOnClickListener {
            getHoroscopeData.id = fortuneItem.fortune?.id
            getHoroscopeData.thread = fortuneItem.fortune?.prompt?.thread
            getHoroscopeData.good = fortuneItem.fortune?.prompt?.good
            getHoroscopeData.bad = fortuneItem.fortune?.prompt?.bad
            getHoroscopeData.summary = fortuneItem.fortune?.prompt?.summary
            getHoroscopeData.is_favourite = true
            getHoroscopeData.favourite_id = fortuneItem.id
            navigateToHoroscope = true
            navigateBackToProfileActivity = true

            val options = ActivityOptions.makeCustomAnimation(context, R.anim.activity_slide_down, 0)
            val intent = Intent(context, MainActivity::class.java)
            startActivity(context, intent, options.toBundle())
        }
    }
}