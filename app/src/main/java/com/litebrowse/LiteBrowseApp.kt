package com.litebrowse

import android.app.Application
import com.google.android.material.color.DynamicColors
import com.litebrowse.data.AppDatabase
import org.mozilla.geckoview.ContentBlocking
import org.mozilla.geckoview.GeckoRuntime
import org.mozilla.geckoview.GeckoRuntimeSettings

class LiteBrowseApp : Application() {

    lateinit var geckoRuntime: GeckoRuntime
        private set

    lateinit var database: AppDatabase
        private set

    override fun onCreate() {
        super.onCreate()
        DynamicColors.applyToActivitiesIfAvailable(this)

        val settings = GeckoRuntimeSettings.Builder()
            .javaScriptEnabled(true)
            .consoleOutput(BuildConfig.DEBUG)
            .contentBlocking(
                ContentBlocking.Settings.Builder()
                    .antiTracking(ContentBlocking.AntiTracking.DEFAULT)
                    .enhancedTrackingProtectionLevel(ContentBlocking.EtpLevel.DEFAULT)
                    .cookieBehavior(ContentBlocking.CookieBehavior.ACCEPT_NON_TRACKERS)
                    .safeBrowsing(ContentBlocking.SafeBrowsing.DEFAULT)
                    .build()
            )
            .build()

        geckoRuntime = GeckoRuntime.create(this, settings)
        database = AppDatabase.getInstance(this)
    }
}
