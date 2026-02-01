package com.shivam.downn.data.models

import androidx.compose.ui.graphics.Color

import java.time.LocalDateTime

enum class ProfileType {
    PERSONAL,
    BUSINESS
}

data class ProfileResponse(
    val id: Long,
    val name: String,
    val avatar: String?,
    val coverImage: String?,
    val vibes: String?,
    val bio: String?,
    val location: String?,
    val type: ProfileType,
    val createdAt: LocalDateTime,
    val avatarThumbnail: String?=""
)

data class CreateProfileRequest(
    val name: String,
    val avatar: String? = null,
    val coverImage: String? = null,
    val vibes: String? = null,
    val bio: String? = null,
    val type: ProfileType = ProfileType.BUSINESS
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
    val vibes: List<String>
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
    val avatarThumbnail: String?=""
)

data class UserProfileData(
    val id: Long,
    val name: String="",
    val avatar: String?="",
    val coverImage: String? = "",
    val vibes: List<String>? = emptyList(),
    val bio: String? = "",
    val location: String? = "",
    val type: ProfileType,
    val avatarThumbnail: String?=""
)

