package com.shivam.downn.ui.screens.notification

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil.compose.AsyncImage
import com.shivam.downn.data.models.NotificationResponse
import com.shivam.downn.data.models.NotificationType
import com.shivam.downn.data.models.NotificationContent
import com.shivam.downn.data.models.ActivityStatus
import com.shivam.downn.data.network.NetworkResult
import com.google.gson.Gson
import com.shivam.downn.utils.TimeUtils
import java.time.LocalDateTime

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsRoute(
    outerPadding: PaddingValues,
    onNotificationClick: (NotificationItem) -> Unit = {},
    onMarkAllRead: () -> Unit = {}
) {
    val viewModel = hiltViewModel<NotificationViewModel>()
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    
    // Refresh notifications when screen resumes (user switches back to this tab)
    val state by viewModel.state.collectAsState()
    val actionState by viewModel.actionState.collectAsState()
    val notificationActions by viewModel.notificationActions.collectAsState()
    val actioningNotificationId by viewModel.actioningNotificationId.collectAsState()
    
    NotificationsContent(
        state = state,
        actionState = actionState,
        notificationActions = notificationActions,
        actioningNotificationId = actioningNotificationId,
        outerPadding = outerPadding,
        onNotificationClick = onNotificationClick,
        onApprove = { id -> viewModel.approveNotification(id) },
        onReject = { id -> viewModel.rejectNotification(id) },
        onClearAll = { viewModel.clearNotifications() },
        onRetry = { viewModel.fetchNotifications() },
        onMarkAllRead = onMarkAllRead
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsContent(
    state: NetworkResult<List<NotificationResponse>>,
    actionState: NetworkResult<Unit>? = null,
    notificationActions: Map<Long, String> = emptyMap(),
    actioningNotificationId: Long? = null,
    outerPadding: PaddingValues,
    onNotificationClick: (NotificationItem) -> Unit = {},
    onApprove: (Long) -> Unit = {},
    onReject: (Long) -> Unit = {},
    onClearAll: () -> Unit = {},
    onRetry: () -> Unit = {},
    onMarkAllRead: () -> Unit = {}
) {
    val gson = remember { Gson() }
    var filter by remember { mutableStateOf("all") }

    val pinkPurple = Brush.linearGradient(listOf(Color(0xFFA855F7), Color(0xFFEC4899)))
    val blueCyan = Brush.linearGradient(listOf(Color(0xFF3B82F6), Color(0xFF06B6D4)))
    val greenEmerald = Brush.linearGradient(listOf(Color(0xFF10B981), Color(0xFF059669)))
    val orangeAmber = Brush.linearGradient(listOf(Color(0xFFF97316), Color(0xFFF59E0B)))

    fun mapToUI(response: NotificationResponse): NotificationItem {
        val (icon, iconBg, gradient, actionable) = when (response.type) {
            NotificationType.JOIN_REQUEST -> {
                Quadruple(Icons.Default.PersonAdd, pinkPurple, pinkPurple, true)
            }
            NotificationType.JOIN_ACTIVITY_REQUEST -> {
                Quadruple(Icons.Default.Place, orangeAmber, orangeAmber, true)
            }
            NotificationType.MESSAGE -> {
                Quadruple(Icons.Default.ChatBubble, greenEmerald, greenEmerald, false)
            }
            NotificationType.SYSTEM_ALERT -> {
                Quadruple(Icons.Default.Info, blueCyan, blueCyan, false)
            }
        }

        val parsedContent = response.content?.let {
            try {
                gson.fromJson(it, NotificationContent::class.java)
            } catch (e: Exception) {
                null
            }
        }

        val title = when (response.type) {
            NotificationType.JOIN_REQUEST -> "${parsedContent?.senderUserName ?: response.senderUser?.name ?: "Someone"} requested to join"
            NotificationType.JOIN_ACTIVITY_REQUEST -> "${parsedContent?.senderUserName ?: "Someone"} wants to join ${parsedContent?.activityTitle ?: "Activity"}"
            NotificationType.MESSAGE -> "New Message"
            NotificationType.SYSTEM_ALERT -> {
                when (parsedContent?.status) {
                    "ACCEPTED" -> "Request Accepted!"
                    "REJECTED" -> "Request Rejected"
                    else -> "System Alert"
                }
            }
        }

        val description = when {
            response.type == NotificationType.SYSTEM_ALERT && parsedContent?.message != null -> parsedContent.message
            parsedContent?.activityTitle != null -> "Requested to join: ${parsedContent.activityTitle}"
            else -> response.content ?: ""
        }

        val actionStatusValue = when {
            response.activityStatus == ActivityStatus.ACCEPTED -> "APPROVED"
            response.activityStatus == ActivityStatus.REJECTED -> "REJECTED"
            response.type == NotificationType.SYSTEM_ALERT && parsedContent?.status == "ACCEPTED" -> "APPROVED"
            response.type == NotificationType.SYSTEM_ALERT && parsedContent?.status == "REJECTED" -> "REJECTED"
            else -> notificationActions[response.id]
        }

        return NotificationItem(
            id = response.id,
            type = response.type,
            title = title,
            description = description,
            time = TimeUtils.formatRelativeTime(response.createdAt.toString()),
            avatar = response.senderUser?.avatar,
            icon = icon,
            iconBg = iconBg,
            gradient = gradient,
            actionable = actionable,
            senderUserId = parsedContent?.senderUserId ?: response.senderUser?.id,
            activityId = parsedContent?.activityId,
            actionStatus = actionStatusValue,
            isUnread = !response.read
        )
    }

    val snackbarHostState = remember { SnackbarHostState() }
    
    LaunchedEffect(actionState) {
        if (actionState is NetworkResult.Error) {
            snackbarHostState.showSnackbar(actionState.message ?: "Action failed")
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        modifier = Modifier.padding(8.dp),
                        text = "Notifications",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                actions = {
                    TextButton(onClick = onClearAll) {
                        Text(
                            "Clear All",
                            color = Color(0xFFA855F7),
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0F172A))
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color(0xFF0F172A)
    ) { innerPadding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(bottom = outerPadding.calculateBottomPadding(), top = innerPadding.calculateTopPadding())
        ) {
            val isRefreshing = state is NetworkResult.Loading
            
            androidx.compose.material3.pulltorefresh.PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = onRetry,
                modifier = Modifier.fillMaxSize()
            ) {
                when (val currentState = state) {
                    is NetworkResult.Loading -> {
                        if (currentState.data.isNullOrEmpty()) {
                            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = Color(0xFFA855F7))
                        } else {
                            NotificationList(
                                notifications = currentState.data!!.map { mapToUI(it) },
                                filter = filter,
                                onNotificationClick = onNotificationClick,
                                onApprove = onApprove,
                                onReject = onReject,
                                actioningNotificationId = actioningNotificationId,
                                notificationActions = notificationActions
                            )
                        }
                    }
                    is NetworkResult.Error -> {
                        if (currentState.data.isNullOrEmpty()) {
                            Column(
                                modifier = Modifier.align(Alignment.Center).padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(text = currentState.message ?: "An error occurred", color = Color.Red.copy(alpha = 0.8f))
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(
                                    onClick = { onRetry() },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B))
                                ) {
                                    Text("Retry", color = Color.White)
                                }
                            }
                        } else {
                             // Show cached data with error toast maybe? For now just show list
                             NotificationList(
                                notifications = currentState.data!!.map { mapToUI(it) },
                                filter = filter,
                                onNotificationClick = onNotificationClick,
                                onApprove = onApprove,
                                onReject = onReject,
                                actioningNotificationId = actioningNotificationId,
                                notificationActions = notificationActions
                             )
                        }
                    }
                    is NetworkResult.Success -> {
                        NotificationList(
                            notifications = currentState.data!!.map { mapToUI(it) },
                            filter = filter,
                            onNotificationClick = onNotificationClick,
                            onApprove = onApprove,
                            onReject = onReject,
                            actioningNotificationId = actioningNotificationId,
                            notificationActions = notificationActions
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationList(
    notifications: List<NotificationItem>,
    filter: String,
    onNotificationClick: (NotificationItem) -> Unit,
    onApprove: (Long) -> Unit,
    onReject: (Long) -> Unit,
    actioningNotificationId: Long?,
    notificationActions: Map<Long, String>
) {
    val filteredNotifications = if (filter == "unread") notifications.filter { it.isUnread } else notifications

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            SectionHeader("Recent")
        }
        
        if (filteredNotifications.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(48.dp), contentAlignment = Alignment.Center) {
                    Text("No notifications found", color = Color(0xFF94A3B8))
                }
            }
        } else {
            items(filteredNotifications) { notification ->
                NotificationRow(
                    notification = notification,
                    onClick = { 
                        if (notification.activityId != null) {
                            onNotificationClick(notification) 
                        }
                    },
                    onApprove = { onApprove(notification.id) },
                    onReject = { onReject(notification.id) },
                    isLoading = actioningNotificationId == notification.id,
                    anyActionInProgress = actioningNotificationId != null
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
            WeeklyStatsCard()
        }
    }
}

data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title.uppercase(),
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF94A3B8),
        letterSpacing = 1.sp,
        modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
    )
}

@Composable
private fun NotificationRow(
    notification: NotificationItem,
    onClick: (NotificationItem) -> Unit,
    onApprove: () -> Unit,
    onReject: () -> Unit,
    isLoading: Boolean = false,
    anyActionInProgress: Boolean = false
) {
    Surface(
        onClick = { if (!anyActionInProgress) onClick(notification) },
        enabled = !anyActionInProgress,
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp),
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFF1E293B),
        border = if (notification.isUnread) BorderStroke(
            2.dp,
            Color(0xFF3B82F6).copy(alpha = 0.3f)
        ) else BorderStroke(1.dp, Color(0xFF334155)),
        shadowElevation = 0.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Icon / Avatar
            Box(contentAlignment = Alignment.BottomEnd) {
                if (notification.avatar != null) {
                    AsyncImage(
                        model = notification.avatar,
                        contentDescription = null,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .border(2.dp, Color.White, CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .offset(x = 4.dp, y = 4.dp)
                            .clip(CircleShape)
                            .background(notification.iconBg)
                            .padding(4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            notification.icon,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(notification.iconBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            notification.icon,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        notification.title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.weight(1f)
                    )
                    if (notification.isUnread) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(Color(0xFF3B82F6), CircleShape)
                                .offset(y = 6.dp)
                        )
                    }
                }
                Text(
                    notification.description,
                    fontSize = 14.sp,
                    color = Color(0xFFCBD5E1),
                    modifier = Modifier.padding(top = 2.dp)
                )
                Text(
                    notification.time,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF94A3B8),
                    modifier = Modifier.padding(top = 8.dp)
                )

                if (notification.actionable) {
                    if (notification.actionStatus != null) {
                        Surface(
                            modifier = Modifier.padding(top = 12.dp),
                            shape = RoundedCornerShape(8.dp),
                            color = if (notification.actionStatus == "APPROVED") 
                                Color(0xFF10B981).copy(alpha = 0.1f) 
                            else Color(0xFFEF4444).copy(alpha = 0.1f),
                            border = BorderStroke(1.dp, if (notification.actionStatus == "APPROVED") 
                                Color(0xFF10B981).copy(alpha = 0.3f) 
                            else Color(0xFFEF4444).copy(alpha = 0.3f))
                        ) {
                            Text(
                                text = if (notification.actionStatus == "APPROVED") "ALLOWED" else "REJECTED",
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (notification.actionStatus == "APPROVED") Color(0xFF10B981) else Color(0xFFEF4444),
                                letterSpacing = 1.sp
                            )
                        }
                    } else if (isLoading) {
                        Box(
                            modifier = Modifier
                                .padding(top = 12.dp)
                                .height(36.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color(0xFFA855F7),
                                strokeWidth = 2.dp
                            )
                        }
                    } else {
                        Row(
                            modifier = Modifier.padding(top = 12.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { onApprove() },
                                enabled = !anyActionInProgress,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(36.dp),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.Transparent,
                                    disabledContainerColor = Color.Gray.copy(alpha = 0.2f)
                                ),
                                contentPadding = PaddingValues()
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(if (anyActionInProgress) Brush.linearGradient(listOf(Color.Gray, Color.Gray)) else notification.gradient),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        if (notification.type == NotificationType.JOIN_REQUEST) "Accept" else "Allow",
                                        color = Color.White,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            
                            Button(
                                onClick = { onReject() },
                                enabled = !anyActionInProgress,
                                modifier = Modifier
                                    .weight(if (notification.type == NotificationType.JOIN_REQUEST) 0.5f else 1f)
                                    .height(36.dp),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFF1F5F9),
                                    disabledContainerColor = Color(0xFFF1F5F9).copy(alpha = 0.5f)
                                )
                            ) {
                                Text(
                                    if (notification.type == NotificationType.JOIN_REQUEST) "Decline" else "Reject",
                                    color = if (anyActionInProgress) Color.Gray else Color(0xFF64748B),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WeeklyStatsCard() {
    Card(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .background(
                    Brush.linearGradient(
                        listOf(
                            Color(0xFF9333EA),
                            Color(0xFFDB2777)
                        )
                    )
                )
                .padding(24.dp)
        ) {
            Column {
                Text(
                    "This Week's Activity",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    StatMetric("12", "Join Requests")
                    StatMetric("28", "Messages")
                    StatMetric("64", "Vibes")
                }
            }
        }
    }
}

@Composable
private fun StatMetric(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Text(label, color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0F172A)
@Composable
fun NotificationsPreview() {
    val mockNotifications = listOf(
        NotificationResponse(
            id = 1,
            senderUser = null,
            type = NotificationType.JOIN_REQUEST,
            content = "{\"senderUserId\": 2, \"senderUserName\": \"Max\", \"activityId\": 101, \"activityTitle\": \"Jazz Night\"}",
            createdAt = LocalDateTime.now().minusHours(2),
            read = false,
            activityStatus = ActivityStatus.PENDING
        ),
        NotificationResponse(
            id = 2,
            senderUser = null,
            type = NotificationType.SYSTEM_ALERT,
            content = "{\"message\": \"Your request to join 'Club Hub' has been accepted!\", \"activityId\": 2, \"activityTitle\": \"Club Hub\", \"status\": \"ACCEPTED\"}",
            createdAt = LocalDateTime.now().minusHours(1),
            read = false,
            activityStatus = ActivityStatus.PENDING
        ),
        NotificationResponse(
            id = 3,
            senderUser = null,
            type = NotificationType.MESSAGE,
            content = "Hey, looking forward to the hike!",
            createdAt = LocalDateTime.now().minusHours(5),
            read = true,
            activityStatus = ActivityStatus.PENDING
        )
    )

    NotificationsContent(
        state = NetworkResult.Success(mockNotifications),
        notificationActions = emptyMap(),
        outerPadding = PaddingValues(0.dp)
    )
}
