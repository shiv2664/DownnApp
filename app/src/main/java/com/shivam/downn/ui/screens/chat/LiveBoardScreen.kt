package com.shivam.downn.ui.screens.chat

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil.compose.AsyncImage
import com.shivam.downn.data.models.ChatMessageResponse
import com.shivam.downn.utils.DateUtils
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveBoardScreen(
    socialId: Int,
    socialTitle: String,
    businessName: String, // Or owner name
    businessAvatar: String,
    isOwner: Boolean,
    onClose: () -> Unit,
    viewModel: ChatViewModel = hiltViewModel()
) {
    val messages by viewModel.messages.collectAsState()
    val scrollState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    // Connect to chat socket
    DisposableEffect(socialId) {
        viewModel.connectToChat(socialId.toLong())
        onDispose {
            viewModel.disconnectFromChat()
        }
    }

    // Auto-scroll on new message
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            scrollState.animateScrollToItem(messages.size - 1)
        }
    }

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
        },
        bottomBar = {
            if (isOwner) {
                // Determine category color based on something? Or just hardcode business orange
                val categoryColor = Color(0xFFF97316)
                BroadcastInput(
                    onSendMessage = { content ->
                        viewModel.sendMessage(socialId.toLong(), content)
                    },
                    accentColor = categoryColor
                )
            } else {
                 Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF1E293B))
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Only the Venue can post updates to Live Board",
                        color = Color(0xFF94A3B8),
                        fontSize = 12.sp
                    )
                }
            }
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
                    model = com.shivam.downn.utils.ImageUtils.getFullImageUrl(businessAvatar),
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
                        text = "Official Updates Channel",
                        color = Color(0xFFF97316),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Icon(
                    imageVector = Icons.Default.Campaign, // Notification/Campaign icon
                    contentDescription = null,
                    tint = Color(0xFFF97316)
                )
            }

            // Messages List
            LazyColumn(
                state = scrollState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                if (messages.isEmpty()) {
                    item {
                        Text(
                            "No updates yet.",
                            color = Color(0xFF64748B),
                            fontSize = 14.sp,
                            modifier = Modifier.padding(top = 20.dp)
                        )
                    }
                }
                
                items(messages) { msg ->
                    BoardMessageItem(msg)
                }
            }
        }
    }
}

@Composable
fun BroadcastInput(
    onSendMessage: (String) -> Unit,
    accentColor: Color
) {
    var text by remember { mutableStateOf("") }
    val maxLength = 500
    
    Column(modifier = Modifier.background(Color(0xFF0F172A))) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = text,
                onValueChange = { if (it.length <= maxLength) text = it },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(24.dp),
                placeholder = { Text("Broadcast an update...", color = Color(0xFF64748B)) },
                supportingText = {
                    Text(
                        text = "${text.length} / $maxLength",
                        color = if (text.length >= maxLength) Color.Red else Color(0xFF64748B),
                        fontSize = 10.sp,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = androidx.compose.ui.text.style.TextAlign.End
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFF1E293B),
                    unfocusedContainerColor = Color(0xFF1E293B),
                    focusedBorderColor = accentColor,
                    unfocusedBorderColor = Color(0xFF334155),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            IconButton(
                onClick = {
                    if (text.isNotBlank()) {
                        onSendMessage(text)
                        text = ""
                    }
                },
                modifier = Modifier
                    .size(48.dp)
                    .background(if (text.isNotBlank()) accentColor else Color(0xFF1E293B), CircleShape)
            ) {
                Icon(
                    Icons.Default.Send,
                    contentDescription = "Send",
                    tint = if (text.isNotBlank()) Color.White else Color(0xFF64748B)
                )
            }
        }
    }
}

@Composable
fun BoardMessageItem(msg: ChatMessageResponse) {
    Row(modifier = Modifier.fillMaxWidth()) {
        // Timeline indicator
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF97316)) // Orange for updates
            )
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .height(60.dp) // Minimum height line
                    .background(Color(0xFF1E293B))
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Message Card
        Column {
            Text(
                text = DateUtils.formatEventTime(msg.createdAt), // Use util
                color = Color(0xFF94A3B8),
                fontSize = 12.sp,
                modifier = Modifier.padding(bottom = 6.dp)
            )
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF1E293B)
                ),
                border = BorderStroke(
                    width = 1.dp,
                    color = Color(0xFFF97316).copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(topStart = 0.dp, topEnd = 16.dp, bottomEnd = 16.dp, bottomStart = 16.dp)
            ) {
                Text(
                    text = msg.content,
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
    LiveBoardScreen(1, "Live Jazz Night 🎷", "The Daily Grind", "", true, {})
}
