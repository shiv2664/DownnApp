package com.shivam.downn.data.models

import com.google.gson.annotations.SerializedName

data class ActivityResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("category") val category: String,
    @SerializedName("city") val city: String,
    @SerializedName("locationName") val locationName: String,
    @SerializedName("scheduledTime") val scheduledTime: String,
    @SerializedName("maxParticipants") val maxParticipants: Int,
    @SerializedName("participantCount") val participantCount: Int = 0,
    @SerializedName("userName") val userName: String? = null,
    @SerializedName("userAvatar") val userAvatar: String? = null,
    @SerializedName("timeAgo") val timeAgo: String? = null,
    @SerializedName("distance") val distance: String? = null,
    @SerializedName("participantAvatars") val participantAvatars: List<String> = emptyList()
)

data class CreateActivityRequest(
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("category") val category: String,
    @SerializedName("city") val city: String,
    @SerializedName("locationName") val locationName: String,
    @SerializedName("scheduledTime") val scheduledTime: String,
    @SerializedName("maxParticipants") val maxParticipants: Int
)

data class JoinResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String?
)
