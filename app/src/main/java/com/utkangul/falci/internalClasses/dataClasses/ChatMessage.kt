package com.utkangul.falci.internalClasses.dataClasses

var isFromHoroscope = false
data class ChatMessage(
    val text: String,
    val isMyMessage: Boolean
)

data class ChatDetails(
    var coin: Int?,
    var remainingMessages: Int?,
    var addMessage: Boolean
)

var chatDetails = ChatDetails(
    coin = null,
    remainingMessages = null,
    addMessage = false
)