package com.utkangul.falci

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.text.Editable
import android.text.TextWatcher
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.utkangul.falci.internalClasses.dataClasses.controlVariables
import com.utkangul.falci.internalClasses.dataClasses.tokensDataClass
import com.utkangul.falci.internalClasses.dataClasses.urls
import okhttp3.*
import java.io.IOException

class EmailVerificationFragment : Fragment() {
    private lateinit var firstDigit: EditText
    private lateinit var secondDigit: EditText
    private lateinit var thirdDigit: EditText
    private lateinit var forthDigit: EditText
    private lateinit var fifthDigit: EditText
    private lateinit var sixthDigit: EditText
    private var inputCode: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val v = inflater.inflate(R.layout.fragment_email_verification, container, false)

        firstDigit = v.findViewById(R.id.firstDigit)
        secondDigit = v.findViewById(R.id.secondDigit)
        thirdDigit = v.findViewById(R.id.thirdDigit)
        forthDigit = v.findViewById(R.id.forthDigit)
        fifthDigit = v.findViewById(R.id.fifthDigit)
        sixthDigit = v.findViewById(R.id.sixthDigit)
        val checkVerificationCodeButton = v.findViewById<AppCompatButton>(R.id.checkVerificationCodeButton)

        // Ortak bir TextWatcher'ı tüm EditText bileşenlerine ekleyin
        val commonTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (s?.length == 1) {
                    focusToNextEditText()
                }
            }
        }

        // Ortak TextWatcher'ı tüm EditText bileşenlerine ekleyin
        firstDigit.addTextChangedListener(commonTextWatcher)
        secondDigit.addTextChangedListener(commonTextWatcher)
        thirdDigit.addTextChangedListener(commonTextWatcher)
        forthDigit.addTextChangedListener(commonTextWatcher)
        fifthDigit.addTextChangedListener(commonTextWatcher)
        sixthDigit.addTextChangedListener(commonTextWatcher)

        checkVerificationCodeButton.setOnClickListener{
            if (controlVariables.allowCheck){
                inputCode = "${firstDigit.text}${secondDigit.text}${thirdDigit.text}${forthDigit.text}${fifthDigit.text}${sixthDigit.text}"
                println(inputCode)

                println("${urls.emailVerificationURL}$inputCode")
                checkEmail()
            }
        }

        val resendCard = v.findViewById<CardView>(R.id.resendEmail)
        val resendEmailImage = v.findViewById<ImageView>(R.id.resendEmailImage)
        resendEmailImage.setOnClickListener{resendCard.performClick()}
        resendCard.setOnClickListener{
            val client = OkHttpClient()
            val request = Request.Builder()
                .url(urls.resendVerificationEmail)
                .get()
                .header("Authorization", "Bearer ${tokensDataClass.accessToken}")
                .build()
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) { println("exception $e") }
                override fun onResponse(call: Call, response: Response) { val responseCode = response.code;println("responseCode $responseCode")}
            })
        }

        return v
    }

    private fun checkEmail(){
        val apiUrl = "${urls.emailVerificationURL}$inputCode"
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
                println("response $response")
                val responseCode = response.code
                println("responseCode $responseCode")
                if (responseCode == 200){
                    val options = ActivityOptions.makeCustomAnimation(requireContext(), R.anim.activity_slide_down, 0)
                    val intent = Intent(requireActivity(), CompleteProfile::class.java);startActivity(intent, options.toBundle())
                } else{
                    requireActivity().runOnUiThread{
                        Toast.makeText(requireContext(), "Some Error Occured, Probably You Entered The Wrong Code $responseCode", Toast.LENGTH_SHORT).show()
                    }
                }

            }
        })
    }
    fun focusToNextEditText() {
        when {
            firstDigit.isFocused -> secondDigit.requestFocus()
            secondDigit.isFocused -> thirdDigit.requestFocus()
            thirdDigit.isFocused -> forthDigit.requestFocus()
            forthDigit.isFocused -> fifthDigit.requestFocus()
            fifthDigit.isFocused -> sixthDigit.requestFocus()
            sixthDigit.isFocused -> controlVariables.allowCheck = true
        }
    }
}