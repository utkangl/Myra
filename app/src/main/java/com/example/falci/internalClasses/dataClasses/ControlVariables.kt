package com.example.falci.internalClasses.dataClasses

data class ControlVariables(
    var isBurcCardOpen: Boolean,
    var isGeneralModeSelected : Boolean,
    var isLoveModeSelected : Boolean,
    var isCareerModeSelected : Boolean,
    var isSavedUsersLoaded : Boolean,
    var isTimeIntervalSelected : Boolean,
    var getProfileAgain : Boolean,
    var swipeBack : Boolean,
    var navigateBackToProfileActivity : Boolean,
    var navigateToFavs : Boolean,
    var navigateToSignUp: Boolean,
    var isFromLoveHoroscope: Boolean,
    var navigateToHoroscope: Boolean,
    var inLocationPickCard: Boolean,
)

var controlVariables = ControlVariables(
    isBurcCardOpen = false,
    isGeneralModeSelected = false,
    isLoveModeSelected  = false,
    isCareerModeSelected = false,
    isSavedUsersLoaded  = false,
    isTimeIntervalSelected = false,
    getProfileAgain = false,
    swipeBack = false,
    navigateBackToProfileActivity = false,
    navigateToFavs = false,
    navigateToSignUp = false,
    isFromLoveHoroscope = false,
    navigateToHoroscope = false,
    inLocationPickCard = false,
)