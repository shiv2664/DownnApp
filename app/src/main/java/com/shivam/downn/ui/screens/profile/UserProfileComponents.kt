package com.shivam.downn.ui.screens.profile

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.shivam.downn.R
import com.shivam.downn.data.models.InterestTag
import com.shivam.downn.data.models.UserProfileData
import com.shivam.downn.ui.theme.DarkSurface
import com.shivam.downn.ui.theme.Dimens
import com.shivam.downn.ui.theme.Pink40
import com.shivam.downn.ui.theme.Purple40
import com.shivam.downn.utils.ImageUtils
import com.shivam.downn.utils.PlaceholderUtils

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun InterestsSection(
    tags: List<InterestTag>,
    isOwnProfile: Boolean,
    onEditInterestsClick: () -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = Dimens.PaddingLarge, vertical = Dimens.PaddingMedium)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = Dimens.PaddingMedium),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Interests & Vibes",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = Color.White
            )
            if (isOwnProfile) {
                Text(
                    "Edit",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                    color = Purple40,
                    modifier = Modifier.clickable { onEditInterestsClick() }
                )
            }
        }
        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimens.PaddingSmall),
            horizontalArrangement = Arrangement.spacedBy(Dimens.PaddingSmall),
            verticalArrangement = Arrangement.spacedBy(Dimens.PaddingSmall)
        ) {
            tags.forEach { tag ->
                Box(
                    modifier = Modifier
                        .background(Brush.horizontalGradient(tag.colors), RoundedCornerShape(Dimens.CornerRadiusMedium))
                        .padding(horizontal = Dimens.PaddingMedium, vertical = 10.dp)
                ) {
                    Text(
                        tag.name,
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun ProfileHeader(profile: UserProfileData?) {
    Card(
        modifier = Modifier
            .padding(Dimens.PaddingLarge)
            .fillMaxWidth(),
        shape = RoundedCornerShape(Dimens.CornerRadiusLarge),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = BorderStroke(1.dp, Color(0xFF334155))
    ) {
        Column {
            Box(modifier = Modifier.height(160.dp)) {
                val coverPlaceholder = painterResource(PlaceholderUtils.getPersonalCoverPlaceholder())
                AsyncImage(
                    model = ImageUtils.getFullImageUrl(profile?.coverImage),
                    placeholder = coverPlaceholder,
                    error = coverPlaceholder,
                    contentDescription = "Cover Image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.6f)
                                )
                            )
                        )
                )

                // Host Score badge
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(Dimens.PaddingMedium),
                    shape = RoundedCornerShape(Dimens.CornerRadiusMedium),
                    color = Color(0xFF0F172A).copy(alpha = 0.8f)
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = Dimens.PaddingSmall),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                Icons.Default.Star,
                                contentDescription = null,
                                tint = Color(0xFFEAB308),
                                modifier = Modifier.size(Dimens.IconSizeSmall)
                            )
                            Text(
                                "4.9",
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                        }
                        Text(
                            "Host Score",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF94A3B8)
                        )
                    }
                }
            }

            Box(modifier = Modifier.padding(horizontal = Dimens.PaddingLarge)) {
                // Profile Photo
                Box(modifier = Modifier.offset(y = (-64).dp)) {
                    Surface(
                        modifier = Modifier.size(128.dp),
                        shape = RoundedCornerShape(Dimens.CornerRadiusLarge),
                        color = Color.Transparent,
                        border = BorderStroke(
                            4.dp,
                            Brush.linearGradient(
                                listOf(
                                    Color(0xFFC084FC),
                                    Color(0xFFF472B6),
                                    Color(0xFFFB923C)
                                )
                            )
                        )
                    ) {
                        if(profile?.avatarThumbnail==null){
                            Image(
                                painter = painterResource(R.drawable.placeholder),
                                contentDescription = "Cover Image",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }else{
                            AsyncImage(
                                model = ImageUtils.getFullImageUrl(profile?.avatarThumbnail),
                                contentDescription = "Profile Picture",
                                modifier = Modifier.clip(RoundedCornerShape(20.dp)),
                                contentScale = ContentScale.Crop
                            )
                        }

                    }
                    // Verified Badge
                    Surface(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .offset(x = Dimens.PaddingSmall, y = Dimens.PaddingSmall),
                        shape = RoundedCornerShape(12.dp),
                        color = Color.Transparent,
                        shadowElevation = 4.dp
                    ) {
                        Row(
                            modifier = Modifier
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(
                                            Color(0xFF7C3AED),
                                            Color(0xFFDB2777)
                                        )
                                    ), RoundedCornerShape(12.dp)
                                )
                                .padding(horizontal = 12.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = "Verified",
                                tint = Color.White,
                                modifier = Modifier.size(Dimens.IconSizeSmall)
                            )
                            Text(
                                "Verified",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                        }
                    }
                }

                Column(modifier = Modifier.padding(top = 80.dp, bottom = Dimens.PaddingLarge)) {
                    Text(
                        profile?.name ?: "User Name",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )

                    Text(
                        profile?.bio ?: "No bio available.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFFCBD5E1),
                        modifier = Modifier.padding(top = Dimens.PaddingSmall)
                    )

                    // Follower Counts (Hidden as per request)
                    /*
                    Row(
                        modifier = Modifier.padding(top = Dimens.PaddingMedium),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ProfileStat(count = profile?.followersCount ?: 0, label = "Followers")
                        ProfileStat(count = profile?.followingCount ?: 0, label = "Following")
                    }
                    */
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileTopBar(
    isOwnProfile: Boolean,
    onClose: () -> Unit,
    onSettingsClick: () -> Unit,
    onFollowClick: () -> Unit,
    activeProfile: UserProfileData? = null,
    hasMultipleProfiles: Boolean = false,
    canCreateProfile: Boolean = true,
    onTitleClick: () -> Unit = {},
    onBlockClick: () -> Unit = {},
    onReportClick: () -> Unit = {},
    isBlocked: Boolean = false
) {
    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clickable(enabled = isOwnProfile && canCreateProfile) { onTitleClick() }
                    .padding(start = Dimens.PaddingSmall)
            ) {
                Text(
                    text = activeProfile?.name ?: if (isOwnProfile) "My Profile" else "User Profile",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
                if (isOwnProfile && canCreateProfile) {
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
        },
        navigationIcon = {
            if (!isOwnProfile) {
                IconButton(
                    onClick = onClose,
                    modifier = Modifier
                        .padding(Dimens.PaddingSmall)
                        .size(40.dp)
                        .clip(RoundedCornerShape(Dimens.CornerRadiusMedium))
                        .background(Color.White.copy(alpha = 0.1f))
                ) {
                    Icon(
                        Icons.Default.ChevronLeft,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
            }
        },
        actions = {
            if (!isOwnProfile) {
                var showMenu by remember { mutableStateOf(false) }
                Box {
                    IconButton(
                        onClick = { showMenu = true },
                        modifier = Modifier
                            .padding(Dimens.PaddingSmall)
                            .size(40.dp)
                            .clip(RoundedCornerShape(Dimens.CornerRadiusMedium))
                            .background(Color.White.copy(alpha = 0.1f))
                    ) {
                        Icon(
                            Icons.Default.MoreVert,
                            contentDescription = "More options",
                            tint = Color.White
                        )
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false },
                        modifier = Modifier.background(Color(0xFF1E293B))
                    ) {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    if (isBlocked) "Unblock User" else "Block User",
                                    color = if (isBlocked) Color.White else Color(0xFFF87171)
                                )
                            },
                            onClick = {
                                showMenu = false
                                onBlockClick()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Report User", color = Color(0xFFF87171)) },
                            onClick = {
                                showMenu = false
                                onReportClick()
                            }
                        )
                    }
                }
            }

            if (isOwnProfile) {
                IconButton(
                    onClick = onSettingsClick,
                    modifier = Modifier
                        .padding(Dimens.PaddingSmall)
                        .size(40.dp)
                        .clip(RoundedCornerShape(Dimens.CornerRadiusMedium))
                        .background(Color.White.copy(alpha = 0.1f))
                ) {
                    Icon(
                        Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = Color.White
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0F172A).copy(alpha = 0.7f))
    )
}

@Composable
fun ShimmerActivityItem() {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1000,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer"
    )

    val brush = Brush.linearGradient(
        colors = listOf(
            Color.White.copy(alpha = 0.1f),
            Color.White.copy(alpha = 0.3f),
            Color.White.copy(alpha = 0.1f)
        ),
        start = Offset.Zero,
        end = Offset(x = translateAnim, y = translateAnim)
    )

    Card(
        modifier = Modifier
            .padding(horizontal = Dimens.PaddingLarge, vertical = Dimens.PaddingSmall)
            .fillMaxWidth()
            .height(120.dp),
        shape = RoundedCornerShape(Dimens.CornerRadiusLarge),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = BorderStroke(1.dp, Color(0xFF334155))
    ) {
        Row(modifier = Modifier.padding(Dimens.PaddingMedium)) {
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(RoundedCornerShape(Dimens.CornerRadiusMedium))
                    .background(brush)
            )
            Spacer(modifier = Modifier.width(Dimens.PaddingMedium))
            Column(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .height(20.dp)
                        .fillMaxWidth(0.7f)
                        .clip(RoundedCornerShape(4.dp))
                        .background(brush)
                )
                Spacer(modifier = Modifier.height(Dimens.PaddingSmall))
                Box(
                    modifier = Modifier
                        .height(16.dp)
                        .fillMaxWidth(0.5f)
                        .clip(RoundedCornerShape(4.dp))
                        .background(brush)
                )
            }
        }
    }
}

@Composable
fun ActivityFeedItem(
    social: com.shivam.downn.data.models.SocialResponse,
    onItemClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .padding(horizontal = Dimens.PaddingLarge, vertical = Dimens.PaddingSmall)
            .fillMaxWidth()
            .clickable { onItemClick() },
        shape = RoundedCornerShape(Dimens.CornerRadiusLarge),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, Color(0xFF334155))
    ) {
        Row(
            modifier = Modifier.padding(Dimens.PaddingMedium),
            horizontalArrangement = Arrangement.spacedBy(Dimens.PaddingMedium)
        ) {
            AsyncImage(
                model = ImageUtils.getFullImageUrl(social.images.firstOrNull())
                    ?: "https://images.unsplash.com/photo-1551818255-e6e10975bc17", // Fallback or first image
                contentDescription = social.title,
                modifier = Modifier
                    .size(96.dp)
                    .clip(RoundedCornerShape(Dimens.CornerRadiusMedium)),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    social.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(Dimens.IconSizeSmall),
                        tint = Color(0xFFE0E0E0)
                    )
                    Text(
                        social.locationName ?: social.city,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFFE0E0E0)
                    )
                }
                Row(
                    modifier = Modifier.padding(top = Dimens.PaddingSmall),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatBadge(
                        Icons.Default.Groups,
                        social.participantCount.toString(),
                        Color(0xFFF3E8FF),
                        Color(0xFF7C3AED)
                    )
                    StatBadge(
                        Icons.Default.Person,
                        "${social.maxParticipants}",
                        Color(0xFFFCE7F3),
                        Color(0xFFDB2777)
                    )
                }
            }
        }
    }
}

@Composable
fun StatBadge(icon: ImageVector, text: String, bgColor: Color, iconColor: Color) {
    Row(
        modifier = Modifier
            .background(bgColor, RoundedCornerShape(100.dp))
            .padding(horizontal = 12.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(Dimens.IconSizeSmall)
        )
        Text(
            text,
            color = iconColor,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
        )
    }
}

@Composable
fun ProfileStat(count: Int, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = Color.White
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = Color(0xFF94A3B8)
        )
    }
}
