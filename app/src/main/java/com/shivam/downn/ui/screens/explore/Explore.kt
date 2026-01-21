package com.shivam.downn.ui.screens.explore

import androidx.compose.animation.*
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import android.app.Activity
import androidx.core.view.WindowCompat
import coil.compose.AsyncImage
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.snap
import kotlinx.coroutines.launch

data class MapSocial(
    val id: Int,
    val title: String,
    val userName: String,
    val avatar: String,
    val distance: String,
    val participantCount: Int,
    val categoryIcon: ImageVector,
    val categoryColor: Color,
    val gradient: Brush,
    val timePosted: String,
    val xPercent: Float,
    val yPercent: Float
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Explore(
    innerPadding: PaddingValues,
    onSocialClick: (MapSocial) -> Unit = {}
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    var selectedFilter by remember { mutableStateOf("all") }
    var selectedPinId by remember { mutableStateOf<Int?>(null) }



    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            initialValue = SheetValue.PartiallyExpanded
        )
    )
    val coroutineScope = rememberCoroutineScope()

    val socials = listOf(
        MapSocial(1, "Coffee Tasting", "Alex M.", "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d", "0.2 mi", 4, Icons.Default.Coffee, Color(0xFFF59E0B), Brush.linearGradient(listOf(Color(0xFFFB923C), Color(0xFFF59E0B))), "30m ago", 0.3f, 0.4f),
        MapSocial(2, "Live Jazz", "Sarah K.", "https://images.unsplash.com/photo-1566330429822-c413e4bc27a5", "0.5 mi", 8, Icons.Default.MusicNote, Color(0xFFA855F7), Brush.linearGradient(listOf(Color(0xFFC084FC), Color(0xFFEC4899))), "1h ago", 0.6f, 0.2f),
        MapSocial(3, "Bridge Walk", "Mike R.", "https://images.unsplash.com/photo-1567516364473-233c4b6fcfbe", "1.2 mi", 6, Icons.Default.Flight, Color(0xFF3B82F6), Brush.linearGradient(listOf(Color(0xFF60A5FA), Color(0xFF06B6D4))), "2h ago", 0.8f, 0.7f),
    )

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = 200.dp,
        sheetContainerColor = Color(0xFF1E293B),
        sheetContentColor = Color.White,
        sheetShape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
        sheetShadowElevation = 16.dp,
        sheetDragHandle = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(width = 40.dp, height = 4.dp)
                        .background(Color(0xFF334155), CircleShape)
                )
            }
        },
        sheetContent = {
            Column(modifier = Modifier.fillMaxHeight(0.85f)) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            "${socials.size} Nearby Moves",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text("Tap a pin to see details", fontSize = 14.sp, color = Color(0xFF94A3B8))
                    }
                    if (scaffoldState.bottomSheetState.currentValue == SheetValue.Expanded) {
                        IconButton(onClick = {
                            coroutineScope.launch { scaffoldState.bottomSheetState.partialExpand() }
                            selectedPinId = null
                        }) {
                            Icon(Icons.Default.Close, contentDescription = "Collapse", tint = Color.Gray)
                        }
                    }
                }

                // Social List
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(socials) { social ->
                        SocialListItem(social, isSelected = selectedPinId == social.id) {
                            onSocialClick(social)
                        }
                    }
                }
            }
        },
        containerColor = Color(0xFF0F172A)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Map Background
            AsyncImage(
                model = "https://images.unsplash.com/photo-1590393820812-8a2ed3ece96f",
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.2f))
            )

            // Filter Header
            Column(modifier = Modifier.padding(top = 16.dp + innerPadding.calculateTopPadding())) {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        FilterChip(
                            "All",
                            Icons.Default.AllInclusive,
                            selectedFilter == "all"
                        ) { selectedFilter = "all" }
                    }
                    item {
                        FilterChip(
                            "Travel",
                            Icons.Default.Flight,
                            selectedFilter == "travel"
                        ) { selectedFilter = "travel" }
                    }
                    item {
                        FilterChip(
                            "Food",
                            Icons.Default.Restaurant,
                            selectedFilter == "food"
                        ) { selectedFilter = "food" }
                    }
                    item {
                        FilterChip(
                            "Party",
                            Icons.Default.Celebration,
                            selectedFilter == "party"
                        ) { selectedFilter = "party" }
                    }
                }
            }

            // Map Pins
            socials.forEach { social ->
                MapPin(
                    social = social,
                    isSelected = selectedPinId == social.id,
                    onClick = {
                        selectedPinId = social.id
                        coroutineScope.launch { scaffoldState.bottomSheetState.expand() }
                    }
                )
            }

            // Current Location FAB
            FloatingActionButton(
                onClick = { },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 16.dp, bottom = 24.dp) // Scaffold handles content padding
                    .size(48.dp),
                shape = CircleShape,
                containerColor = Color.White
            ) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(Color(0xFF3B82F6), CircleShape)
                        .border(2.dp, Color.White, CircleShape)
                )
            }
        }
    }
}

@Composable
private fun FilterChip(label: String, icon: ImageVector, isActive: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = Color.Transparent,
        modifier = Modifier.shadow(4.dp, CircleShape)
    ) {
        Row(
            modifier = Modifier
                .background(if (isActive) Brush.horizontalGradient(listOf(Color(0xFF9333EA), Color(0xFFDB2777))) else Brush.linearGradient(listOf(Color(0xFF334155), Color(0xFF334155))))
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(16.dp), tint = if (isActive) Color.White else Color(0xFF94A3B8))
            Text(label, color = if (isActive) Color.White else Color(0xFF94A3B8), fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun BoxScope.MapPin(social: MapSocial, isSelected: Boolean, onClick: () -> Unit) {
    val scale by animateFloatAsState(if (isSelected) 1.2f else 1f)

    Box(
        modifier = Modifier
            .align(Alignment.TopStart)
            .offset(x = (LocalConfiguration.current.screenWidthDp * social.xPercent).dp, y = (LocalConfiguration.current.screenHeightDp * social.yPercent).dp)
            .scale(scale)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(contentAlignment = Alignment.TopEnd) {
                Surface(
                    modifier = Modifier.size(48.dp),
                    shape = CircleShape,
                    color = social.categoryColor,
                    border = BorderStroke(3.dp, Color.White),
                    shadowElevation = 8.dp
                ) {
                    Icon(social.categoryIcon, contentDescription = null, tint = Color.White, modifier = Modifier.padding(10.dp))
                }
                // Participant Badge
                Box(
                    modifier = Modifier.offset(x = 4.dp, y = (-4).dp).size(20.dp).background(Brush.horizontalGradient(listOf(Color(0xFF9333EA), Color(0xFFDB2777))), CircleShape).border(1.5.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(social.participantCount.toString(), color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
            // Pointer
            Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = social.categoryColor, modifier = Modifier.size(24.dp).offset(y = (-8).dp))
        }
    }
}

@Composable
private fun SocialListItem(social: MapSocial, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFF0F172A),
        border = BorderStroke(2.dp, if (isSelected) Color(0xFFA855F7) else Color(0xFF334155)),
        shadowElevation = 0.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                AsyncImage(
                    model = social.avatar,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp).clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.SpaceBetween) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(social.title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White, maxLines = 1)
                            Text("${social.userName} · ${social.timePosted}", fontSize = 13.sp, color = Color(0xFF94A3B8))
                        }
                        Box(
                            modifier = Modifier.size(40.dp).clip(RoundedCornerShape(12.dp)).background(social.categoryColor),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(social.categoryIcon, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        }
                    }
                    Row(modifier = Modifier.padding(top = 12.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(Icons.Default.Place, contentDescription = null, tint = Color(0xFF94A3B8), modifier = Modifier.size(14.dp))
                            Text(social.distance, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color(0xFF94A3B8))
                        }
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(Icons.Default.Groups, contentDescription = null, tint = Color(0xFF94A3B8), modifier = Modifier.size(14.dp))
                            Text("${social.participantCount} joined", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color(0xFF94A3B8))
                        }
                    }
                }
            }
            Button(
                onClick = { },
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp).height(44.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues()
            ) {
                Box(modifier = Modifier.fillMaxSize().background(social.gradient), contentAlignment = Alignment.Center) {
                    Text("I'm Down", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Preview
@Composable
fun ExplorePreview(){
    Explore(PaddingValues(0.dp))
}