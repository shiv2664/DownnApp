package com.shivam.downn.ui.screens.chat

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

data class BoardUpdate(
    val id: Int,
    val message: String,
    val time: String,
    val isCrucial: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveBoardScreen(
    socialId: Int,
    socialTitle: String,
    businessName: String,
    businessAvatar: String,
    onClose: () -> Unit
) {
    // Mock updates for the live board
    val updates = listOf(
        BoardUpdate(1, "The band is starting in 10 minutes! Grab your drinks 🎷", "Just now", true),
        BoardUpdate(2, "Happy Hour extended till 9 PM for all Move attendees!", "15m ago", false),
        BoardUpdate(3, "We just opened up some extra seating near the stage.", "45m ago", false),
        BoardUpdate(4, "Welcome everyone! Hope you have a great Jazz night.", "1h ago", false)
    )

    Scaffold(
        containerColor = Color(0xFF0F172A),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Live Board",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = socialTitle,
                            fontSize = 12.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1E293B))
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Business Info Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF1E293B))
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = businessAvatar,
                    contentDescription = null,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = businessName,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = null,
                            tint = Color(0xFF3B82F6),
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    Text(
                        text = "Broadcast Mode Only",
                        color = Color(0xFFF97316),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = null,
                    tint = Color(0xFF94A3B8)
                )
            }

            // Timeline
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                items(updates) { update ->
                    BoardUpdateItem(update)
                }
                
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Only the Venue can post updates to Live Board",
                            color = Color(0xFF475569),
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BoardUpdateItem(update: BoardUpdate) {
    Row(modifier = Modifier.fillMaxWidth()) {
        // Timeline indicator
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(if (update.isCrucial) Color(0xFFF97316) else Color(0xFF334155))
            )
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .height(60.dp)
                    .background(Color(0xFF1E293B))
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Message Card
        Column {
            Text(
                text = update.time,
                color = Color(0xFF94A3B8),
                fontSize = 12.sp,
                modifier = Modifier.padding(bottom = 6.dp)
            )
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (update.isCrucial) Color(0xFFF97316).copy(alpha = 0.1f) else Color(0xFF1E293B)
                ),
                border = BorderStroke(
                    width = 1.dp,
                    color = if (update.isCrucial) Color(0xFFF97316).copy(alpha = 0.5f) else Color(0xFF334155).copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(topStart = 0.dp, topEnd = 16.dp, bottomEnd = 16.dp, bottomStart = 16.dp)
            ) {
                Text(
                    text = update.message,
                    color = Color.White,
                    fontSize = 15.sp,
                    modifier = Modifier.padding(16.dp),
                    lineHeight = 22.sp
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewLiveBoard() {
    LiveBoardScreen(1, "Live Jazz Night 🎷", "The Daily Grind", "", {})
}
