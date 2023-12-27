package com.utkangul.falci.internalClasses

import android.app.Activity
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import com.utkangul.falci.MainActivity
import com.utkangul.falci.R
import com.utkangul.falci.internalClasses.AuthenticationFunctions.CreateJsonObject.createJsonObject
import com.utkangul.falci.internalClasses.dataClasses.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
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

            fun postJsonNoHeader(context: Context, activity:Activity, url: String, json: JSONObject,  callback: (String?, Exception?) -> Unit) {

                val requestBody = json.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
                val request = Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build()

                client.newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        activity.runOnUiThread{ Toast.makeText(context, activity.resources.getString(R.string.unexpected_error_occured_onServer_text), Toast.LENGTH_SHORT).show()}
                        val intent = Intent(context, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                        startActivity(context,intent,null)
                        activity.finish()
                        callback(null, e)
                    }

                    override fun onResponse(call: Call, response: Response) {
                        statusCode = response.code
                        val responseBody = response.body?.string()
                        println("responseBody $responseBody")
                        val responseJson = responseBody?.let { it1 -> JSONObject(it1) }
                        println("responseJson $responseJson")
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

            fun postJsonWithHeader(activity: Activity, url: String, json: JSONObject, context: Context, callback: (String?, Exception?) -> Unit) {

                val requestBody = json.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
                val request = Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .header("Authorization", "Bearer ${tokensDataClass.accessToken}")
                    .build()

                client.newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        activity.runOnUiThread{ Toast.makeText(context, activity.resources.getString(R.string.unexpected_error_occured_onServer_text), Toast.LENGTH_SHORT).show()}
                        val intent = Intent(context, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                        startActivity(context,intent,null)
                        activity.finish()
                        e.printStackTrace()
                    }

                    override fun onResponse(call: Call, response: Response) {
                        statusCode = response.code
                        if (statusCode == 401){
                            println("unauthorized 401, taking new access token")
                            takeFreshTokens(activity,urls.refreshURL, context) { responseBody, exception ->
                                if (responseBody != null) {
                                    println(tokensDataClass.accessToken)
                                    postJsonWithHeader(activity,url, json, context, callback)
                                } else {
                                    println(exception)
                                }
                            }
                        }

                        val responseBody = response.body?.string()
                        println("statusCode $statusCode")
                        callback(responseBody, null)
                    }
                })
            }

            fun takeFreshTokens(activity: Activity,url: String, context: Context, callback: (String?, Exception?) -> Unit) {

                val refreshJson = createJsonObject("refresh" to tokensDataClass.refreshToken)
                println(tokensDataClass)
                val requestBody = refreshJson.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
                val request = Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build()

                client.newCall(request).enqueue(object : Callback {

                    override fun onFailure(call: Call, e: IOException) {
                        activity.runOnUiThread{ Toast.makeText(context, activity.resources.getString(R.string.unexpected_error_occured_onServer_text), Toast.LENGTH_SHORT).show()}
                        val intent = Intent(context, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                        startActivity(context,intent,null)
                        activity.finish()
                        callback(null, e)
                    }

                    override fun onResponse(call: Call, response: Response) {

                        val newTokenStatusCode = response.code
                        println("new token statusCode $newTokenStatusCode")

                        if (newTokenStatusCode == 401){
                            println("yeni token alma fonksiyonu da 401 geldi her şeyi silip cikis yaptiriom")
                            val sharedPreferences: SharedPreferences = context.getSharedPreferences("token_prefs", Context.MODE_PRIVATE)
                            val editor = sharedPreferences.edit()
                            editor.putString("access_token", null)
                            editor.putString("refresh_token", null)
                            editor.putLong("token_creation_time", 0)
                            editor.putBoolean("didLogin", false)
                            editor.apply()
                            val options = ActivityOptions.makeCustomAnimation(context, R.anim.activity_slide_down, 0)
                            val intent = Intent(context, MainActivity::class.java);startActivity(context,intent, options.toBundle())
                            authenticated.isLoggedIn = false
                        }


                        val responseBody = response.body?.string()
                        val responseJson = responseBody?.let { it1 -> JSONObject(it1) }

                        if (newTokenStatusCode == 200){
                            val newAccessToken = responseJson?.optString("access")
                            val newRefreshToken = responseJson?.optString("refresh")
                            println("yeni access token $newAccessToken")

                            if (newAccessToken != null && newRefreshToken!= null) {
                                tokensDataClass.accessToken = newAccessToken
                                tokensDataClass.refreshToken = newRefreshToken
                                tokensDataClass.tokensCreatedAt = System.currentTimeMillis() / 1000
                                val sharedPreferences = context.getSharedPreferences("token_prefs", Context.MODE_PRIVATE)
                                val editor = sharedPreferences.edit()
                                editor.putString("access_token", newAccessToken)
                                editor.putString("refresh_token", newRefreshToken)
                                editor.putLong("token_creation_time", tokensDataClass.tokensCreatedAt)
                                editor.apply()
                            }
                        }
                        callback(responseBody, null)
                    }
                })
            }

        fun checkIsAccessExpired(activity: Activity, nowTime: Long, creationTime: Long, livingTime: Long, context: Context){
            if (nowTime - creationTime > livingTime) {
                println("token is expired")

                takeFreshTokens(activity, urls.refreshURL,  context) { responseBody, exception ->
                    controlVariables.isInExpireControl = false

                    if (responseBody != null) {
                        println("expire olan tokene yenileme çağırdım, response null değildi: $responseBody")
                        println(tokensDataClass.accessToken)
                    } else{
                        println("expire olan tokene yenileme çağırdım, response null idi")
                    }
                    exception?.printStackTrace()
                }
            } else {
                println("token is not expired")
                controlVariables.isInExpireControl = false
            }
        }

        }

    object CreateJsonObject{
        fun createJsonObject(vararg pairs: Pair<String, Any>): JSONObject {
            val jsonObject = JSONObject()
            pairs.forEach { (key, value) -> jsonObject.put(key, value) }
            return jsonObject
        }
    }





}