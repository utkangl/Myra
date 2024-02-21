package com.utkangul.falci
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.utkangul.falci.internalClasses.AuthenticationFunctions
import com.utkangul.falci.internalClasses.ChatFuncs
import com.utkangul.falci.internalClasses.InternalFunctions.SetVisibilityFunctions.setViewGone
import com.utkangul.falci.internalClasses.InternalFunctions.SetVisibilityFunctions.setViewVisible
import com.utkangul.falci.internalClasses.dataClasses.*
import com.utkangul.falci.internalClasses.statusCode
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

class   ChatFragment : Fragment() {

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
        val chatBackButton = v.findViewById<ImageButton>(R.id.chatBackButton)

        val coinAmountText = v.findViewById<TextView>(R.id.coinAmountTextChat)
        val coinAmountTextLayout = v.findViewById<RelativeLayout>(R.id.coinAmountContainerLayoutChat)

        val chatListView = v.findViewById<ListView>(R.id.chatListView)

        val messages = mutableListOf<ChatMessage>()
        val oldMessages = mutableListOf<ChatMessage>()

        threadNumber = getHoroscopeData.thread

        val chatAdapter = ChatAdapter(requireContext(), messages)
        chatListView.adapter = chatAdapter

        val chatFuncs = ChatFuncs()

        coinAmountText.text = coin.current_coin.toString()

        threadNumber?.let { chatFuncs.getOldMessages(requireContext(), requireActivity() , it, oldMessages, chatAdapter, messages, chatListView) }

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

            if (chatDetails.remainingMessages == 0 && coin.current_coin <2){
                requireActivity().runOnUiThread{
                    Toast.makeText(requireContext(), "You dont have enough coin to send a message", Toast.LENGTH_SHORT).show()
                }
            }

            else{

                val scale = requireContext().resources.displayMetrics.density
                messageInputParams.width = (330 * scale + 0.5).toInt()
                messageInput.layoutParams = messageInputParams
                setViewGone(cancelUserMessage)
                val inputMethodManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

                inputMethodManager.hideSoftInputFromWindow(sendUserMessage.windowToken, 0)
                println(messageInput.text.toString())

                usermessagetomira = messageInput.text.toString()
                if (!messageInput.text.isNullOrEmpty()) {
                    chatDetails.addMessage = chatDetails.remainingMessages == 0
                    val myMessage = ChatMessage(usermessagetomira, true)
                    messages.add(myMessage)
                    messageInput.setText("")
                    chatAdapter.notifyDataSetChanged()
                    chatListView.smoothScrollToPosition(messages.size - 1)

                    val chatJson = createChatJSON(usermessagetomira, threadNumber, chatDetails.addMessage)
                    println("thread bu: $threadNumber")
                    println("sending chat json as: $chatJson")

                    postChatJson(urls.chatGptURL, chatJson)
                    { responseBody, exception ->
                        if (exception != null) {
                            println("Error: ${exception.message}")
                        }
                        else {
                            println("thread verdim response bu: $responseBody")
                            val jsonResponse = responseBody?.let { JSONObject(it) }
                            println("jsonResponse $jsonResponse")
                            if (statusCode == 200) {
                                activity?.runOnUiThread {
                                    if (jsonResponse != null) {

                                        if (jsonResponse.has("chat_messages")) {
                                            val chatMessagesArray = jsonResponse.getJSONArray("chat_messages")
                                            // En son mesaji bulmak icin chatMessagesArray'ı tersten dön
                                            for (i in chatMessagesArray.length() - 1 downTo 0) {
                                                val chatMessageObject = chatMessagesArray.getJSONObject(i)
                                                val sender = chatMessageObject.getString("owner")
                                                val message = chatMessageObject.getString("message")
                                                threadNumber = chatMessageObject.getInt("thread")
                                                println("sender $sender")
                                                val apiMessage = ChatMessage(message, false)
                                                println(apiMessage)
                                                messages.add(apiMessage)

                                                // En son mesajı bulduktan sonra döngüyü sonlandır
                                                break
                                            }

                                        }
                                        if (jsonResponse.has("coin")) {
                                            coin.current_coin = jsonResponse.getInt("coin")
                                            chatDetails.coin = jsonResponse.getInt("coin")
                                            coinAmountText.text = coin.current_coin.toString()
                                        }
                                        if (jsonResponse.has("remaining_message")) {
                                            chatDetails.remainingMessages =
                                                jsonResponse.getInt("remaining_message")
                                        }
                                        chatAdapter.notifyDataSetChanged()
                                        chatListView.smoothScrollToPosition(messages.size - 1)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        chatBackButton.setOnClickListener{requireActivity().onBackPressed()}
        return v
    }


    private fun createChatJSON(message: String, thread: Int? = null, addMessage: Boolean?): JSONObject {
        val chatJsonObject = JSONObject()
        chatJsonObject.put("message", message)
        if (thread != null) { chatJsonObject.put("thread", thread) }
        chatJsonObject.put("add_message", addMessage)

        return chatJsonObject
    }

     fun postChatJson(url: String, json: JSONObject, callback: (String?, Exception?) -> Unit) {

        val chatclient = OkHttpClient()

        val requestBody =
            json.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .header("Authorization", "Bearer ${tokensDataClass.accessToken}")
            .build()


        chatclient.newCall(request).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {
                requireActivity().runOnUiThread{ Toast.makeText(context, requireActivity().resources.getString(R.string.unexpected_error_occured_onServer_text), Toast.LENGTH_SHORT).show()}
                val intent = Intent(context, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent,null)
                requireActivity().finish()
                callback(null, e)
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                statusCode = response.code
                if (statusCode == 401){
                    println("unauthorized 401, taking new access token")
                    AuthenticationFunctions.PostJsonFunctions.takeFreshTokens(requireActivity(),urls.refreshURL, requireContext()) { responseBody401, exception ->
                        if (responseBody401 != null) {
                            println(tokensDataClass.accessToken)
                            postChatJson(url, json, callback)
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

