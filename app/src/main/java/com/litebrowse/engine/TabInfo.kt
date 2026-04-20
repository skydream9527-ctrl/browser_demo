package com.litebrowse.engine

import org.mozilla.geckoview.GeckoSession

data class TabInfo(
    val id: String = java.util.UUID.randomUUID().toString(),
    val session: GeckoSession,
    var title: String = "",
    var url: String = "",
    var isPrivate: Boolean = false,
    var isSecure: Boolean = false
)
