package com.example.falci.internalClasses

import androidx.fragment.app.FragmentManager
import com.airbnb.lottie.LottieAnimationView
import com.example.falci.HoroscopeDetailFragment
import com.example.falci.internalClasses.InternalFunctions.SetVisibilityFunctions.setViewGone
import com.example.falci.internalClasses.InternalFunctions.SetVisibilityFunctions.setViewVisible
import com.example.falci.internalClasses.TransitionToFragment.ReplaceActivityToFragment.replaceMainActivityToFragment
import com.example.falci.internalClasses.dataClasses.*
import com.example.falci.loginTokens
import com.google.gson.Gson
import kotlinx.coroutines.*
import org.json.JSONObject

object HoroscopeFunctions {

    fun getHoroscope(animationView: LottieAnimationView, fm: FragmentManager) {
        val getHoroscopeJson = AuthenticationFunctions.CreateJsonObject.createJsonObject(
            "type" to postHoroscopeData.type.toString(),
            "time_interval" to postHoroscopeData.time_interval.toString()
        )

        println(getHoroscopeJson)
        val gson = Gson()

        CoroutineScope(Dispatchers.IO).launch {
            animationView.post {
                setViewVisible(animationView)
                animationView.playAnimation()
            }

            try {
                AuthenticationFunctions.PostJsonFunctions.postJsonWithHeader(
                    urls.getHoroscopeURL,
                    getHoroscopeJson,
                    loginTokens.loginAccessToken
                ) { responseBody, exception ->
                    animationView.post {
                        setViewGone(animationView)
                        animationView.cancelAnimation()
                        replaceMainActivityToFragment(fm, HoroscopeDetailFragment())

                    }

                    if (responseBody != null) {
                        getHoroscopeData = gson.fromJson(responseBody, GetHoroscopeData::class.java)
                        val errorResponseJson = JSONObject(responseBody)

                        println(statusCode)

                        if (statusCode == 200) {
                            val id = getHoroscopeData.id
                            val user = getHoroscopeData.user
                            val updated_at = getHoroscopeData.updated_at
                            val created_at = getHoroscopeData.created_at
                            val chat_messages = getHoroscopeData.chat_messages

                            chat_messages?.forEach { annen ->
                                val messageId = annen.id
                                val owner = annen.owner
                                val message = annen.message
                                val messageUpdatedAt = annen.updated_at
                                val messageCreatedAt = annen.created_at
                                val thread = annen.thread
                                horoscopeChatMessageData = annen
                            }
                            horoscopeMessage.message = formatHoroscopeMessage(horoscopeChatMessageData)
                            println(horoscopeMessage.message)
                        } else {
                            val detail = errorResponseJson.optString("detail")
                            println(detail)
                        }
                    } else if (exception != null) {
                        exception.printStackTrace()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

//    fun getHoroscope(){
//
//        val getHoroscopeJson = AuthenticationFunctions.CreateJsonObject.createJsonObject(
//            "type" to postHoroscopeData.type.toString(),
//            "time_interval" to postHoroscopeData.time_interval.toString()
//        )
//
//        println(getHoroscopeJson)
//        val gson = Gson()
//        AuthenticationFunctions.PostJsonFunctions.postJsonWithHeader(
//            urls.getHoroscopeURL,
//            getHoroscopeJson,
//            loginTokens.loginAccessToken
//        ) { responseBody, exception ->
//            getHoroscopeData = responseBody?.let { gson.fromJson(it, GetHoroscopeData::class.java) }!!
//            val errorResponseJson = JSONObject(responseBody)
//
//            println(statusCode)
//
//            if (statusCode == 200) {
//
//                val id = getHoroscopeData.id
//                val user = getHoroscopeData.user
//                val updated_at = getHoroscopeData.updated_at
//                val created_at = getHoroscopeData.created_at
//                val chat_messages = getHoroscopeData.chat_messages
//
//
//                chat_messages?.forEach { annen ->
//                    val messageId = annen.id
//                    val owner = annen.owner
//                    val message = annen.message
//                    val messageUpdatedAt = annen.updated_at
//                    val messageCreatedAt = annen.created_at
//                    val thread = annen.thread
//                    horoscopeChatMessageData = annen
//                }
//                horoscopeMessage.message = formatHoroscopeMessage(horoscopeChatMessageData)
//                println(horoscopeMessage.message)
//
//            } else {
//                val detail = errorResponseJson.optString("detail")
//                println(detail)
//            }
//        }
//    }


    private fun formatHoroscopeMessage(message: GetHoroscopeData.HoroscopeChatMessageData): String{
        return if (message.owner == "assistant") {
            message.message
        } else "owner assistant degil"

    }

}