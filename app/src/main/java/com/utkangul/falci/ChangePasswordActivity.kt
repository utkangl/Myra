package com.utkangul.falci

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import com.utkangul.falci.internalClasses.dataClasses.controlVariables

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
        setContentView(R.layout.activity_change_password)

        firstDigit  = findViewById(R.id.firstDigit)
        secondDigit = findViewById(R.id.secondDigit)
        thirdDigit  = findViewById(R.id.thirdDigit)
        forthDigit  = findViewById(R.id.forthDigit)
        fifthDigit  = findViewById(R.id.fifthDigit)
        sixthDigit  = findViewById(R.id.sixthDigit)

        val sendVerificationCodeButton   = findViewById<AppCompatButton>(R.id.sendVerificationCodeButton)
        val resendEmailCountdownTextview = findViewById<TextView>(R.id.resendEmailCountdownTextview)
        val takeInfoEmailVerificationChangePassword    = findViewById<ImageView>(R.id.takeInfoEmailVerificationChangePassword)
        val resendEmailChangePassword    = findViewById<ImageView>(R.id.resendEmailChangePassword)

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