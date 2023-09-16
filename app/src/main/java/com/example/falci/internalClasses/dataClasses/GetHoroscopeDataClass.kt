package com.example.falci.internalClasses.dataClasses

data class PostHoroscopeData(
    var type: String?,
    var time_interval: String?,
)

val postHoroscopeData = PostHoroscopeData(type = "", time_interval = "")

data class GetHoroscopeData(
    val id: Int?,
    val user: Int?,
    val updated_at: String?,
    val created_at: String?,
    val chat_messages: List<HoroscopeChatMessageData>?
) {
    data class HoroscopeChatMessageData(
        var id: Int?,
        val owner: String,
        val message: String,
        val updated_at: String,
        val created_at: String,
        val thread: Int?,
    )
}

var horoscopeChatMessageData = GetHoroscopeData.HoroscopeChatMessageData(
    id = null, owner = "", message = "initial message",
    updated_at = "", created_at = "", thread = null)

var getHoroscopeData = GetHoroscopeData(
    id = null,
    user = null,
    updated_at = "",
    created_at = "",
    chat_messages =  listOf(horoscopeChatMessageData),
)