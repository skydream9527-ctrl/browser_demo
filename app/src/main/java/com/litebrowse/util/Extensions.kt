package com.litebrowse.util

import java.text.SimpleDateFormat
import java.util.*

fun Long.toDateGroup(): String {
    val cal = Calendar.getInstance()
    val today = cal.get(Calendar.DAY_OF_YEAR)
    val year = cal.get(Calendar.YEAR)

    cal.timeInMillis = this
    val entryDay = cal.get(Calendar.DAY_OF_YEAR)
    val entryYear = cal.get(Calendar.YEAR)

    return when {
        entryYear == year && entryDay == today -> "今天"
        entryYear == year && entryDay == today - 1 -> "昨天"
        else -> SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).format(Date(this))
    }
}

fun Long.toTimeString(): String {
    return SimpleDateFormat("HH:mm", Locale.CHINA).format(Date(this))
}
