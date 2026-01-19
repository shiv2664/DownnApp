package com.shivam.downn.react

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Coffee
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import com.shivam.downn.data.models.ActivityResponse

data class Participant(
    val id: Int,
    val name: String,
    val avatar: String
)

data class ChatPreview(
    val id: Int,
    val user: String,
    val message: String,
    val time: String
)

@Composable
fun ActivityDetail(
    outerPadding: PaddingValues,
    activity: ActivityResponse,
    onClose: () -> Unit,
    onOpenChat: () -> Unit
) {

    val title = activity.title
    val userName = activity.userName ?: "Unknown"
    val userAvatar = activity.userAvatar ?: ""
    val distance = activity.distance ?: "Nearby"
    val participantCount = activity.participantCount
    val categoryIcon = @Composable {
        Icon(
            imageVector = Icons.Outlined.Coffee,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(20.dp)
        )
    }
    val categoryColor = Color(0xFFA855F7)
    val images = emptyList<String>() // You can add images from API if available
    val description: String? = activity.description

    val headerImage =
        images.firstOrNull() ?: "https://images.unsplash.com/photo-1668884405041-aa8963908538"
    val participants = listOf(
        Participant(1, "Sarah K.", "https://images.unsplash.com/photo-1638996030249-abc99a735463"),
        Participant(2, "Mike R.", "https://images.unsplash.com/photo-1567516364473-233c4b6fcfbe"),
        Participant(
            3,
            "Emma L.",
            "https://images.unsplash.com/flagged/photo-1596479042555-9265a7fa7983"
        ),
    )
    val chats = listOf(
        ChatPreview(1, "Sarah K.", "Can't wait for this! 🎉", "2m ago"),
        ChatPreview(2, "Mike R.", "Should I bring anything?", "5m ago"),
    )

    Scaffold() { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFF0F172A))
        )
        {

            Box {
                val scrollState = rememberScrollState()

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(bottom = 100.dp),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally

                )
                {
                    // Header Image
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                    )
                    {
                        AsyncImage(
                            model = headerImage,
                            contentDescription = title,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )

                        // Category Badge
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(bottom = 24.dp, end = 20.dp)
                                .size(56.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(categoryColor),
                            contentAlignment = Alignment.Center
                        ) {
                            categoryIcon()
                        }
                    }

                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                    )


                    // Title section
                    Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 24.dp)) {
                        Text(
                            title,
                            color = Color.White,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Row(
                            modifier = Modifier.padding(top = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            InfoChip(
                                Icons.Default.Schedule,
                                activity.scheduledTime,
                                Color(0xFFA855F7)
                            )
                            InfoChip(Icons.Default.Place, distance, Color(0xFF3B82F6))
                        }
                    }

                    // Host Profile
                    Card(
                        modifier = Modifier.padding(horizontal = 20.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF1E293B).copy(
                                alpha = 0.5f
                            )
                        ),
                        border = BorderStroke(1.dp, Color(0xFF334155).copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                "HOSTED BY",
                                color = Color(0xFF94A3B8),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                            Row(
                                modifier = Modifier.padding(top = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                AsyncImage(
                                    model = userAvatar,
                                    contentDescription = userName,
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .border(
                                            2.dp,
                                            Color(0xFFA855F7).copy(alpha = 0.5f),
                                            CircleShape
                                        ),
                                    contentScale = ContentScale.Crop
                                )
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        userName,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                    Text(
                                        "Member since 2024",
                                        color = Color(0xFF94A3B8),
                                        fontSize = 12.sp
                                    )
                                }
                                Button(
                                    onClick = { },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(
                                            0xFF334155
                                        ).copy(alpha = 0.5f)
                                    ),
                                    shape = RoundedCornerShape(100.dp),
                                    contentPadding = PaddingValues(
                                        horizontal = 16.dp,
                                        vertical = 8.dp
                                    )
                                ) {
                                    Text(
                                        "View Profile",
                                        color = Color(0xFFE2E8F0),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }

                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                    )

                    // About
                    if (activity.description != "") {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text(
                                "About",
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                description
                                    ?: "Join us for an amazing time! This is going to be a great opportunity to meet new people and have fun together. Everyone is welcome! 🌟",
                                color = Color(0xFFCBD5E1),
                                fontSize = 16.sp,
                                lineHeight = 24.sp,
                                modifier = Modifier.padding(top = 12.dp)
                            )
                        }
                        Spacer(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(10.dp)
                        )
                    }


                    // Participants
                    Column(modifier = Modifier.padding(bottom = 24.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Participants ($participantCount)",
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "See all",
                                color = Color(0xFFA855F7),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 20.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(participants) { participant ->
                                AvatarsItem(participant.avatar, participant.name)
                            }
                        }
                    }

                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                    )

                    // Map Preview Placeholder
                    Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)) {
                        Text(
                            "Location",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Box(
                            modifier = Modifier
                                .padding(top = 16.dp)
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(24.dp))
                                .background(
                                    Brush.linearGradient(
                                        listOf(
                                            Color(0xFF1E3A8A).copy(alpha = 0.5f),
                                            Color(0xFF064E3B).copy(alpha = 0.5f)
                                        )
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = Color(0xFF1E293B),
                                border = BorderStroke(1.dp, Color(0xFF334155))
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Place,
                                        contentDescription = null,
                                        tint = Color(0xFFF87171)
                                    )
                                    Column {
                                        Text(
                                            text = activity.locationName,
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        )
                                        Text(distance, color = Color(0xFF94A3B8), fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }

                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                    )

                    // Chat Preview
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Group Chat",
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "${chats.size} messages",
                                color = Color(0xFF94A3B8),
                                fontSize = 12.sp
                            )
                        }
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(24.dp))
                                .background(Color(0xFF1E293B).copy(alpha = 0.5f))
                                .border(1.dp, Color(0xFF334155).copy(alpha = 0.5f))
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            chats.forEach { chat ->
                                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape)
                                            .background(
                                                Brush.linearGradient(
                                                    listOf(
                                                        Color(0xFF9333EA),
                                                        Color(0xFFDB2777)
                                                    )
                                                )
                                            )
                                    )
                                    Column {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Text(
                                                chat.user,
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp
                                            )
                                            Text(
                                                chat.time,
                                                color = Color(0xFF94A3B8),
                                                fontSize = 12.sp
                                            )
                                        }
                                        Text(
                                            chat.message,
                                            color = Color(0xFFCBD5E1),
                                            fontSize = 14.sp,
                                            modifier = Modifier.padding(top = 2.dp)
                                        )
                                    }
                                }
                            }
                            TextButton(
                                onClick = { },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    "View all messages",
                                    color = Color(0xFFA855F7),
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                }

                /*Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color.Black.copy(alpha = 0.4f),
                                    Color.Transparent,
                                    Color(0xFF0F172A)
                                )
                            )
                        )
                )*/

                // Top Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color.Black.copy(alpha = 0.6f),
                                    Color.Transparent
                                )
                            )
                        )
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                )
                {
                    IconButton(
                        onClick = onClose,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f))
                    ) {
                        Icon(
                            Icons.Default.ChevronLeft,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                    IconButton(
                        onClick = { },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f))
                    ) {
                        Icon(
                            Icons.Outlined.Share,
                            contentDescription = "Share",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Bottom Actions
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .background(Color(0xFF0F172A).copy(alpha = 0.95f))
                        .border(
                            0.5.dp,
                            Color(0xFF334155).copy(alpha = 0.5f),
                            RoundedCornerShape(
                                topStart = 0.dp,
                                topEnd = 0.dp,
                                bottomStart = 0.dp,
                                bottomEnd = 0.dp
                            )
                        )
                        .padding(
                            vertical = 20.dp
                        )
                )
                {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Button(
                            onClick = { onOpenChat() },
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                            border = BorderStroke(2.dp, Color(0xFFA855F7))
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    Icons.Default.ChatBubble,
                                    contentDescription = null,
                                    tint = Color(0xFFA855F7)
                                )
                                Text(
                                    "CHAT",
                                    color = Color(0xFFA855F7),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Button(
                            onClick = { },
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                            contentPadding = PaddingValues()
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(
                                                Color(0xFF9333EA),
                                                Color(0xFFDB2777)
                                            )
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "JOIN ACTIVITY",
                                    color = Color.White,
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
private fun InfoChip(
    icon: ImageVector = Icons.Default.Schedule,
    text: String = "Today 3:00 PM",
    color: Color = Color(0xFFA855F7)
) {
    Row(
        modifier = Modifier
            .clip(CircleShape)
            .background(color.copy(alpha = 0.2f))
            .border(1.dp, color.copy(alpha = 0.3f), CircleShape)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            icon,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = color.copy(alpha = 0.8f)
        )
        Text(
            text,
            color = color.copy(alpha = 0.8f),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun AvatarsItem(avatar: String = "", name: String = "") {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(64.dp)
    )
    {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        listOf(
                            Color(0xFF9333EA),
                            Color(0xFFDB2777)
                        )
                    )
                )
                .padding(2.dp)
        ) {
            AsyncImage(
                model = avatar,
                contentDescription = name,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .border(2.dp, Color(0xFF0F172A), CircleShape),
                contentScale = ContentScale.Crop
            )
        }
        Text(
            name.split(" ")[0],
            color = Color(0xFFCBD5E1),
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}


@Preview(showBackground = true, backgroundColor = 0xFF0F172A)
@Composable
fun ActivityDetailPreview() {
    ActivityDetail(
        outerPadding = PaddingValues(0.dp),
        activity = ActivityResponse(
            title = "Coffee at Blue Tokai ☕",
            userName = "Rahul",
            userAvatar = "",
            distance = "0.8 km away",
            participantCount = 3,
            description = "Anyone up for a quick coffee and chat this evening?",
            id = 123,
            category = "",
            city = "",
            locationName = "",
            scheduledTime = "",
            maxParticipants = 10,
            timeAgo = ""
        ),
        onClose = {},
        onOpenChat = {}
    )
}
