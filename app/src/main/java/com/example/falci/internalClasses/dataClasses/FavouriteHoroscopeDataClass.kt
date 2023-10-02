package com.example.falci.internalClasses.dataClasses

data class FavouriteHoroscopeDataClass(
    var id: Int?,
    var title: String?,
    var user: Int?,
    var fortune: Int?)

var favouriteHoroscope = FavouriteHoroscopeDataClass(
    id = null,
    title = null,
    user = null,
    fortune = null)