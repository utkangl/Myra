package com.example.falci.internalClasses.dataClasses

data class CreateFavouriteHoroscopeDataClass(
    var title: String,
    var horoscopeId: Int?,
    var isThisHoroscopeFavourite: Boolean)

val createFavouriteHoroscope = CreateFavouriteHoroscopeDataClass(
    title = "null",
    horoscopeId = null,
    isThisHoroscopeFavourite = false
)