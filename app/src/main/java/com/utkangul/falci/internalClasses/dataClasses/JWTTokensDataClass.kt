package com.utkangul.falci.internalClasses.dataClasses


data class JWTTokensDataClass(
    var accessToken: String,
    var refreshToken: String,
    var tokensCreatedAt: Long,
)

var tokensDataClass= JWTTokensDataClass(
    accessToken = "",
    refreshToken = "",
    tokensCreatedAt = 0,
    )
