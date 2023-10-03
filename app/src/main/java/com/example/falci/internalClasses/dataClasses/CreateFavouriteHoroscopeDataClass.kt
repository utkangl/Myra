package com.example.falci.internalClasses.dataClasses

data class CreateFavouriteHoroscopeDataClass(
    var title: String,
    var horoscopeId: Int?)

val createFavouriteHoroscope = CreateFavouriteHoroscopeDataClass(
    title = "null",
    horoscopeId = null,)