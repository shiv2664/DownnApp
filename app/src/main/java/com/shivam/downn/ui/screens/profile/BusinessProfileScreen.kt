package com.shivam.downn.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import com.shivam.downn.ui.screens.feed.MoveCard
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.shivam.downn.data.network.NetworkResult
import com.shivam.downn.data.models.ProfileType
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import com.shivam.downn.data.models.UserProfileData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BusinessProfileScreen(
    businessId: Long,
    onClose: () -> Unit,
    onMoveClick: (Int) -> Unit,
    isOwnProfile: Boolean = true,
    onEditBusinessProfileClick: (businessId: Long) -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val activeProfile by viewModel.activeProfile.collectAsState()
    val profiles by viewModel.profiles.collectAsState()
    val canCreateBusinessProfile by viewModel.canCreateBusinessProfile.collectAsState()
    val viewedProfileResult by viewModel.viewedProfile.collectAsState()

    LaunchedEffect(businessId, isOwnProfile) {
        if (!isOwnProfile) {
            viewModel.fetchProfileDetails(businessId)
        }
    }

    val profileToDisplay = if (isOwnProfile) activeProfile else (viewedProfileResult as? NetworkResult.Success)?.data

    if (!isOwnProfile && viewedProfileResult is NetworkResult.Loading) {
        Scaffold(containerColor = Color(0xFF0F172A)) {
            Box(modifier = Modifier.fillMaxSize().padding(it), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
    } else if (!isOwnProfile && viewedProfileResult is NetworkResult.Error) {
        Scaffold(containerColor = Color(0xFF0F172A)) {
            Box(modifier = Modifier.fillMaxSize().padding(it), contentAlignment = Alignment.Center) {
                Text((viewedProfileResult as NetworkResult.Error).message ?: "Error", color = Color.White)
            }
        }
    } else if (profileToDisplay != null) {
        BusinessProfileContent(
            businessId = businessId,
            onClose = onClose,
            onMoveClick = onMoveClick,
            isOwnProfile = isOwnProfile,
            activeProfile = profileToDisplay,
            profiles = profiles,
            canCreateBusinessProfile = canCreateBusinessProfile,
            onSwitchProfile = { viewModel.switchProfile(it) },
            onCreateProfile = { /* Navigation to create_profile would be handled here */ },
            onEditBusinessProfileClick = onEditBusinessProfileClick
        )
    } else if (isOwnProfile) {
        // Loading state for own profile
        Scaffold(containerColor = Color(0xFF0F172A)) {
            Box(modifier = Modifier.fillMaxSize().padding(it), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BusinessProfileContent(
    businessId: Long,
    onClose: () -> Unit,
    onMoveClick: (Int) -> Unit,
    isOwnProfile: Boolean,
    activeProfile: UserProfileData?,
    profiles: List<UserProfileData>,
    canCreateBusinessProfile: Boolean,
    onSwitchProfile: (UserProfileData) -> Unit,
    onCreateProfile: () -> Unit,
    onEditBusinessProfileClick: (businessId: Long) -> Unit
) {
    var showProfileSwitcher by remember { mutableStateOf(false) }

    val businessName = activeProfile?.name ?: ""
    val businessAvatar = activeProfile?.avatar ?: ""
    val coverImage = activeProfile?.coverImage ?: ""
    val vibes = activeProfile?.vibes ?: emptyList()

    Scaffold(
        containerColor = Color(0xFF0F172A),
        topBar = {
            TopAppBar(
                title = {
                    if (isOwnProfile) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clickable { showProfileSwitcher = true }
                                .padding(start = 8.dp)
                        ) {
                            Text(
                                text = activeProfile?.name ?: "",
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            if (profiles.size > 1) {
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowDown,
                                    contentDescription = "Switch Profile",
                                    tint = Color.White,
                                    modifier = Modifier
                                        .padding(start = 4.dp)
                                        .size(20.dp)
                                )
                            }
                        }
                    } else {
                        Text(
                            text = businessName,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                navigationIcon = {
                    if (!isOwnProfile) {
                        IconButton(onClick = onClose) {
                            Icon(
                                Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White,
                            )
                        }
                    }
                },
                actions = {
                    if (isOwnProfile) {
                        IconButton(onClick = { onEditBusinessProfileClick(businessId) }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit Business Profile", tint = Color.White)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
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
                    VibeSection(vibes)
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
                        title = if (businessId == 16L) "Live Jazz Night 🎷" else "Friday Night Fever 🕺",
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

            if (showProfileSwitcher && isOwnProfile) {
                ProfileSwitcherBottomSheet(
                    profiles = profiles,
                    activeProfile = activeProfile,
                    onDismiss = { showProfileSwitcher = false },
                    canCreateProfile = canCreateBusinessProfile,
                    onProfileSelected = {
                        onSwitchProfile(it)
                        showProfileSwitcher = false
                    },
                    onCreateProfile = {
                        showProfileSwitcher = false
                        onCreateProfile()
                    }
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
fun VibeSection(vibes: List<String>) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Our Vibe", color = Color(0xFF94A3B8), fontSize = 14.sp, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            if (vibes.isNotEmpty()) {
                vibes.forEach { vibe ->
                    VibeTag(vibe, Color(0xFFEC4899))
                }
            } else {
                VibeTag("Cozy", Color(0xFFEC4899))
                VibeTag("Jazz", Color(0xFF8B5CF6))
                VibeTag("Craft Coffee", Color(0xFFF59E0B))
            }
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileSwitcherBottomSheet(
    profiles: List<UserProfileData>,
    activeProfile: UserProfileData?,
    onDismiss: () -> Unit,
    canCreateProfile: Boolean = false,
    onProfileSelected: (UserProfileData) -> Unit,
    onCreateProfile: () -> Unit = {}
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF1E293B),
        dragHandle = { BottomSheetDefaults.DragHandle(color = Color(0xFF334155)) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .padding(bottom = 40.dp)
        ) {
            Text(
                "Switch Profile",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            profiles.forEach { profile ->
                val isSelected = profile.id == activeProfile?.id
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isSelected) Color(0xFF0F172A) else Color.Transparent)
                        .clickable { onProfileSelected(profile) }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    AsyncImage(
                        model = profile.avatar,
                        contentDescription = profile.name,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = profile.name,
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (profile.type == ProfileType.BUSINESS) "Business Profile" else "Personal Profile",
                            color = Color(0xFF94A3B8),
                            fontSize = 12.sp
                        )
                    }
                    if (isSelected) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = "Selected",
                            tint = Color(0xFFA855F7),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (canCreateProfile) {
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 16.dp),
                    color = Color(0xFF334155)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { onCreateProfile() }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(Color(0xFF334155), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
                    }
                    Text(
                        text = "Create Business Profile",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewBusinessProfileScreen() {
    val profiles = listOf(
        UserProfileData(
            id = 1,
            name = "Shivam",
            avatar = "https://images.unsplash.com/photo-1521119989659-a83eee488004?w=400",
            type = ProfileType.PERSONAL,
            coverImage = "",
            vibes = emptyList()
        ),
        UserProfileData(
            id = 16,
            name = "The Daily Grind",
            avatar = "https://images.unsplash.com/photo-1509042239860-f550ce710b93?w=400",
            type = ProfileType.BUSINESS,
            coverImage = "https://images.unsplash.com/photo-1495474472287-4d71bcdd2085?w=800",
            vibes = listOf("Cozy", "Jazz", "Craft Coffee")
        )
    )
    BusinessProfileContent(
        businessId = 16L,
        onClose = {},
        onMoveClick = {},
        isOwnProfile = true,
        activeProfile = profiles.first { it.id == 16L },
        profiles = profiles,
        canCreateBusinessProfile = true,
        onSwitchProfile = {},
        onCreateProfile = {},
        onEditBusinessProfileClick = {}
    )
}
