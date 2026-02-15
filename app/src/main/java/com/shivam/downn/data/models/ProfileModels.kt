package com.shivam.downn.data.models

import androidx.compose.ui.graphics.Color

import java.time.LocalDateTime

enum class ProfileType {
    PERSONAL,
    BUSINESS
}

data class ProfileResponse(
    val id: Long,
    val userId: Long, // Added userId
    val name: String,
    val avatar: String?,
    val coverImage: String?,
    val vibes: String?,
    val bio: String?,
    val location: String?,
    val type: ProfileType,
    val createdAt: LocalDateTime,
    val avatarThumbnail: String?="",
    val latitude: Double? = null,
    val longitude: Double? = null,
    val followersCount: Int = 0,
    val followingCount: Int = 0,
    val isFollowing: Boolean = false
)

data class CreateProfileRequest(
    val name: String,
    val avatar: String? = null,
    val coverImage: String? = null,
    val vibes: String? = null,
    val bio: String? = null,
    val type: ProfileType = ProfileType.BUSINESS,
    val latitude: Double? = null,
    val longitude: Double? = null
)

data class InterestTag(
    val id: Int,
    val name: String,
    val colors: List<Color>
)

data class UpdateUserRequest(
    val name: String,
    val bio: String,
    val location: String
)

data class UpdateProfileRequest(
    val name: String,
    val bio: String,
    val location: String,
    val vibes: String,
    val latitude: Double? = null,
    val longitude: Double? = null
)

data class UserDetailsResponse(
    val id: Long,
    val name: String="",
    val avatar: String?="",
    val coverImage: String?,
    val vibes: String?,
    val bio: String?,
    val location: String?="",
    val type: ProfileType?,
    val createdAt: LocalDateTime?,
    val avatarThumbnail: String?="",
    val latitude: Double? = null,
    val longitude: Double? = null,
    val followersCount: Int = 0,
    val followingCount: Int = 0,
    val isFollowing: Boolean = false
)

data class UserProfileData(
    val id: Long,
    val userId: Long, // Added userId (For Business profiles, this is owner ID. For Personal, same as id)
    val name: String="",
    val avatar: String?="",
    val coverImage: String? = "",
    val vibes: List<String>? = emptyList(),
    val bio: String? = "",
    val location: String? = "Denver",
    val type: ProfileType,
    val avatarThumbnail: String?="",
    val latitude: Double? = null,
    val longitude: Double? = null,
    val followersCount: Int = 0,
    val followingCount: Int = 0,
    val isFollowing: Boolean = false
)

