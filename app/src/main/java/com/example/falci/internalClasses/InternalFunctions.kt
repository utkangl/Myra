package com.example.falci.internalClasses
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import com.example.falci.internalClasses.InternalFunctions.SetVisibilityFunctions.setViewGone
import com.example.falci.internalClasses.InternalFunctions.SetVisibilityFunctions.setViewVisible
import org.json.JSONObject

class InternalFunctions {

    object SetVisibilityFunctions{
        fun <T : View> setViewVisible(vararg components: T) {
            for (component in components) {
                component.visibility = View.VISIBLE
            }
        }

        fun <T : View> setViewGone(vararg components: T) {
            for (component in components) {
                component.visibility = View.GONE
            }
        }
    }

    object UpdateProfileFieldIfChanged{

        fun updateProfileFieldIfChanged(fieldKey: String, textField: TextView, json: JSONObject, profileValue: String) {
            val fieldValue = textField.text.toString()
            if (fieldValue != profileValue) {
                json.put(fieldKey, fieldValue)
            }
        }

        fun updateBirthDayIfChanged(jsonField: String, birthDateField: TextView, birthTimeField: TextView, userProfile: UserProfileDataClass, editProfileJson: JSONObject) {
            val newBirthDate = birthDateField.text.toString()
            val newBirthTime = birthTimeField.text.toString()
            val currentBirthDay = userProfile.profileBirth_day
            val currentBirthTime = userProfile.profileBirth_time

            if (newBirthDate != currentBirthDay || newBirthTime != currentBirthTime) {
                val combinedBirthDay = "$newBirthDate $newBirthTime +0000"
                editProfileJson.put(jsonField, combinedBirthDay)
            }
        }

    }

    object AddTextWatcher{
        fun addTextWatcher(vararg views: TextView, textWatcher: TextWatcher){
            for (view in views){
                view.addTextChangedListener(textWatcher)
            }
        }
    }

    object SetupSpinnerAndField{
        fun setupSpinnerAndField(
            spinner: Spinner,
            hintView: View,
            adapter:  ArrayAdapter<CharSequence>,
            isUserInteractedFlag: Boolean,
            onItemSelectedAction: (String) -> Unit
        ) {
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
            var isUserInteracted = isUserInteractedFlag
            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    if (isUserInteracted) {
                        val selectedValue = parent?.getItemAtPosition(position) as? String
                        selectedValue?.let { onItemSelectedAction(it) }
                        setViewVisible(hintView) ; setViewGone(spinner)
                    } else { isUserInteracted = true }
                }
                override fun onNothingSelected(parent: AdapterView<*>?) { println("Nothing Selected") }
            }
        }
    }

    object TimeFormatFunctions{
        fun seperateBirthTime(birthDay: String): String {
            return birthDay.split("T")[1].substring(0,8)
        }

        fun seperateBirthDate(birthDay: String): String {
            return birthDay.split("T")[0]
        }
    }

}