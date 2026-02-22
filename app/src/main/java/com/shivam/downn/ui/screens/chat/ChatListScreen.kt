package com.shivam.downn.ui.screens.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.shivam.downn.data.models.ChatListResponse
import com.shivam.downn.data.network.NetworkResult
import com.shivam.downn.utils.ImageUtils

@Composable
fun ChatListRoute(
    navController: NavController,
    viewModel: ChatViewModel = hiltViewModel()
) {
    val chatListState by viewModel.chatList.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadMyChats()
    }

    ChatListContent(
        chatListState = chatListState,
        onBackClick = { navController.navigateUp() },
        onChatClick = { chat ->
            if (chat.isBusiness) {
                val encodedTitle = java.net.URLEncoder.encode(
                    chat.title,
                    java.nio.charset.StandardCharsets.UTF_8.toString()
                )
                val bName = chat.businessName ?: ""
                val encodedName = java.net.URLEncoder.encode(
                    bName,
                    java.nio.charset.StandardCharsets.UTF_8.toString()
                )
                val bAvatar = chat.businessAvatar ?: ""
                val encodedAvatar = java.net.URLEncoder.encode(
                    bAvatar,
                    java.nio.charset.StandardCharsets.UTF_8.toString()
                )

                navController.navigate("live_board/${chat.activityId}?title=$encodedTitle&businessName=$encodedName&businessAvatar=$encodedAvatar&isOwner=${chat.isOwner}")
            } else {
                navController.navigate("group_chat/${chat.activityId}?title=${chat.title}")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatListContent(
    chatListState: NetworkResult<List<ChatListResponse>>?,
    onBackClick: () -> Unit,
    onChatClick: (ChatListResponse) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chats", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0F172A))
            )
        },
        containerColor = Color(0xFF020617)
    ) { padding ->
        Box(modifier = Modifier
            .padding(padding)
            .fillMaxSize()) {
            when (val result = chatListState) {
                is NetworkResult.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                is NetworkResult.Error -> {
                    Text(
                        text = result.message ?: "Failed to load chats",
                        color = Color.Red,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                is NetworkResult.Success -> {
                    val chats = result.data ?: emptyList()
                    if (chats.isEmpty()) {
                        Text(
                            text = "No active chats. Join a move to start chatting!",
                            color = Color.Gray,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    } else {
                        LazyColumn {
                            items(chats) { chat ->
                                ChatListItem(chat) {
                                    onChatClick(chat)
                                }
                                HorizontalDivider(color = Color(0xFF334155), thickness = 0.5.dp)
                            }
                        }
                    }
                }
                else -> {}
            }
        }
    }
}

@Composable
fun ChatListItem(chat: ChatListResponse, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val imageUrl = ImageUtils.getFullImageUrl(chat.businessAvatar)
        AsyncImage(
            model = imageUrl.takeIf { it.isNotEmpty() },
            placeholder = rememberVectorPainter(Icons.Default.Groups),
            error = rememberVectorPainter(Icons.Default.Groups),
            fallback = rememberVectorPainter(Icons.Default.Groups),
            contentDescription = null,
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .then(
                    if (chat.isBusiness) Modifier.border(2.dp, Color(0xFFF97316), CircleShape)
                    else Modifier
                )
                .background(Color.DarkGray),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = chat.title,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = chat.lastMessage ?: "No messages yet",
                color = Color(0xFF94A3B8),
                fontSize = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChatListContentPreview() {
    val dummyChats = listOf(
        ChatListResponse(
            activityId = 1,
            title = "Weekend Hike to Mountains",
            image = "",
            lastMessage = "See you all tomorrow! 🏔️",
            isBusiness = false,
            lastMessageTime = ""
        ),
        ChatListResponse(
            activityId = 2,
            title = "Friday Night Live Jazz 🎷",
            image = "https://example.com/image.jpg",
            lastMessage = "What time does it start?",
            isBusiness = true,
            businessName = "The Daily Grind",
            businessAvatar = "",
            lastMessageTime = ""
        ),
        ChatListResponse(
            activityId = 3,
            title = "Tech Meetup Denver",
            image = "",
            lastMessage = "I am bringing some swag.",
            isBusiness = false,
            lastMessageTime = ""
        )
    )

    MaterialTheme {
        ChatListContent(
            chatListState = NetworkResult.Success(dummyChats),
            onChatClick = {},
            onBackClick = {}
        )
    }
}
