package com.example.falci

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.falci.internalClasses.AuthenticationFunctions
import com.example.falci.internalClasses.dataClasses.JWTTokensDataClass
import com.example.falci.internalClasses.dataClasses.authenticated
import com.example.falci.internalClasses.dataClasses.createFavouriteHoroscope
import com.example.falci.internalClasses.dataClasses.tokensDataClass
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SplashViewModel(application: Application) : AndroidViewModel(application) {
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()
    private val tokensSharedPreferences = application.getSharedPreferences("token_prefs", Context.MODE_PRIVATE)
    init {

        val savedAccessToken = tokensSharedPreferences.getString("access_token",null) // access token is saved if user checks the remember me checkbox; if not, default value is null
        val savedRefreshToken = tokensSharedPreferences.getString("refresh_token",null) // refresh token is saved in last login, does not matter if remember me is checked or not, saved until logout
        val savedTokenCreationTime = tokensSharedPreferences.getLong("token_creation_time",0) // it is set in last login, does not matter if remember me is checked or not, saved until logout
        var didLogin = tokensSharedPreferences.getBoolean("didLogin",false) // this boolean is true when user checks remember me checkbox
        val authFuncs = AuthenticationFunctions()
        val currentTime = System.currentTimeMillis() / 1000

        // this function checks if the access is expired; if so, takes new tokens and replaces with the old ones in dataclass and sharedPReferences
        authFuncs.checkIsAccessExpired(currentTime, savedTokenCreationTime, 86400, savedRefreshToken!!, application)

        // if it has been more than a week since the tokens were created, have user login again
        if(currentTime - savedTokenCreationTime > 604800){didLogin = false}

        // if didLogin is true and the token in sharedPreferences is not null or empty
        if (didLogin && !savedAccessToken.isNullOrEmpty()){
            println("user tokens are saved in device, didLogin is true, user automatically will be logged in until logout ")


            tokensDataClass= JWTTokensDataClass(
                accessToken = savedAccessToken,
                refreshToken = savedRefreshToken,
                tokensCreatedAt = savedTokenCreationTime)

            // initialize data class instance with sharedPreferences
            tokensDataClass.accessToken = savedAccessToken
            tokensDataClass.refreshToken = savedRefreshToken
            tokensDataClass.tokensCreatedAt = savedTokenCreationTime
            authenticated.isLoggedIn = true
        }

        if(!didLogin){println("access token is not saved in sharedPreferences and didLogin is false")}

        // wait at least 2 seconds before launch
        viewModelScope.launch{delay(2000); _isLoading.value = false}
    }
}