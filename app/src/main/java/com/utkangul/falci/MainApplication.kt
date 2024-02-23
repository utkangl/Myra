package com.utkangul.falci

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import com.revenuecat.purchases.LogLevel
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.PurchasesConfiguration
import java.util.*

class MainApplication : Application() {
        override fun onCreate() {
            super.onCreate()

            // RevenueCat SDK konfigürasyonu

            Purchases.logLevel = LogLevel.DEBUG
            val ai: ApplicationInfo = this.packageManager.getApplicationInfo(this.packageName, PackageManager.GET_META_DATA)
            val value = ai.metaData["REVENUECAT_API_KEY"].toString()
            Purchases.configure(PurchasesConfiguration.Builder(this,value).build())

        }
    }