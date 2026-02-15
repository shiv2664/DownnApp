package com.shivam.downn.ui.screens.profile

import androidx.compose.foundation.*
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.shivam.downn.data.models.InterestTag
import androidx.compose.foundation.layout.FlowRow
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.shivam.downn.data.models.ProfileType
import com.shivam.downn.data.models.UserProfileData
import com.shivam.downn.data.network.NetworkResult
import com.shivam.downn.ui.theme.Dimens
import com.shivam.downn.ui.theme.Purple40


enum class ProfileTab { Recent, Stats }

@Composable
fun UserProfileRoute(
    outerPadding: PaddingValues,
    onClose: () -> Unit,
    onSettingsClick: () -> Unit = {},
    onEditClick: () -> Unit = {},
    onCreateProfileClick: () -> Unit = {},
    onBusinessMoveClick: (Int) -> Unit = {},
    viewModel: ProfileViewModel = hiltViewModel(),
    onEditBusinessProfileClick: (businessId: Long) -> Unit
) {
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    
    // Refresh user profile when screen resumes
    DisposableEffect(lifecycleOwner) {
        val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
            if (event == androidx.lifecycle.Lifecycle.Event.ON_RESUME) {
                viewModel.fetchCurrentUserDetails()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
    
    LaunchedEffect(Unit) {
        viewModel.fetchCurrentUserDetails()
    }
    
    val activeProfile by viewModel.activeProfile.collectAsState()
    val profiles by viewModel.profiles.collectAsState()
    val canCreateProfile by viewModel.canCreateBusinessProfile.collectAsState()
    val userActivitiesState by viewModel.userActivities.collectAsState()
    
    // Trigger load more for user activities
    val onLoadMoreUserActivities = { viewModel.loadMoreUserActivities() }

    if (activeProfile?.type == ProfileType.BUSINESS) {
        BusinessProfileRoute(
            businessId = activeProfile?.id ?: 16L,
            onClose = { viewModel.switchProfile(profiles.first { it.type == ProfileType.PERSONAL }) },
            onMoveClick = onBusinessMoveClick,
            isOwnProfile = true,
            onEditBusinessProfileClick = { id -> onEditBusinessProfileClick(id) },
            viewModel = viewModel
        )
    } else {
        ProfileContent(
            isOwnProfile = true,
            outerPadding = outerPadding,
            onClose = onClose,
            onSettingsClick = onSettingsClick,
            onEditClick = onEditClick,
            onCreateProfileClick = onCreateProfileClick,
            activeProfile = activeProfile,
            profiles = profiles,
            canCreateProfile = canCreateProfile,
            onProfileSwitch = { viewModel.switchProfile(it) },
            userActivitiesState = userActivitiesState,
            onLoadMore = onLoadMoreUserActivities
        )
    }
}

@Composable
fun ProfileContent(
    isOwnProfile: Boolean,
    outerPadding: PaddingValues,
    onClose: () -> Unit,
    onFollowClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onEditClick: () -> Unit = {},
    onCreateProfileClick: () -> Unit = {},
    activeProfile: UserProfileData? = null,
    profiles: List<UserProfileData> = emptyList(),
    canCreateProfile: Boolean = true,
    userActivitiesState: NetworkResult<List<com.shivam.downn.data.models.SocialResponse>>? = null,
    onProfileSwitch: (UserProfileData) -> Unit = {},
    onLoadMore: () -> Unit = {}
) {
    var activeTab by remember { mutableStateOf(ProfileTab.Recent) }
    var showInterestsSheet by remember { mutableStateOf(false) }
    var showProfileSwitcher by remember { mutableStateOf(false) }

    val allInterests = listOf(
        InterestTag(1, "☕ Coffee", listOf(Color(0xFFFBBF24), Color(0xFFF97316))),
        InterestTag(2, "🏔️ Hiking", listOf(Color(0xFF4ADE80), Color(0xFF059669))),
        InterestTag(3, "🎵 Live Music", listOf(Color(0xFFC084FC), Color(0xFFEC4899))),
        InterestTag(4, "🍕 Foodie", listOf(Color(0xFFF87171), Color(0xFFF97316))),
        InterestTag(5, "🎮 Gaming", listOf(Color(0xFF60A5FA), Color(0xFF3B82F6))),
        InterestTag(6, "🎨 Art", listOf(Color(0xFFF472B6), Color(0xFFDB2777))),
        InterestTag(7, "🎬 Movies", listOf(Color(0xFF94A3B8), Color(0xFF475569))),
        InterestTag(8, "🏀 Sports", listOf(Color(0xFFFB923C), Color(0xFFEA580C)))
    )

    var selectedInterests by remember { mutableStateOf(allInterests.take(4)) }


    Scaffold(
        topBar = {
            ProfileTopBar(
                isOwnProfile = isOwnProfile,
                onClose = onClose,
                onSettingsClick = onSettingsClick,
                onFollowClick = onFollowClick,
                activeProfile = activeProfile,
                hasMultipleProfiles = profiles.size > 1,
                canCreateProfile=canCreateProfile,
                onTitleClick = { if (isOwnProfile) showProfileSwitcher = true }
            )
        },
        containerColor = Color.Transparent
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0F172A))
                .padding(0.dp, padding.calculateTopPadding(), 0.dp, outerPadding.calculateBottomPadding())
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                item { ProfileHeader(activeProfile) }
                item {
                    InterestsSection(selectedInterests, isOwnProfile) {
                        showInterestsSheet = true
                    }
                }

                if (activeTab == ProfileTab.Recent) {
                    when (userActivitiesState) {
                        is NetworkResult.Success -> {
                            val activities = userActivitiesState.data ?: emptyList()
                            if (activities.isEmpty()) {
                                item {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(20.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "No recent activities",
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = Color(0xFF94A3B8)
                                        )
                                    }
                                }
                            } else {
                                items(activities) { social ->
                                    if (social == activities.last()) {
                                        LaunchedEffect(Unit) {
                                            onLoadMore()
                                        }
                                    }
                                    
                                    ActivityFeedItem(social)
                                }
                            }
                        }
                        is NetworkResult.Loading -> {
                             // No loader as per user request
                        }
                        is NetworkResult.Error -> {
                            item {
                                Text(
                                    text = "Error loading activities",
                                    color = Color.Red,
                                    modifier = Modifier.padding(20.dp)
                                )
                            }
                        }
                        else -> {}
                    }
                }
            }
        }

        if (showInterestsSheet && isOwnProfile) {
            InterestsBottomSheet(
                allInterests = allInterests,
                selectedInterests = selectedInterests,
                onDismiss = { showInterestsSheet = false },
                onInterestsChanged = { selectedInterests = it }
            )
        }

        if (showProfileSwitcher && isOwnProfile) {
            ProfileSwitcherBottomSheet(
                profiles = profiles,
                activeProfile = activeProfile,
                onDismiss = { showProfileSwitcher = false },
                canCreateProfile = canCreateProfile,
                onProfileSelected = {
                    onProfileSwitch(it)
                    showProfileSwitcher = false
                },
                onCreateProfile = {
                    showProfileSwitcher = false
                    onCreateProfileClick()
                }
            )
        }
    }
}

@Composable
fun FabButton(innerPadding: PaddingValues=PaddingValues(0.dp), onEditClick: () -> Unit = {}){
    Box(
        modifier = Modifier
            .padding(
                bottom = innerPadding.calculateBottomPadding(),
                end = 4.dp
            )
            .size(64.dp)
            .clip(CircleShape)
            .background(
                Brush.horizontalGradient(
                    listOf(Color(0xFF9333EA), Color(0xFFDB2777))
                )
            )
            .clickable { onEditClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            Icons.Default.Edit,
            contentDescription = "Edit Profile",
            tint = Color.White
        )
    }

}


@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun InterestsBottomSheet(
    allInterests: List<InterestTag>,
    selectedInterests: List<InterestTag>,
    onDismiss: () -> Unit,
    onInterestsChanged: (List<InterestTag>) -> Unit
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
                "Select Interests",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = Color.White,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            androidx.compose.foundation.layout.FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                allInterests.forEach { tag ->
                    val isSelected = selectedInterests.any { it.id == tag.id }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                brush = if (isSelected) Brush.horizontalGradient(tag.colors) else SolidColor(Color(0xFF0F172A)),
                                shape = RoundedCornerShape(16.dp),
                                alpha = 1f
                            )
                            .border(
                                1.dp,
                                if (isSelected) Color.Transparent else Color(0xFF334155),
                                RoundedCornerShape(16.dp)
                            )
                            .clickable {
                                val newList = if (isSelected) {
                                    selectedInterests.filter { it.id != tag.id }
                                } else {
                                    selectedInterests + tag
                                }
                                onInterestsChanged(newList)
                            }
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        Text(
                            tag.name,
                            color = if (isSelected) Color.White else Color(0xFF94A3B8),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
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
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
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
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = if (profile.type == ProfileType.BUSINESS) "Business Profile" else "Personal Profile",
                            color = Color(0xFF94A3B8),
                            style = MaterialTheme.typography.bodySmall
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
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium)
                    )
                }
            }
        }
    }
}


@Composable
@Preview
fun UserProfilePreview() {
    ProfileContent(
        isOwnProfile = true,
        outerPadding = PaddingValues(0.dp),
        onClose = {}
    )
}
