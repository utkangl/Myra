package com.example.falci.internalClasses

data class CompleteProfileUserDataClass(
    var name: String,
    var gender: String,
    var date: String,
    var time: String,
    var location: String,
    var relation: String,
    var occupation: String,
)

val userRegister = CompleteProfileUserDataClass(
    name = "",
    gender = "",
    date = "",
    time = "",
    location = "",
    relation = "",
    occupation = "",
)