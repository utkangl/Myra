package com.utkangul.falci.internalClasses.dataClasses

data class PostHoroscopeData(
    var type: String?,
    var time_interval: String?,
)

val postHoroscopeData = PostHoroscopeData(type = "", time_interval = "")

data class GetHoroscopeData(
    var id: Int?,
    var thread: Int?,
    var summary: String?,
    var good: String?,
    var bad: String?,
    var time_remaining: String?,
    var score: String?,
    var is_favourite: Boolean,
    var favourite_id: Int?
)

var getHoroscopeData = GetHoroscopeData(
    id = null,
    thread = null,
    summary = null,
    good = null,
    bad =  null,
    time_remaining = null,
    score = null,
    is_favourite = false,
    favourite_id = null
)