package com.example.falci

import android.app.Application
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.PurchasesConfiguration

    class MainApplication : Application() {
        override fun onCreate() {
            super.onCreate()

            // RevenueCat SDK konfigürasyonu
            val purchasesConfig = PurchasesConfiguration.Builder(this,"goog_KKfsiviLhKXqLyyuGGldrMPgnAG")
                .build()

            Purchases.configure(purchasesConfig)
        }
    }