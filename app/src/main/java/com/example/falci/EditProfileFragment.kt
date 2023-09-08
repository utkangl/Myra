package com.example.falci

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.widget.AppCompatButton

class EditProfileFragment : Fragment() {

    lateinit var nameField: EditText
    lateinit var genderField: EditText
    lateinit var birthDateField: EditText
    lateinit var birthTimeField: EditText
    lateinit var locationField: EditText
    lateinit var occupationField: EditText
    lateinit var relationShipStatusField: EditText
    lateinit var savebutton: AppCompatButton

    // Define a map to keep track of the original text for each EditText
    val originalTextMap = mutableMapOf<EditText, String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_editprofile, container, false)

        nameField = v.findViewById(R.id.nameedittext)
        genderField = v.findViewById(R.id.genderedittext)
        birthDateField = v.findViewById(R.id.birthdateedittext)
        birthTimeField = v.findViewById(R.id.birthtimeedittext)
        locationField = v.findViewById(R.id.locationedittext)
        occupationField = v.findViewById(R.id.occupationedittext)
        relationShipStatusField = v.findViewById(R.id.relationshipstatusedittext)
        savebutton = v.findViewById(R.id.savebutton)

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Store the original text before it changes
                originalTextMap[nameField] = nameField.text.toString()
                originalTextMap[genderField] = genderField.text.toString()
                originalTextMap[birthDateField] = birthDateField.text.toString()
                originalTextMap[birthTimeField] = birthTimeField.text.toString()
                originalTextMap[locationField] = locationField.text.toString()
                originalTextMap[occupationField] = occupationField.text.toString()
                originalTextMap[relationShipStatusField] = relationShipStatusField.text.toString()
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                savebutton.visibility = View.VISIBLE
            }
        }

        // Add TextWatcher to each EditText
        nameField.addTextChangedListener(textWatcher)
        genderField.addTextChangedListener(textWatcher)
        birthDateField.addTextChangedListener(textWatcher)
        birthTimeField.addTextChangedListener(textWatcher)
        locationField.addTextChangedListener(textWatcher)
        occupationField.addTextChangedListener(textWatcher)
        relationShipStatusField.addTextChangedListener(textWatcher)

        // Set OnClickListener for the savebutton
        savebutton.setOnClickListener {
            for ((editText, originalText) in originalTextMap) {
                val newText = editText.text.toString()
                if (newText != originalText) {
                    println(newText)
                    originalTextMap[editText] = newText
                }
            }
            savebutton.visibility = View.INVISIBLE
        }

        return v
    }
}