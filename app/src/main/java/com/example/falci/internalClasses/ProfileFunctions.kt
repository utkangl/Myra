package com.example.falci.internalClasses

import android.content.Context
import com.example.falci.internalClasses.dataClasses.tokensDataClass
import com.example.falci.internalClasses.dataClasses.urls
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class ProfileFunctions {
    object ProfileFunctions{
        fun makeGetProfileRequest(url: String, accessToken: String, context: Context, callback: (String?, Exception?) -> Unit) {

            println("get profile için access token $accessToken")

            val getProfileClient = OkHttpClient()
            val request = Request.Builder()
                .url(url)
                .header("Authorization", "Bearer $accessToken")
                .build()


            getProfileClient.newCall(request).enqueue(object : Callback {


                override fun onFailure(call: Call, e: IOException) {

                    callback(null, e)
                    println(call)
                    println("failure")
                }


                override fun onResponse(call: Call, response: Response) {
                    statusCode = response.code()

                    if (statusCode == 401){
                        println("unauthorized 401, taking new access token")
                        AuthenticationFunctions.PostJsonFunctions.takeNewAccessToken(
                            urls.refreshURL,
                            tokensDataClass.refreshToken,
                            context,
                        ) { responseBody401, exception ->
                            if (responseBody401 != null) {
                                println("yeni access token ${tokensDataClass.accessToken}")
                            } else {
                                println(exception)
                            }
                        }
                    }
                    val responseBody = response.body()?.string()
                    val responseDetail = JSONObject(responseBody!!).optString("detail")

                    println("get profile response bu $responseBody")

                    println(responseDetail)
                    println("response code $statusCode")
                    callback(responseBody, null)
                }
            })
        }
        fun putEditProfileJson(url: String, json: JSONObject, accessToken: String, context: Context, callback: (String?, Exception?) -> Unit) {

            val editProfileClient = OkHttpClient()

            val requestBody = RequestBody.create(
                MediaType.parse("application/json; charset=utf-8"),
                json.toString()
            )

            val request = Request.Builder()
                .url(url)
                .put(requestBody)
                .header("Authorization", "Bearer $accessToken")
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
                            tokensDataClass.refreshToken,
                            context
                        ) { responseBody401, exception ->
                            if (responseBody401 != null) {
                                println(tokensDataClass.accessToken)
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