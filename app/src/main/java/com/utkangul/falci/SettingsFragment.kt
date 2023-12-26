package com.utkangul.falci

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.utkangul.falci.internalClasses.AuthenticationFunctions.PostJsonFunctions.takeFreshTokens
import com.utkangul.falci.internalClasses.InternalFunctions
import com.utkangul.falci.internalClasses.TransitionToFragment.ReplaceFragmentWithAnimation.replaceProfileFragmentWithAnimation
import com.utkangul.falci.internalClasses.dataClasses.*
import okhttp3.*
import java.io.IOException
import java.util.*


class SettingsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v =  inflater.inflate(R.layout.fragment_settings, container, false)

        val buyCoinContainerLayout = v.findViewById<RelativeLayout>(R.id.buyCoinContainerLayout)
        val deleteAccountContainerLayout = v.findViewById<RelativeLayout>(R.id.deleteAccountContainerLayout)
        val changeLanguageContainerLayout = v.findViewById<RelativeLayout>(R.id.changeLanguageContainerLayout)
        val changePasswordContainerLayout = v.findViewById<RelativeLayout>(R.id.changePasswordContainerLayout)

        buyCoinContainerLayout.setOnClickListener{ replaceProfileFragmentWithAnimation(parentFragmentManager, PurchaseFragment()) }

        fun setLocale(languageCode: String) {
            // Dil ayarlarını güncelle
            val locale = Locale(languageCode)
            Locale.setDefault(locale)
            val config = Configuration()
            config.locale = locale
            resources.updateConfiguration(config, resources.displayMetrics)


            val intent = Intent(context, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            requireActivity().finish()
        }

        changeLanguageContainerLayout.setOnClickListener{
            val builder = AlertDialog.Builder(requireContext())
            builder.setMessage(R.string.askIfChangeLanguage)
            builder.setPositiveButton(R.string.yes) { _, _ ->
                val sharedPreferences: SharedPreferences = requireContext().getSharedPreferences("language_choice", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()

                val config: Configuration = resources.configuration
                if (config.locale.language == "tr") {
                    setLocale("en")
                    editor.putString("language", "en")
                    editor.apply()
                } else {
                    setLocale("tr")
                    editor.putString("language", "tr")
                    editor.apply()
                }
            }
            builder.setNegativeButton(R.string.no, null)
            builder.create().show()
        }

        deleteAccountContainerLayout.setOnClickListener {
            fun deleteAccount() {
                val builder = AlertDialog.Builder(requireContext())
                builder.setMessage("This action will delete your account permanently and you will not be able to restore it. Are you sure?")

                builder.setPositiveButton("Yes") { _, _ ->
                    val apiUrl = urls.deleteAccountURL
                    val client = OkHttpClient()
                    val request = Request.Builder()
                        .url(apiUrl)
                        .delete()
                        .header("Authorization", "Bearer ${tokensDataClass.accessToken}")
                        .build()

                    client.newCall(request).enqueue(object : Callback {
                        override fun onFailure(call: Call, e: IOException) {
                            println("exception $e")
                        }

                        override fun onResponse(call: Call, response: Response) {
                            println(response)
                            if (response.code == 204) {
                                activity?.runOnUiThread {
                                    Toast.makeText(requireContext(), "Deletion successfull", Toast.LENGTH_LONG).show()
                                    val sharedPreferences: SharedPreferences = requireContext().getSharedPreferences("token_prefs", Context.MODE_PRIVATE)
                                    val editor = sharedPreferences.edit()
                                    editor.putString("access_token", null)
                                    editor.putString("refresh_token", null)
                                    editor.putLong("token_creation_time", 0)
                                    editor.putBoolean("didLogin", false)
                                    editor.apply()
                                    val options = ActivityOptions.makeCustomAnimation(requireContext(), R.anim.activity_slide_down, 0)
                                    val intent = Intent(requireContext(), MainActivity::class.java);startActivity(intent, options.toBundle())
                                    authenticated.isLoggedIn = false
                                }
                            }
                            if (response.code == 401) {
                                takeFreshTokens(urls.refreshURL, requireContext()) { responseBody, exception ->
                                    if (responseBody != null) {
                                        println(tokensDataClass.accessToken)
                                        deleteAccount()
                                    } else {
                                        println(exception)
                                    }
                                }
                            } else {
                                requireActivity().runOnUiThread {
                                    Toast.makeText(context, "unexpected error: ${response.code}", Toast.LENGTH_SHORT).show()
                                    Toast.makeText(context, "Redirecting to main activity: ${response.code}", Toast.LENGTH_SHORT).show()
                                    val options = ActivityOptions.makeCustomAnimation(requireContext(), R.anim.activity_slide_down, 0)
                                    val intent = Intent(requireContext(), MainActivity::class.java);startActivity(intent, options.toBundle())
                                }
                            }
                        }
                    })
                }
                builder.setNegativeButton("No", null)
                builder.create().show()
            }
            deleteAccount()
        }


        return v
    }

}