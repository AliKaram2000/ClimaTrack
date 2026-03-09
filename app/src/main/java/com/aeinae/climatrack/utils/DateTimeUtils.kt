package com.aeinae.climatrack.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit

object DateTimeUtils {

    // ═══════════════════════════════════════════
    // Hero Section
    // ═══════════════════════════════════════════

    /** "Mon, Mar 15" — hero date */
    fun formatDayDate(timestampSeconds: Long): String {
        val sdf = SimpleDateFormat("EEE, MMM d", Locale.getDefault())
        return sdf.format(Date(timestampSeconds * 1000))
    }

    /** "3:45 PM" — hero time */
    fun formatTime(timestampSeconds: Long, timezoneOffset: Int = 0): String {
        val sdf = SimpleDateFormat("h:mm a", Locale.getDefault())
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        val adjustedTime = (timestampSeconds + timezoneOffset) * 1000
        return sdf.format(Date(adjustedTime))
    }

    // ═══════════════════════════════════════════
    // Hourly Cards
    // ═══════════════════════════════════════════

    /** "3 PM" — hourly forecast */
    fun formatHour(timestampSeconds: Long, timezoneOffset: Int = 0): String {
        val sdf = SimpleDateFormat("h a", Locale.getDefault())
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        val adjustedTime = (timestampSeconds + timezoneOffset) * 1000
        return sdf.format(Date(adjustedTime))
    }

    // ═══════════════════════════════════════════
    // Daily Rows
    // ═══════════════════════════════════════════

    /** "Monday" — daily forecast */
    fun formatDayName(timestampSeconds: Long): String {
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format(Date(timestampSeconds * 1000))
    }

    // ═══════════════════════════════════════════
    // System Time
    // ═══════════════════════════════════════════

    /** Current date: "Mon, Mar 15" */
    fun currentDateFormatted(): String {
        return formatDayDate(System.currentTimeMillis() / 1000)
    }

    /** Current time: "3:45 PM" */
    fun currentTimeFormatted(timezoneOffset: Int = 0): String {
        return formatTime(System.currentTimeMillis() / 1000, timezoneOffset)
    }

    // ═══════════════════════════════════════════
    // Cache
    // ═══════════════════════════════════════════

    /** "Mar 15, 3:45 PM" — for last updated display */
    fun formatDateTime(timestampMillis: Long): String {
        val sdf = SimpleDateFormat("MMM d, h:mm a", Locale.getDefault())
        return sdf.format(Date(timestampMillis))
    }

    /** "Last updated: Mar 15, 3:45 PM" */
    fun lastUpdatedText(lastUpdatedMillis: Long): String {
        return "Last updated: ${formatDateTime(lastUpdatedMillis)}"
    }

    /** Is cache older than validity period? */
    fun isCacheExpired(lastUpdatedMillis: Long): Boolean {
        val diff = System.currentTimeMillis() - lastUpdatedMillis
        return diff > TimeUnit.MINUTES.toMillis(Constants.CACHE_VALIDITY_MINUTES)
    }

    // ═══════════════════════════════════════════
    // Epoch Helpers
    // ═══════════════════════════════════════════

    fun nowMillis(): Long = System.currentTimeMillis()
    fun nowSeconds(): Long = System.currentTimeMillis() / 1000
}