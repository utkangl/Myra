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
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
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
    private var inputCode: String? = null
    private var countdownTimer: CountDownTimer? = null
    private var remainingTimeMillis: Long = 120000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.runOnUiThread {
            val languageSharedPreferences: SharedPreferences = application.getSharedPreferences("language_choice", Context.MODE_PRIVATE)
            val languageChoice = languageSharedPreferences.getString("language", null)
            if (!languageChoice.isNullOrEmpty()) {
                val locale = Locale(languageChoice)
                Locale.setDefault(locale)
                val config = Configuration()
                config.locale = locale
                resources.updateConfiguration(config, resources.displayMetrics)
            }
        }

        setContentView(R.layout.activity_change_password)

        firstDigit = findViewById(R.id.firstDigit)
        secondDigit = findViewById(R.id.secondDigit)
        thirdDigit = findViewById(R.id.thirdDigit)
        forthDigit = findViewById(R.id.forthDigit)
        fifthDigit = findViewById(R.id.fifthDigit)
        sixthDigit = findViewById(R.id.sixthDigit)

        val sendVerificationCodeButton = findViewById<AppCompatButton>(R.id.sendVerificationCodeButton)
        val resendEmailCountdownTextview = findViewById<TextView>(R.id.resendEmailCountdownTextviewChangePassword)
        val takeInfoEmailVerificationChangePassword = findViewById<ImageView>(R.id.takeInfoEmailVerificationChangePassword)
        val resendEmailChangePassword = findViewById<CardView>(R.id.resendEmailChangePassword)
        val resendEmailImageChangePassword = findViewById<ImageView>(R.id.resendEmailImageChangePassword)
        val changePasswordNewPassword = findViewById<EditText>(R.id.changePasswordNewPassword)
        val changePasswordNewPasswordAgain = findViewById<EditText>(R.id.changePasswordNewPasswordAgain)
        val changePasswordButton = findViewById<AppCompatButton>(R.id.changePasswordButton)
        val changePasswordBackButton = findViewById<ImageButton>(R.id.changePasswordBackButton)


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
            Toast.makeText(this, this.resources.getString(R.string.check_your_spambox_text), Toast.LENGTH_SHORT).show()
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
                            Toast.makeText(
                                this@ChangePasswordActivity,
                                this@ChangePasswordActivity.resources.getString(R.string.unexpected_error_occured_onServer_text),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        val intent = Intent(this@ChangePasswordActivity, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                        startActivity(intent, null)
                        this@ChangePasswordActivity.finish()
                        e.printStackTrace()
                    }

                    override fun onResponse(call: Call, response: Response) {
                        val responseCode = response.code
                        println("responseCode $responseCode")
                        when (responseCode) {
                            200 -> {
                                this@ChangePasswordActivity.runOnUiThread {
                                    Toast.makeText(
                                        this@ChangePasswordActivity,
                                        this@ChangePasswordActivity.resources.getString(R.string.mail_sent_success),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    startCountdownTimer()
                                }
                            }
                            401 -> {
                                AuthenticationFunctions.PostJsonFunctions.takeFreshTokens(
                                    this@ChangePasswordActivity, urls.refreshURL, this@ChangePasswordActivity
                                ) { responseBody401, exception401 ->
                                    if (exception401 != null) exception401.printStackTrace()
                                    else {
                                        if (responseBody401 != null) {
                                            sendVerificationCodeForChangePassword()
                                        }
                                    }
                                }
                            }
                            400 -> {
                                this@ChangePasswordActivity.runOnUiThread {
                                    Toast.makeText(
                                        this@ChangePasswordActivity,
                                        this@ChangePasswordActivity.resources.getString(R.string.mail_cooldown_warning), Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                            else -> {
                                this@ChangePasswordActivity.runOnUiThread {
                                    Toast.makeText(
                                        this@ChangePasswordActivity,
                                        this@ChangePasswordActivity.resources.getString(R.string.unexpected_error_occured_onServer_text),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                                val options = ActivityOptions.makeCustomAnimation(this@ChangePasswordActivity, R.anim.activity_slide_down, 0)
                                val intent = Intent(this@ChangePasswordActivity, MainActivity::class.java)
                                ContextCompat.startActivity(this@ChangePasswordActivity, intent, options.toBundle())
                            }
                        }
                    }
                })
            }
            if (controlVariables.resendMailChangePasswordCountdownFinished) sendVerificationCodeForChangePassword()
            else Toast.makeText(this, this.resources.getString(R.string.mail_cooldown_warning), Toast.LENGTH_LONG).show()
        }

        changePasswordButton.setOnClickListener {
            if (controlVariables.allowCheckChangePasswordCode) {
                inputCode = "${firstDigit.text}${secondDigit.text}${thirdDigit.text}${forthDigit.text}${fifthDigit.text}${sixthDigit.text}"
                println(inputCode)
                if (inputCode!!.length == 6) {
                    resetPasswordDataClass.code = inputCode
                    if (changePasswordNewPassword.text.contentEquals(changePasswordNewPasswordAgain.text) && !changePasswordNewPassword.text.isNullOrEmpty()) {
                        resetPasswordDataClass.password = changePasswordNewPassword.text.toString()
                        val changePasswordJSON =
                            createJsonObject("code" to resetPasswordDataClass.code!!, "password" to resetPasswordDataClass.password!!)
                        postJsonWithHeader(
                            this,
                            "https://api.kircibros.com/auth/reset/password/",
                            changePasswordJSON,
                            this
                        ) { responseBody, exception ->
                            exception?.printStackTrace()
                            if (exception == null) {
                                if (statusCode == 200) {
                                    println("change password responseBody $responseBody")
                                    this@ChangePasswordActivity.runOnUiThread {
                                        Toast.makeText(
                                            this,
                                            this@ChangePasswordActivity.resources.getString(R.string.password_change_success),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        val options = ActivityOptions.makeCustomAnimation(this, R.anim.activity_slide_down, 0)
                                        val intent = Intent(this, MainActivity::class.java)
                                        ContextCompat.startActivity(this, intent, options.toBundle())
                                    }
                                } else if (statusCode == 400) {
                                    println("wrong verification code, statuscode: 400")
                                    this@ChangePasswordActivity.runOnUiThread {
                                        Toast.makeText(
                                            this,
                                            this.resources.getString(R.string.wrong_verif_code),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            }
                        }
                    } else this.runOnUiThread {
                        Toast.makeText(this, this.resources.getString(R.string.empty_or_unmatch_passwords), Toast.LENGTH_SHORT).show()
                    }
                }
            } else Toast.makeText(this, this.resources.getString(R.string.code_is_not_Six), Toast.LENGTH_SHORT).show()
        }

        resendEmailImageChangePassword.setOnClickListener { resendEmailChangePassword.performClick() }

        resendEmailChangePassword.setOnClickListener {
            if (controlVariables.resendMailChangePasswordCountdownFinished) {
                sendVerificationCodeButton.performClick()
            } else Toast.makeText(this, this.resources.getString(R.string.mail_cooldown_warning), Toast.LENGTH_LONG).show()

        }


        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val options = ActivityOptions.makeCustomAnimation(this@ChangePasswordActivity, R.anim.activity_slide_down, 0)
                val mainActivityIntent = Intent(this@ChangePasswordActivity, ProfileActivity::class.java)
                startActivity(mainActivityIntent, options.toBundle())
                controlVariables.isFromChangePassword = true
            }
        };this.onBackPressedDispatcher.addCallback(this, callback)

        changePasswordBackButton.setOnClickListener { callback.handleOnBackPressed() }

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