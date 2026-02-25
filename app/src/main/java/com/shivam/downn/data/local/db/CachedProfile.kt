package com.shivam.downn.data.local.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "profiles")
data class CachedProfile(
    @PrimaryKey val id: Long,
    val userId: Long,
    val name: String,
    val avatar: String?,
    val coverImage: String?,
    val vibes: String, // Comma separated List<String>
    val bio: String?,
    val location: String?,
    val type: String, // PERSONAL or BUSINESS
    val followersCount: Int,
    val followingCount: Int
)
