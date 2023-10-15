package com.example.falci.internalClasses.dataClasses

import org.json.JSONObject

var isFromLoveHoroscope= false
var navigateToHoroscope = false
lateinit var postLookupUserJson: JSONObject

data class postPartnerProfileDataClass(
var partnerName: String,
var partnerGender: String,
var partnerDate: String,
var partnerTime: String,
var partnerLocation: String,
var partnerOccupation: String,
var partnerRelationStatus: String
)
val postPartnerProfile = postPartnerProfileDataClass(
    partnerName = "",
    partnerGender = "",
    partnerDate =  "",
    partnerTime =  "",
    partnerLocation = "",
    partnerOccupation = "",
    partnerRelationStatus = ""
)

data class GetPartnerProfileDataClass(
    var id: Int,
    var name: String,
    var gender: String,
    var birth_day: String,
    var birth_place: String,
    var occupation: String,
    var relationship_status: String
)
var getPartnerProfile = GetPartnerProfileDataClass(
    id = 0,
    name = "",
    gender = "",
    birth_day =  "",
    birth_place = "",
    occupation = "",
    relationship_status = ""
)