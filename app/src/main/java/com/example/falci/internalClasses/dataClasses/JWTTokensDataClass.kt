package com.example.falci.internalClasses.dataClasses

lateinit var tokensDataClass: JWTTokensDataClass

data class JWTTokensDataClass(
    var accessToken: String,
    var refreshToken: String,
    var tokensCreatedAt: Long,
)

data class TokensTimeRemaining(
    var refreshTokenRemainingTime: Long,
    var accessTokenRemainingTime: Long,
    )