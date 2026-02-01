package com.shivam.downn.data.models

import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody

enum class SocialType {
    PERSONAL,
    BUSINESS
}

data class SocialResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String?,
    @SerializedName("category") val category: String,
    @SerializedName("city") val city: String,
    @SerializedName("locationName") val locationName: String?,
    @SerializedName("scheduledTime") val scheduledTime: String?,
    @SerializedName("maxParticipants") val maxParticipants: Int?,
    @SerializedName("participantCount") val participantCount: Int = 0,
    @SerializedName("userId") val userId: Int? = null,
    @SerializedName("userName") val userName: String? = null,
    @SerializedName("userAvatar") val userAvatar: String? = null,
    @SerializedName("userBio") val userBio: String? = null,
    @SerializedName("userIsVerified") val userIsVerified: Boolean = false,
    @SerializedName("timeAgo") val timeAgo: String? = null,
    @SerializedName("distance") val distance: String? = null,
    @SerializedName("participantAvatars") val participantAvatars: List<String> = emptyList(),
    @SerializedName("isActive") val isActive: Boolean = true,
    @SerializedName("createdAt") val createdAt: String? = null,
    @SerializedName("socialType") val socialType: SocialType = SocialType.BUSINESS
)

data class CreateSocialRequest(
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("category") val category: String,
    @SerializedName("city") val city: String,
    @SerializedName("locationName") val locationName: String,
    @SerializedName("scheduledTime") val scheduledTime: String,
    @SerializedName("maxParticipants") val maxParticipants: Int,
    @SerializedName("profileId") val profileId: Long,
    @SerializedName("socialType") val socialType: SocialType = SocialType.BUSINESS,
    @SerializedName("images") val images: MutableList<MultipartBody.Part>?=null
)

data class JoinResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String?
)
