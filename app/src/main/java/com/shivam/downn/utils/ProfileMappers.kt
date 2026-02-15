package com.shivam.downn.utils

import com.shivam.downn.data.models.ProfileResponse
import com.shivam.downn.data.models.ProfileType
import com.shivam.downn.data.models.UserDetailsResponse
import com.shivam.downn.data.models.UserProfileData

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
        id = this.id,
        userId = this.id, // For Personal profile, id is userId
        name = this.name,
        avatar = ImageUtils.getFullImageUrl(this.avatar),
        coverImage = ImageUtils.getFullImageUrl(this.coverImage),
        vibes = this.vibes?.split(",")?.filter { it.isNotBlank() } ?: emptyList(),
        bio = this.bio ?: "",
        location = this.location,
        type = this.type?:ProfileType.PERSONAL,
        avatarThumbnail=this.avatarThumbnail,
        latitude = this.latitude,
        longitude = this.longitude,
        followersCount = this.followersCount,
        followingCount = this.followingCount,
        isFollowing = this.isFollowing
    )
}

fun com.shivam.downn.data.models.AuthResponse.toUserProfileData(): UserProfileData {
    return UserProfileData(
        id = this.userId,
        userId = this.userId,
        name = this.name,
        avatar = "https://images.unsplash.com/photo-1566330429822-c413e4bc27a5", // Default avatar
        type = ProfileType.PERSONAL,
        followersCount = 0,
        followingCount = 0,
        isFollowing = false
    )
}
