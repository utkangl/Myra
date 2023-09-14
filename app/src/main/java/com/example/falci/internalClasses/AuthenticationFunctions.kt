package com.example.falci.internalClasses

import com.example.falci.loginTokens
import com.example.falci.registerTokens
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import kotlin.properties.Delegates

var statusCode by Delegates.notNull<Int>()

class AuthenticationFunctions {

    object PostJsonFunctions{
        private val client = OkHttpClient()

        fun postJsonNoHeader(
            url: String,
            json: JSONObject,
            type: String,
            callback: (String?, Exception?) -> Unit,
        ) {
            val requestBody = RequestBody.create(
                MediaType.parse("application/json; charset=utf-8"),
                json.toString()
            )

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
                    val detail = responseJson?.optString("detail")
                    val accessToken = responseJson?.optString("access")
                    val refreshToken = responseJson?.optString("refresh")

                    if (detail != null){println(detail)}
                    if (accessToken != null || refreshToken != null) {

                        if (type == "register"){
                            registerTokens = RegisterTokens(registerAccessToken = accessToken.toString(), registerRefreshToken = refreshToken.toString())
                        }
                        if (type == "login"){
                            loginTokens = LoginTokens(loginAccessToken = accessToken.toString(), loginRefreshToken = refreshToken.toString())
                        }

                    }

                    statusCode = response.code()
                    println("statusCode $statusCode")
                    callback(responseBody, null)
                }
            })
        }

        fun postJsonWithHeader(
            url: String,
            json: JSONObject,
            accessToken: String,
            callback: (String?, Exception?) -> Unit
        ) {
            val requestBody = RequestBody.create(
                MediaType.parse("application/json; charset=utf-8"),
                json.toString()
            )

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
    }


    object CreateJsonObject{
        fun createJsonObject(vararg pairs: Pair<String, Any>): JSONObject {
            val jsonObject = JSONObject()
            pairs.forEach { (key, value) -> jsonObject.put(key, value) }
            return jsonObject
        }
    }


}