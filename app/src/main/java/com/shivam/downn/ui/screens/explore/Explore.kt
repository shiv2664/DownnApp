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
import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.Stroke
import com.shivam.downn.data.models.SocialType
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import android.app.Activity
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.core.view.WindowCompat
import coil.compose.AsyncImage
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.ui.graphics.SolidColor
import kotlinx.coroutines.launch
import com.google.android.gms.maps.model.LatLng
import com.shivam.downn.ui.components.FancyMap
import com.google.maps.android.compose.MarkerComposable
import com.google.maps.android.compose.rememberMarkerState
import android.graphics.Bitmap
import androidx.compose.ui.platform.LocalContext
import coil.request.ImageRequest
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.shivam.downn.data.network.NetworkResult
import com.shivam.downn.SocialCategory
import com.shivam.downn.data.models.SocialResponse
import com.shivam.downn.ui.screens.feed.MoveCard

import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.ui.unit.LayoutDirection
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.rememberCameraPositionState
import com.shivam.downn.ui.screens.feed.CategoryChip


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
    val latLng: LatLng,
    val socialType: SocialType = SocialType.PERSONAL
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreRoute(
    outerPadding: PaddingValues,
    onSocialClick: (SocialResponse) -> Unit = {},
    viewModel: ExploreViewModel = hiltViewModel()
) {
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    
    // Refresh explore when screen resumes (user switches back to this tab)
    DisposableEffect(lifecycleOwner) {
        val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
            if (event == androidx.lifecycle.Lifecycle.Event.ON_RESUME) {
                viewModel.fetchSocials()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
    
    val state by viewModel.state.collectAsState()
    
    ExploreContent(
        state = state,
        outerPadding = outerPadding,
        onSocialClick = onSocialClick,
        onCategorySelected = { category ->
            viewModel.fetchSocials(category = category)
        },
        onFetchAll = { viewModel.fetchSocials() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreContent(
    state: NetworkResult<List<SocialResponse>>,
    outerPadding: PaddingValues,
    onSocialClick: (SocialResponse) -> Unit = {},
    onCategorySelected: (String) -> Unit = {},
    onFetchAll: () -> Unit = {}
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    var selectedFilter by remember { mutableStateOf("All") }
    
    val socials = when (val currentState = state) {
        is NetworkResult.Success -> currentState.data ?: emptyList()
        else -> emptyList()
    }

    // Pager and Camera State
    val pagerState = rememberPagerState(pageCount = { socials.size })
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(37.4219999, -122.0840575), 12f)
    }
    val coroutineScope = rememberCoroutineScope()

    // Sync Pager with Map
    LaunchedEffect(pagerState.currentPage, socials) {
        if (socials.isNotEmpty() && pagerState.currentPage < socials.size) {
            val social = socials[pagerState.currentPage]
            val lat = social.latitude
            val lng = social.longitude
            if (lat != null && lng != null) {
                cameraPositionState.animate(
                    update = CameraUpdateFactory.newLatLngZoom(LatLng(lat, lng), 15f),
                    durationMs = 1000
                )
            }
        }
    }

    Scaffold(
        containerColor = Color.Black
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(top = 0.dp, bottom = outerPadding.calculateBottomPadding())) {
            // Fancy Stylized Map Background
            FancyMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState
            ) {
                socials.forEachIndexed { index, social ->
                    val lat = social.latitude
                    val lng = social.longitude
                    if (lat != null && lng != null) {
                        MarkerComposable(
                            state = rememberMarkerState(position = LatLng(lat, lng)),
                            title = social.title,
                            anchor = Offset(0.5f, 1.0f),
                            onClick = {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(index)
                                }
                                true
                            }
                        ) {
                            val categoryEmoji = SocialCategory.entries.find { it.name.equals(social.category, ignoreCase = true) }?.emoji ?: "📌"
                            MapPin(
                                avatar = social.userAvatar ?: "",
                                isBusiness = social.socialType == SocialType.BUSINESS,
                                categoryEmoji = categoryEmoji,
                                isSelected = pagerState.currentPage == index
                            )
                        }
                    }
                }
            }

            // Gradient Overlays for better visibility
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color.Black.copy(alpha = 0.4f),
                                Color.Transparent,
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.5f)
                            )
                        )
                    )
            )

            // Header Controls (Filter Chips)
            Column(
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(top = 16.dp)
            ) {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        CategoryChip(
                            label = "All",
                            emoji = "🌟",
                            isSelected = selectedFilter == "All",
                            onClick = { 
                                selectedFilter = "All"
                                onFetchAll()
                            }
                        )
                    }
                    items(SocialCategory.entries.toTypedArray()) { category ->
                        CategoryChip(
                            label = category.displayName.replace("Activity", "Move"),
                            emoji = category.emoji,
                            isSelected = selectedFilter == category.displayName,
                            onClick = { 
                                selectedFilter = category.displayName
                                onCategorySelected(category.name)
                            }
                        )
                    }
                }
            }

            // Bottom Horizontal Pager
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .padding(bottom = 24.dp)
            ) {
                if (state is NetworkResult.Loading) {
                    Box(modifier = Modifier.fillMaxWidth().height(180.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color.White)
                    }
                } else if (socials.isEmpty() && state is NetworkResult.Success) {
                    Box(modifier = Modifier.fillMaxWidth().height(180.dp), contentAlignment = Alignment.Center) {
                        Text("No moves nearby", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                } else {
                    HorizontalPager(
                        state = pagerState,
                        contentPadding = PaddingValues(horizontal = 32.dp),
                        pageSpacing = 16.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) { page ->
                        val social = socials[page]
                        val categoryEmoji = SocialCategory.entries.find { it.name.equals(social.category, ignoreCase = true) }?.emoji ?: "📌"
                        
                        ExploreCard(
                            social = social,
                            categoryEmoji = categoryEmoji,
                            onClick = { onSocialClick(social) }
                        )
                    }
                }
            }

            // Current Location FAB
            FloatingActionButton(
                onClick = { /* Handle current location center */ },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .statusBarsPadding()
                    .padding(top = 80.dp, end = 16.dp)
                    .size(44.dp),
                shape = CircleShape,
                containerColor = Color(0xFF1E293B).copy(alpha = 0.8f),
                contentColor = Color.White
            ) {
                Icon(Icons.Default.MyLocation, contentDescription = "My Location", modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
private fun ExploreCard(
    social: SocialResponse,
    categoryEmoji: String,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp),
        shape = RoundedCornerShape(28.dp),
        color = Color(0xFF1E293B).copy(alpha = 0.85f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                // Large Avatar with Glow
                Box(contentAlignment = Alignment.Center) {
                    if (social.socialType == SocialType.BUSINESS) {
                        Box(
                            modifier = Modifier
                                .size(54.dp)
                                .background(Color(0xFFF97316).copy(alpha = 0.2f), CircleShape)
                        )
                    }
                    AsyncImage(
                        model = social.userAvatar,
                        contentDescription = null,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .border(1.5.dp, Color.White.copy(alpha = 0.2f), CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            social.title,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (social.socialType == SocialType.BUSINESS) {
                            Icon(Icons.Default.Verified, contentDescription = null, tint = Color(0xFF3B82F6), modifier = Modifier.size(16.dp))
                        }
                    }
                    Text(
                        "${social.userName} · ${social.distance ?: "Nearby"}",
                        fontSize = 14.sp,
                        color = Color(0xFF94A3B8)
                    )
                }

                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(categoryEmoji, fontSize = 20.sp)
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Participant Stack
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box {
                        social.participantAvatars?.take(3)?.forEachIndexed { index, avatar ->
                            AsyncImage(
                                model = avatar,
                                contentDescription = null,
                                modifier = Modifier
                                    .padding(start = (index * 16).dp)
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .border(1.dp, Color(0xFF1E293B), CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                    Text(
                        "${social.participantCount} joined",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF94A3B8)
                    )
                }

                // Action Button
                Button(
                    onClick = onClick, // Navigate to detail
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (social.socialType == SocialType.BUSINESS) Color(0xFFF97316) else Color(0xFFA855F7)
                    ),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Text("I'M DOWN", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                }
            }
        }
    }
}

@Composable
fun VibePulse(color: Color) {
    val infiniteTransition = rememberInfiniteTransition()

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 2.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200),
            repeatMode = RepeatMode.Restart
        )
    )

    Box(
        modifier = Modifier
            .size(48.dp)
            .graphicsLayer {
                scaleX = pulseScale
                scaleY = pulseScale
                alpha = pulseAlpha
            }
            .background(color, CircleShape)
    )
}


@Composable
private fun MapPin(
    avatar: String,
    isBusiness: Boolean,
    categoryEmoji: String,
    isSelected: Boolean
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.3f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessLow)
    )

    Box(
        modifier = Modifier
            .scale(scale),
        contentAlignment = Alignment.Center
    ) {
        if (isBusiness || isSelected) {
            VibePulse(if (isBusiness) Color(0xFFF97316) else Color(0xFFA855F7))
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(contentAlignment = Alignment.TopEnd) {
                // Pin Content (The Avatar)
                Surface(
                    modifier = Modifier.size(52.dp),
                    shape = CircleShape,
                    color = Color.White,
                    shadowElevation = if (isSelected) 12.dp else 6.dp
                ) {
                    Box(modifier = Modifier.padding(3.dp)) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(avatar)
                                .allowHardware(false)
                                .build(),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
                
                // Overlay Badge (Icon/Type)
                Box(
                    modifier = Modifier
                        .offset(x = 4.dp, y = (-4).dp)
                        .size(24.dp)
                        .background(
                            brush = if (isBusiness) Brush.horizontalGradient(listOf(Color(0xFFFDBA74), Color(0xFFF97316))) 
                                    else Brush.horizontalGradient(listOf(Color(0xFF9333EA), Color(0xFFDB2777))),
                            shape = CircleShape
                        )
                        .border(1.5.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (isBusiness) {
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(12.dp)
                        )
                    } else {
                        Text(categoryEmoji, fontSize = 10.sp)
                    }
                }
            }

            // The "Tail" pointer
            Canvas(modifier = Modifier.size(width = 16.dp, height = 12.dp).offset(y = (-4).dp)) {
                val path = androidx.compose.ui.graphics.Path().apply {
                    moveTo(0f, 0f)
                    lineTo(size.width, 0f)
                    lineTo(size.width / 2, size.height)
                    close()
                }
                drawPath(path, color = Color.White)
            }
        }
    }
}
