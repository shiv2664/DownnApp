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
import com.shivam.downn.data.network.NetworkResult
import com.shivam.downn.data.models.SocialResponse

@Composable
fun SocialDetailScreen(
    socialId: Int,
    onClose: () -> Unit,
    onOpenChat: () -> Unit,
    onViewProfile: (userId: Int) -> Unit,
    viewModel: SocialDetailViewModel = hiltViewModel()
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

    LaunchedEffect(socialId) {
        viewModel.loadSocialDetails(socialId)
    }

    when (val currentState = state) {
        is NetworkResult.Loading -> {
            Box(modifier = Modifier.fillMaxSize().background(Color(0xFF0F172A)), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is NetworkResult.Error -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Error: ${currentState.message}", color = Color.Red)
            }
        }
        is NetworkResult.Success -> {
            val social = currentState.data ?: return
            SocialDetail(
                social = social,
                onClose = onClose,
                onOpenChat = onOpenChat,
                onViewProfile = {id-> onViewProfile(id.toInt())}
            )
        }
    }
}
