package com.shivam.downn.ui.screens.feed

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.shivam.downn.data.network.NetworkResult

import com.shivam.downn.SocialCategory
import com.shivam.downn.data.models.SocialResponse
import com.shivam.downn.data.models.SocialType

@Composable
fun FeedRoute(
    onCardClick: (SocialType, Int) -> Unit,
    onJoinedClick: (SocialType, Int) -> Unit
) {
    val viewModel = hiltViewModel<FeedViewModel>()
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.fetchSocials()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
    
    val state by viewModel.state.collectAsState()

    FeedContent(
        state = state,
        currentUserId = viewModel.currentUserId,
        onCategorySelected = { category ->
            viewModel.fetchSocials("Denver", if (category != "All") category else null)
        },
        onCardClick = onCardClick,
        onJoinedClick = onJoinedClick,
        onRetry = { viewModel.fetchSocials() }
    )
}

@Composable
fun FeedContent(
    state: NetworkResult<List<SocialResponse>>,
    currentUserId: Long?,
    onCategorySelected: (String) -> Unit,
    onCardClick: (SocialType, Int) -> Unit,
    onJoinedClick: (SocialType, Int) -> Unit,
    onRetry: () -> Unit
) {
    Scaffold(
        topBar = { 
            FeedTopBar(onCategorySelected = onCategorySelected) 
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0F172A))
        ) {
            when (val currentState = state) {
                is NetworkResult.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is NetworkResult.Error -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "Error: ${currentState.message}", color = Color.Red)
                        Button(onClick = onRetry) {
                            Text("Retry")
                        }
                    }
                }
                is NetworkResult.Success -> {
                    MoveList(
                        socials = currentState.data ?: emptyList(),
                        currentUserId = currentUserId,
                        paddingValues = paddingValues,
                        onCardClick = onCardClick,
                        onJoinClick = onJoinedClick
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedTopBar(onCategorySelected: (String) -> Unit) {
    var selectedCategory by remember { mutableStateOf("All") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF0F172A))
    ) {
        TopAppBar(
            title = { Text("Downn", color = Color.White) },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color(0xFF0F172A)
            )
        )

        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                CategoryChip(
                    label = "All",
                    emoji = "🌟",
                    isSelected = selectedCategory == "All",
                    onClick = { 
                        selectedCategory = "All"
                        onCategorySelected("All")
                    }
                )
            }

            items(SocialCategory.entries.toTypedArray()) { category ->
                CategoryChip(
                    label = category.displayName.replace("Activity", "Move"),
                    emoji = category.emoji,
                    isSelected = selectedCategory == category.displayName,
                    onClick = { 
                        selectedCategory = category.displayName
                        onCategorySelected(category.name)
                    }
                )
            }
        }
    }
}

@Composable
fun MoveList(
    socials: List<SocialResponse>,
    currentUserId: Long?,
    paddingValues: PaddingValues,
    onCardClick: (SocialType, Int) -> Unit,
    onJoinClick: (SocialType,Int) -> Unit
) {
    val businessItems = listOf(
        SocialResponse(
            id = 16,
            userName = "The Daily Grind",
            userAvatar = "https://images.unsplash.com/photo-1509042239860-f550ce710b93?w=150",
            title = "Live Jazz Night 🎷",
            description = "Join us for a chill evening of live jazz and 20% off all brews!",
            category = "Food",
            city = "Delhi",
            locationName = "The Daily Grind",
            scheduledTime = "2026-01-21T20:00:00",
            maxParticipants = 100,
            participantCount = 45,
            socialType = SocialType.BUSINESS,
            timeAgo = "Just now",
            distance = "0.5 km away"
        ),
        SocialResponse(
            id = 17,
            userName = "Club Social",
            userAvatar = "https://images.unsplash.com/photo-1566737236500-c8ac1f852382?w=150",
            title = "Friday Night Fever 🕺",
            description = "The biggest party in town. Special discount for groups of 4!",
            category = "Party",
            city = "Delhi",
            locationName = "Club Social",
            scheduledTime = "2026-01-21T22:00:00",
            maxParticipants = 500,
            participantCount = 120,
            socialType = SocialType.BUSINESS,
            timeAgo = "2h ago",
            distance = "1.2 km away"
        )
    )
    val displaySocials = businessItems + socials

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(paddingValues),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 120.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(
            items = socials,
            key = { it.id }
        ) { social ->
            MoveCard(
                userName = social.userName ?: "Unknown",
                userAvatar = social.userAvatar ?: "",
                moveTitle = social.title,
                description = social.description?:"",
                category = social.category,
                categoryEmoji = "📍", // Default emoji or map from category
                timeAgo = social.timeAgo ?: "Just now",
                distance = social.distance ?: "Nearby",
                participantCount = social.participantCount,
                maxParticipants = social.maxParticipants,
                participantAvatars = social.participantAvatars,
                socialType = social.socialType,
                isRequested = social.requestedUserIds?.contains(currentUserId) == true,
                isRejected = social.rejectedUserIds?.contains(currentUserId) == true,
                isParticipant = social.participants.any { it.id == currentUserId },
                isOwner = social.userId?.toLong() == currentUserId,
                onCardClick = { onCardClick(social.socialType,social.id) },
                onJoinClick = { onJoinClick(social.socialType,social.id) }
            )
        }
    }
}
