package com.example.falci

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ListView
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import com.example.falci.LoginSignupActivity.Loginfunctions.AccessToken

class ChatFragment : Fragment() {

    private lateinit var usermessagetomira: String

    private var threadNumber: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_chat, container, false)

        val messageInput = v.findViewById<EditText>(R.id.chatfragmentmessageinputtext)

        val mainLayout = v.findViewById<View>(R.id.chat_fragment)

        val chatListView = v.findViewById<ListView>(R.id.chatListView)

        val messages = mutableListOf<ChatMessage>()

        val chatAdapter = ChatAdapter(requireContext(), messages)
        chatListView.adapter = chatAdapter


        mainLayout.setOnClickListener {
            val imm =
                requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(it.windowToken, 0)
        }

        messageInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                println()
                println(messageInput.text.toString())
                usermessagetomira = messageInput.text.toString()

                val myMessage = ChatMessage(usermessagetomira, true)
                messages.add(myMessage)
                messageInput.setText("")
                chatAdapter.notifyDataSetChanged()
                chatListView.smoothScrollToPosition(messages.size - 1)

                val chatJson = createChatJSON(usermessagetomira, threadNumber)
                val gptchatUrl = "http://31.210.43.174:1337/gpt/chat/"

                postChatJson(
                    gptchatUrl, chatJson
                ) { responseBody, exception ->
                    if (exception != null) {
                        println("Error: ${exception.message}")
                    } else {
                        println("Response: $responseBody")

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

                                    val apiMessage = ChatMessage(message, false)
                                    if (sender == "assistant") {
                                        messages.add(apiMessage)
                                        // En son mesajı bulduktan sonra döngüyü sonlandır
                                        break
                                    }
                                }
                            }

// Adaptörü güncelle
                            chatAdapter.notifyDataSetChanged()

// ListView'i en alt pozisyona kaydır
                            chatListView.smoothScrollToPosition(messages.size - 1)
                        }

                    }
                }

                true

            } else {
                println("yaz bakalim")
                false
            }

        }

        return v

    }

    private fun createChatJSON(message: String, thread: Int? = null): JSONObject {

        val chatJsonObject = JSONObject()

        chatJsonObject.put("message", message)

        if (thread != null) {
            chatJsonObject.put("thread", thread)
        }

        return chatJsonObject
    }

    private fun postChatJson(
        url: String, json: JSONObject, callback: (String?, Exception?) -> Unit
    ) {

        val chatclient = OkHttpClient()

        val requestBody =
            RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json.toString())

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .header("Authorization", "Bearer $AccessToken")
            .build()


        chatclient.newCall(request).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {
                callback(null, e)

            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body()?.string()

                println("response from mira $responseBody")

                println(AccessToken)

                callback(responseBody, null)

            }
        })

    }

}