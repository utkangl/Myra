package com.example.falci.internalClasses

import androidx.fragment.app.FragmentManager
import com.airbnb.lottie.LottieAnimationView
import com.example.falci.HoroscopeDetailFragment
import com.example.falci.internalClasses.InternalFunctions.SetVisibilityFunctions.setViewGone
import com.example.falci.internalClasses.InternalFunctions.SetVisibilityFunctions.setViewVisible
import com.example.falci.internalClasses.TransitionToFragment.ReplaceActivityToFragment.replaceMainActivityToFragment
import com.example.falci.internalClasses.dataClasses.*
import com.example.falci.loginTokens
import com.google.gson.Gson
import kotlinx.coroutines.*
import org.json.JSONObject

object HoroscopeFunctions {

    fun getHoroscope(animationView: LottieAnimationView, fm: FragmentManager){
        val getHoroscopeJson = AuthenticationFunctions.CreateJsonObject.createJsonObject(
            "type" to postHoroscopeData.type.toString(),
            "time_interval" to postHoroscopeData.time_interval.toString()
        )

        println(getHoroscopeJson)
        val gson = Gson()

        CoroutineScope(Dispatchers.IO).launch {
            animationView.post {
                setViewVisible(animationView)
                animationView.playAnimation()
            }

            try {
                AuthenticationFunctions.PostJsonFunctions.postJsonWithHeader(
                    urls.getHoroscopeURL,
                    getHoroscopeJson,
                    loginTokens.loginAccessToken
                ) { responseBody, exception ->
                    animationView.post {
                        setViewGone(animationView)
                        animationView.cancelAnimation()
                        replaceMainActivityToFragment(fm, HoroscopeDetailFragment())

                    }

                    if (responseBody != null) {
                        getHoroscopeData = gson.fromJson(responseBody, GetHoroscopeData::class.java)
                        val errorResponseJson = JSONObject(responseBody)

                        println(statusCode)

                        if (statusCode == 200) {
                            getHoroscopeData.apply {
                                val thread = thread
                                val summary = summary
                                val good= good
                                val bad = bad
                                println(getHoroscopeData)
                            }

                        } else {
                            val detail = errorResponseJson.optString("detail")
                            println(detail)
                        }
                    } else if (exception != null) {
                        exception.printStackTrace()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


}