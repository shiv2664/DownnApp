package com.shivam.downn.data.models

import java.time.LocalDateTime

enum class NotificationType {
    JOIN_REQUEST,
    JOIN_ACTIVITY_REQUEST,
    MESSAGE,
    SYSTEM_ALERT
}

data class NotificationResponse(
    val id: Long,
    val senderUser: UserDetailsResponse?,
    val type: NotificationType,
    val content: String?,
    val createdAt: LocalDateTime,
    val read: Boolean,
    val activityStatus: ActivityStatus? = ActivityStatus.PENDING

)

enum class ActivityStatus {
    PENDING,
    ACCEPTED,
    REJECTED
}


data class NotificationContent(
    val senderUserId: Long? = null,
    val senderUserName: String? = null,
    val activityId: Long,
    val activityTitle: String,
    val message: String? = null,
    val status: String? = null
)
