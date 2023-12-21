package com.utkangul.falci.internalClasses.dataClasses

data class CompleteProfileUserDataClass(
    var name: String,
    var gender: String,
    var location: String,
    var relation: String,
    var occupation: String,
)

val userCompleteProfile = CompleteProfileUserDataClass(
    name = "",
    gender = "",
    location = "",
    relation = "",
    occupation = "",
)