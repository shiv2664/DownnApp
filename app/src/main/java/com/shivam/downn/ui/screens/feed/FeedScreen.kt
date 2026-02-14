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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EventBusy
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.shivam.downn.ui.components.EmptyState
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.shivam.downn.data.network.NetworkResult

import com.shivam.downn.SocialCategory
import com.shivam.downn.data.models.SocialResponse
import com.shivam.downn.data.models.SocialType
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Close

@Composable
fun FeedRoute(
    onCardClick: (SocialType, Int) -> Unit,
    onJoinedClick: (SocialType, Int) -> Unit
) {
    val viewModel = hiltViewModel<FeedViewModel>()
    val lifecycleOwner = LocalLifecycleOwner.current

    val state by viewModel.state.collectAsState()

    FeedContent(
        state = state,
        currentUserId = viewModel.currentUserId,
        onCategorySelected = { category ->
            viewModel.fetchSocials("Denver", if (category != "All") category else null)
        },
        onCardClick = onCardClick,
        onJoinedClick = onJoinedClick,
        onRetry = { viewModel.fetchSocials() },
        onLoadMore = { viewModel.loadMore() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedContent(
    state: NetworkResult<List<SocialResponse>>,
    currentUserId: Long?,
    onCategorySelected: (String) -> Unit,
    onCardClick: (SocialType, Int) -> Unit,
    onJoinedClick: (SocialType, Int) -> Unit,
    onRetry: () -> Unit,
    onLoadMore: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        topBar = { 
            FeedTopBar(
                onCategorySelected = onCategorySelected,
                onSearchQuery = { searchQuery = it }
            ) 
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0F172A))
                .padding(paddingValues)
        ) {
            val isRefreshing = state is NetworkResult.Loading
            
            androidx.compose.material3.pulltorefresh.PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = onRetry,
                modifier = Modifier.fillMaxSize()
            ) {
                when (val currentState = state) {
                    is NetworkResult.Loading -> {
                        if ((currentState.data ?: emptyList()).isEmpty()) {
                             com.shivam.downn.ui.components.ShimmerLoadingList()
                        } else {
                            // Show list with loading indicator on top (handled by PullToRefreshBox)
                            MoveList(
                                socials = currentState.data ?: emptyList(),
                                currentUserId = currentUserId,
                                paddingValues = PaddingValues(0.dp), // Padding handled by Box
                                onCardClick = onCardClick,
                                onJoinClick = onJoinedClick,
                                onLoadMore = onLoadMore
                            )
                        }
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
                        val allSocials = currentState.data ?: emptyList()
                        val socials = if (searchQuery.isBlank()) allSocials
                                      else allSocials.filter { s ->
                                          s.title.contains(searchQuery, ignoreCase = true) ||
                                          (s.description?.contains(searchQuery, ignoreCase = true) == true) ||
                                          s.category.contains(searchQuery, ignoreCase = true)
                                      }
                        if (socials.isEmpty()) {
                            com.shivam.downn.ui.components.EmptyState(
                                icon = androidx.compose.material.icons.Icons.Default.EventBusy,
                                title = "No Moves Found",
                                description = "Looks like there's nothing happening right now. Be the first to host one!",
                                actionLabel = "Refresh",
                                onActionClick = onRetry,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        } else {
                            MoveList(
                                socials = socials,
                                currentUserId = currentUserId,
                                paddingValues = PaddingValues(0.dp),
                                onCardClick = onCardClick,
                                onJoinClick = onJoinedClick,
                                onLoadMore = onLoadMore
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedTopBar(onCategorySelected: (String) -> Unit, onSearchQuery: (String) -> Unit = {}) {
    var selectedCategory by remember { mutableStateOf("All") }
    var searchQuery by remember { mutableStateOf("") }
    var showSearch by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF0F172A))
    ) {
        TopAppBar(
            title = {
                if (showSearch) {
                    TextField(
                        value = searchQuery,
                        onValueChange = {
                            searchQuery = it
                            onSearchQuery(it)
                        },
                        placeholder = { Text("Search moves...", color = Color(0xFF64748B)) },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFF1E293B),
                            unfocusedContainerColor = Color(0xFF1E293B),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            cursorColor = Color(0xFF818CF8),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    Text("Downn", color = Color.White)
                }
            },
            actions = {
                /*
                IconButton(onClick = {
                    showSearch = !showSearch
                    if (!showSearch) {
                        searchQuery = ""
                        onSearchQuery("")
                    }
                }) {
                    Icon(
                        if (showSearch) Icons.Default.Close else Icons.Default.Search,
                        contentDescription = if (showSearch) "Close search" else "Search",
                        tint = Color.White
                    )
                }
                */
            },
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
    onJoinClick: (SocialType,Int) -> Unit,
    onLoadMore: () -> Unit
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
            // Trigger load more when reaching the last few items
            if (social == socials.last()) {
                LaunchedEffect(Unit) {
                    onLoadMore()
                }
            }
        
            val displayName = if (social.socialType == SocialType.BUSINESS && social.profile != null)
                social.profile.name else (social.userName ?: "Unknown")
            val displayAvatar = if (social.socialType == SocialType.BUSINESS && social.profile != null)
                (social.profile.avatar ?: "") else (social.userAvatar ?: "")

            MoveCard(
                userName = displayName,
                userAvatar = displayAvatar,
                moveTitle = social.title,
                description = social.description?:"",
                category = social.category,
                categoryEmoji = "📍", // Default emoji or map from category
                timeAgo = if (social.scheduledTime != null) com.shivam.downn.utils.DateUtils.formatEventTime(social.scheduledTime) else (social.timeAgo ?: "Just now"),
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
        
        // Show loading indicator at the bottom if needed - handled by parent state for now or add footer item
    }
}
