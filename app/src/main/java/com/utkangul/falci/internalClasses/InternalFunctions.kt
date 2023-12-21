package com.utkangul.falci.internalClasses
import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.text.TextWatcher
import android.view.View
import android.widget.*
import com.utkangul.falci.R
import com.utkangul.falci.internalClasses.InternalFunctions.SetVisibilityFunctions.setViewInvisible
import com.utkangul.falci.internalClasses.InternalFunctions.SetVisibilityFunctions.setViewVisibleWithAnimation
import com.utkangul.falci.internalClasses.InternalFunctions.TimeFormatFunctions.convertDateTimeToTimestamp
import com.utkangul.falci.internalClasses.dataClasses.UserProfileDataClass
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class InternalFunctions {

    object SetVisibilityFunctions{
        fun <T : View> setViewVisible(vararg components: T) {
            for (component in components) {
                component.visibility = View.VISIBLE
            }
        }
        fun <T : View>setViewVisibleWithAnimation(context: Context, vararg components: T?) {
            for (component in components) {
                val fadeOut = AnimatorInflater.loadAnimator(context, R.animator.fade_in)
                fadeOut.setTarget(component)
                fadeOut.start()
                component!!.visibility = View.VISIBLE
            }
        }

        fun <T : View> setViewGone(vararg components: T) {
            for (component in components) {
                component.visibility = View.GONE
            }
        }

        fun setViewInvisible(vararg components: View?) {
            for (component in components) {
                component!!.visibility = View.INVISIBLE
            }
        }

        fun <T : View> setViewGoneWithAnimation(context: Context, vararg components: T) {
            for (component in components) {
                component.visibility = View.GONE
                val fadeIn = AnimatorInflater.loadAnimator(context, R.animator.fade_out)
                fadeIn.setTarget(component)
                fadeIn.start()
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
            val (formattedDate, formattedTime) = TimeFormatFunctions.convertTimestampToDateTime(userProfile.birth_day!!.toLong())
            println(formattedDate)
            println(formattedTime)
            val newBirthDate = birthDateField.text.toString()
            val newBirthTime = birthTimeField.text.toString()

            if (newBirthDate != formattedDate || newBirthTime != formattedTime) {
                val timestamp = convertDateTimeToTimestamp(newBirthDate, newBirthTime)
                editProfileJson.put(jsonField, timestamp)
                userProfile.birth_day = timestamp.toString()
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

    object SpinnerFunctions{
        fun setSpinner(context: Context,spinner: Spinner, hint: TextView, dataResId: Int, defaultText: String, onItemSelectedAction: (String) -> Unit) {
            val adapter = ArrayAdapter.createFromResource(context, dataResId, R.layout.custom_spinner_item)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter

            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    val selectedValue = parent?.getItemAtPosition(position) as? String
                    setViewVisibleWithAnimation(context,hint)
                    if (selectedValue != null && selectedValue != defaultText) {
                        onItemSelectedAction(selectedValue)
                    } else println("duzgun bir sey secmedi")
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    println("Nothing Selected")
                }
            }
        }

        fun setupFieldClickListener(field: TextView, spinner: Spinner, hintView: View) {
            field.setOnClickListener {
                spinner.performClick()
                setViewInvisible(hintView)
            }
        }
    }

    object TimeFormatFunctions{
        fun convertTimestampToDateTime(timestamp: Long): Pair<String, String> {
            val date = Date(timestamp * 1000)
            val dateSdf = SimpleDateFormat("yyyy-MM-dd")
            val timeSdf = SimpleDateFormat("HH:mm:ss")
            val formattedDate = dateSdf.format(date)
            val formattedTime = timeSdf.format(date)
            return Pair(formattedDate, formattedTime)
        }
        fun convertDateTimeToTimestamp(dateStr: String, timeStr: String): Long {
            val dateTimeStr = "$dateStr $timeStr"
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val date = sdf.parse(dateTimeStr)
            return date!!.time / 1000
        }
    }

    object AnimateCardSize{
        fun animateCardSize(context: Context, targetWidthDp: Int, targetHeightDp: Int, targetCard: View, scroll: ScrollView? = null, targetScrollBottomMargin: Int? = null, animationDuration: Long = 300) {
            val scale = context.resources.displayMetrics.density
            val newWidth = (targetWidthDp * scale + 0.5f).toInt()
            val newHeight = (targetHeightDp * scale + 0.5f).toInt()
            if (targetScrollBottomMargin != null){
                val newScrollBottomMargin = (targetScrollBottomMargin * scale + 0.5f).toInt()
                val scrollParams = scroll?.layoutParams as RelativeLayout.LayoutParams
                scrollParams.bottomMargin = newScrollBottomMargin
            }
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
                animatorSet.duration = animationDuration
            animatorSet.start()
        }
    }




}

