package com.utkangul.falci.internalClasses.dataClasses

data class UserStatusDataClass(
    val coin: Int,
    val premium: List<Premium>,
    val campain: List<Campaign> // Dikkat: JSON'da "campaign" olarak geçiyor, burada "campain" olarak kullanıldı
)

var userStatusDataClass = UserStatusDataClass(
    coin = 0,
    premium= emptyList<Premium>(),
    campain = emptyList<Campaign>()
)

data class Premium(
    val id: Int,
    val is_expired: Boolean,
    val premium_type: String,
    val expire_date: String,
    val updated_at: String,
    val created_at: String,
    val user: Int
)

data class Campaign(
    val id: Int,
    val is_expired: Boolean,
    val is_repeatable: Boolean,
    val is_available: Boolean,
    val remaining_time: Int,
    val coin_amount: Int,
    val coin_interval: Long,
    val last_claim_date: String,
    val expire_date: String,
    val remain_repeat: Int,
    val user: Int
)