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
import androidx.cardview.widget.CardView
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import com.example.falci.LoginSignupActivity.Loginfunctions.AccessToken



class ChatFragment : Fragment() {


    private lateinit var usermessagetomira: String


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_chat, container, false)

        val messageInput = v.findViewById<EditText>(R.id.chatfragmentmessageinputtext)
        val messageCardContainer = v.findViewById<CardView>(R.id.chatfragmentmessageinput)


        val mainLayout = v.findViewById<View>(R.id.chat_fragment) // Ana düzenin ID'sini buraya ekleyin
        mainLayout.setOnClickListener {
            val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(it.windowToken, 0)
        }


        messageInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                println(messageInput.text.toString())
                usermessagetomira = messageInput.text.toString()


                val chatJson = createChatJSON(usermessagetomira)
                val gptchatUrl = "http://31.210.43.174:1337/gpt/chat/"

                postChatJson(
                    gptchatUrl,
                    chatJson
                ) { responseBody, exception ->
                    if (exception != null) {
                        println("Error: ${exception.message}")
                    } else {
                        println("Response: $responseBody")
                        println("Yolladim galiba")

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


    fun createChatJSON(message: String, thread: Int? = null ): JSONObject {

        val chatJsonObject = JSONObject()

        chatJsonObject.put("message" , message)

        if (thread != null){
            chatJsonObject.put("thread" , thread)
        }

        return chatJsonObject
    }


    fun postChatJson(url: String, json: JSONObject, callback: (String?, Exception?) -> Unit) {

        val chatclient = OkHttpClient()

        val requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json.toString())

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

                callback(responseBody, null)

            }
        })

    }

}