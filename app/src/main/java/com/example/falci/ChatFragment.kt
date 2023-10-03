package com.example.falci

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ListView
import android.widget.RelativeLayout
import com.example.falci.internalClasses.AuthenticationFunctions
import com.example.falci.internalClasses.InternalFunctions.SetVisibilityFunctions.setViewGone
import com.example.falci.internalClasses.InternalFunctions.SetVisibilityFunctions.setViewVisible
import com.example.falci.internalClasses.dataClasses.*
import com.example.falci.internalClasses.statusCode
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class ChatFragment : Fragment() {

    private lateinit var usermessagetomira: String

    private var threadNumber: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_chat, container, false)

        val messageInput = v.findViewById<EditText>(R.id.chatFragmentMessageInputText)
        val messageInputParams = messageInput.layoutParams as RelativeLayout.LayoutParams

        val sendUserMessage = v.findViewById<ImageButton>(R.id.sendUserMessage)
        val cancelUserMessage = v.findViewById<ImageButton>(R.id.cancelUserMessage)

        val annen = v.findViewById<RelativeLayout>(R.id.annen)

        val chatListView = v.findViewById<ListView>(R.id.chatListView)

        val messages = mutableListOf<ChatMessage>()
        val oldMessages = mutableListOf<ChatMessage>()
        val welcomeMessage = ChatMessage(getHoroscopeData.summary.toString(), false)
        messages.add(welcomeMessage)


        val chatAdapter = ChatAdapter(requireContext(), messages)
        chatListView.adapter = chatAdapter

        if (isFromHoroscope){
            threadNumber = getHoroscopeData.thread
            println("burctan geldim  ve burcun threadı bu $threadNumber")

            val apiUrl = "https://api.atlasuavteam.com/gpt/chat/$threadNumber/"
            val client = OkHttpClient()
            val request = Request.Builder()
                .url(apiUrl)
                .get()
                .header("Authorization", "Bearer ${tokensDataClass.accessToken}")
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    println("exception $e")
                }

                override fun onResponse(call: Call, response: Response) {
                    val responseBody = response.body()?.string()
                    statusCode = response.code()
                    println("get thread response code $statusCode")

                    if (statusCode == 401){
                        println("unauthorized 401, taking new access token")
                        AuthenticationFunctions.PostJsonFunctions.takeNewAccessToken(
                            urls.refreshURL,
                            tokensDataClass.refreshToken,
                            requireContext()
                        ) { responseBody401, exception ->
                            if (responseBody401 != null) {
                                println(tokensDataClass.accessToken)
                            } else {
                                println(exception)
                            }
                        }
                    }

                    if (statusCode == 200){
                        println("code 200")

                        activity?.runOnUiThread {

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

                            println("eski mesajlar $oldMessages")

                            for (oldMessage in oldMessages){
                                messages.add(oldMessage)
                            }
                            chatAdapter.notifyDataSetChanged()
                            chatListView.smoothScrollToPosition(messages.size - 1)

                        }
                    }

                    if (statusCode == 404){
                        println("404 not found")
                    }
                }
            })
        }



        annen.setOnClickListener{
            val imm =
                requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(it.windowToken, 0)
        }



        messageInput.setOnClickListener{
            val scale = requireContext().resources.displayMetrics.density
            messageInputParams.width = (280 * scale + 0.5).toInt()
            messageInput.layoutParams = messageInputParams
            setViewVisible(cancelUserMessage)
        }

        cancelUserMessage.setOnClickListener {
            val scale = requireContext().resources.displayMetrics.density
            messageInputParams.width = (330 * scale + 0.5).toInt()
            messageInput.layoutParams = messageInputParams
            setViewGone(cancelUserMessage)
            val inputMethodManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(sendUserMessage.windowToken, 0)
            messageInput.setText("")
        }

        sendUserMessage.setOnClickListener{

            val scale = requireContext().resources.displayMetrics.density
            messageInputParams.width = (330 * scale + 0.5).toInt()
            messageInput.layoutParams = messageInputParams
            setViewGone(cancelUserMessage)

            val inputMethodManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(sendUserMessage.windowToken, 0)

            println(messageInput.text.toString())
            usermessagetomira = messageInput.text.toString()

            if (!messageInput.text.isNullOrEmpty()) {

                val myMessage = ChatMessage(usermessagetomira, true)
                messages.add(myMessage)
                messageInput.setText("")
                chatAdapter.notifyDataSetChanged()
                chatListView.smoothScrollToPosition(messages.size - 1)

                val chatJson = createChatJSON(usermessagetomira, threadNumber)
                println("thread bu: $threadNumber")
                println("yolladigim chat jsonu $chatJson")

                postChatJson(urls.chatGptURL, chatJson)
                { responseBody, exception ->
                    if (exception != null) {
                        println("Error: ${exception.message}")
                    } else {
                        println("thread verdim response bu: $responseBody")

                        activity?.runOnUiThread {
                            val jsonResponse = responseBody?.let { JSONObject(it) }
                            val chatMessagesArray = jsonResponse?.getJSONArray("chat_messages")
                            if (chatMessagesArray != null) {


                                // En son mesaji bulmak icin chatMessagesArray'ı tersten dön
                                for (i in chatMessagesArray.length() - 1 downTo 0) {
                                    val chatMessageObject = chatMessagesArray.getJSONObject(i)
                                    val sender = chatMessageObject.getString("owner")
                                    val message = chatMessageObject.getString("message")
                                    threadNumber = chatMessageObject.getInt("thread")
                                    println("gonderen $sender")
                                    val apiMessage = ChatMessage(message, false)
                                    println(apiMessage)
                                    messages.add(apiMessage)

                                    // En son mesajı bulduktan sonra döngüyü sonlandır
                                    break
                                }
                            }
                            chatAdapter.notifyDataSetChanged()
                            chatListView.smoothScrollToPosition(messages.size - 1)
                        }
                    }
                }
            }
        }

        return v
    }

    private fun createChatJSON(message: String, thread: Int? = null): JSONObject {
        val chatJsonObject = JSONObject()
        chatJsonObject.put("message", message)
        if (thread != null) { chatJsonObject.put("thread", thread) }
        return chatJsonObject
    }

    private fun postChatJson(url: String, json: JSONObject, callback: (String?, Exception?) -> Unit) {

        val chatclient = OkHttpClient()

        val requestBody =
            RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json.toString())

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .header("Authorization", "Bearer ${tokensDataClass.accessToken}")
            .build()


        chatclient.newCall(request).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {
                callback(null, e)
                println("hata var amuga goyum $e")

            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body()?.string()
                statusCode = response.code()
                if (statusCode == 401){
                    println("unauthorized 401, taking new access token")
                    AuthenticationFunctions.PostJsonFunctions.takeNewAccessToken(urls.refreshURL, tokensDataClass.refreshToken, requireContext()) { responseBody401, exception ->
                        if (responseBody401 != null) {
                            println(tokensDataClass.accessToken)
                        } else {
                            println(exception)
                        }
                    }
                }
                println("response from mira $responseBody")

                callback(responseBody, null)

            }
        })


    }

}

