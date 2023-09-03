package com.example.falci

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import kotlin.properties.Delegates


class LoginSignupActivity : AppCompatActivity() {

    private val loginfragment = Loginfragment()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_signup)

        supportFragmentManager.beginTransaction()
            .replace(R.id.login_signup_container, loginfragment).commit()
    }

    object RegistrationFunctions {
        fun createRegistrationJSON(
            username: String,
            password: String,
            name: String,
            gender: String,
            birthDay: String,
            location: String,
            relationshipStatus: String,
            occupation: String
        ): JSONObject {

            val registerjsonObject = JSONObject()
            registerjsonObject.put("username", username)
            registerjsonObject.put("password", password)


            val zodiacInfo = JSONObject()
            zodiacInfo.put("name", name)
            zodiacInfo.put("location", location)
            zodiacInfo.put("birthDay", birthDay)
            zodiacInfo.put("gender", gender)
            zodiacInfo.put("occupation", occupation)

            val registerInfo = JSONObject()
            registerInfo.put("zodiacInfo", zodiacInfo)
            registerInfo.put("relationshipStatus", relationshipStatus)

            registerjsonObject.put("registerInfo", registerInfo)

            return registerjsonObject
        }


        fun postsignupJson(url: String, json: JSONObject, callback: (String?, Exception?) -> Unit) {
            val signupclient = OkHttpClient()

            val requestBody = RequestBody.create(
                MediaType.parse("application/json; charset=utf-8"),
                json.toString()
            )

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


    object Loginfunctions {

        lateinit var RefreshToken: String
        lateinit var AccessToken: String
        var StatusCode by Delegates.notNull<Int>()

        fun createLoginJSON(username: String, password: String): JSONObject {

            val loginjsonObject = JSONObject()

            loginjsonObject.put("username", username)
            loginjsonObject.put("password", password)

            return loginjsonObject
        }


        fun postloginJson(url: String, json: JSONObject, callback: (String?, Exception?) -> Unit) {

            val loginclient = OkHttpClient()

            val requestBody = RequestBody.create(
                MediaType.parse("application/json; charset=utf-8"),
                json.toString()
            )

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

                    StatusCode = response.code()

                    if (responseBody != null) {

                        val (refreshToken, accessToken) = JwtTokenFunctions.parseJWTTokens(
                            responseBody
                        )

                        println("Refresh Token: $refreshToken")
                        if (refreshToken != null) {
                            RefreshToken = refreshToken
                        }
                        println("Access Token: $accessToken")
                        if (accessToken != null) {
                            AccessToken = accessToken
                        }

                    }

                    callback(responseBody, null)

                }
            })

        }

    }


    object JwtTokenFunctions {
        fun parseJWTTokens(responseBody: String): Pair<String?, String?> {
            var token1: String? = null
            var token2: String? = null

            try {
                val jsonResponse = JSONObject(responseBody)
                token1 = jsonResponse.optString("refresh", null.toString())
                token2 = jsonResponse.optString("access", null.toString())
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            return Pair(token1, token2)
        }

        fun createRefreshTokenJson(tokenRefresh: String): JSONObject {

            val tokenRefreshJson = JSONObject()

            tokenRefreshJson.put("refresh", tokenRefresh)

            return tokenRefreshJson

        }

        fun postRefreshTokenJson(
            url: String,
            json: JSONObject,
            callback: (String?, Exception?) -> Unit
        ) {

            val refreshAccessTokenClient = OkHttpClient()

            val requestBody = RequestBody.create(
                MediaType.parse("application/json; charset=utf-8"),
                json.toString()
            )

            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()


            refreshAccessTokenClient.newCall(request).enqueue(object : Callback {

                override fun onFailure(call: Call, e: IOException) {
                    callback(null, e)
                }

                override fun onResponse(call: Call, response: Response) {
                    val responseBody = response.body()?.string()

                    if (responseBody != null) {

                        val refreshedAccessToken = parseJWTTokens(responseBody)

                        println("Refreshed Access Token: $refreshedAccessToken")
                        Loginfunctions.AccessToken = refreshedAccessToken.toString()

                    }

                    callback(responseBody, null)

                }
            })

        }

    }


}