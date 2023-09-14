package com.example.falci.internalClasses

import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class ProfileFunctions {
    object ProfileFunctions{

        fun makeGetProfileRequest(url: String, accessToken: String, callback: (String?, Exception?) -> Unit) {

            val getProfileClient = OkHttpClient()


            val request = Request.Builder()
                .url(url)
                .header("Authorization", "Bearer $accessToken")
                .build()


            getProfileClient.newCall(request).enqueue(object : Callback {

                override fun onFailure(call: Call, e: IOException) {
                    callback(null, e)

                }

                override fun onResponse(call: Call, response: Response) {
                    val responseBody = response.body()?.string()

                    statusCode = response.code()

                    if (responseBody != null) {

                        println(responseBody)

                    }

                    callback(responseBody, null)

                }
            })

        }


        fun putEditProfileJson(url: String, json: JSONObject, accessToken: String, callback: (String?, Exception?) -> Unit) {

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