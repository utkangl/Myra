package com.utkangul.falci.internalClasses.dataClasses

import java.util.*

data class UserStatusDataClass(
    val coin: Int,
    val premium: List<Premium>,
    val campain: List<Campaign> // Dikkat: JSON'da "campaign" olarak geçiyor, burada "campain" olarak kullanıldı
)

data class Premium(
    val id: Int,
    val is_expired: Boolean,
    val premium_type: String,
    val expire_date: Date,
    val updated_at: Date,
    val created_at: Date,
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
    val last_claim_date: Date,
    val expire_date: Date,
    val remain_repeat: Int,
    val user: Int
)