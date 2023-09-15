package com.example.falci.internalClasses

data class IsAuthenticated(
    var isLoggedIn: Boolean,
    var isFromSignIn: Boolean,
)

val authenticated = IsAuthenticated(
    isLoggedIn = false,
    isFromSignIn = false,
)