package com.example.falci.internalClasses

data class UserRegisterDataClass(
    var email: String,
    var password: String,
)

val userRegister = UserRegisterDataClass(
    email = "",
    password = "",
)
