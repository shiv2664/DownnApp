package com.shivam.downn.ui.screens.explore

import com.shivam.downn.utils.ImageUtils

import androidx.compose.animation.*
import com.shivam.downn.R
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
import android.content.Context
import androidx.compose.animation.core.LinearEasing
import androidx.core.view.WindowCompat
import coil.compose.AsyncImage
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.asImageBitmap
import kotlinx.coroutines.launch
import com.google.android.gms.maps.model.LatLng
import com.shivam.downn.ui.components.FancyMap
import com.google.maps.android.compose.MarkerComposable
import com.google.maps.android.compose.rememberMarkerState
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import coil.compose.rememberAsyncImagePainter
import androidx.compose.ui.unit.LayoutDirection
import androidx.lifecycle.compose.LocalLifecycleOwner
import coil.ImageLoader
import coil.request.ErrorResult
import coil.request.SuccessResult
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.rememberCameraPositionState
import com.shivam.downn.ui.screens.feed.CategoryChip
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel


import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

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

val pinColors = listOf(
    Color(0xFF3B82F6), // Blue
    Color(0xFF06B6D4), // Cyan
    Color(0xFFEC4899), // Pink
    Color(0xFFA855F7), // Purple
    Color(0xFFF97316), // Orange
    Color(0xFFEF4444), // Red
    Color(0xFF22C55E), // Green
    Color(0xFF14B8A6)  // Teal
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreRoute(
    outerPadding: PaddingValues,
    onSocialClick: (SocialResponse) -> Unit = {},
    viewModel: ExploreViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Set up location permission launcher
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val isGranted = permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) ||
                permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false)
        if (isGranted) {
            viewModel.updateCity(context)
        }
    }

    // Check permissions on mount
    LaunchedEffect(Unit) {
        val hasFine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val hasCoarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        
        if (hasFine || hasCoarse) {
            viewModel.updateCity(context)
        } else {
            locationPermissionLauncher.launch(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
            )
        }
    }

    // Refresh explore when screen resumes (user switches back to this tab)
    val state by viewModel.state.collectAsState()

    ExploreContent(
        state = state,
        outerPadding = outerPadding,
        onSocialClick = onSocialClick,
        onCategorySelected = { category ->
            viewModel.fetchSocials(category = category)
        },
        onFetchAll = { viewModel.refresh() },
        onLoadMore = { viewModel.loadMore() },
        onRefreshLocation = { 
            viewModel.updateCity(context)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreContent(
    state: NetworkResult<List<SocialResponse>>,
    outerPadding: PaddingValues,
    onSocialClick: (SocialResponse) -> Unit = {},
    onCategorySelected: (String) -> Unit = {},
    onFetchAll: () -> Unit = {},
    onLoadMore: () -> Unit = {},
    onRefreshLocation: () -> Unit = {}
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
                        val categoryEmoji = SocialCategory.entries.find { it.name.equals(social.category, ignoreCase = true) }?.emoji ?: "📌"
                        
                        var bitmap by remember { mutableStateOf<Bitmap?>(null) }
                        val context = LocalContext.current
                        
                        LaunchedEffect(social.userAvatar) {
                            urlToBitmap(
                                scope = this,
                                imageURL = ImageUtils.getFullImageUrl(social.userAvatar ?: ""),
                                context = context,
                                onSuccess = { bitmap = it },
                                onError = { /* On error, bitmap remains null (placeholder) */ }
                            )
                        }
                        
                        val pinColor = pinColors[(social.id.hashCode() % pinColors.size + pinColors.size) % pinColors.size]

                        MarkerComposable(
                            state = rememberMarkerState(position = LatLng(lat, lng)),
                            title = social.title,
                            anchor = Offset(0.5f, 1.0f),
                            keys = arrayOf(social.id, bitmap?.hashCode() ?: 0), // Force redraw when bitmap loads
                            onClick = {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(index)
                                }
                                true
                            }
                        ) {
                            MapPin(
                                bitmap = bitmap,
                                isBusiness = social.socialType == SocialType.BUSINESS,
                                categoryEmoji = categoryEmoji,
                                isSelected = pagerState.currentPage == index,
                                baseColor = pinColor
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
                    Box(modifier = Modifier.fillMaxWidth().height(180.dp).padding(horizontal = 16.dp), contentAlignment = Alignment.Center) {
                        com.shivam.downn.ui.components.ShimmerCardItem()
                    }
                } else if (state is NetworkResult.Error) {
                    Box(modifier = Modifier.fillMaxWidth().height(180.dp), contentAlignment = Alignment.Center) {
                        com.shivam.downn.ui.components.EmptyState(
                            icon = androidx.compose.material.icons.Icons.Default.ErrorOutline,
                            title = "Error",
                            description = state.message ?: "Something went wrong",
                            actionLabel = "Retry",
                            onActionClick = onFetchAll,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                } else if (socials.isEmpty()) {
                    Box(modifier = Modifier.fillMaxWidth().height(180.dp), contentAlignment = Alignment.Center) {
                        com.shivam.downn.ui.components.EmptyState(
                            icon = androidx.compose.material.icons.Icons.Default.EventBusy,
                            title = "No Moves Nearby",
                            description = "Try changing filters or search a different area.",
                            actionLabel = "Refresh",
                            onActionClick = onFetchAll,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                } else {
                    HorizontalPager(
                        state = pagerState,
                        contentPadding = PaddingValues(horizontal = 32.dp),
                        pageSpacing = 16.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) { page ->
                        // Add safety check
                        if (page < socials.size) {
                            val social = socials[page]

                            // Trigger load more when reaching the last item
                            if (page == socials.size - 1) {
                                LaunchedEffect(Unit) {
                                    onLoadMore()
                                }
                            }

                            val categoryEmoji = SocialCategory.entries.find { it.name.equals(social.category, ignoreCase = true) }?.emoji ?: "📌"

                            ExploreCard(
                                social = social,
                                categoryEmoji = categoryEmoji,
                                onClick = { onSocialClick(social) }
                            )
                        }
                    }
                }
            }

            // Current Location FAB
            FloatingActionButton(
                onClick = onRefreshLocation,
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
                        model = ImageUtils.getFullImageUrl(social.userAvatar),
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
                                model = ImageUtils.getFullImageUrl(avatar),
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

            }
        }
    }
}




@Composable
private fun MapPin(
    bitmap: Bitmap?,
    isBusiness: Boolean,
    categoryEmoji: String,
    isSelected: Boolean,
    baseColor: Color
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.2f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessLow)
    )

    Box(
        modifier = Modifier
            .size(60.dp, 70.dp), // Fixed size buffer to prevent clipping
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.scale(scale).padding(bottom = 5.dp) // Lift up slightly to leave room for shadow spreading down
        ) {
            // Pin Head
            Surface(
                modifier = Modifier.size(42.dp),
                shape = CircleShape,
                color = Color.White,
                shadowElevation = if (isSelected) 8.dp else 4.dp
            ) {
                // Avatar Content
                Box(
                    modifier = Modifier.padding(2.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (bitmap != null) {
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color(0xFFF1F5F9), CircleShape)
                        ) {
                            Text(
                                text = categoryEmoji,
                                fontSize = 20.sp
                            )
                        }
                    }
                }
            }

            // The "Tail" pointer
            Canvas(modifier = Modifier.size(width = 12.dp, height = 8.dp).offset(y = (-2).dp)) {
                val path = Path().apply {
                    moveTo(0f, 0f)
                    lineTo(size.width, 0f)
                    lineTo(size.width / 2, size.height)
                    close()
                }
                drawPath(path, color = Color.White)

                val inset = 2.dp.toPx()
                val innerPath = Path().apply {
                    moveTo(inset, 0f)
                    lineTo(size.width - inset, 0f)
                    lineTo(size.width / 2, size.height - inset)
                    close()
                }
                // Use same gradient for tail interior
//                drawPath(innerPath, color = if (isBusiness) Color(0xFFF97316) else baseColor)
            }
        }
    }
}


@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
fun PreviewMapPin() {
    Row(
        modifier = Modifier
            .padding(20.dp)
            .width(300.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Normal Pin (Blue)
        MapPin(
            bitmap = null,
            isBusiness = false,
            categoryEmoji = "🏀",
            isSelected = false,
            baseColor = Color(0xFF3B82F6) // Blue
        )
        
        // Business Pin (Orange)
        MapPin(
            bitmap = null,
            isBusiness = true,
            categoryEmoji = "",
            isSelected = false,
            baseColor = Color(0xFFF97316)
        )
        
        // Selected Pin (Pink)
        MapPin(
            bitmap = null,
            isBusiness = false,
            categoryEmoji = "🎵",
            isSelected = true,
            baseColor = Color(0xFFEC4899)
        )
    }
}

fun urlToBitmap(
    scope: CoroutineScope,
    imageURL: String,
    context: Context,
    onSuccess: (bitmap: Bitmap) -> Unit,
    onError: (error: Throwable) -> Unit
) {
    var bitmap: Bitmap? = null
    val loadBitmap = scope.launch(Dispatchers.IO) {
        val loader = ImageLoader(context)
        val request = ImageRequest.Builder(context)
            .data(imageURL)
            .allowHardware(false)
            .build()
        val result = loader.execute(request)
        if (result is SuccessResult) {
            bitmap = (result.drawable as BitmapDrawable).bitmap
        } else if (result is ErrorResult) {
            cancel(result.throwable.localizedMessage ?: "ErrorResult", result.throwable)
        }
    }
    loadBitmap.invokeOnCompletion { throwable ->
        bitmap?.let {
            onSuccess(it)
        } ?: throwable?.let {
            onError(it)
        } ?: onError(Throwable("Undefined Error"))
    }
}
