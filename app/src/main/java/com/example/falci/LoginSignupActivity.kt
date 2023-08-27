package com.example.falci

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException


class LoginSignupActivity : AppCompatActivity() {

    private val loginfragment = Loginfragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_signup)

        supportFragmentManager.beginTransaction().replace(R.id.container, loginfragment).commit()
    }

    object RegistrationFunctions{

        fun createRegistrationJSON(username: String, password: String): JSONObject {
            val registerjsonObject = JSONObject()

            registerjsonObject.put("username", username)
            registerjsonObject.put("password", password)

            val registerInfo = JSONObject()
            val zodiacInfo = JSONObject()

            zodiacInfo.put("name", "utkan")
            zodiacInfo.put("location", "adana")
            zodiacInfo.put("birthDay", "2023-08-02 02:32:11 PM +0000")
            zodiacInfo.put("gender", "male")
            zodiacInfo.put("occupation", "actor")

            registerInfo.put("zodiacInfo", zodiacInfo)
            registerInfo.put("relationshipStatus", "single")

            registerjsonObject.put("registerInfo", registerInfo)

            return registerjsonObject
        }


        fun postsignupJson(url: String, json: JSONObject, callback: (String?, Exception?) -> Unit) {
            val signupclient = OkHttpClient()

            val requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json.toString())

            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            signupclient.newCall(request).enqueue(object : Callback {

                override fun onFailure(call: Call, e: IOException) {
                    callback(null, e)

                }

                override fun onResponse(call: Call, response: Response) {
                    val responseBody = response.body()?.string()


                    callback(responseBody, null)

                }
            })
        }

    }


    object Loginfunctions{

        lateinit var RefreshToken : String
        lateinit var AccessToken : String

        fun createLoginJSON(username: String, password: String): JSONObject {

            val loginjsonObject = JSONObject()

            loginjsonObject.put("username", username)
            loginjsonObject.put("password", password)

            return loginjsonObject
        }


        fun postloginJson(url: String, json: JSONObject, callback: (String?, Exception?) -> Unit) {

            val loginclient = OkHttpClient()

            val requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json.toString())

            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()


            loginclient.newCall(request).enqueue(object : Callback {

                override fun onFailure(call: Call, e: IOException) {
                    callback(null, e)

                }

                override fun onResponse(call: Call, response: Response) {
                    val responseBody = response.body()?.string()

                    if (responseBody != null) {

                        val (refreshToken, accessToken) = parseJWTTokens(responseBody)

                        println("Token 1: $refreshToken")
                        if (refreshToken != null) {
                            RefreshToken = refreshToken
                        }
                        println("Token 2: $accessToken")
                        if (accessToken != null) {
                            AccessToken = accessToken
                        }

                    }

                    callback(responseBody, null)

                }
            })

        }

        fun parseJWTTokens(responseBody: String): Pair<String?, String?> {
            var token1: String? = null
            var token2: String? = null

            try {
                val jsonResponse = JSONObject(responseBody)
                token1 = jsonResponse.optString("refresh", null.toString()) // "token1_key" yerine gerçek anahtarınızı kullanın
                token2 = jsonResponse.optString("access", null.toString()) // "token2_key" yerine gerçek anahtarınızı kullanın
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            return Pair(token1, token2)
        }





    }






}