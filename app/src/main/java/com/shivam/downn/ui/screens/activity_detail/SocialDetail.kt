package com.shivam.downn.ui.screens.activity_detail

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.shivam.downn.ui.components.FancyMap
import com.google.maps.android.compose.MarkerComposable
import com.google.maps.android.compose.rememberMarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.shivam.downn.data.models.SocialResponse
import com.shivam.downn.data.models.SocialType
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import com.shivam.downn.data.network.NetworkResult
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SocialDetail(
    social: SocialResponse,
    currentUserId: Long,
    leaveState: NetworkResult<Unit>? = null,
    removeState: NetworkResult<Unit>? = null,
    onClose: () -> Unit,
    onOpenChat: () -> Unit,
    onViewProfile: (userId: Long) -> Unit,
    onSeeAllParticipants: () -> Unit,
    onJoinSocial: (socialId: Int) -> Unit,
    onLeaveSocial: (socialId: Int) -> Unit,
    onRemoveParticipant: (socialId: Int, participantId: Long) -> Unit
) {
    val isOwner = social.userId?.toLong() == currentUserId
    val isParticipant = social.participants.any { it.id == currentUserId }
    val isRequested = social.requestedUserIds?.contains(currentUserId) == true
    val isRejected = social.rejectedUserIds?.contains(currentUserId) == true

    var showParticipantActionSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    val snackbarHostState = remember { SnackbarHostState() }

    // Handle Leave Success
    LaunchedEffect(leaveState) {
        if (leaveState is NetworkResult.Success) {
            onClose() // Close the detail screen if we leave
        } else if (leaveState is NetworkResult.Error) {
            snackbarHostState.showSnackbar(leaveState.message ?: "Failed to leave")
        }
    }

    // Handle Remove Success
    LaunchedEffect(removeState) {
        if (removeState is NetworkResult.Success) {
            snackbarHostState.showSnackbar("Participant removed")
        } else if (removeState is NetworkResult.Error) {
            snackbarHostState.showSnackbar(removeState.message ?: "Failed to remove participant")
        }
    }

    val title = social.title
    val userName = social.userName ?: "Unknown"
    val userAvatar = social.userAvatar ?: ""
    val distance = social.distance ?: "Nearby"
    val participantCount = social.participantCount
    val isBusiness = social.socialType == SocialType.BUSINESS
    val primaryColor = if (isBusiness) Color(0xFFF97316) else Color(0xFFA855F7)
    val secondaryColor = if (isBusiness) Color(0xFFFDBA74) else Color(0xFF3B82F6)
    val accentBrush = if (isBusiness) {
        Brush.horizontalGradient(listOf(Color(0xFFFDBA74), Color(0xFFF97316)))
    } else {
        Brush.horizontalGradient(listOf(Color(0xFF9333EA), Color(0xFFDB2777)))
    }

    val eventLatLng = remember(social.latitude, social.longitude) {
        if (social.latitude != null && social.longitude != null) {
            LatLng(social.latitude, social.longitude)
        } else {
            LatLng(39.7392, -104.9903) // Default to Denver if missing
        }
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(eventLatLng, 15f)
    }

    // Sync camera if coordinates change after initial load
    LaunchedEffect(eventLatLng) {
        cameraPositionState.position = CameraPosition.fromLatLngZoom(eventLatLng, 15f)
    }

    val categoryIcon = @Composable {
        Icon(
            imageVector = if (isBusiness) Icons.Default.Verified else Icons.Outlined.Coffee,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(20.dp)
        )
    }
    val categoryColor = primaryColor
    val images = emptyList<String>() // You can add images from API if available
    val description: String? = social.description

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

    Scaffold(
        containerColor = Color(0xFF0F172A),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
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

                ) {
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
                                social.scheduledTime ?: "TBD",
                                primaryColor
                            )
                            InfoChip(Icons.Default.Place, distance ?: "Nearby", secondaryColor)
                            if (isBusiness) {
                                InfoChip(Icons.Default.Star, "4.9", Color(0xFFF59E0B))
                            }
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
                                            primaryColor.copy(alpha = 0.5f),
                                            CircleShape
                                        ),
                                    contentScale = ContentScale.Crop
                                )
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text(
                                            userName,
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp
                                        )
                                        if (isBusiness) {
                                            Icon(
                                                Icons.Default.Verified,
                                                contentDescription = null,
                                                tint = Color(0xFF3B82F6),
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    }
                                    Text(
                                        if (isBusiness) "Verified Business" else "Member since 2024",
                                        color = Color(0xFF94A3B8),
                                        fontSize = 12.sp
                                    )
                                }
                                Button(
                                    onClick = { onViewProfile(1) },
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
                    if (social.description != "") {
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
                                color = primaryColor,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.clickable { onSeeAllParticipants() }
                            )
                        }
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 20.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(participants) { participant ->
                                AvatarsItem(participant.avatar, participant.name, accentBrush)
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
                                .height(if (isBusiness) 240.dp else 200.dp)
                                .clip(RoundedCornerShape(24.dp))
                                .background(Color(0xFF1E293B))
                                .border(1.dp, Color(0xFF334155), RoundedCornerShape(24.dp))
                        ) {
                            FancyMap(
                                modifier = Modifier.fillMaxSize(),
                                cameraPositionState = cameraPositionState,
                                gesturesEnabled = false
                            ) {
                                MarkerComposable(
                                    state = rememberMarkerState(
                                        key = "${eventLatLng.latitude}_${eventLatLng.longitude}",
                                        position = eventLatLng
                                    ),
                                    anchor = Offset(0.5f, 1.0f)
                                ) {
                                    Icon(
                                        Icons.Default.Place,
                                        contentDescription = null,
                                        tint = primaryColor,
                                        modifier = Modifier.size(36.dp)
                                    )
                                }
                            }

                            // Location info overlay
                            Surface(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .padding(16.dp)
                                    .fillMaxWidth(0.9f),
                                shape = RoundedCornerShape(16.dp),
                                color = Color(0xFF0F172A).copy(alpha = 0.9f),
                                border = BorderStroke(1.dp, Color(0xFF334155))
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(primaryColor.copy(alpha = 0.2f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            Icons.Default.Place,
                                            contentDescription = null,
                                            tint = primaryColor,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Column {
                                        Text(
                                            text = social.locationName ?: "Unknown Location",
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        )
                                        Text(
                                            social.city,
                                            color = Color(0xFF94A3B8),
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                            }
                        }

                        if (isBusiness) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Button(
                                    onClick = {},
                                    modifier = Modifier.weight(1f).height(48.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(
                                            0xFF1E293B
                                        )
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    border = BorderStroke(1.dp, Color(0xFF334155))
                                ) {
                                    Icon(
                                        Icons.Default.Directions,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Text("Directions", fontSize = 14.sp)
                                }
                                Button(
                                    onClick = {},
                                    modifier = Modifier.size(48.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(
                                            0xFF1E293B
                                        )
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    border = BorderStroke(1.dp, Color(0xFF334155)),
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Phone,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
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

                    // Chat Preview
                    /* Column(modifier = Modifier.padding(10.dp)) {
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
                                            .background(accentBrush)
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
                                    color = primaryColor,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }*/

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
                        .statusBarsPadding()
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
                        onClick = {
                            if (isOwner) {
                                showParticipantActionSheet = true
                            } else {
                                // Default share action
                            }
                        },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f))
                    ) {
                        Icon(
                            if (isOwner) Icons.Default.Groups else Icons.Outlined.Share,
                            contentDescription = if (isOwner) "Manage Participants" else "Share",
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
                            RoundedCornerShape(0.dp)
                        )
                        .navigationBarsPadding()
                        .padding(vertical = 20.dp, horizontal = 20.dp)
                )
                {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        // Chat Button - Only for participants/owners
                        if (isParticipant || isOwner) {
                            Button(
                                onClick = { onOpenChat() },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(56.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(
                                        0xFF1E293B
                                    )
                                ),
                                border = BorderStroke(2.dp, primaryColor)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        Icons.Default.ChatBubble,
                                        contentDescription = null,
                                        tint = primaryColor
                                    )
                                    Text(
                                        "CHAT",
                                        color = primaryColor,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        // Right Action: Leave (Participant), Edit (Owner), Requested/Rejected Status, or I'M DOWN
                        when {
                            isParticipant && !isOwner -> {
                                Button(
                                    onClick = { onLeaveSocial(social.id) },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(56.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(
                                            0xFFEF4444
                                        ).copy(alpha = 0.1f)
                                    ),
                                    border = BorderStroke(
                                        1.dp,
                                        Color(0xFFEF4444).copy(alpha = 0.5f)
                                    )
                                ) {
                                    Text(
                                        "LEAVE",
                                        color = Color(0xFFEF4444),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            isOwner -> {
                                Button(
                                    onClick = { /* Edit action */ },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(56.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color.White.copy(
                                            alpha = 0.1f
                                        )
                                    ),
                                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f))
                                ) {
                                    Text(
                                        "EDIT",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            isRequested -> {
                                StatusBadge(
                                    text = "REQUESTED",
                                    color = Color(0xFFF97316),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(56.dp)
                                )
                            }

                            isRejected -> {
                                StatusBadge(
                                    text = "REJECTED",
                                    color = Color(0xFFEF4444),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(56.dp)
                                )
                            }

                            else -> {
                                // I'M DOWN button (Default for non-participants)
                                Button(
                                    onClick = { onJoinSocial(social.id) },
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
                                            .background(accentBrush),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            "I'M DOWN",
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
     // Participant Management Sheet
                    if (showParticipantActionSheet) {
                        ModalBottomSheet(
                            onDismissRequest = { showParticipantActionSheet = false },
                            sheetState = sheetState,
                            containerColor = Color(0xFF1E293B),
                            scrimColor = Color.Black.copy(alpha = 0.5f)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 24.dp)
                                    .padding(bottom = 48.dp)
                            ) {
                                Text(
                                    "Manage Participants",
                                    color = Color.White,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(bottom = 24.dp)
                                )

                                social.participants.forEach { participant ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        AsyncImage(
                                            model = participant.avatar,
                                            contentDescription = null,
                                            modifier = Modifier.size(40.dp).clip(CircleShape)
                                        )
                                        Text(
                                            participant.name,
                                            color = Color.White,
                                            modifier = Modifier.weight(1f)
                                                .padding(horizontal = 16.dp),
                                            fontWeight = FontWeight.Medium
                                        )
                                        if (participant.id != currentUserId) {
                                            TextButton(
                                                onClick = {
                                                    onRemoveParticipant(
                                                        social.id,
                                                        participant.id
                                                    )
                                                },
                                                colors = ButtonDefaults.textButtonColors(
                                                    contentColor = Color(0xFFEF4444)
                                                )
                                            ) {
                                                Text("Remove")
                                            }
                                        } else {
                                            Text(
                                                "Owner",
                                                color = primaryColor,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                    Divider(color = Color(0xFF334155), thickness = 0.5.dp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }


    @Composable
    fun InfoChip(
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
    fun AvatarsItem(avatar: String = "", name: String = "", brush: Brush? = null) {
        val defaultBrush = Brush.linearGradient(listOf(Color(0xFF9333EA), Color(0xFFDB2777)))
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(64.dp)
        )
        {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(brush ?: defaultBrush)
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
    fun SocialDetailPreview() {
        SocialDetail(
            social = SocialResponse(
                title = "Coffee at Blue Tokai ☕",
                userName = "Rahul",
                userAvatar = "",
                distance = "0.8 km away",
                participantCount = 3,
                description = "Anyone up for a quick coffee and chat this evening?",
                id = 123,
                category = "FOOD",
                city = "Delhi",
                locationName = "Blue Tokai",
                scheduledTime = "Today 5:00 PM",
                maxParticipants = 10,
                timeAgo = "2h ago",
                userId = 1
            ),
            currentUserId = 2, // Not the owner
            onClose = {},
            onOpenChat = {},
            onViewProfile = {},
            onSeeAllParticipants = {},
            onJoinSocial = {},
            onLeaveSocial = {},
            onRemoveParticipant = { _, _ -> }
        )
    }

    @Composable
    fun StatusBadge(
        text: String,
        color: Color,
        modifier: Modifier = Modifier
    ) {
        Surface(
            modifier = modifier,
            shape = RoundedCornerShape(16.dp),
            color = color.copy(alpha = 0.1f),
            border = BorderStroke(1.dp, color.copy(alpha = 0.5f))
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = text,
                    color = color,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }

