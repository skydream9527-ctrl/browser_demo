package com.litebrowse

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.litebrowse.engine.GeckoEngineManager
import com.litebrowse.engine.SearchEngineManager
import com.litebrowse.engine.TabManager

class MainActivity : AppCompatActivity() {

    lateinit var tabManager: TabManager
        private set
    lateinit var searchEngineManager: SearchEngineManager
        private set
    lateinit var geckoEngineManager: GeckoEngineManager
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val app = application as LiteBrowseApp
        geckoEngineManager = GeckoEngineManager(app.geckoRuntime)
        tabManager = TabManager(geckoEngineManager)
        searchEngineManager = SearchEngineManager(this)
    }

    @Deprecated("Use OnBackPressedCallback")
    override fun onBackPressed() {
        val navHost = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHost.navController
        if (!navController.popBackStack()) {
            super.onBackPressed()
        }
    }
}
