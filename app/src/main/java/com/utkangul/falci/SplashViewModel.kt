package com.utkangul.falci

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.utkangul.falci.internalClasses.dataClasses.JWTTokensDataClass
import com.utkangul.falci.internalClasses.dataClasses.authenticated
import com.utkangul.falci.internalClasses.dataClasses.tokensDataClass
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.*

class SplashViewModel(application: Application) : AndroidViewModel(application) {
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()
    private val tokensSharedPreferences = application.getSharedPreferences("token_prefs", Context.MODE_PRIVATE)
    init {

        val savedAccessToken = tokensSharedPreferences.getString("access_token",null) // access token is saved if user checks the remember me checkbox; if not, default value is null
        val savedRefreshToken = tokensSharedPreferences.getString("refresh_token",null) // refresh token is saved in last login, does not matter if remember me is checked or not, saved until logout
        val savedTokenCreationTime = tokensSharedPreferences.getLong("token_creation_time",0) // it is set in last login, does not matter if remember me is checked or not, saved until logout
        var didLogin = tokensSharedPreferences.getBoolean("didLogin",false) // this boolean is true when user checks remember me checkbox
        val editor = tokensSharedPreferences.edit()
        val currentTime = System.currentTimeMillis() / 1000
        if(currentTime - savedTokenCreationTime > 600000){ didLogin = false } // refresh token lasts 30 minutes
        //didLogin = false
        // if didLogin is true and the token in sharedPreferences is not null or empty
        if (didLogin && !savedAccessToken.isNullOrEmpty()){
            println("didlogin true kayıtlı token var")
            tokensDataClass= JWTTokensDataClass(
                accessToken = savedAccessToken,
                refreshToken = savedRefreshToken!!,
                tokensCreatedAt = savedTokenCreationTime)

            // initialize data class instance with sharedPreferences
            tokensDataClass.accessToken = savedAccessToken
            tokensDataClass.refreshToken = savedRefreshToken
            tokensDataClass.tokensCreatedAt = savedTokenCreationTime
            authenticated.isLoggedIn = true
        }
        else if (didLogin && savedAccessToken.isNullOrEmpty()){
            println("didlogin true ama kayıtlı token yok, didlogin false yapıyom")
            editor.putBoolean("didLogin", false)
            editor.apply()
            didLogin = false
        }
        if(!didLogin){
            println("didLogin is false")
            if (!savedAccessToken.isNullOrEmpty()){
                println("$savedAccessToken")
                println("didlogin false ama token var, didlogin true yapıyorum main activity de expire kontrolüne bak")
                editor.putBoolean("didLogin", true)
                editor.apply()
                didLogin = true
            }
            else if(savedAccessToken.isNullOrEmpty()){
                println("didlogin false token de yok")
            }
        }

        // wait at least 2 seconds before launch
        viewModelScope.launch{delay(2000); _isLoading.value = false}
    }
}