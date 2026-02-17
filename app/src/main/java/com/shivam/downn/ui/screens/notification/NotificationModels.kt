package com.shivam.downn.ui.screens.notification

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import com.shivam.downn.data.models.NotificationType

data class NotificationItem(
    val id: Long,
    val type: NotificationType,
    val title: String,
    val description: String,
    val time: String,
    val avatar: String? = null,
    val icon: ImageVector,
    val iconBg: Brush,
    val gradient: Brush,
    val isUnread: Boolean,
    val actionable: Boolean = false,
    val senderUserId: Long? = null,
    val activityId: Long? = null,
    val actionStatus: String? = null
)