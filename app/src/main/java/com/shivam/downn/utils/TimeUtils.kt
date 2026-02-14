package com.shivam.downn.utils

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

object TimeUtils {

    fun formatRelativeTime(isoString: String?): String {
        if (isoString.isNullOrBlank()) return ""
        return try {
            val dateTime = LocalDateTime.parse(isoString, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            val now = LocalDateTime.now()
            val minutesDiff = ChronoUnit.MINUTES.between(dateTime, now)
            val hoursDiff = ChronoUnit.HOURS.between(dateTime, now)
            val daysDiff = ChronoUnit.DAYS.between(dateTime, now)

            when {
                minutesDiff < 0 -> formatFutureTime(dateTime, now)
                minutesDiff < 1 -> "Just now"
                minutesDiff < 60 -> "${minutesDiff}m ago"
                hoursDiff < 24 -> "${hoursDiff}h ago"
                daysDiff < 7 -> "${daysDiff}d ago"
                daysDiff < 30 -> "${daysDiff / 7}w ago"
                else -> dateTime.format(DateTimeFormatter.ofPattern("MMM d, yyyy"))
            }
        } catch (e: Exception) {
            isoString
        }
    }

    private fun formatFutureTime(dateTime: LocalDateTime, now: LocalDateTime): String {
        val minutesDiff = ChronoUnit.MINUTES.between(now, dateTime)
        val hoursDiff = ChronoUnit.HOURS.between(now, dateTime)
        val daysDiff = ChronoUnit.DAYS.between(now, dateTime)

        return when {
            minutesDiff < 60 -> "In ${minutesDiff}m"
            hoursDiff < 24 -> "In ${hoursDiff}h"
            daysDiff == 1L -> "Tomorrow at ${dateTime.format(DateTimeFormatter.ofPattern("h:mm a"))}"
            daysDiff < 7 -> "${dateTime.format(DateTimeFormatter.ofPattern("EEEE"))} at ${dateTime.format(DateTimeFormatter.ofPattern("h:mm a"))}"
            else -> dateTime.format(DateTimeFormatter.ofPattern("MMM d 'at' h:mm a"))
        }
    }

    fun formatScheduledTime(isoString: String?): String {
        if (isoString.isNullOrBlank()) return "Anytime"
        return try {
            val dateTime = LocalDateTime.parse(isoString, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            val now = LocalDateTime.now()
            val daysDiff = ChronoUnit.DAYS.between(now.toLocalDate(), dateTime.toLocalDate())

            val timeStr = dateTime.format(DateTimeFormatter.ofPattern("h:mm a"))
            when {
                daysDiff == 0L -> "Today at $timeStr"
                daysDiff == 1L -> "Tomorrow at $timeStr"
                daysDiff < 7 -> "${dateTime.format(DateTimeFormatter.ofPattern("EEEE"))} at $timeStr"
                else -> dateTime.format(DateTimeFormatter.ofPattern("MMM d, yyyy 'at' h:mm a"))
            }
        } catch (e: Exception) {
            isoString
        }
    }
}
