package com.example.falci

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.airbnb.lottie.LottieAnimationView
import com.example.falci.internalClasses.InternalFunctions
import com.example.falci.internalClasses.dataClasses.*
import com.example.falci.internalClasses.statusCode
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
                        animationView.post {
                            InternalFunctions.SetVisibilityFunctions.setViewGone(animationView)
                            animationView.cancelAnimation()
                        }

                        val responseBody = response.body()?.string()
                        statusCode = response.code()
                        println("get fav horoscopes response body: $responseBody")

                        if (statusCode == 200) {
                            println("get fav horoscopes response code $statusCode")
                            println("before responseBody $listOfFavouriteHoroscopes")
                            val listOfFavouriteHoroscopesDataClass = gson.fromJson(responseBody, ListOfFavouriteHoroscopesDataClass::class.java)
                            println("after responseBody $listOfFavouriteHoroscopesDataClass")
                            println(listOfFavouriteHoroscopesDataClass.results.size)

                            withContext(Dispatchers.Main) {
                                println(listOfFavouriteHoroscopes.count)
                                for (i in listOfFavouriteHoroscopesDataClass.results.indices.reversed()) {
                                    val favCardView = FavCardView(context!!)
                                    val favCardTitle = favCardView.findViewById<TextView>(R.id.favCardTitle)
                                    val favCardExplanation = favCardView.findViewById<TextView>(R.id.favCardExplanation)

                                    val fortuneItem = listOfFavouriteHoroscopesDataClass.results[i]
                                    val summary = fortuneItem.fortune?.prompt?.summary

                                    favCardTitle.text = fortuneItem.title
                                    favCardExplanation.text = summary

                                    favCardView.layoutParams = ViewGroup.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.MATCH_PARENT
                                    )

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