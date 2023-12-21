package com.utkangul.falci


import android.content.Context
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView


class LinkTextView @JvmOverloads constructor(context: Context?, attrs: AttributeSet? = null) :
    AppCompatTextView(context!!, attrs) {
    override fun setText(text: CharSequence, type: BufferType) {
        val content = SpannableString(text)
        content.setSpan(UnderlineSpan(), 0, content.length, 0)
        super.setText(content, type)
    }
}