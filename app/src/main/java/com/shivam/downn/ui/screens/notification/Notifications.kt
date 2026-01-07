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
import coil.compose.AsyncImage

enum class NotificationType {
    Invite, Follow, Message, Like, Reminder, Achievement, Trending
}

data class NotificationItem(
    val id: Int,
    val type: NotificationType,
    val title: String,
    val description: String,
    val time: String,
    val avatar: String? = null,
    val icon: ImageVector,
    val iconBg: Brush,
    val gradient: Brush,
    val isUnread: Boolean,
    val actionable: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Notifications(
    onNotificationClick: (NotificationItem) -> Unit = {},
    onMarkAllRead: () -> Unit = {}
) {
    var filter by remember { mutableStateOf("all") }

    val pinkPurple = Brush.linearGradient(listOf(Color(0xFFA855F7), Color(0xFFEC4899)))
    val blueCyan = Brush.linearGradient(listOf(Color(0xFF3B82F6), Color(0xFF06B6D4)))
    val greenEmerald = Brush.linearGradient(listOf(Color(0xFF10B981), Color(0xFF059669)))
    val orangeAmber = Brush.linearGradient(listOf(Color(0xFFF97316), Color(0xFFF59E0B)))

    val mockNotifications = listOf(
        NotificationItem(1, NotificationType.Invite, "Sarah invited you to join", "Beach Sunset Vibes happening tonight at 7 PM", "5 min ago", "https://images.unsplash.com/photo-1566330429822-c413e4bc27a5", Icons.Default.Place, pinkPurple, pinkPurple, true, true),
        NotificationItem(2, NotificationType.Follow, "Alex Rivera started following you", "Check out their profile and follow back!", "15 min ago", "https://images.unsplash.com/photo-1638996030249-abc99a735463", Icons.Default.PersonAdd, blueCyan, blueCyan, true, true),
        NotificationItem(3, NotificationType.Message, "New message in Coffee Crawl", "Jordan: \"See you all at 3pm! Can't wait 🎉\"", "32 min ago", "https://images.unsplash.com/flagged/photo-1596479042555-9265a7fa7983", Icons.Default.ChatBubble, greenEmerald, greenEmerald, true),
        NotificationItem(4, NotificationType.Reminder, "Activity starting in 1 hour", "Morning Coffee Run with Alex and 2 others", "1 hr ago", null, Icons.Default.Schedule, orangeAmber, orangeAmber, true),
    )

    val unreadCount = mockNotifications.count { it.isUnread }

    Scaffold(
        topBar = {
            Column(modifier = Modifier.background(Color.White)) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = Color(0xFF9333EA), modifier = Modifier.size(28.dp))
                            Text("Notifications", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                        }
                        if (unreadCount > 0) {
                            Text("$unreadCount unread notifications", fontSize = 14.sp, color = Color(0xFF64748B), modifier = Modifier.padding(top = 4.dp))
                        }
                    }
                    if (unreadCount > 0) {
                        TextButton(
                            onClick = onMarkAllRead,
                            colors = ButtonDefaults.textButtonColors(containerColor = Color(0xFFF3E8FF)),
                            shape = RoundedCornerShape(100.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text("Mark all read", color = Color(0xFF7E22CE), fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }

                // Filter Tabs
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterTab("All", filter == "all", Modifier.weight(1f)) { filter = "all" }
                    FilterTab("Unread", filter == "unread", Modifier.weight(1f), unreadCount) { filter = "unread" }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Divider(color = Color(0xFFE2E8F0), thickness = 1.dp)
            }
        },
        containerColor = Color(0xFFF8FAFC)
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            item {
                SectionHeader("Today")
            }
            items(mockNotifications) { notification ->
                NotificationRow(notification, onNotificationClick)
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
                WeeklyStatsCard()
            }
        }
    }
}

@Composable
private fun FilterTab(label: String, isActive: Boolean, modifier: Modifier = Modifier, count: Int = 0, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = modifier.height(44.dp),
        shape = RoundedCornerShape(12.dp),
        color = if (isActive) Color.Transparent else Color(0xFFF1F5F9),
        contentColor = if (isActive) Color.White else Color(0xFF64748B)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .then(
                    if (isActive) Modifier.background(Brush.horizontalGradient(listOf(Color(0xFF9333EA), Color(0xFFDB2777))))
                    else Modifier
                ),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(label, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                if (count > 0) {
                    Box(
                        modifier = Modifier
                            .background(if (isActive) Color.White.copy(alpha = 0.25f) else Color(0xFF9333EA), CircleShape)
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(count.toString(), color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

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
private fun NotificationRow(notification: NotificationItem, onClick: (NotificationItem) -> Unit) {
    Surface(
        onClick = { onClick(notification) },
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp),
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        border = if (notification.isUnread) BorderStroke(2.dp, Color(0xFFF3E8FF)) else BorderStroke(1.dp, Color(0xFFF1F5F9)),
        shadowElevation = 2.dp
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
                        modifier = Modifier.size(48.dp).clip(CircleShape).border(2.dp, Color.White, CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Box(
                        modifier = Modifier.size(24.dp).offset(x = 4.dp, y = 4.dp).clip(CircleShape).background(notification.iconBg).padding(4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(notification.icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                    }
                } else {
                    Box(
                        modifier = Modifier.size(48.dp).clip(RoundedCornerShape(14.dp)).background(notification.iconBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(notification.icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                    Text(notification.title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B), modifier = Modifier.weight(1f))
                    if (notification.isUnread) {
                        Box(modifier = Modifier.size(8.dp).background(Color(0xFF9333EA), CircleShape).offset(y = 6.dp))
                    }
                }
                Text(notification.description, fontSize = 14.sp, color = Color(0xFF64748B), modifier = Modifier.padding(top = 2.dp))
                Text(notification.time, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color(0xFF94A3B8), modifier = Modifier.padding(top = 8.dp))

                if (notification.actionable) {
                    Row(modifier = Modifier.padding(top = 12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { },
                            modifier = Modifier.weight(1f).height(36.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                            contentPadding = PaddingValues()
                        ) {
                            Box(modifier = Modifier.fillMaxSize().background(notification.gradient), contentAlignment = Alignment.Center) {
                                Text(if (notification.type == NotificationType.Invite) "Accept" else "Follow Back", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        if (notification.type == NotificationType.Invite) {
                            Button(
                                onClick = { },
                                modifier = Modifier.weight(0.5f).height(36.dp),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF1F5F9))
                            ) {
                                Text("Decline", color = Color(0xFF64748B), fontSize = 13.sp, fontWeight = FontWeight.Bold)
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
        modifier = Modifier.padding(horizontal = 20.dp).fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(modifier = Modifier.background(Brush.linearGradient(listOf(Color(0xFF9333EA), Color(0xFFDB2777)))).padding(24.dp)) {
            Column {
                Text("This Week's Activity", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Row(modifier = Modifier.padding(top = 16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    StatMetric("12", "New Invites")
                    StatMetric("28", "New Followers")
                    StatMetric("64", "Reactions")
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

@Preview
@Composable
fun NotificationsPreview(){
    Notifications({},{})
}



/*enum class AlertVariant {
    Default,
    Destructive
}

@Composable
fun Alert(
    variant: AlertVariant = AlertVariant.Default,
    title: String? = null,
    description: String? = null,
    icon: @Composable (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when (variant) {
        AlertVariant.Default -> MaterialTheme.colorScheme.surface
        AlertVariant.Destructive -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.1f)
    }
    
    val borderColor = when (variant) {
        AlertVariant.Default -> MaterialTheme.colorScheme.outlineVariant
        AlertVariant.Destructive -> MaterialTheme.colorScheme.error.copy(alpha = 0.5f)
    }

    val textColor = when (variant) {
        AlertVariant.Default -> MaterialTheme.colorScheme.onSurface
        AlertVariant.Destructive -> MaterialTheme.colorScheme.error
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, borderColor, RoundedCornerShape(8.dp))
            .background(backgroundColor, RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (icon != null) {
                Box(modifier = Modifier.size(16.dp).offset(y = 2.dp)) {
                    icon()
                }
            }
            
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                if (title != null) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Medium,
                        color = textColor,
                        lineHeight = 16.sp
                    )
                }
                if (description != null) {
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (variant == AlertVariant.Destructive) textColor.copy(alpha = 0.9f) else MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 20.sp
                    )
                }
            }
        }
    }
}

// Add these Previews to the end of your Notifications.kt file

@Preview(showBackground = true, name = "Default Alert")
@Composable
fun AlertPreviewDefault() {
    // It's a good practice to wrap your component in your app's theme
    // YourAppTheme {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Alert(
            variant = AlertVariant.Default,
            icon = {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Information",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            },
            title = "Heads up!",
            description = "This is a default alert. It can be used to show important information to the user."
        )
    }
    // }
}

@Preview(showBackground = true, name = "Destructive Alert")
@Composable
fun AlertPreviewDestructive() {
    // YourAppTheme {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Alert(
            variant = AlertVariant.Destructive,
            icon = {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Warning",
                    tint = MaterialTheme.colorScheme.error // Match the icon tint
                )
            },
            title = "Something went wrong",
            description = "This is a destructive alert. Use it to notify the user about an error or a critical issue."
        )
    }
    // }
}

@Preview(showBackground = true, name = "Alert with Title Only")
@Composable
fun AlertPreviewTitleOnly() {
    // YourAppTheme {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Alert(
            variant = AlertVariant.Default,
            icon = {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Information",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            },
            title = "Your profile has been updated."
        )
    }
    // }
}*/

