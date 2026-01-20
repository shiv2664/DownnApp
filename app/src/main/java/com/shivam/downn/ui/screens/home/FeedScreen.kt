package com.shivam.downn.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.shivam.downn.ActivityCategory
import com.shivam.downn.data.models.ActivityResponse
import com.shivam.downn.ui.ActivityCard
import com.shivam.downn.ui.CategoryChip

@Composable
fun FeedScreen(
    outerPadding: PaddingValues,
    onCardClick: (Int) -> Unit,
    onJoinedClick: () -> Unit
) {
    val viewModel = hiltViewModel<FeedViewModel>()
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = { 
            FeedTopBar(onCategorySelected = { category ->
                viewModel.fetchActivities(category)
            }) 
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize().background(Color(0xFF0F172A))
        ) {
            when (val currentState = state) {
                is FeedState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is FeedState.Error -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "Error: ${currentState.message}", color = Color.Red)
                        Button(onClick = { viewModel.fetchActivities() }) {
                            Text("Retry")
                        }
                    }
                }
                is FeedState.Success -> {
                    ActivityList(
                        activities = currentState.activities,
                        paddingValues = paddingValues,
                        onCardClick = onCardClick,
                        onJoinClick = { activityId ->
                            viewModel.joinActivity(activityId)
                            onJoinedClick()
                        }
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
                        onCategorySelected("SPORTS") // Defaulting to SPORTS as per requirement for now
                    }
                )
            }

            items(ActivityCategory.entries.toTypedArray()) { category ->
                CategoryChip(
                    label = category.displayName,
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
fun ActivityList(
    activities: List<ActivityResponse>,
    paddingValues: PaddingValues,
    onCardClick: (Int) -> Unit,
    onJoinClick: (Int) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(paddingValues),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 120.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(
            items = activities,
            key = { it.id }
        ) { activity ->
            ActivityCard(
                userName = activity.userName ?: "Unknown",
                userAvatar = activity.userAvatar ?: "",
                activityTitle = activity.title,
                description = activity.description?:"",
                category = activity.category,
                categoryEmoji = "📍", // Default emoji or map from category
                timeAgo = activity.timeAgo ?: "Just now",
                distance = activity.distance ?: "Nearby",
                participantCount = activity.participantCount,
                maxParticipants = activity.maxParticipants,
                participantAvatars = activity.participantAvatars,
                onCardClick = { onCardClick(activity.id) },
                onJoinClick = { onJoinClick(activity.id) }
            )
        }
    }
}