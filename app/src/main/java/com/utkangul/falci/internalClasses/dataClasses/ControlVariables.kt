package com.utkangul.falci.internalClasses.dataClasses

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
    var isInDelay: Boolean,
    var isSearchTextChanged: Boolean,
    var selectedTimeInterval: String?,
    var inPicker: Boolean,
    var allowCheck: Boolean,
    var allowCheckChangePasswordCode: Boolean,
    var favsSwipedDown: Boolean,
    var isFromCompleteLookup: Boolean,
    var isFromAddLookupUser: Boolean,
    var resendMailCountdownFinished: Boolean,
    var resendMailChangePasswordCountdownFinished: Boolean,
    var isgender: Boolean,
    var inNotifyPurchase: Boolean,
    var isInExpireControl: Boolean,
    var isSameFortune: Boolean,
    var isLoveSameFortune: Boolean,
    var isFromChangePassword: Boolean,
    var isZodiacCardOpen: Boolean,
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
    isInDelay = false,
    isSearchTextChanged = false,
    selectedTimeInterval = null,
    inPicker = false,
    allowCheck = false,
    allowCheckChangePasswordCode = false,
    favsSwipedDown = false,
    isFromCompleteLookup = false,
    isFromAddLookupUser = false,
    resendMailCountdownFinished = true,
    resendMailChangePasswordCountdownFinished = true,
    isgender = false,
    inNotifyPurchase = false,
    isInExpireControl = false,
    isSameFortune = false,
    isLoveSameFortune = false,
    isFromChangePassword = false,
    isZodiacCardOpen = false,
)