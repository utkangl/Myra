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
)


val urls = URLDataClass(
    loginURL= "http://31.210.43.174:1337/auth/token/",
    logoutURL= "http://31.210.43.174:1337/auth/logout/",
    signUpURL= "http://31.210.43.174:1337/auth/register/",
    completeProfileURL= "http://31.210.43.174:1337/auth/profile/complete/",
    refreshURL= "http://31.210.43.174:1337/auth/token/refresh/",
    getProfileURL= "http://31.210.43.174:1337/auth/profile/",
    editProfileURL= "http://31.210.43.174:1337/auth/profile/edit/",
    chatGptURL= "http://31.210.43.174:1337/gpt/chat/",
)