package com.utkangul.falci.internalClasses

import android.app.Activity
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.FragmentManager
import com.airbnb.lottie.LottieAnimationView
import com.utkangul.falci.*
import com.utkangul.falci.internalClasses.AuthenticationFunctions.CreateJsonObject.createJsonObject
import com.utkangul.falci.internalClasses.InternalFunctions.SetVisibilityFunctions.setViewGone
import com.utkangul.falci.internalClasses.InternalFunctions.SetVisibilityFunctions.setViewVisible
import com.utkangul.falci.internalClasses.TransitionToFragment.ReplaceActivityToFragment.replaceMainActivityToFragment
import com.utkangul.falci.internalClasses.dataClasses.*
import com.google.gson.Gson
import kotlinx.coroutines.*
import org.json.JSONObject

object HoroscopeFunctions {

    fun getHoroscope(animationView: LottieAnimationView, fm: FragmentManager, context: Context, activity: Activity) {
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

                        when (statusCode) {
                            200 -> {
                                getHoroscopeData = gson.fromJson(responseBody, GetHoroscopeData::class.java)
                                replaceMainActivityToFragment(fm, HoroscopeDetailFragment())
                                println(getHoroscopeData)
                            }
                            401 -> {
                                AuthenticationFunctions.PostJsonFunctions.takeFreshTokens(urls.refreshURL, context) { responseBody401, exception401 ->
                                    if (exception401 != null) exception401.printStackTrace()
                                    else {
                                        if (responseBody401 != null) {
                                            getHoroscope(animationView, fm, context, activity)
                                        }
                                    }
                                }
                            }
                            else -> {
                                val errorDetail = responseJson.optString("detail")
                                activity.runOnUiThread { Toast.makeText(context, "unexpected error: $statusCode", Toast.LENGTH_SHORT).show() }
                                activity.runOnUiThread { Toast.makeText(context, " redirecting to main screen ...", Toast.LENGTH_SHORT).show() }
                                println(errorDetail)
                                val options = ActivityOptions.makeCustomAnimation(context, R.anim.activity_slide_down, 0)
                                val intent = Intent(context, MainActivity::class.java)
                                startActivity(context, intent, options.toBundle())
                            }
                        }

                    }
                    if (responseBody == null) {
                        println("response body null"); println("exception is : $exception")
                    }

                }
            } catch (e: Exception) {
                e.printStackTrace(); println("(try) catch in get horoscope")
            }
        }
    }

    fun getLoveHoroscope(animationView: LottieAnimationView, context: Context, id: Int, fm: FragmentManager, activity: Activity) {
        val getLoveHoroscopeJson = createJsonObject(
            "type" to postHoroscopeData.type.toString(),
            "time_interval" to postHoroscopeData.time_interval.toString(),
            "lookup_user" to id
        )

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
                    }
                    if (responseBody != null) {
                        val errorResponseJson = JSONObject(responseBody)
                        println(responseBody)
                        println(statusCode)

                        when (statusCode) {
                            200 -> {
                                getHoroscopeData = gson.fromJson(responseBody, GetHoroscopeData::class.java)
                                if (!controlVariables.isFromCompleteLookup) {
                                    replaceMainActivityToFragment(fm, HoroscopeDetailFragment())
                                }

                                if (controlVariables.isFromCompleteLookup) {
                                    controlVariables.navigateToHoroscope = true
                                    controlVariables.isFromCompleteLookup = false
                                    val options = ActivityOptions.makeCustomAnimation(context, R.anim.activity_slide_down, 0)
                                    val intent = Intent(context, MainActivity::class.java)
                                    context.startActivity(intent, options.toBundle())
                                }
                            }

                            401 -> {
                                AuthenticationFunctions.PostJsonFunctions.takeFreshTokens(urls.refreshURL, context) { responseBody401, exception401 ->
                                    if (exception401 != null) exception401.printStackTrace()
                                    else {
                                        if (responseBody401 != null) {
                                            getLoveHoroscope(animationView, context, id, fm,activity)
                                        }
                                    }
                                }
                            }

                            else -> {
                                val errorDetail = errorResponseJson.optString("detail")
                                activity.runOnUiThread { Toast.makeText(context, "unexpected error: $statusCode", Toast.LENGTH_SHORT).show() }
                                activity.runOnUiThread { Toast.makeText(context, " redirecting to main screen ...", Toast.LENGTH_SHORT).show() }
                                println(errorDetail)
                                val options = ActivityOptions.makeCustomAnimation(context, R.anim.activity_slide_down, 0)
                                val intent = Intent(context, MainActivity::class.java)
                                startActivity(context, intent, options.toBundle())
                            }
                        }
                    } else exception?.printStackTrace()
                }
            } catch (e: Exception) { e.printStackTrace() }
        }
    }
}