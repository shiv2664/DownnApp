package com.shivam.downn.data.local.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "social_activities")
data class CachedSocial(
    @PrimaryKey val id: Int,
    val title: String,
    val description: String?,
    val category: String,
    val city: String,
    val locationName: String?,
    val scheduledTime: String?,
    val maxParticipants: Int?,
    val participantCount: Int,
    val userId: Int?,
    val userName: String?,
    val userAvatar: String?,
    val socialType: String,
    val latitude: Double?,
    val longitude: Double?,
    val imagesJson: String, // List<String> converted to JSON
    val isJoined: Boolean = false // Helper flag for UI
)
