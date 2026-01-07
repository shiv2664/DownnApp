package com.shivam.downn.ui.screens.home

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.shivam.downn.ActivityCategory
import com.shivam.downn.DummyData
import com.shivam.downn.DummyData.dummyActivities
import com.shivam.downn.ui.ActivityCard
import com.shivam.downn.ui.CategoryChip

@Composable
fun FeedScreen(innerPadding: PaddingValues,onCardClick: () -> Unit,onJoinedClick:()-> Unit) {

    val viewModel = hiltViewModel<FeedViewModel>()
//    val state by viewModel.state.collectAsState()
    Scaffold(
        topBar = { FeedTopBar() },
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        floatingActionButton = {
            FloatingActionButton(
                onClick = {},
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Create Activity"
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            ActivityList(
                dummyActivities, paddingValues,
                onCardClick = {onCardClick},
                onJoinedClick={
                    Log.d("MyTag","Feed Screen onJoinedClick")
                    onJoinedClick()
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedTopBar() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        TopAppBar(
            title = { Text("Downn") },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface
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
                    isSelected = true,
                    onClick = {  }
                )
            }

            items(ActivityCategory.entries.toTypedArray()) { category ->
                CategoryChip(
                    label = category.displayName,
                    emoji = category.emoji,
                    isSelected =true,
                    onClick = { }
                )
            }
        }
    }
}

@Composable
fun ActivityList(
    activities: List<DummyData.ActivityItem>,
    paddingValues: PaddingValues,
    onCardClick:()-> Unit,
    onJoinedClick: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(paddingValues),
        contentPadding = PaddingValues(16.dp, end = 16.dp, bottom = 120.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(
            items = activities,
            key = { it.id }
        ) { activity ->
            ActivityCard(
                activity?.userName?:"",
                activity?.userAvatar?:"",
                activity.activityTitle,
                activity?.description?:"",
                activity.category,
                activity.categoryEmoji,
                activity.timeAgo,
                activity.distance,
                activity.participantCount,
                activity.maxParticipants,
                activity.participantAvatars,
                {onCardClick()},
                {onJoinedClick()},
            )
        }
    }
}