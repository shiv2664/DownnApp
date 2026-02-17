package com.shivam.downn.data.models

import com.google.gson.annotations.SerializedName

data class ChatListResponse(
    @SerializedName("activityId") val activityId: Long,
    @SerializedName("title") val title: String,
    @SerializedName("image") val image: String?,
    @SerializedName("lastMessage") val lastMessage: String?,
    @SerializedName("lastMessageTime") val lastMessageTime: String?,
    @SerializedName("unreadCount") val unreadCount: Int = 0
)

data class ChatMessageResponse(
    @SerializedName("id") val id: Long,
    @SerializedName("activityId") val activityId: Long,
    @SerializedName("profileId") val profileId: Long,
    @SerializedName("profileName") val profileName: String,
    @SerializedName("profileAvatar") val profileAvatar: String?,
    @SerializedName("content") val content: String,
    @SerializedName("createdAt") val createdAt: String
)
