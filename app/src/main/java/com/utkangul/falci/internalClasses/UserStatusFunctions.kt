package com.utkangul.falci.internalClasses

import android.content.Context
import android.content.Intent
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat.startActivity
import com.google.gson.Gson
import com.utkangul.falci.MainActivity
import com.utkangul.falci.internalClasses.AuthenticationFunctions.PostJsonFunctions.takeFreshTokens
import com.utkangul.falci.internalClasses.InternalFunctions.SetVisibilityFunctions.setViewGoneWithAnimation
import com.utkangul.falci.internalClasses.InternalFunctions.SetVisibilityFunctions.setViewVisibleWithAnimation
import com.utkangul.falci.internalClasses.dataClasses.*
import okhttp3.*
import java.io.IOException

class UserStatusFunctions {
    object UserStatusFunctionsObject {
        fun getUserStatus(
            context: Context,
            activity: MainActivity,
            useOrGainCoinMenuCurrentCoinText: TextView,
            coinAmountText: TextView,
            claimCampaignCard: CardView,
            claimCampaignClaimButton: AppCompatButton
        ) {
            println("get user status burada cagirildi")
            val request = Request.Builder()
                .url(urls.userStatusURL)
                .get()
                .header("Authorization", "Bearer ${tokensDataClass.accessToken}")
                .build()
            val client = OkHttpClient()
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    activity.runOnUiThread{ Toast.makeText(context, "Unexpected error occured on our server. Directing you to main page", Toast.LENGTH_SHORT).show()}
                    val intent = Intent(context, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(context,intent,null)
                    activity.finish()
                    e.printStackTrace()
                }

                override fun onResponse(call: Call, response: Response) {
                    println("response code from get user status : ${response.code}")
                    println("response from get user status : $response")

                    when (response.code) {

                        200 -> {
                            val responseBody = response.body?.string()
                            val gson = Gson()
                            userStatusDataClass = gson.fromJson(responseBody, UserStatusDataClass::class.java)
                            println(userStatusDataClass.fortune)
                            coin.current_coin = userStatusDataClass.coin
                            activity.runOnUiThread {
                                useOrGainCoinMenuCurrentCoinText.text = "${userStatusDataClass.coin}"
                                coinAmountText.text = "${userStatusDataClass.coin}"
                            }

                            if (userStatusDataClass.campain.isEmpty()) {
                                println("kampanya listesi bos")
                            } else {
                                fun claimCampaign() {
                                    for (campaign in userStatusDataClass.campain) {
                                        println("campaign $campaign")
                                        if (campaign.is_available && !campaign.is_expired) {
                                            activity.runOnUiThread {
                                                setViewVisibleWithAnimation(context, claimCampaignCard)
                                                claimCampaignClaimButton.setOnClickListener {
                                                    activity.runOnUiThread { setViewGoneWithAnimation(context, claimCampaignCard) }
                                                    val claimCampaignClient = OkHttpClient()
                                                    val claimCampaignRequest = Request.Builder()
                                                        .url("${urls.claimCampaignURl}${campaign.id}")
                                                        .get()
                                                        .header("Authorization", "Bearer ${tokensDataClass.accessToken}")
                                                        .build()
                                                    claimCampaignClient.newCall(claimCampaignRequest).enqueue(object : Callback {
                                                        override fun onFailure(call: Call, e: IOException) {
                                                            activity.runOnUiThread{ Toast.makeText(context, "Unexpected error occured on our server. Directing you to main page", Toast.LENGTH_SHORT).show()}
                                                            val intent = Intent(context, MainActivity::class.java)
                                                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                                                            startActivity(context,intent,null)
                                                            activity.finish()
                                                            println("exception $e")
                                                        }

                                                        override fun onResponse(call: Call, response: Response) {
                                                            println(" claim campaign response code ${response.code}")
                                                            if (response.code == 200) {
                                                                userStatusDataClass.campain.remove(campaign)
                                                                activity.runOnUiThread { Toast.makeText(context, "Campaign Successfuly Claimed", Toast.LENGTH_SHORT).show() }
                                                                getUserStatus(context, activity, useOrGainCoinMenuCurrentCoinText, coinAmountText, claimCampaignCard, claimCampaignClaimButton)
                                                            } else if (response.code == 401) {
                                                                takeFreshTokens(activity,urls.refreshURL, context) { responseBody401, exception ->
                                                                    if (responseBody401 != null) {
                                                                        println("take fresh tokenin responsu: $responseBody401")
                                                                        claimCampaign()
                                                                    } else exception?.printStackTrace()
                                                                }
                                                            }
                                                        }
                                                    })
                                                }
                                            }
                                        } else {
                                            println("no available campaign")
                                        }
                                    }
                                }
                                if (!controlVariables.navigateBackToProfileActivity && !controlVariables.isFromCompleteLookup) {
                                    println("claim cagiriyorum cünkü isFromCompleteLookup false: ${controlVariables.isFromCompleteLookup}")
                                    claimCampaign()
                                }
                            }
                        }

                        401 -> {
                            takeFreshTokens(activity,urls.refreshURL, context) { responseBody401, exception401 ->
                                if (responseBody401 != null) {
                                    getUserStatus(context, activity, useOrGainCoinMenuCurrentCoinText, coinAmountText, claimCampaignCard, claimCampaignClaimButton)
                                } else exception401?.printStackTrace()

                            }
                        }

                        else -> {
                            activity.runOnUiThread { Toast.makeText(context, "unexpected error: $statusCode", Toast.LENGTH_SHORT).show() }
                            activity.runOnUiThread { Toast.makeText(context, "Please try restarting the app", Toast.LENGTH_SHORT).show() }
                        }
                    }
                }
            })
        }
    }
}