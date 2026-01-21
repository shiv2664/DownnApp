package com.shivam.downn.ui.screens.profile

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
import com.shivam.downn.data.models.SocialResponse
import com.shivam.downn.data.models.SocialType
import com.shivam.downn.ui.screens.home.MoveCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BusinessProfileScreen(
    businessId: Int,
    onClose: () -> Unit,
    onMoveClick: (Int) -> Unit
) {
    // Mock data for business
    val businessName = if (businessId == 16) "The Daily Grind" else "Club Social"
    val businessAvatar = if (businessId == 16) 
        "https://images.unsplash.com/photo-1509042239860-f550ce710b93?w=400" 
    else "https://images.unsplash.com/photo-1566737236500-c8ac1f852382?w=400"
    
    val coverImage = if (businessId == 16)
        "https://images.unsplash.com/photo-1495474472287-4d71bcdd2085?w=800"
    else "https://images.unsplash.com/photo-1514525253361-bee1a95e792e?w=800"

    Scaffold(
        containerColor = Color(0xFF0F172A),
        topBar = {
            TopAppBar(
                title = { Text("") },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 16.dp)
        ) {
            item {
                BusinessHeader(coverImage, businessAvatar, businessName)
            }

            item {
                BusinessStats()
            }

            item {
                BusinessActions()
            }

            item {
                VibeSection()
            }

            item {
                Text(
                    text = "Active Moves",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(16.dp)
                )
            }

            // Mock active moves
            val activeMoves = listOf(
                SocialResponse(
                    id = 101,
                    title = if (businessId == 16) "Live Jazz Night 🎷" else "Friday Night Fever 🕺",
                    description = "Special event tonight!",
                    category = "Events",
                    city = "Delhi",
                    locationName = businessName,
                    scheduledTime = "2026-01-21T20:00:00",
                    maxParticipants = 100,
                    participantCount = 45,
                    userName = businessName,
                    userAvatar = businessAvatar,
                    socialType = SocialType.BUSINESS,
                    timeAgo = "Just now",
                    distance = "0.5 km away"
                )
            )

            items(activeMoves) { move ->
                MoveCard(
                    userName = move.userName ?: "",
                    userAvatar = move.userAvatar ?: "",
                    moveTitle = move.title,
                    description = move.description ?: "",
                    category = move.category,
                    categoryEmoji = "🔥",
                    timeAgo = move.timeAgo ?: "",
                    distance = move.distance ?: "",
                    participantCount = move.participantCount,
                    maxParticipants = move.maxParticipants,
                    socialType = move.socialType,
                    onCardClick = { onMoveClick(move.id) },
                    onJoinClick = {}
                )
            }
        }
    }
}

@Composable
fun BusinessHeader(coverImage: String, avatar: String, name: String) {
    Box(modifier = Modifier.fillMaxWidth().height(280.dp)) {
        AsyncImage(
            model = coverImage,
            contentDescription = null,
            modifier = Modifier.fillMaxWidth().height(200.dp),
            contentScale = ContentScale.Crop
        )
        
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 20.dp, bottom = 20.dp)
        ) {
            Column {
                AsyncImage(
                    model = avatar,
                    contentDescription = null,
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .border(4.dp, Color(0xFF0F172A), CircleShape),
                    contentScale = ContentScale.Crop
                )
                
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 8.dp)) {
                    Text(
                        text = name,
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.Default.Verified,
                        contentDescription = "Verified",
                        tint = Color(0xFF3B82F6),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun BusinessStats() {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        StatItem("4.8k", "Regulars")
        StatItem("12", "Active Moves")
        StatItem("4.9", "Vibe Rating")
    }
}

@Composable
fun StatItem(value: String, label: String) {
    Column {
        Text(text = value, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Text(text = label, color = Color(0xFF94A3B8), fontSize = 12.sp)
    }
}

@Composable
fun BusinessActions() {
    Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ActionIconButton(Icons.Default.Directions, "Directions", Color(0xFF334155))
        ActionIconButton(Icons.Default.Phone, "Call", Color(0xFF334155))
        Button(
            onClick = {},
            modifier = Modifier.weight(1f).height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF97316)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Follow Vibe", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ActionIconButton(icon: ImageVector, label: String, color: Color) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(color),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = label, tint = Color.White)
    }
}

@Composable
fun VibeSection() {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Our Vibe", color = Color(0xFF94A3B8), fontSize = 14.sp, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            VibeTag("Cozy", Color(0xFFEC4899))
            VibeTag("Jazz", Color(0xFF8B5CF6))
            VibeTag("Craft Coffee", Color(0xFFF59E0B))
        }
    }
}

@Composable
fun VibeTag(text: String, color: Color) {
    Surface(
        color = color.copy(alpha = 0.1f),
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.5f))
    ) {
        Text(
            text = text,
            color = color,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview
@Composable
fun PreviewBusinessProfileScreen(){
    BusinessProfileScreen(1,{},{})
}
