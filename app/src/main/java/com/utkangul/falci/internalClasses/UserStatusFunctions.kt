package com.utkangul.falci.internalClasses

import android.content.Context
import android.widget.TextView
import com.google.gson.Gson
import com.utkangul.falci.MainActivity
import com.utkangul.falci.internalClasses.dataClasses.*
import okhttp3.*
import java.io.IOException

class UserStatusFunctions {
    object UserStatusFunctionsObject{
        fun getUserStatus(url:String, client: OkHttpClient, context: Context,activity: MainActivity, useOrGainCoinMenuCurrentCoinText:TextView,coinAmountText:TextView){
            val request = Request.Builder()
                .url(url)
                .get()
                .header("Authorization", "Bearer ${tokensDataClass.accessToken}")
                .build()
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) { println("exception $e") }
                override fun onResponse(call: Call, response: Response) {
                    println("response code : ${response.code}")
                    println("response : $response")
                    if (response.code == 401){
                        AuthenticationFunctions.PostJsonFunctions.takeFreshTokens(urls.refreshURL, context) { responseBody, exception ->
                            if (responseBody != null) {
                                println(tokensDataClass.accessToken)
                                getUserStatus(url, client, context,activity,useOrGainCoinMenuCurrentCoinText,coinAmountText)
                            } else {
                                println(exception)
                            }
                        }
                    }
                    if (response.code ==200){
                        val responseBody = response.body?.string()
                        val gson = Gson()
                        userStatusDataClass =  gson.fromJson(responseBody, UserStatusDataClass::class.java)
                        println(userStatusDataClass)
                        coin.current_coin = userStatusDataClass.coin
                        var campaignId: Int? = null
                        if (userStatusDataClass.campain.isNotEmpty()) {
                            campaignId = userStatusDataClass.campain[0].id
                        } else println("kampanya listesi bos")
                        activity.runOnUiThread{
                            useOrGainCoinMenuCurrentCoinText.text = "${userStatusDataClass.coin}"
                            coinAmountText.text = "${userStatusDataClass.coin}"
                        }
                        if (campaignId != null){
                            val claimCampaignClient = OkHttpClient()
                            val claimCampaignRequest = Request.Builder()
                                .url("${urls.claimCampaignURl}$campaignId")
                                .get()
                                .header("Authorization", "Bearer ${tokensDataClass.accessToken}")
                                .build()
                            claimCampaignClient.newCall(claimCampaignRequest).enqueue(object : Callback {
                                override fun onFailure(call: Call, e: IOException) {
                                    println("exception $e")
                                }
                                override fun onResponse(call: Call, response: Response) {
                                    println(response.code)
                                }
                            })
                        } else { println("kampanya id si yoktu") }
                    }
                }
            })
        }
    }
}