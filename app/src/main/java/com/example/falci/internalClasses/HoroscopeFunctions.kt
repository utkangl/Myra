package com.example.falci.internalClasses

import android.app.ActivityOptions
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

    fun getHoroscope(animationView: LottieAnimationView, fm: FragmentManager, context: Context){
        val getHoroscopeJson = createJsonObject(
            "type" to postHoroscopeData.type.toString(),
            "time_interval" to postHoroscopeData.time_interval.toString()
        )


        println("sending this json to get horoscope $getHoroscopeJson")


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
                    context
                ) { responseBody, exception ->
                    animationView.post {
                        setViewGone(animationView)
                        animationView.cancelAnimation()
                    }

                    if (responseBody != null) {
                        val responseJson = JSONObject(responseBody)
                        println(statusCode)

                        if (statusCode == 200) {
                            getHoroscopeData = gson.fromJson(responseBody, GetHoroscopeData::class.java)
                            replaceMainActivityToFragment(fm, HoroscopeDetailFragment())
                            println(getHoroscopeData)

                        }else {
                            val errorDetail = responseJson.optString("detail")
                            println(errorDetail)
                        }

                    }
                    if (responseBody == null) {println("response body null"); println("exception is : $exception")}

                }
            } catch (e: Exception) {
                e.printStackTrace(); println("(try) catch in get horoscope")
            }
        }
    }

    fun getLoveHoroscope(animationView: LottieAnimationView, context: Context, id: Int){
        val getLoveHoroscopeJson = createJsonObject(
            "type" to postHoroscopeData.type.toString(),
            "time_interval" to postHoroscopeData.time_interval.toString(),
            "lookup_user" to id)

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
                    context
                ) { responseBody, exception ->
                    animationView.post {
                        setViewGone(animationView)
                        animationView.cancelAnimation()
                        controlVariables.navigateToHoroscope = true
                        val options = ActivityOptions.makeCustomAnimation(context, R.anim.activity_slide_down, 0)
                        val intent = Intent(context, MainActivity::class.java)
                        context.startActivity(intent, options.toBundle())

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