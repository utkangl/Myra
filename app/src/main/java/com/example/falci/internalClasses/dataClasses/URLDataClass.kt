package com.example.falci.internalClasses.dataClasses

data class URLDataClass(
    var loginURL: String,
    var logoutURL: String,
    var signUpURL: String,
    var completeProfileURL: String,
    var refreshURL: String,
    var getProfileURL: String,
    var editProfileURL: String,
    var chatGptURL: String,
    var getHoroscopeURL: String,
    var favouriteHoroscopeURL: String,
    var partnerProfileURL: String,
)


val urls = URLDataClass(
    loginURL= "https://api.atlasuavteam.com/auth/token/",
    logoutURL= "https://api.atlasuavteam.com/auth/logout/",
    signUpURL= "https://api.atlasuavteam.com/auth/register/",
    completeProfileURL= "https://api.atlasuavteam.com/auth/profile/complete/",
    refreshURL= "https://api.atlasuavteam.com/auth/token/refresh/",
    getProfileURL= "https://api.atlasuavteam.com/auth/profile/",
    editProfileURL= "https://api.atlasuavteam.com/auth/profile/edit/",
    chatGptURL= "https://api.atlasuavteam.com/gpt/chat/",
    getHoroscopeURL= "https://api.atlasuavteam.com/api/fortune/",
    favouriteHoroscopeURL = "https://api.atlasuavteam.com/api/favourite/",
    partnerProfileURL = "https://api.atlasuavteam.com/api/lookup_user/",
)