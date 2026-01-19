package com.shivam.downn.react

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
fun ActivityDetailScreen(
    activityId: Int,
    innerPadding: PaddingValues,
    onClose: () -> Unit,
    onOpenChat: () -> Unit,
    viewModel: ActivityDetailViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(activityId) {
        viewModel.loadActivityDetails(activityId)
    }

    when (val currentState = state) {
        is ActivityDetailState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is ActivityDetailState.Error -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Error: ${currentState.message}", color = Color.Red)
            }
        }
        is ActivityDetailState.Success -> {
            val activity = currentState.activity
            ActivityDetail(
                outerPadding = innerPadding,
                activity = activity,
                onClose = onClose,
                onOpenChat = onOpenChat
            )
        }
    }
}
