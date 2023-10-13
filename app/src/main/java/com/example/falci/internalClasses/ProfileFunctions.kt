package com.example.falci.internalClasses

import android.content.Context
import com.example.falci.internalClasses.dataClasses.UserProfileDataClass
import com.example.falci.internalClasses.dataClasses.tokensDataClass
import com.example.falci.internalClasses.dataClasses.urls
import com.example.falci.internalClasses.dataClasses.userProfile
import com.google.gson.Gson
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream

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
                        AuthenticationFunctions.PostJsonFunctions.takeFreshTokens(
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
                        AuthenticationFunctions.PostJsonFunctions.takeFreshTokens(
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

        fun getPlanetZodiacExplanationJsonValue(context: Context, jsonFileName: String, key1: String, key2: String): String {
            val jsonString: String?
            try {
                val resourceId =
                    context.resources.getIdentifier(jsonFileName, "raw", context.packageName)
                if (resourceId == 0) {
                    return "JSON file could not found."
                }
                val inputStream: InputStream = context.resources.openRawResource(resourceId)
                val size = inputStream.available()
                val buffer = ByteArray(size)
                inputStream.read(buffer)
                inputStream.close()
                jsonString = String(buffer)

                val jsonObject = JSONObject(jsonString)

                val value1 = jsonObject.getJSONObject(key1)
                println("value1: $value1")
                val value2 = value1.getString(key2)
                println("value2: $value2")
                return value2


            } catch (e: JSONException) {
                e.printStackTrace()
                return "JSON data could not solved."
            }
        }

        fun getPlanetExplanationJsonValue(context: Context, jsonFileName: String, key: String): String {
            val jsonString: String?
            try {
                val resourceId =
                    context.resources.getIdentifier(jsonFileName, "raw", context.packageName)
                if (resourceId == 0) {
                    return "JSON file could not found."
                }
                val inputStream: InputStream = context.resources.openRawResource(resourceId)
                val size = inputStream.available()
                val buffer = ByteArray(size)
                inputStream.read(buffer)
                inputStream.close()
                jsonString = String(buffer)

                val jsonObject = JSONObject(jsonString)

                val value1 = jsonObject.getString(key)
                println("value1: $value1")
                return value1


            } catch (e: JSONException) {
                e.printStackTrace()
                return "JSON data could not be solved."
            }
        }
    }
}