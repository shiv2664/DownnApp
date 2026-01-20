package com.shivam.downn.ui.screens.activity_detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import android.app.Activity
import androidx.core.view.WindowCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
fun ActivityDetailScreen(
    activityId: Int,
    onClose: () -> Unit,
    onOpenChat: () -> Unit,
    onViewProfile: (userId: Int) -> Unit,
    viewModel: ActivityDetailViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color(0xFF0F172A).toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    LaunchedEffect(activityId) {
        viewModel.loadActivityDetails(activityId)
    }

    when (val currentState = state) {
        is ActivityDetailState.Loading -> {
            Box(modifier = Modifier.fillMaxSize().background(Color(0xFF0F172A)), contentAlignment = Alignment.Center) {
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
                activity = activity,
                onClose = onClose,
                onOpenChat = onOpenChat,
                onViewProfile = {id-> onViewProfile(id.toInt())}
            )
        }
    }
}
