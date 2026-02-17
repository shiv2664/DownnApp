package com.shivam.downn.ui.screens.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.shivam.downn.data.models.ChatListResponse
import com.shivam.downn.data.network.NetworkResult
import com.shivam.downn.utils.DateUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatListScreen(
    navController: NavController,
    viewModel: ChatViewModel = hiltViewModel()
) {
    val chatListState by viewModel.chatList.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadMyChats()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chats", color = Color.White, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0F172A))
            )
        },
        containerColor = Color(0xFF020617)
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
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
                                    navController.navigate("group_chat/${chat.activityId}?title=${chat.title}")
                                }
                                HorizontalDivider(color = Color(0xFF334155), thickness = 0.5.dp)
                            }
                        }
                    }
                }
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
        AsyncImage(
            model = chat.image ?: "https://via.placeholder.com/150",
            contentDescription = null,
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
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
        
    //    if (chat.lastMessageTime != null) {
      //      Text(
        //        text = DateUtils.getTimeAgo(chat.lastMessageTime), // Assuming you have this util
          //      color = Color(0xFF64748B),
            //    fontSize = 12.sp
//            )
  //      }
    }
}
