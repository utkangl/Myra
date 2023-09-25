package com.example.falci.internalClasses

import android.content.Context
import android.content.Intent
import androidx.fragment.app.FragmentManager
import com.airbnb.lottie.LottieAnimationView
import com.example.falci.*
import com.example.falci.internalClasses.AuthenticationFunctions.CreateJsonObject.createJsonObject
import com.example.falci.internalClasses.InternalFunctions.SetVisibilityFunctions.setViewGone
import com.example.falci.internalClasses.InternalFunctions.SetVisibilityFunctions.setViewVisible
import com.example.falci.internalClasses.TransitionToFragment.ReplaceActivityToFragment.replaceMainActivityToFragment
import com.example.falci.internalClasses.dataClasses.*
import com.google.gson.Gson
import kotlinx.coroutines.*
import org.json.JSONObject

object HoroscopeFunctions {

    fun getHoroscope(animationView: LottieAnimationView, fm: FragmentManager){
        val getHoroscopeJson = createJsonObject(
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
                        val errorResponseJson = JSONObject(responseBody)
                        println(statusCode)

                        if (statusCode == 200) {
                            getHoroscopeData = gson.fromJson(responseBody, GetHoroscopeData::class.java)

                        }   else {
                            val detail = errorResponseJson.optString("detail")
                            println(detail)
                        }
                    } else exception?.printStackTrace()

                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun getLoveHoroscope(animationView: LottieAnimationView, context: Context){
        val getLoveHoroscopeJson = createJsonObject(
            "type" to postHoroscopeData.type.toString(),
            "time_interval" to postHoroscopeData.time_interval.toString(),
            "lookup_user" to lookupUserJson)

        val gson = Gson()
        println(getLoveHoroscopeJson)

        CoroutineScope(Dispatchers.IO).launch {
            animationView.post {
                setViewVisible(animationView)
                animationView.playAnimation()
            }

            try {
                AuthenticationFunctions.PostJsonFunctions.postJsonWithHeader(
                    urls.getHoroscopeURL,
                    getLoveHoroscopeJson,
                    loginTokens.loginAccessToken
                ) { responseBody, exception ->
                    animationView.post {
                        setViewGone(animationView)
                        animationView.cancelAnimation()
                        navigateToHoroscope = true
                        navigateToHoroscope = true
                        val intent = Intent(context, MainActivity::class.java)
                        context.startActivity(intent)
                    }

                    if (responseBody != null) {
                        val errorResponseJson = JSONObject(responseBody)
                        println(responseBody)
                        println(statusCode)

                        if (statusCode == 200) {
                            getHoroscopeData = gson.fromJson(responseBody, GetHoroscopeData::class.java)

                        }   else {
                            val detail = errorResponseJson.optString("detail")
                            println(detail)
                        }
                    } else exception?.printStackTrace()

                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}