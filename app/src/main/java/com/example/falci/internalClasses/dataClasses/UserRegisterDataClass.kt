package com.example.falci.internalClasses.dataClasses

data class UserRegisterDataClass(
    var email: String,
    var password: String,
)

val userRegister = UserRegisterDataClass(
    email = "",
    password = "",
)
