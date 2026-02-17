package com.shivam.downn.ui.screens.chat

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil.compose.AsyncImage
import com.shivam.downn.utils.DateUtils

data class Message(
    val id: Int,
    val userId: Int,
    val userName: String,
    val avatar: String,
    val text: String,
    val timestamp: String,
    val isCurrentUser: Boolean = false
)

data class QuickReply(
    val id: Int,
    val icon: ImageVector? = null,
    val text: String
)

@Composable
fun GroupChatRoute(
    activityId: Long,
    innerPadding: PaddingValues,
    socialTitle: String,
    categoryIcon: @Composable () -> Unit,
    categoryColor: Color,
    participantCount: Int,
    onClose: () -> Unit,
    viewModel: ChatViewModel = hiltViewModel()
) {
    val messages by viewModel.messages.collectAsState()
    val currentProfileId by viewModel.currentProfileId.collectAsState()

    // Connect to chat on enter, disconnect on leave
    DisposableEffect(activityId) {
        viewModel.connectToChat(activityId)
        onDispose {
            viewModel.disconnectFromChat()
        }
    }

    GroupChatContent(
        activityId = activityId,
        innerPadding = innerPadding,
        socialTitle = socialTitle,
        categoryIcon = categoryIcon,
        categoryColor = categoryColor,
        participantCount = participantCount,
        messages = messages,
        currentProfileId = currentProfileId ?: -1L,
        onSendMessage = { content -> viewModel.sendMessage(activityId, content) },
        onClose = onClose
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupChatContent(
    activityId: Long,
    innerPadding: PaddingValues,
    socialTitle: String,
    categoryIcon: @Composable () -> Unit,
    categoryColor: Color,
    participantCount: Int,
    messages: List<com.shivam.downn.data.models.ChatMessageResponse>,
    currentProfileId: Long,
    onSendMessage: (String) -> Unit,
    onClose: () -> Unit
) {
    var messageText by remember { mutableStateOf("") }
    val context = LocalContext.current
    val listState = rememberLazyListState()
    
    // Auto-scroll to bottom when new messages arrive
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    val quickReplies = listOf(
        QuickReply(1, Icons.Default.Place, "Share location"),
        QuickReply(2, Icons.Default.Schedule, "I'm on my way!"),
        QuickReply(3, null, "Running 5 min late"),
        QuickReply(4, null, "I'm here! 👋"),
    )

    Scaffold(
        topBar = {
            Column(modifier = Modifier.background(Color(0xFF0F172A))) {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Box(
                                modifier = Modifier.size(40.dp).clip(RoundedCornerShape(12.dp)).background(categoryColor),
                                contentAlignment = Alignment.Center
                            ) {
                                categoryIcon()
                            }
                            Column {
                                Text(socialTitle, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Icon(Icons.Default.Groups, contentDescription = null, modifier = Modifier.size(12.dp), tint = Color(0xFF94A3B8))
                                    Text("$participantCount members", color = Color(0xFF94A3B8), fontSize = 12.sp)
                                }
                            }
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onClose) {
                            Icon(Icons.Default.ChevronLeft, contentDescription = "Back", tint = Color.White)
                        }
                    },
                    actions = {
                        IconButton(onClick = { }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "More", tint = Color(0xFF94A3B8))
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0F172A))
                )
                // Pinned Move Info
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Brush.horizontalGradient(listOf(Color(0xFF581C87).copy(alpha = 0.3f), Color(0xFF831843).copy(alpha = 0.3f))))
                        .border(1.dp, Color(0xFFA855F7).copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                        .padding(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.PushPin, contentDescription = null, tint = Color(0xFFD8B4FE), modifier = Modifier.size(16.dp).offset(y = 2.dp))
                        Column {
                            Text("Move Details", color = Color(0xFFD8B4FE), fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 4.dp)) {
                                Icon(Icons.Default.Schedule, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color(0xFFCBD5E1))
                                Text("Today at 3:00 PM", color = Color(0xFFCBD5E1), fontSize = 12.sp)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 2.dp)) {
                                Icon(Icons.Default.Place, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color(0xFFCBD5E1))
                                Text("Downtown Café, 0.3 mi away", color = Color(0xFFCBD5E1), fontSize = 12.sp)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Divider(color = Color(0xFF334155).copy(alpha = 0.5f), thickness = 0.5.dp)
            }
        },
        bottomBar = {
            Column(modifier = Modifier.padding(innerPadding).background(Color(0xFF0F172A))) {
                HorizontalDivider(thickness = 0.5.dp, color = Color(0xFF334155).copy(alpha = 0.5f))
                // Quick Replies
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(quickReplies) { reply ->
                        Row(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(Brush.horizontalGradient(listOf(Color(0xFF9333EA).copy(alpha = 0.2f), Color(0xFFDB2777).copy(alpha = 0.2f))))
                                .border(1.dp, Color(0xFFA855F7).copy(alpha = 0.3f), CircleShape)
                                .clickable { messageText = reply.text }
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            if (reply.icon != null) {
                                Icon(reply.icon, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color(0xFFD8B4FE))
                            }
                            Text(reply.text, color = Color(0xFFD8B4FE), fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }
                // Message Input
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = { },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF1E293B))
                    ) {
                        Icon(Icons.Default.Image, contentDescription = "Add Image", tint = Color(0xFFCBD5E1))
                    }
                    OutlinedTextField(
                        value = messageText,
                        onValueChange = { messageText = it },
                        placeholder = {
                            Text("Type a message...", color = Color(0xFF64748B))
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(24.dp),
                        trailingIcon = {
                            IconButton(onClick = { }) {
                                Icon(
                                    Icons.Default.EmojiEmotions,
                                    contentDescription = "Emoji",
                                    tint = Color(0xFF94A3B8)
                                )
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFF1E293B),
                            unfocusedContainerColor = Color(0xFF1E293B),
                            focusedBorderColor = Color(0xFFA855F7),
                            unfocusedBorderColor = Color(0xFF334155),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            cursorColor = Color.White
                        )
                    )

                    IconButton(
                        onClick = { 
                            if (messageText.isNotBlank()) {
                                onSendMessage(messageText)
                                messageText = "" 
                            }
                        },
                        enabled = messageText.isNotBlank(),
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .then(
                                if (messageText.isNotBlank()) Modifier.background(Brush.horizontalGradient(listOf(Color(0xFF9333EA), Color(0xFFDB2777))))
                                else Modifier.background(Color(0xFF1E293B))
                            )
                    ) {
                        Icon(Icons.Default.Send, contentDescription = "Send", tint = if (messageText.isNotBlank()) Color.White else Color(0xFF475569))
                    }
                }
            }
        },
        containerColor = Color(0xFF020617)
    ) { padding ->
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize().padding(padding),
            reverseLayout = false,
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(messages) { msg ->
                val isCurrentUser = msg.profileId == currentProfileId
                ChatMessageItem(
                    Message(
                        id = msg.id.toInt(),
                        userId = msg.profileId.toInt(),
                        userName = msg.profileName,
                        avatar = msg.profileAvatar ?: "",
                        text = msg.content,
                        timestamp = DateUtils.formatEventTime(msg.createdAt), // Or use a proper chat time formatter
                        isCurrentUser = isCurrentUser
                    )
                )
            }
        }
    }
}

@Composable
private fun ChatMessageItem(msg: Message) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (msg.isCurrentUser) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Bottom
    ) {
        if (!msg.isCurrentUser) {
            AsyncImage(
                model = msg.avatar,
                contentDescription = msg.userName,
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .border(2.dp, Color(0xFF334155), CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        Column(horizontalAlignment = if (msg.isCurrentUser) Alignment.End else Alignment.Start) {
            if (!msg.isCurrentUser) {
                Text(msg.userName, color = Color(0xFF94A3B8), fontSize = 12.sp, fontWeight = FontWeight.Medium, modifier = Modifier.padding(start = 4.dp, bottom = 4.dp))
            }
            Surface(
                shape = RoundedCornerShape(
                    topStart = 16.dp, 
                    topEnd = 16.dp, 
                    bottomStart = if (msg.isCurrentUser) 16.dp else 4.dp, 
                    bottomEnd = if (msg.isCurrentUser) 4.dp else 16.dp
                ),
                color = if (msg.isCurrentUser) Color.Transparent else Color(0xFF1E293B),
                border = if (!msg.isCurrentUser) BorderStroke(1.dp, Color(0xFF334155).copy(alpha = 0.5f)) else null,
                modifier = if (msg.isCurrentUser) Modifier.background(Brush.horizontalGradient(listOf(Color(0xFF9333EA), Color(0xFFDB2777))), RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 4.dp)) else Modifier
            ) {
                Text(
                    text = msg.text,
                    color = Color.White,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                )
            }
            Text(msg.timestamp, color = Color(0xFF475569), fontSize = 10.sp, modifier = Modifier.padding(top = 4.dp, start = 4.dp, end = 4.dp))
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, backgroundColor = 0xFF020617)
@Composable
fun GroupChatPreview() {
    GroupChatContent(
        activityId = 1L,
        innerPadding = PaddingValues(0.dp),
        socialTitle = "Downtown Coffee Meetup",
        categoryIcon = {
            // Using an emoji as a simple icon for the preview
            Text("☕️", fontSize = 20.sp)
        },
        categoryColor = Color(0xFFFBBF24).copy(alpha = 0.2f),
        participantCount = 12,
        messages = emptyList(),
        currentProfileId = 1L,
        onSendMessage = {},
        onClose = {}
    )
}
