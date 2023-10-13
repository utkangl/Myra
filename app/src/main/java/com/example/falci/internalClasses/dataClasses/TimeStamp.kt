package com.example.falci.internalClasses.dataClasses

data class TimeStamp(
    var year: Int,
    var month: Int,
    var day: Int,
    var hour: Int,
    var minute: Int,
)

val partnerProfileTimeStamp = TimeStamp(
    year = 0,
    month = 0,
    day = 0,
    hour = 0,
    minute = 0,
)
val completeProfileTimeStamp = TimeStamp(
    year = 0,
    month = 0,
    day = 0,
    hour = 0,
    minute = 0,
)