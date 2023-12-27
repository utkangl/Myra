package com.utkangul.falci.internalClasses

import android.content.Context
import android.content.Intent
import android.view.animation.AnimationUtils
import android.widget.ListView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.FragmentActivity
import com.utkangul.falci.MainActivity
import com.utkangul.falci.R
import com.utkangul.falci.internalClasses.dataClasses.ChatMessage
import com.utkangul.falci.internalClasses.dataClasses.getHoroscopeData
import com.utkangul.falci.internalClasses.dataClasses.tokensDataClass
import com.utkangul.falci.internalClasses.dataClasses.urls
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class ChatFuncs {
    fun getOldMessages(context: Context, activity: FragmentActivity, threadNumber: Int, oldMessages: MutableList<ChatMessage>, chatAdapter: com.utkangul.falci.ChatAdapter, messages: MutableList<ChatMessage>, chatListView: ListView){
        val apiUrl = "https://api.kircibros.com/gpt/chat/$threadNumber/"
        val client = OkHttpClient()

        val request = Request.Builder()
            .url(apiUrl)
            .get()
            .header("Authorization", "Bearer ${tokensDataClass.accessToken}")
            .build()

        client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                activity.runOnUiThread{ Toast.makeText(context, activity.resources.getString(R.string.unexpected_error_occured_onServer_text), Toast.LENGTH_SHORT).show()}
                val intent = Intent(context, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(context,intent,null)
                activity.finish()
                println("exception $e")
            }
            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                statusCode = response.code
                println("get thread response code $statusCode")

                if (statusCode == 401){
                    println("unauthorized 401, taking new access token")
                    AuthenticationFunctions.PostJsonFunctions.takeFreshTokens(activity, urls.refreshURL, context) { responseBody401, exception ->
                        if (responseBody401 != null) {
                            println(tokensDataClass.accessToken)
                            getOldMessages(context,activity, threadNumber,oldMessages,chatAdapter,messages, chatListView)
                        } else {
                            println(exception)
                        }
                    }
                }
                if (statusCode == 200){
                    println("code 200")
                    activity.runOnUiThread {
                        val welcomeMessage = ChatMessage(getHoroscopeData.summary.toString(), false)
                        messages.add(welcomeMessage)
                        val jsonResponse = responseBody?.let { JSONObject(it) }
                        val chatMessagesArray = jsonResponse?.getJSONArray("chat_messages")

                        for (i in 0 until chatMessagesArray!!.length()) {

                            val jsonObject = chatMessagesArray.getJSONObject(i)
                            val oldMessage = jsonObject.getString("message")
                            val owner = jsonObject.getString("owner")

                            if (owner == "user") {
                                oldMessages.add(ChatMessage(oldMessage, true))
                            }
                            if (owner == "assistant") {
                                oldMessages.add(ChatMessage(oldMessage, false))
                            }
                        }
                        for (oldMessage in oldMessages){
                            messages.add(oldMessage)
                        }
                        activity.runOnUiThread {
                            val animation = AnimationUtils.loadAnimation(context, R.anim.chatlist_slide_down)
                            chatListView.startAnimation(animation)
                            chatAdapter.notifyDataSetChanged()
                            chatListView.smoothScrollToPosition(messages.size - 1)
                        }
                    }
                }
            }
        })
    }
}