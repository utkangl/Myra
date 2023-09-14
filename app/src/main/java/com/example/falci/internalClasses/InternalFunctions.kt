package com.example.falci.internalClasses
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.app.Activity
import android.content.Context
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.appcompat.widget.AppCompatButton
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.falci.R
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
            val currentBirthDay = userProfile.birthDay
            val currentBirthTime = userProfile.birthTime

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
    object SetupFieldClickListener{
        fun setupFieldClickListener(field: TextView, spinner: Spinner, hintView: View) {
            field.setOnClickListener {
                setViewVisible(spinner)
                setViewGone(hintView)
            }
        }
    }

    object TimeFormatFunctions{
        fun separateBirthTime(birthDay: String): String {
            return birthDay.split("T")[1].substring(0,8)
        }

        fun separateBirthDate(birthDay: String): String {
            return birthDay.split("T")[0]
        }
    }

    object AnimateCardSize{
        fun animateCardSize(context: Context, targetWidthDp: Int, targetHeightDp: Int, targetCard: View) {
            val scale = context.resources.displayMetrics.density
            val newWidth = (targetWidthDp * scale + 0.5f).toInt()
            val newHeight = (targetHeightDp * scale + 0.5f).toInt()
            val params = targetCard.layoutParams as RelativeLayout.LayoutParams

            val animatorWidth = ValueAnimator.ofInt(targetCard.width, newWidth)
            animatorWidth.addUpdateListener { animation ->
                val value = animation.animatedValue as Int
                params.width = value
                targetCard.layoutParams = params
            }

            val animatorHeight = ValueAnimator.ofInt(targetCard.height, newHeight)
            animatorHeight.addUpdateListener { animation ->
                val value = animation.animatedValue as Int
                params.height = value
                targetCard.layoutParams = params
            }

            val animatorSet = AnimatorSet()
            animatorSet.playTogether(animatorWidth, animatorHeight)
            animatorSet.duration = 300
            animatorSet.start()
        }
    }

    object ReplaceFragmentWithAnimation{
        fun replaceFragmentWithAnimation(fragmentManager: FragmentManager, targetFragment: Fragment) {
            fragmentManager.beginTransaction().apply {
                setCustomAnimations(
                    R.anim.slide_down,
                    R.anim.slide_up,
                    R.anim.slide_down,
                    R.anim.slide_up
                )
                replace(R.id.main_fragment_container, targetFragment)
                addToBackStack(null)
                commit()
            }
        }
    }

    object ReplaceActivityToFragment{
        fun replaceActivityToFragment(fragmentManager: FragmentManager, targetFragment: Fragment) {
            fragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.slide_down,
                    R.anim.slide_up,
                    R.anim.slide_down,
                    R.anim.slide_up
                )
                .replace(R.id.main_fragment_container, targetFragment)
                .addToBackStack(null)
                .commit()
        }
    }

}

