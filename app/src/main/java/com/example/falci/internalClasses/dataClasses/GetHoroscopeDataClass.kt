package com.example.falci.internalClasses.dataClasses

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
    var isFav: Boolean
)

var getHoroscopeData = GetHoroscopeData(
    id = null,
    thread = null,
    summary = null,
    good = null,
    bad =  null,
    time_remaining = null,
    score = null,
    isFav = false,
)