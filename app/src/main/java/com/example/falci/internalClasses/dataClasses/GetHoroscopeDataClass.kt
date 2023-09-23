package com.example.falci.internalClasses.dataClasses

data class PostHoroscopeData(
    var type: String?,
    var time_interval: String?,
)

val postHoroscopeData = PostHoroscopeData(type = "", time_interval = "")

data class GetHoroscopeData(
    var thread: Int?,
    var summary: String?,
    var good: String?,
    var bad: String?,
)

var getHoroscopeData = GetHoroscopeData(
    thread = null,
    summary = "",
    good = "",
    bad =  "",)