package com.example.falci.internalClasses

import com.example.falci.internalClasses.dataClasses.JWTTokensDataClass
import com.example.falci.internalClasses.dataClasses.tokensDataClass
import com.example.falci.internalClasses.dataClasses.urls
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit
import kotlin.properties.Delegates


var statusCode by Delegates.notNull<Int>()

class AuthenticationFunctions {



    object PostJsonFunctions{
            private val client = OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build()

            fun postJsonNoHeader(url: String, json: JSONObject,callback: (String?, Exception?) -> Unit) {

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

                        statusCode = response.code()
                        println("statusCode $statusCode")
                        callback(responseBody, null)
                    }
                })
            }

            fun postJsonWithHeader(url: String, json: JSONObject, accessToken: String, callback: (String?, Exception?) -> Unit) {

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
                        val responseBody = response.body()?.string()
                        statusCode = response.code()
                        println("statusCode $statusCode")
                        callback(responseBody, null)
                    }
                })
            }

            fun takeNewAccessToken(url: String, refreshToken: String, callback: (String?, Exception?) -> Unit) {

                val requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), refreshToken)
                val request = Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build()

                client.newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        callback(null, e)
                    }

                    override fun onResponse(call: Call, response: Response) {

                        val responseBody = response.body()?.string()
                        val responseJson = responseBody?.let { it1 -> JSONObject(it1) }
                        val newAccessToken = responseJson?.optString("access")

                        if (newAccessToken != null) {
                            tokensDataClass.accessToken = newAccessToken
                        }

                        statusCode = response.code()
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


    fun checkIsAccessExpired(){
        val currentTimeMillis = System.currentTimeMillis() / 1000
        println(currentTimeMillis)

        if (currentTimeMillis - tokensDataClass.tokensCreatedAt > 10) { //if the time differance between now and token's creation time is longer than a day (in seconds)

            PostJsonFunctions.takeNewAccessToken(
                urls.refreshURL,
                tokensDataClass.refreshToken
            ) { newAccessToken, exception ->
                if (newAccessToken != null) {
                    println("token is expired")
                    tokensDataClass.accessToken = newAccessToken
                    println(newAccessToken)
                } else {
                    println(exception)
                }
            }

        } else {
            println("token is not expired")
        }
    }


}