package com.example.falci.internalClasses.dataClasses

data class IsAuthenticated(
    var isLoggedIn: Boolean,
    var isFromSignIn: Boolean,
    var rememberMe: Boolean,
)

val authenticated = IsAuthenticated(
    isLoggedIn = false,
    isFromSignIn = false,
    rememberMe = false
)