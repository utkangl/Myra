package com.utkangul.falci.internalClasses.dataClasses

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
    var lookupUserURL: String,
    var emailVerificationURL: String,
    var deleteAccountURL: String,
)

val urls = URLDataClass(
    loginURL= "https://api.kircibros.com/auth/token/",
    logoutURL= "https://api.kircibros.com/auth/logout/",
    signUpURL= "https://api.kircibros.com/auth/register/",
    completeProfileURL= "https://api.kircibros.com/auth/profile/complete/",
    refreshURL= "https://api.kircibros.com/auth/token/refresh/",
    getProfileURL= "https://api.kircibros.com/auth/profile/",
    editProfileURL= "https://api.kircibros.com/auth/profile/edit/",
    chatGptURL= "https://api.kircibros.com/gpt/chat/",
    getHoroscopeURL= "https://api.kircibros.com/api/fortune/",
    favouriteHoroscopeURL = "https://api.kircibros.com/api/favourite/",
    lookupUserURL = "https://api.kircibros.com/api/lookup_user/",
    emailVerificationURL = "https://api.kircibros.com/auth/verify/",
    deleteAccountURL = "https://api.kircibros.com/auth/profile/delete/",
)