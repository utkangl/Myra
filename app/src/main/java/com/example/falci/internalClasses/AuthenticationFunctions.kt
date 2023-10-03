package com.example.falci.internalClasses

import android.content.Context
import com.example.falci.internalClasses.AuthenticationFunctions.CreateJsonObject.createJsonObject
import com.example.falci.internalClasses.dataClasses.JWTTokensDataClass
import com.example.falci.internalClasses.dataClasses.tokensDataClass
import com.example.falci.internalClasses.dataClasses.urls
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit
import kotlin.properties.Delegates


var statusCode by Delegates.notNull<Int>()

class AuthenticationFunctions() {


    object PostJsonFunctions{
            private val client = OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build()

            fun postJsonNoHeader(url: String, json: JSONObject, context: Context, callback: (String?, Exception?) -> Unit) {

                val requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json.toString())
                val request = Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build()

                client.newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        callback(null, e)
                    }

                    override fun onResponse(call: Call, response: Response) {

                        statusCode = response.code()

                        if (statusCode == 401){
                            println("unauthorized 401, taking new access token")
                            takeNewAccessToken(urls.refreshURL, tokensDataClass.refreshToken, context) { responseBody, exception ->
                                if (responseBody != null) {
                                    println(tokensDataClass.accessToken)
                                } else {
                                    println(exception)
                                }
                            }
                        }

                        val responseBody = response.body()?.string()
                        val responseJson = responseBody?.let { it1 -> JSONObject(it1) }
                        val accessToken = responseJson?.optString("access")
                        val refreshToken = responseJson?.optString("refresh")

                        if (accessToken != null || refreshToken != null) {
                            tokensDataClass = JWTTokensDataClass(
                                accessToken = accessToken.toString(),
                                refreshToken = refreshToken.toString(),
                                tokensCreatedAt = System.currentTimeMillis() / 1000 // in seconds
                            )

                        }

                        println("statusCode $statusCode")
                        callback(responseBody, null)
                    }
                })
            }

            fun postJsonWithHeader(url: String, json: JSONObject, accessToken: String, context: Context, callback: (String?, Exception?) -> Unit) {

                val requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json.toString())
                val request = Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .header("Authorization", "Bearer $accessToken")
                    .build()

                client.newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        callback(null, e)
                    }

                    override fun onResponse(call: Call, response: Response) {
                        statusCode = response.code()
                        if (statusCode == 401){
                            println("unauthorized 401, taking new access token")
                            takeNewAccessToken(urls.refreshURL, tokensDataClass.refreshToken, context) { responseBody, exception ->
                                if (responseBody != null) {
                                    println(tokensDataClass.accessToken)
                                } else {
                                    println(exception)
                                }
                            }
                        }

                        val responseBody = response.body()?.string()
                        println("statusCode $statusCode")
                        callback(responseBody, null)
                    }
                })
            }

            fun takeNewAccessToken(url: String, refreshToken: String, context: Context, callback: (String?, Exception?) -> Unit) {

                val refreshJson = createJsonObject("refresh" to refreshToken)
                val requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), refreshJson.toString())
                val request = Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build()

                client.newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        callback(null, e)
                    }

                    override fun onResponse(call: Call, response: Response) {

                        statusCode = response.code()
                        println("unauthorized 401, taking new access token")
                        AuthenticationFunctions.PostJsonFunctions.takeNewAccessToken(
                            urls.refreshURL,
                            tokensDataClass.refreshToken,
                            context,
                        ) { responseBody401, exception ->
                            if (responseBody401 != null) {
                                println(tokensDataClass.accessToken)
                            } else {
                                println(exception)
                            }
                        }

                        val responseBody = response.body()?.string()
                        val responseJson = responseBody?.let { it1 -> JSONObject(it1) }

                        if (statusCode == 200){
                            val newAccessToken = responseJson?.optString("access")
                            val newRefreshToken = responseJson?.optString("refresh")
                            if (newAccessToken != null && newRefreshToken!= null) {
                                tokensDataClass.accessToken = newAccessToken
                                tokensDataClass.refreshToken = newRefreshToken

                                val sharedPreferences = context.getSharedPreferences("token_prefs", Context.MODE_PRIVATE)
                                val editor = sharedPreferences.edit()
                                editor.putString("access_token", newAccessToken)
                                editor.putString("refresh_token", newRefreshToken)
                                editor.apply()
                            }
                        }

                        println("statusCode $statusCode")
                        callback(responseBody, null)
                    }
                })
            }



        }

    object CreateJsonObject{
        fun createJsonObject(vararg pairs: Pair<String, Any>): JSONObject {
            val jsonObject = JSONObject()
            pairs.forEach { (key, value) -> jsonObject.put(key, value) }
            return jsonObject
        }
    }


    fun checkIsAccessExpired(nowTime: Long, creationTime: Long, livingTime: Long, refreshToken: String, context: Context){

        if (nowTime - creationTime > livingTime) {
            println("token is expired")

            PostJsonFunctions.takeNewAccessToken(urls.refreshURL, refreshToken, context) { responseBody, exception ->
                if (responseBody != null) {
                    println(tokensDataClass.accessToken)
                } else {
                    println(exception)
                }
            }

        } else {
            println("token is not expired")
        }
    }


}