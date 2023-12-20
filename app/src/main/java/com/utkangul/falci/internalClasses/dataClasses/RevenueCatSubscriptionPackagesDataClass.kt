package com.utkangul.falci.internalClasses.dataClasses

data class RevenueCatSubscriptionPackagesDataClass(
    var weeklySubsPackage: com.revenuecat.purchases.Package?,
    var monthlySubsPackage: com.revenuecat.purchases.Package?,
    var yearlySubsPackage: com.revenuecat.purchases.Package?
)

var revenueCatSubscriptionPackages = RevenueCatSubscriptionPackagesDataClass(
    weeklySubsPackage = null,
    monthlySubsPackage = null,
    yearlySubsPackage = null
)


