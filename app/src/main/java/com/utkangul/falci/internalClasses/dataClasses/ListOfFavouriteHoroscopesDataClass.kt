package com.utkangul.falci.internalClasses.dataClasses


data class ListOfFavouriteHoroscopesDataClass(
    val count: Int,
    var next: String?,
    val previous: String?,
    val results: MutableList<FortuneItem>
)

data class FortuneItem(
    val id: Int,
    val user: String,
    val fortune: Fortune?,
    val title: String
)

data class Fortune(
    val id: Int,
    val type: String,
    val time_interval: String,
    val prompt: Prompt?,
    val updated_at: String,
    val created_at: String,
    val user: Int,
    val lookup_user: Int
)

data class Prompt(
    val thread: Int,
    val summary: String,
    val good: String,
    val bad: String,
)

var listOfFavouriteHoroscopes = ListOfFavouriteHoroscopesDataClass(
    count = 0,
    next = null,
    previous = null,
    results = mutableListOf()
)