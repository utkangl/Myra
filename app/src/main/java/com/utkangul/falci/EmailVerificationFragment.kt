package com.utkangul.falci

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.utkangul.falci.internalClasses.AuthenticationFunctions.PostJsonFunctions.takeFreshTokens
import com.utkangul.falci.internalClasses.dataClasses.controlVariables
import com.utkangul.falci.internalClasses.dataClasses.tokensDataClass
import com.utkangul.falci.internalClasses.dataClasses.urls
import com.utkangul.falci.internalClasses.statusCode
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
    private var countdownTimer: CountDownTimer? = null
    private var remainingTimeMillis: Long = 120000

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
        val resendEmailCountdownTextview = v.findViewById<TextView>(R.id.resendEmailCountdownTextview)
        val takeInfoEmailVerification = v.findViewById<ImageView>(R.id.takeInfoEmailVerification)
        val emailVerificationBackButton = v.findViewById<ImageButton>(R.id.emailVerificationBackButton)


        fun updateCountdownUI() {
            // Süreyi UI'da güncelleyebilirsiniz, örneğin bir TextView üzerinde
            val minutes = (remainingTimeMillis / 1000) / 60
            val seconds = (remainingTimeMillis / 1000) % 60
            // Örnek olarak bir TextView güncelleme:
            resendEmailCountdownTextview.text = String.format("%02d:%02d", minutes, seconds)
        }

        // Geri sayım sürecini durduran fonksiyon
        fun stopCountdownTimer() {
            countdownTimer?.cancel()
        }

        fun startCountdownTimer() {
            countdownTimer = object : CountDownTimer(remainingTimeMillis, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    // Geri sayım süresi her saniye azaldıkça burası çalışır
                    remainingTimeMillis = millisUntilFinished
                    controlVariables.resendMailCountdownFinished = false
                    updateCountdownUI()
                }

                override fun onFinish() {
                    // Geri sayım tamamlandığında burası çalışır
                    remainingTimeMillis = 0
                    updateCountdownUI()
                    stopCountdownTimer()
                    controlVariables.resendMailCountdownFinished = true
                    // İlgili işlemleri burada gerçekleştirebilirsiniz
                }
            }.start()
        }

        startCountdownTimer()

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

        takeInfoEmailVerification.setOnClickListener {
            Toast.makeText(requireContext(), requireActivity().resources.getString(R.string.check_your_spambox_text), Toast.LENGTH_SHORT).show()
        }

        checkVerificationCodeButton.setOnClickListener {
            if (controlVariables.allowCheck) {
                inputCode = "${firstDigit.text}${secondDigit.text}${thirdDigit.text}${forthDigit.text}${fifthDigit.text}${sixthDigit.text}"
                println(inputCode)
                println("${urls.emailVerificationURL}$inputCode")
                checkEmail()
            } else Toast.makeText(requireContext(), requireActivity().resources.getString(R.string.code_is_not_Six), Toast.LENGTH_SHORT).show()
        }

        val resendCard = v.findViewById<CardView>(R.id.resendEmail)
        val resendEmailImage = v.findViewById<ImageView>(R.id.resendEmailImage)
        resendEmailImage.setOnClickListener { resendCard.performClick() }
        resendCard.setOnClickListener {
            if (controlVariables.resendMailCountdownFinished) {
                fun resendVerificationEmail(){
                    remainingTimeMillis = 120000
                    startCountdownTimer()
                    updateCountdownUI()
                    val client = OkHttpClient()
                    val request = Request.Builder()
                        .url(urls.resendVerificationEmailURL)
                        .get()
                        .header("Authorization", "Bearer ${tokensDataClass.accessToken}")
                        .build()
                    client.newCall(request).enqueue(object : Callback {

                        override fun onFailure(call: Call, e: IOException) {
                            requireActivity().runOnUiThread{ Toast.makeText(context, requireActivity().resources.getString(R.string.unexpected_error_occured_onServer_text), Toast.LENGTH_SHORT).show()}
                            val intent = Intent(context, MainActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                            startActivity(intent,null)
                            requireActivity().finish()
                            e.printStackTrace()
                        }

                        override fun onResponse(call: Call, response: Response) {

                            val responseCode = response.code
                            println("responseCode $responseCode")
                            if (responseCode == 200) {
                                requireActivity().runOnUiThread{
                                    Toast.makeText(requireContext(), requireActivity().resources.getString(R.string.mail_sent_success), Toast.LENGTH_SHORT)
                                        .show()
                                }
                            }
                            else if (statusCode == 401) {
                                takeFreshTokens(requireActivity(),urls.refreshURL, requireContext()) { responseBody401, exception401 ->
                                    if (exception401 != null) exception401.printStackTrace()
                                    else {
                                        if (responseBody401 != null) {
                                            resendVerificationEmail()
                                        }
                                    }
                                }
                            }
                            else{
                                requireActivity().runOnUiThread { Toast.makeText(context, requireActivity().resources.getString(R.string.unexpected_error_occured_onServer_text), Toast.LENGTH_SHORT).show()}
                                val options = ActivityOptions.makeCustomAnimation(context, R.anim.activity_slide_down, 0)
                                val intent = Intent(context, MainActivity::class.java)
                                ContextCompat.startActivity(requireContext(), intent, options.toBundle())
                            }
                        }
                    })
                }
                resendVerificationEmail()
            } else requireActivity().runOnUiThread{
                Toast.makeText(requireContext(), requireActivity().resources.getString(R.string.mail_cooldown_warning), Toast.LENGTH_LONG).show()
            }

        }

        emailVerificationBackButton.setOnClickListener{
            requireActivity().onBackPressed()
        }
        return v
    }

    internal fun checkEmail() {
        val apiUrl = "${urls.emailVerificationURL}$inputCode"
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(apiUrl)
            .get()
            .header("Authorization", "Bearer ${tokensDataClass.accessToken}")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                requireActivity().runOnUiThread{ Toast.makeText(context, requireActivity().resources.getString(R.string.unexpected_error_occured_onServer_text), Toast.LENGTH_SHORT).show()}
                val intent = Intent(context, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent,null)
                requireActivity().finish()
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                println("response $response")
                val responseCode = response.code
                println("responseCode $responseCode")
                when (responseCode) {
                    200 -> {
                        val options = ActivityOptions.makeCustomAnimation(requireContext(), R.anim.activity_slide_down, 0)
                        val intent = Intent(requireActivity(), CompleteProfile::class.java);startActivity(intent, options.toBundle())
                    }
                    401 -> {
                        takeFreshTokens(requireActivity(),urls.refreshURL, requireContext()) { responseBody401, exception ->
                            if (responseBody401 != null) {
                                checkEmail()
                            } else {
                                exception?.printStackTrace()
                            }
                        }
                    }
                    else -> {
                        requireActivity().runOnUiThread {
                            Toast.makeText(requireContext(), requireActivity().resources.getString(R.string.wrong_verif_code), Toast.LENGTH_SHORT).show()
                        }
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