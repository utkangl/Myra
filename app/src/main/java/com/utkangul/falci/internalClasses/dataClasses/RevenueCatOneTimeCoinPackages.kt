package com.utkangul.falci.internalClasses.dataClasses

data class RevenueCatOneTimeCoinPackages(
    var fiveCoinPackage: com.revenuecat.purchases.Package?,
    var twentyFiveCoinPackage: com.revenuecat.purchases.Package?,
    var fiftyCoinPackage: com.revenuecat.purchases.Package?
)

var revenueCatOneTimeCoinPackages = RevenueCatOneTimeCoinPackages(
    fiveCoinPackage = null,
    twentyFiveCoinPackage = null,
    fiftyCoinPackage = null
)

