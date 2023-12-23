package com.utkangul.falci

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.utkangul.falci.internalClasses.AuthenticationFunctions.PostJsonFunctions.takeFreshTokens
import com.utkangul.falci.internalClasses.TransitionToFragment.ReplaceFragmentWithAnimation.replaceProfileFragmentWithAnimation
import com.utkangul.falci.internalClasses.UserStatusFunctions
import com.utkangul.falci.internalClasses.dataClasses.authenticated
import com.utkangul.falci.internalClasses.dataClasses.tokensDataClass
import com.utkangul.falci.internalClasses.dataClasses.urls
import okhttp3.*
import java.io.IOException


class SettingsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v =  inflater.inflate(R.layout.fragment_settings, container, false)

        val buyCoinContainerLayout = v.findViewById<RelativeLayout>(R.id.buyCoinContainerLayout)
        val deleteAccountContainerLayout = v.findViewById<RelativeLayout>(R.id.deleteAccountContainerLayout)

        buyCoinContainerLayout.setOnClickListener{ replaceProfileFragmentWithAnimation(parentFragmentManager, PurchaseFragment()) }

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
                                    Toast.makeText(requireContext(), "unexpected error: new token status code is 401", Toast.LENGTH_LONG).show()
                                    Toast.makeText(requireContext(), "please login again, an error occured", Toast.LENGTH_LONG).show()
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
                                }
                            }
                        }
                    })
                }
                builder.setNegativeButton("No", null)
                builder.create().show()
            }
            deleteAccount()
        };
        return v
    }
}