package com.example.falci

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import com.example.falci.internalClasses.dataClasses.controlVariables

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
                if (inputCode == "123456"){
                    val options = ActivityOptions.makeCustomAnimation(requireContext(), R.anim.activity_slide_down, 0)
                    val intent = Intent(requireActivity(), CompleteProfile::class.java);startActivity(intent, options.toBundle())
                }
            }
        }
        return v
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