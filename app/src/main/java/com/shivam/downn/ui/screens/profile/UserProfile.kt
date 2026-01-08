package com.shivam.downn.ui.screens.profile

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

data class PastActivity(
    val id: Int,
    val image: String,
    val title: String,
    val participants: Int,
    val likes: Int,
    val date: String,
    val location: String
)

data class InterestTag(
    val id: Int,
    val name: String,
    val colors: List<Color>
)

enum class ProfileTab { Recent, Stats }

@Composable
fun UserProfile(
    innerPadding: PaddingValues,
    onClose: () -> Unit,
    onFollowClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {}
) {
    var activeTab by remember { mutableStateOf(ProfileTab.Recent) }

    val interestTags = listOf(
        InterestTag(1, "☕ Coffee", listOf(Color(0xFFFBBF24), Color(0xFFF97316))),
        InterestTag(2, "🏔️ Hiking", listOf(Color(0xFF4ADE80), Color(0xFF059669))),
        InterestTag(3, "🎵 Live Music", listOf(Color(0xFFC084FC), Color(0xFFEC4899))),
        InterestTag(4, "🍕 Foodie", listOf(Color(0xFFF87171), Color(0xFFF97316))),
    )

    val pastActivities = listOf(
        PastActivity(1, "https://images.unsplash.com/photo-1640350408899-9d432cc32bca", "Beach Sunset Vibes", 8, 24, "Dec 28", "Santa Monica"),
        PastActivity(2, "https://images.unsplash.com/photo-1595368062405-e4d7840cba14", "Mountain Hiking", 5, 31, "Dec 20", "Yosemite"),
    )

    Scaffold(
        topBar = {
            ProfileTopBar(onClose, onSettingsClick, onFollowClick)
        },
        floatingActionButton = {
            FabButton(innerPadding,)
        },
        containerColor = Color.Transparent
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0F172A))
                .padding(0.dp,padding.calculateTopPadding(),0.dp,innerPadding.calculateBottomPadding()+padding.calculateBottomPadding())
        )
        {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 100.dp)
            )
            {
                // Hero Card
                item {
                    HeroCard()
                }

                // Interests
                item {
                    InterestsSection(interestTags)
                }

                // Achievements
                item {
                    AchievementsSection()
                }

                // Tabs
                item {
                    ProfileTabs(activeTab) { activeTab = it }
                }

                // Tab Content
                if (activeTab == ProfileTab.Recent) {
                    items(pastActivities) { activity ->
                        ActivityListItem(activity)
                    }
                } else {
                    item {
                        StatsSection()
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileTopBar(onClose: () -> Unit, onSettingsClick: () -> Unit, onFollowClick: () -> Unit) {
    TopAppBar(
        title = {},
        navigationIcon = {
            IconButton(
                onClick = onClose,
                modifier = Modifier
                    .padding(8.dp)
                    .size(40.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White.copy(alpha = 0.1f))
            ) {
                Icon(Icons.Default.ChevronLeft, contentDescription = "Back", tint = Color.White)
            }
        },
        actions = {
            Button(
                onClick = onFollowClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier
                    .height(36.dp)
                    .background(
                        Brush.horizontalGradient(listOf(Color(0xFF9333EA), Color(0xFFDB2777))),
                        CircleShape
                    )
                    .padding(horizontal = 16.dp)
            ) {
                Text("Follow", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            }
            IconButton(
                onClick = onSettingsClick,
                modifier = Modifier
                    .padding(8.dp)
                    .size(40.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White.copy(alpha = 0.1f))
            ) {
                Icon(Icons.Default.Settings, contentDescription = "Settings", tint = Color.White)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0F172A).copy(alpha = 0.7f))
    )
}

@Composable
private fun HeroCard() {
    Card(
        modifier = Modifier
            .padding(20.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        border = BorderStroke(1.dp, Color(0xFF334155))
    ) {
        Column {
            Box(modifier = Modifier.height(160.dp)) {
                AsyncImage(
                    model = "https://images.unsplash.com/photo-1506905925346-21bda4d32df4",
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.4f))))
                )
                
                // Host Score
                Surface(
                    modifier = Modifier.align(Alignment.TopEnd).padding(16.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFF0F172A).copy(alpha = 0.8f)
                ) {
                    Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFEAB308), modifier = Modifier.size(16.dp))
                            Text("4.9", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)
                        }
                        Text("Host Score", fontSize = 10.sp, color = Color(0xFF94A3B8))
                    }
                }
            }

            Box(modifier = Modifier.padding(horizontal = 24.dp)) {
                // Profile Photo
                Box(modifier = Modifier.offset(y = (-64).dp)) {
                    Surface(
                        modifier = Modifier.size(128.dp),
                        shape = RoundedCornerShape(24.dp),
                        color = Color.Transparent,
                        border = BorderStroke(4.dp, Brush.linearGradient(listOf(Color(0xFFC084FC), Color(0xFFF472B6), Color(0xFFFB923C))))
                    ) {
                        AsyncImage(
                            model = "https://images.unsplash.com/photo-1566330429822-c413e4bc27a5",
                            contentDescription = "Profile",
                            modifier = Modifier.clip(RoundedCornerShape(20.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                    Surface(
                        modifier = Modifier.align(Alignment.BottomEnd).offset(x = 8.dp, y = 8.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = Color.Transparent,
                        shadowElevation = 4.dp
                    ) {
                         Row(
                             modifier = Modifier.background(Brush.horizontalGradient(listOf(Color(0xFF7C3AED), Color(0xFFDB2777))), RoundedCornerShape(12.dp)).padding(horizontal = 12.dp, vertical = 4.dp),
                             verticalAlignment = Alignment.CenterVertically,
                             horizontalArrangement = Arrangement.spacedBy(4.dp)
                         ) {
                             Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                             Text("Verified", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                         }
                    }
                }

                Column(modifier = Modifier.padding(top = 80.dp, bottom = 24.dp)) {
                    Text("Sarah Kim", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(vertical = 4.dp)) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFFA855F7), modifier = Modifier.size(16.dp))
                        Text("San Francisco, CA", fontSize = 14.sp, color = Color(0xFF94A3B8))
                    }
                    Text(
                        "Adventure seeker making friends one activity at a time ✨ Marathon runner & coffee enthusiast",
                        fontSize = 14.sp,
                        color = Color(0xFFCBD5E1),
                        lineHeight = 20.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )

                    // Quick Stats
                    Row(modifier = Modifier.padding(top = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        StatItem("127", "Activities", Brush.linearGradient(listOf(Color(0xFFF3E8FF), Color(0xFFFAE8FF))), Modifier.weight(1f))
                        StatItem("2.4K", "Friends", Brush.linearGradient(listOf(Color(0xFFDBEAFE), Color(0xFFCFFAFE))), Modifier.weight(1f))
                        StatItem("89%", "Join Rate", Brush.linearGradient(listOf(Color(0xFFFFEDD5), Color(0xFFFEF3C7))), Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun StatItem(value: String, label: String, brush: Brush, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.background(brush).padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
        Text(label, fontSize = 10.sp, color = Color.White.copy(alpha = 0.7f), fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun InterestsSection(tags: List<InterestTag>) {
    Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)) {
        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Interests & Vibes", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.White)
            Text("Edit", color = Color(0xFFA855F7), fontWeight = FontWeight.SemiBold, fontSize = 14.sp, modifier = Modifier.clickable { })
        }
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            tags.forEach { tag ->
                Box(
                    modifier = Modifier
                        .background(Brush.horizontalGradient(tag.colors), RoundedCornerShape(16.dp))
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Text(tag.name, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
private fun AchievementsSection() {
    Column(modifier = Modifier.padding(vertical = 12.dp)) {
        Text("Achievements", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.White, modifier = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 16.dp))
        LazyRow(contentPadding = PaddingValues(horizontal = 20.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
/*             item { AchievementItem("Top Host", "Level 10", Icons.Default.Star, listOf(Color(0xFFFACC15), Color(0xFFF97316))) }
             item { AchievementItem("Connector", "Level 8", Icons.Default.Groups, listOf(Color(0xFFC084FC), Color(0xFFEC4899))) }
             item { AchievementItem("Explorer", "100+ spots", Icons.Default.LocationOn, listOf(Color(0xFF60A5FA), Color(0xFF06B6D4))) }*/
            item { AchievementItem("Top Host", "Level 10", Icons.Default.Star, listOf(Color(0xFFFACC15), Color(0xFFF97316))) }
            item { AchievementItem("Connector", "Level 8", Icons.Default.Groups, listOf(Color(0xFFC084FC), Color(0xFFEC4899))) }
            item { AchievementItem("Explorer", "100+ spots", Icons.Default.LocationOn, listOf(Color(0xFF60A5FA), Color(0xFF06B6D4))) }
        }
    }
}

@Composable
private fun AchievementItem(title: String, subtitle: String, icon: ImageVector, colors: List<Color>) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(96.dp)) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(Brush.linearGradient(colors), RoundedCornerShape(24.dp)),
//                .shadow(8.dp, RoundedCornerShape(24.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(40.dp))
        }
        Text(title, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp))
        Text(subtitle, fontSize = 12.sp, color = Color.Gray)
    }
}

@Composable
private fun ProfileTabs(activeTab: ProfileTab, onTabClick: (ProfileTab) -> Unit) {
    Row(
        modifier = Modifier
            .padding(20.dp)
            .fillMaxWidth()
            .background(Color(0xFF1E293B), RoundedCornerShape(100.dp))
            .padding(6.dp)
    ) {
        TabItem("Recent Activities", activeTab == ProfileTab.Recent, Modifier.weight(1f)) { onTabClick(ProfileTab.Recent) }
        TabItem("My Stats", activeTab == ProfileTab.Stats, Modifier.weight(1f)) { onTabClick(ProfileTab.Stats) }
    }
}

@Composable
private fun TabItem(label: String, isActive: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .fillMaxHeight()
            .background(
                if (isActive) Brush.horizontalGradient(listOf(Color(0xFF9333EA), Color(0xFFDB2777))) else Brush.linearGradient(listOf(Color.Transparent, Color.Transparent)),
                RoundedCornerShape(100.dp)
            )
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = if (isActive) Color.White else Color(0xFF94A3B8),
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp
        )
    }
}

@Composable
private fun ActivityListItem(activity: PastActivity) {
    Card(
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp).fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, Color(0xFF334155))
    ) {
        Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            AsyncImage(
                model = activity.image,
                contentDescription = activity.title,
                modifier = Modifier.size(96.dp).clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(activity.title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.Gray)
                    Text(activity.location, color = Color.Gray, fontSize = 14.sp)
                }
                Row(modifier = Modifier.padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatBadge(Icons.Default.Groups, activity.participants.toString(), Color(0xFFF3E8FF), Color(0xFF7C3AED))
                    StatBadge(Icons.Default.Favorite, activity.likes.toString(), Color(0xFFFCE7F3), Color(0xFFDB2777))
                }
            }
        }
    }
}

@Composable
private fun StatBadge(icon: ImageVector, text: String, bgColor: Color, iconColor: Color) {
    Row(
        modifier = Modifier.background(bgColor, RoundedCornerShape(100.dp)).padding(horizontal = 12.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(14.dp))
        Text(text, color = iconColor, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun StatsSection() {
    Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Activity Breakdown
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
            modifier = Modifier.fillMaxWidth(),
            border = BorderStroke(1.dp, Color(0xFF334155))
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text("Activity Breakdown", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.White)
                Spacer(modifier = Modifier.height(16.dp))
                StatsLinearProgress("Coffee Meetups", "45", 0.75f, Color(0xFFFBBF24))
                StatsLinearProgress("Outdoor Adventures", "32", 0.55f, Color(0xFF4ADE80))
                StatsLinearProgress("Nightlife & Events", "28", 0.48f, Color(0xFFC084FC))
            }
        }
    }
}

@Composable
private fun StatsLinearProgress(label: String, value: String, progress: Float, color: Color) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            Text(value, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF9333EA))
        }
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth().height(8.dp).padding(top = 4.dp).clip(CircleShape),
            color = color,
            trackColor = Color(0xFF334155)
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FlowRow(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    content: @Composable () -> Unit
) {
    androidx.compose.foundation.layout.FlowRow(
        modifier = modifier,
        horizontalArrangement = horizontalArrangement,
        verticalArrangement = verticalArrangement
    ) {
        content()
    }
}


@Composable
@Preview
fun FabButton(innerPadding: PaddingValues=PaddingValues(0.dp)){
    Box(
        modifier = Modifier
            .padding(
                bottom = innerPadding.calculateBottomPadding(),
                end = 16.dp
            )
            .size(64.dp)
            .clip(CircleShape)
            .background(
                Brush.horizontalGradient(
                    listOf(Color(0xFF9333EA), Color(0xFFDB2777))
                )
            )
            .clickable { },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            Icons.Default.Edit,
            contentDescription = "Edit",
            tint = Color.White
        )
    }

}


@Composable
fun UserProfilePreview() {
    // It's a good practice to wrap your preview in your app's theme
    // to ensure colors, fonts, and other theme attributes are applied correctly.
    // YourAppTheme {
    UserProfile(
        innerPadding = PaddingValues(0.dp),
        onClose = {}
    )
    // }
}
