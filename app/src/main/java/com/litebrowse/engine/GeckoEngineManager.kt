package com.litebrowse.engine

import org.mozilla.geckoview.GeckoRuntime
import org.mozilla.geckoview.GeckoSession
import org.mozilla.geckoview.GeckoSessionSettings

class GeckoEngineManager(private val runtime: GeckoRuntime) {

    fun createSession(isPrivate: Boolean = false): GeckoSession {
        val settings = GeckoSessionSettings.Builder()
            .usePrivateMode(isPrivate)
            .useTrackingProtection(true)
            .userAgentMode(GeckoSessionSettings.USER_AGENT_MODE_MOBILE)
            .allowJavascript(true)
            .build()

        val session = GeckoSession(settings)
        session.open(runtime)
        return session
    }

    fun closeSession(session: GeckoSession) {
        if (session.isOpen) {
            session.close()
        }
    }
}
