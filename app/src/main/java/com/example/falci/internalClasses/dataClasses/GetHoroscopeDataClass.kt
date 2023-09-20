package com.example.falci.internalClasses.dataClasses

data class PostHoroscopeData(
    var type: String?,
    var time_interval: String?,
)

val postHoroscopeData = PostHoroscopeData(type = "", time_interval = "")

//data class GetHoroscopeData(
//    val id: Int?,
//    val user: Int?,
//    val updated_at: String?,
//    val created_at: String?,
//    val chat_messages: List<HoroscopeChatMessageData>?
//) {
//    data class HoroscopeChatMessageData(
//        var id: Int?,
//        val owner: String,
//        val message: String,
//        val updated_at: String,
//        val created_at: String,
//        val thread: Int?,
//    )
//}

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