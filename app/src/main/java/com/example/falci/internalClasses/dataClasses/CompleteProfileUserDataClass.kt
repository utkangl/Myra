package com.example.falci.internalClasses.dataClasses

data class CompleteProfileUserDataClass(
    var name: String,
    var gender: String,
    var date: String,
    var time: String,
    var location: String,
    var relation: String,
    var occupation: String,
)

val userCompleteProfile = CompleteProfileUserDataClass(
    name = "",
    gender = "",
    date = "",
    time = "",
    location = "",
    relation = "",
    occupation = "",
)