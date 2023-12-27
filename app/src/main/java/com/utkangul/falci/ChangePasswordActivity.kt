package com.utkangul.falci

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.utkangul.falci.internalClasses.AuthenticationFunctions
import com.utkangul.falci.internalClasses.AuthenticationFunctions.CreateJsonObject.createJsonObject
import com.utkangul.falci.internalClasses.AuthenticationFunctions.PostJsonFunctions.postJsonWithHeader
import com.utkangul.falci.internalClasses.dataClasses.controlVariables
import com.utkangul.falci.internalClasses.dataClasses.resetPasswordDataClass
import com.utkangul.falci.internalClasses.dataClasses.tokensDataClass
import com.utkangul.falci.internalClasses.dataClasses.urls
import com.utkangul.falci.internalClasses.statusCode
import okhttp3.*
import java.io.IOException
import java.util.*

class ChangePasswordActivity : AppCompatActivity() {
    private lateinit var firstDigit: EditText
    private lateinit var secondDigit: EditText
    private lateinit var thirdDigit: EditText
    private lateinit var forthDigit: EditText
    private lateinit var fifthDigit: EditText
    private lateinit var sixthDigit: EditText
    private var          inputCode: String? = null
    private var          countdownTimer: CountDownTimer? = null
    private var          remainingTimeMillis: Long = 120000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.runOnUiThread{
            val languageSharedPreferences: SharedPreferences = application.getSharedPreferences("language_choice", Context.MODE_PRIVATE)
            val languageChoice = languageSharedPreferences.getString("language",null)
            if (!languageChoice.isNullOrEmpty()){
                val locale = Locale(languageChoice)
                Locale.setDefault(locale)
                val config = Configuration()
                config.locale = locale
                resources.updateConfiguration(config, resources.displayMetrics)
            }
        }

        setContentView(R.layout.activity_change_password)

        firstDigit  = findViewById(R.id.firstDigit)
        secondDigit = findViewById(R.id.secondDigit)
        thirdDigit  = findViewById(R.id.thirdDigit)
        forthDigit  = findViewById(R.id.forthDigit)
        fifthDigit  = findViewById(R.id.fifthDigit)
        sixthDigit  = findViewById(R.id.sixthDigit)

        val sendVerificationCodeButton               = findViewById<AppCompatButton>(R.id.sendVerificationCodeButton)
        val resendEmailCountdownTextview             = findViewById<TextView>(R.id.resendEmailCountdownTextviewChangePassword)
        val takeInfoEmailVerificationChangePassword  = findViewById<ImageView>(R.id.takeInfoEmailVerificationChangePassword)
        val resendEmailChangePassword                = findViewById<CardView>(R.id.resendEmailChangePassword)
        val resendEmailImageChangePassword           = findViewById<ImageView>(R.id.resendEmailImageChangePassword)
        val changePasswordNewPassword                = findViewById<EditText>(R.id.changePasswordNewPassword)
        val changePasswordNewPasswordAgain           = findViewById<EditText>(R.id.changePasswordNewPasswordAgain)
        val changePasswordButton                     = findViewById<AppCompatButton>(R.id.changePasswordButton)


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
                    controlVariables.resendMailChangePasswordCountdownFinished = false
                    updateCountdownUI()
                }

                override fun onFinish() {
                    // Geri sayım tamamlandığında burası çalışır
                    remainingTimeMillis = 0
                    updateCountdownUI()
                    stopCountdownTimer()
                    controlVariables.resendMailChangePasswordCountdownFinished = true
                    // İlgili işlemleri burada gerçekleştirebilirsiniz
                }
            }.start()
        }

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

        takeInfoEmailVerificationChangePassword.setOnClickListener {
            Toast.makeText(this, "Try checking your spambox if you \n cant see mail in inbox", Toast.LENGTH_SHORT).show()
        }

        sendVerificationCodeButton.setOnClickListener {
            fun sendVerificationCodeForChangePassword() {
                val client = OkHttpClient()
                val request = Request.Builder()
                    .url("https://api.kircibros.com/auth/reset/code/")
                    .get()
                    .header("Authorization", "Bearer ${tokensDataClass.accessToken}")
                    .build()
                client.newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        this@ChangePasswordActivity.runOnUiThread {
                            println("exception $e")
                            Toast.makeText(this@ChangePasswordActivity, "An unexpected error occured, mail could not be sent", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onResponse(call: Call, response: Response) {
                        val responseCode = response.code
                        println("responseCode $responseCode")
                        when (responseCode) {
                            200 -> {
                                this@ChangePasswordActivity.runOnUiThread {
                                    Toast.makeText(this@ChangePasswordActivity, "mail sent succesfully, dont forget to check your spambox", Toast.LENGTH_SHORT).show()
                                    startCountdownTimer()
                                }
                            }
                            401 -> {
                                AuthenticationFunctions.PostJsonFunctions.takeFreshTokens(
                                    urls.refreshURL, this@ChangePasswordActivity) { responseBody401, exception401 ->
                                    if (exception401 != null) exception401.printStackTrace()
                                    else {
                                        if (responseBody401 != null) {
                                            sendVerificationCodeForChangePassword()
                                        }
                                    }
                                }
                            }
                            400 -> {
                                this@ChangePasswordActivity.runOnUiThread{ Toast.makeText(this@ChangePasswordActivity, "You got an email less then 2 minutes ago", Toast.LENGTH_SHORT).show() }
                            }
                            else -> {
                                this@ChangePasswordActivity.runOnUiThread { Toast.makeText(this@ChangePasswordActivity, "unexpected error: $responseCode", Toast.LENGTH_SHORT).show() }
                                this@ChangePasswordActivity.runOnUiThread { Toast.makeText(this@ChangePasswordActivity, " redirecting to main screen ...", Toast.LENGTH_SHORT).show() }
//                                val options = ActivityOptions.makeCustomAnimation(this@ChangePasswordActivity, R.anim.activity_slide_down, 0)
//                                val intent = Intent(this@ChangePasswordActivity, MainActivity::class.java)
//                                ContextCompat.startActivity(this@ChangePasswordActivity, intent, options.toBundle())
                            }
                        }
                    }
                })
            }
            if (controlVariables.resendMailChangePasswordCountdownFinished) sendVerificationCodeForChangePassword()
            else Toast.makeText(this, "You can get verification mails in every 2 minutes, wait for countdown", Toast.LENGTH_LONG).show()
        }

        changePasswordButton.setOnClickListener {
            if (controlVariables.allowCheckChangePasswordCode) {
                inputCode = "${firstDigit.text}${secondDigit.text}${thirdDigit.text}${forthDigit.text}${fifthDigit.text}${sixthDigit.text}"
                println(inputCode)
                if (inputCode!!.length == 6){
                    resetPasswordDataClass.code = inputCode
                    if (changePasswordNewPassword.text.contentEquals(changePasswordNewPasswordAgain.text) && !changePasswordNewPassword.text.isNullOrEmpty()){
                        resetPasswordDataClass.password = changePasswordNewPassword.text.toString()
                        val changePasswordJSON = createJsonObject("code" to resetPasswordDataClass.code!!, "password" to resetPasswordDataClass.password!!)
                        postJsonWithHeader("https://api.kircibros.com/auth/reset/password/", changePasswordJSON,this ){
                            responseBody, exception ->
                            exception?.printStackTrace()
                            if (exception == null){
                                if (statusCode == 200){
                                    println("change password responseBody $responseBody")
                                    this@ChangePasswordActivity.runOnUiThread{
                                        Toast.makeText(this, "Password Successfuly Changed", Toast.LENGTH_SHORT).show()
                                        val options = ActivityOptions.makeCustomAnimation(this, R.anim.activity_slide_down, 0)
                                        val intent = Intent(this, MainActivity::class.java)
                                        ContextCompat.startActivity(this, intent, options.toBundle())
                                    }
                                }
                                else if( statusCode == 400){
                                    println("wrong verification code, statuscode: 400")
                                    this@ChangePasswordActivity.runOnUiThread{
                                        Toast.makeText(this, "Password could not change, You entered a wrong verification code probably", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        }
                    } else this@ChangePasswordActivity.runOnUiThread{ Toast.makeText(this, "Passwords are empty or doesnt match", Toast.LENGTH_SHORT).show() }
                }
            } else Toast.makeText(this, "Code must be 6 digits exactly", Toast.LENGTH_SHORT).show()
        }

        resendEmailImageChangePassword.setOnClickListener { resendEmailChangePassword.performClick() }

        resendEmailChangePassword.setOnClickListener {
            if (controlVariables.resendMailChangePasswordCountdownFinished) {
                sendVerificationCodeButton.performClick()
            } else Toast.makeText(this, "You can get verification mails in every 2 minutes, wait for countdown", Toast.LENGTH_LONG).show()

        }
    }

    fun focusToNextEditText() {
        when {
            firstDigit.isFocused -> secondDigit.requestFocus()
            secondDigit.isFocused -> thirdDigit.requestFocus()
            thirdDigit.isFocused -> forthDigit.requestFocus()
            forthDigit.isFocused -> fifthDigit.requestFocus()
            fifthDigit.isFocused -> sixthDigit.requestFocus()
            sixthDigit.isFocused -> controlVariables.allowCheckChangePasswordCode = true
        }
    }
}