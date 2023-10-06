package com.example.falci.internalClasses

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat.startActivity
import com.example.falci.ProfileActivity
import com.example.falci.R
import com.example.falci.internalClasses.dataClasses.UserProfileDataClass
import com.example.falci.internalClasses.dataClasses.tokensDataClass
import com.example.falci.internalClasses.dataClasses.urls
import com.example.falci.internalClasses.dataClasses.userProfile
import com.google.gson.Gson
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class ProfileFunctions {
    object ProfileFunctions{
        fun makeGetProfileRequest(url: String,  context: Context, callback: (String?, Exception?) -> Unit) {

            println("get profile sending access token ${tokensDataClass.accessToken}")

            val getProfileClient = OkHttpClient()
            val request = Request.Builder()
                .url(url)
                .header("Authorization", "Bearer ${tokensDataClass.accessToken}")
                .build()

            getProfileClient.newCall(request).enqueue(object : Callback {

                override fun onFailure(call: Call, e: IOException) {
                    callback(null, e)
                    println(call)
                    println("failure")
                }
                override fun onResponse(call: Call, response: Response) {
                    val getProfileStatusCode = response.code()
                    val responseBody = response.body()?.string()

                    println("response code $getProfileStatusCode")
                    callback(responseBody, null)

                    if (getProfileStatusCode == 401){
                        println("unauthorized 401, taking new access token")
                        AuthenticationFunctions.PostJsonFunctions.takeNewAccessToken(
                            urls.refreshURL,
                            context,
                        ) { responseBody401, exception ->
                            if (responseBody401 != null) {
                                makeGetProfileRequest(url,  context, callback)
                            } else {
                                println(exception)
                            }
                        }
                    }
                    if (getProfileStatusCode == 200){
                        val gson = Gson()
                        userProfile =  gson.fromJson(responseBody, UserProfileDataClass::class.java)
                        //and navigate user to ProfileActivity
                            val options = ActivityOptions.makeCustomAnimation(context, R.anim.activity_slide_down, 0)
                            val intent = Intent(context, ProfileActivity::class.java)
                            startActivity(context, intent, options.toBundle())

                    }
                }
            })
        }
        fun putEditProfileJson(url: String, json: JSONObject, context: Context, callback: (String?, Exception?) -> Unit) {

            val editProfileClient = OkHttpClient()

            val requestBody = RequestBody.create(
                MediaType.parse("application/json; charset=utf-8"),
                json.toString()
            )

            val request = Request.Builder()
                .url(url)
                .put(requestBody)
                .header("Authorization", "Bearer ${tokensDataClass.accessToken}")
                .build()


            editProfileClient.newCall(request).enqueue(object : Callback {

                override fun onFailure(call: Call, e: IOException) {
                    callback(null, e)

                }

                override fun onResponse(call: Call, response: Response) {
                    val responseBody = response.body()?.string()

                    statusCode = response.code()
                    if (statusCode == 401){
                        println("unauthorized 401, taking new access token")
                        AuthenticationFunctions.PostJsonFunctions.takeNewAccessToken(
                            urls.refreshURL,
                            context
                        ) { responseBody401, exception ->
                            if (responseBody401 != null) {
                                println(tokensDataClass.accessToken)
                                putEditProfileJson(url, json,context, callback)
                            } else {
                                println(exception)
                            }
                        }
                    }

                    if (responseBody != null) {
                        println(responseBody)
                        println(statusCode)
                    }

                    callback(responseBody, null)
                }
            })
        }
    }
}