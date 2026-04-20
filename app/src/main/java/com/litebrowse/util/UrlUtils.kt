package com.litebrowse.util

import java.net.URI

object UrlUtils {
    fun isUrl(input: String): Boolean {
        return input.contains(".") && !input.contains(" ")
    }

    fun ensureScheme(url: String): String {
        return if (url.startsWith("http://") || url.startsWith("https://")) url
        else "https://$url"
    }

    fun extractDomain(url: String): String {
        return try {
            URI(url).host ?: url
        } catch (_: Exception) {
            url
        }
    }
}
