package com.example.falci.internalClasses.dataClasses

import org.json.JSONObject

var isFromLoveHoroscope= false
var navigateToHoroscope = false
lateinit var lookupUserJson: JSONObject

data class PartnerProfileDataClass(
var partnerName: String,
var partnerGender: String,
var partnerDate: String,
var partnerTime: String,
var partnerLocation: String,
var partnerOccupation: String,
)
val partnerProfile = PartnerProfileDataClass(
    partnerName = "",
    partnerGender = "",
    partnerDate =  "",
    partnerTime =  "",
    partnerLocation = "",
    partnerOccupation = "",
)