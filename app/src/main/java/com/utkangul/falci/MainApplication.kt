package com.utkangul.falci

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
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

            Purchases.configure(PurchasesConfiguration.Builder(this,"goog_xmUfVWYwmhSoVdIdQudYzcYrKjg").build())

        }
    }