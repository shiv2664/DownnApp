package com.shivam.downn.utils

import com.shivam.downn.data.models.ProfileResponse
import com.shivam.downn.data.models.ProfileType
import com.shivam.downn.data.models.UserDetailsResponse
import com.shivam.downn.data.models.UserProfileData

fun ProfileResponse.toUserProfileData(): UserProfileData {
    return UserProfileData(
        id = this.id,
        name = this.name,
        avatar = ImageUtils.getFullImageUrl(this.avatar),
        coverImage = ImageUtils.getFullImageUrl(this.coverImage),
        vibes = this.vibes?.split(",")?.filter { it.isNotBlank() } ?: emptyList(),
        bio = this.bio ?: "",
        location = this.location,
        type = this.type,
        avatarThumbnail=this.avatarThumbnail,
        latitude = this.latitude,
        longitude = this.longitude
    )
}

fun UserDetailsResponse.toUserProfileData(): UserProfileData {
    return UserProfileData(
        id = this.id,
        name = this.name,
        avatar = ImageUtils.getFullImageUrl(this.avatar),
        coverImage = ImageUtils.getFullImageUrl(this.coverImage),
        vibes = this.vibes?.split(",")?.filter { it.isNotBlank() } ?: emptyList(),
        bio = this.bio ?: "",
        location = this.location,
        type = this.type?:ProfileType.PERSONAL,
        avatarThumbnail=this.avatarThumbnail,
        latitude = this.latitude,
        longitude = this.longitude
    )
}

fun com.shivam.downn.data.models.AuthResponse.toUserProfileData(): UserProfileData {
    return UserProfileData(
        id = this.userId,
        name = this.name,
        avatar = "https://images.unsplash.com/photo-1566330429822-c413e4bc27a5", // Default avatar
        type = ProfileType.PERSONAL
    )
}
