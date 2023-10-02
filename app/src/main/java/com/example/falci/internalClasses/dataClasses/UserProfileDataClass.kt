package com.example.falci.internalClasses.dataClasses

data class UserProfileDataClass(
    var username: String?,
    var first_name: String?,
    var birth_place: String?,
    var birth_day: String?,
    var relationship_status: String?,
    var gender: String?,
    var occupation: String?,
    var signs: List<Sign>,
    var is_completed: Boolean
)

var userProfile = UserProfileDataClass(
    username = null,
    first_name = null,
    birth_place = null,
    birth_day = null,
    relationship_status = null,
    gender = null,
    occupation = null,
    signs = emptyList(),
    is_completed = false
)

data class Sign(
    var id: Int,
    var planet: String,
    var sign: String,
    var position: Double,
    var abs_position: Double,
    var house: String,
    var element: String,
    var retrograde: Boolean,
    var user: Int
)
