package com.shivam.downn.utils

import java.time.Duration
import java.time.LocalDateTime

fun LocalDateTime.toTimeAgo(): String {
    val now = LocalDateTime.now()
    val duration = Duration.between(this, now)
    
    val seconds = duration.seconds
    if (seconds < 60) return "Just now"
    
    val minutes = duration.toMinutes()
    if (minutes < 60) return "${minutes}m ago"
    
    val hours = duration.toHours()
    if (hours < 24) return "${hours}h ago"
    
    val days = duration.toDays()
    if (days < 7) return "${days}d ago"
    
    return "${days / 7}w ago"
}
