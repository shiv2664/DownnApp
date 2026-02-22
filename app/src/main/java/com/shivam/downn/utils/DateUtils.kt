package com.shivam.downn.utils

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

object DateUtils {

    fun formatEventTime(timeString: String?): String {
        if (timeString.isNullOrEmpty() || timeString == "TBD") return "TBD"

        return try {
            val dateTime = LocalDateTime.parse(timeString, DateTimeFormatter.ISO_DATE_TIME)
            val now = LocalDateTime.now()
            
            val timeFormatter = DateTimeFormatter.ofPattern("h:mm a", Locale.getDefault())

            if (dateTime.isAfter(now)) {
                // Future Event
                val minutesUntil = ChronoUnit.MINUTES.between(now, dateTime)
                if (minutesUntil < 60) {
                    return "in $minutesUntil min"
                } else {
                    return dateTime.format(timeFormatter)
                }
            } else {
                // Past Event (Started)
                val minutesAgo = ChronoUnit.MINUTES.between(dateTime, now)
                if (minutesAgo < 60) {
                    return "Started $minutesAgo min ago"
                } else {
                    return "Started at ${dateTime.format(timeFormatter)}"
                }
            }
        } catch (e: Exception) {
            timeString
        }
    }
}
