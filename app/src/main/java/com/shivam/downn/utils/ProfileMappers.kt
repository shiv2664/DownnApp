package com.shivam.downn.utils

import com.shivam.downn.data.models.ProfileResponse
import com.shivam.downn.data.models.ProfileType
import com.shivam.downn.data.models.UserDetailsResponse
import com.shivam.downn.data.models.UserProfileData
import com.shivam.downn.data.local.db.CachedProfile

fun ProfileResponse.toUserProfileData(): UserProfileData {
    return UserProfileData(
        id = this.id,
        userId = this.userId,
        name = this.name,
        avatar = ImageUtils.getFullImageUrl(this.avatar),
        coverImage = ImageUtils.getFullImageUrl(this.coverImage),
        vibes = this.vibes?.split(",")?.filter { it.isNotBlank() } ?: emptyList(),
        bio = this.bio ?: "",
        location = this.location,
        type = this.type,
        avatarThumbnail=this.avatarThumbnail,
        latitude = this.latitude,
        longitude = this.longitude,
        followersCount = this.followersCount,
        followingCount = this.followingCount,
        isFollowing = this.isFollowing
    )
}

fun UserDetailsResponse.toUserProfileData(): UserProfileData {
    return UserProfileData(
        id = if (this.personalProfileId > 0) this.personalProfileId else this.id,
        userId = this.id,
        name = this.name,
        avatar = ImageUtils.getFullImageUrl(this.avatar),
        coverImage = ImageUtils.getFullImageUrl(this.coverImage),
        vibes = this.vibes?.split(",")?.filter { it.isNotBlank() } ?: emptyList(),
        bio = this.bio ?: "",
        location = this.location,
        type = this.type ?: ProfileType.PERSONAL,
        avatarThumbnail = this.avatarThumbnail,
        latitude = this.latitude,
        longitude = this.longitude,
        followersCount = this.followersCount,
        followingCount = this.followingCount,
        isFollowing = this.isFollowing,
        isBlocked = this.isBlocked
    )
}

fun com.shivam.downn.data.models.AuthResponse.toUserProfileData(): UserProfileData {
    return UserProfileData(
        id = this.personalProfileId,
        userId = this.userId,
        name = this.name,
        avatar = null,
        type = ProfileType.PERSONAL,
        followersCount = 0,
        followingCount = 0,
        isFollowing = false
    )
}

fun CachedProfile.toProfileResponse(): ProfileResponse {
    return ProfileResponse(
        id = this.id,
        userId = this.userId,
        name = this.name,
        avatar = this.avatar,
        coverImage = this.coverImage,
        vibes = this.vibes,
        bio = this.bio,
        location = this.location,
        type = ProfileType.valueOf(this.type),
        createdAt = java.time.LocalDateTime.now(),
        avatarThumbnail = null,
        followersCount = this.followersCount,
        followingCount = this.followingCount
    )
}

fun ProfileResponse.toCachedProfile(): CachedProfile {
    return CachedProfile(
        id = this.id,
        userId = this.userId,
        name = this.name,
        avatar = this.avatar,
        coverImage = this.coverImage,
        vibes = this.vibes ?: "",
        bio = this.bio,
        location = this.location,
        type = this.type.name,
        followersCount = this.followersCount,
        followingCount = this.followingCount
    )
}
