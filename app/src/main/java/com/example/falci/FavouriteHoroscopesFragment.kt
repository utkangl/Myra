package com.example.falci

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import com.airbnb.lottie.LottieAnimationView
import com.example.falci.internalClasses.AuthenticationFunctions
import com.example.falci.internalClasses.InternalFunctions
import com.example.falci.internalClasses.InternalFunctions.SetVisibilityFunctions.setViewGone
import com.example.falci.internalClasses.dataClasses.*
import com.example.falci.internalClasses.statusCode
import com.google.gson.Gson
import kotlinx.coroutines.*
import okhttp3.*
import java.io.IOException

class FavouriteHoroscopesFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v =  inflater.inflate(R.layout.fragment_favourite_horoscopes, container, false)

        val favHoroscopeLinearLayout = v.findViewById<LinearLayout>(R.id.favourite_horoscopes_linearlayout)
        val favHoroscopeLoadingAnimation = v.findViewById<LottieAnimationView>(R.id.favHoroscopeLoadingAnimation)
        val searchFavHoroscope = v.findViewById<EditText>(R.id.searchFavHoroscope)


        fun getFavouriteHoroscopes(animationView: LottieAnimationView) {
            val gson = Gson()

            val client = OkHttpClient()
            val request = Request.Builder()
                .url(urls.favouriteHoroscopeURL)
                .get()
                .header("Authorization", "Bearer ${tokensDataClass.accessToken}")
                .build()

            CoroutineScope(Dispatchers.IO).launch {
                animationView.post {
                    InternalFunctions.SetVisibilityFunctions.setViewVisible(animationView)
                    animationView.playAnimation()
                }

                try {
                    val response = client.newCall(request).execute()
                    if (response.isSuccessful) {
                        val responseBody = response.body()?.string()
                        statusCode = response.code()
                        delay(2000)

                        if (statusCode == 401) {
                            println("unauthorized 401, taking new access token")
                            AuthenticationFunctions.PostJsonFunctions.takeNewAccessToken(
                                urls.refreshURL,
                                tokensDataClass.refreshToken,
                                requireContext()
                            ) { responseBody401, exception ->
                                if (responseBody401 != null) {
                                    println(tokensDataClass.accessToken)
                                } else {
                                    println(exception)
                                }
                            }
                        }
                        println("get fav horoscopes response body: $responseBody")

                        if (statusCode == 200) {
                            animationView.post {
                                setViewGone(animationView)
                                animationView.cancelAnimation()
                            }
                            println("get fav horoscopes response code $statusCode")
                            val listOfFavouriteHoroscopes = gson.fromJson(responseBody, ListOfFavouriteHoroscopesDataClass::class.java)

                            searchFavHoroscope.setOnEditorActionListener { _, actionId, _ ->
                                if (actionId == EditorInfo.IME_ACTION_SEND) {
                                    val searchText = searchFavHoroscope.text.toString()
                                    favHoroscopeLinearLayout.removeAllViews()

                                    for (fortuneItem in listOfFavouriteHoroscopes.results) {
                                        val summary = fortuneItem.fortune?.prompt?.summary

                                        if (summary?.contains(searchText, ignoreCase = true) == true) {
                                            val favCardView = FavCardView(context!!)
                                            val favCardTitle = favCardView.findViewById<TextView>(R.id.favCardTitle)
                                            val favCardExplanation = favCardView.findViewById<TextView>(R.id.favCardExplanation)

                                            favCardTitle.text = fortuneItem.title
                                            favCardExplanation.text = summary

                                            favCardView.layoutParams = ViewGroup.LayoutParams(
                                                ViewGroup.LayoutParams.MATCH_PARENT,
                                                ViewGroup.LayoutParams.MATCH_PARENT
                                            )

                                            val animation = AnimationUtils.loadAnimation(context, R.anim.slow_slide_down)
                                            favCardView.startAnimation(animation)
                                            favHoroscopeLinearLayout.addView(favCardView)
                                        }
                                    }
                                    true
                                } else {
                                    false
                                }
                            }

                            withContext(Dispatchers.Main) {
                                for (i in listOfFavouriteHoroscopes.results.indices.reversed()) {
                                    val favCardView = FavCardView(context!!)
                                    val favCardTitle = favCardView.findViewById<TextView>(R.id.favCardTitle)
                                    val favCardExplanation = favCardView.findViewById<TextView>(R.id.favCardExplanation)

                                    val fortuneItem = listOfFavouriteHoroscopes.results[i]
                                    val summary = fortuneItem.fortune?.prompt?.summary

                                    favCardTitle.text = fortuneItem.title
                                    favCardExplanation.text = summary

                                    favCardView.layoutParams = ViewGroup.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.MATCH_PARENT
                                    )

                                    val animation = AnimationUtils.loadAnimation(context, R.anim.fragment_slide_down)
                                    favCardView.startAnimation(animation)
                                    favHoroscopeLinearLayout.addView(favCardView)
                                }
                            }


                        }
                    } else {
                        println("Request failed with code ${response.code()}")
                    }
                } catch (e: IOException) {
                    println("Exception $e")
                }
            }
        }

        getFavouriteHoroscopes(favHoroscopeLoadingAnimation)

        return v

    }


}