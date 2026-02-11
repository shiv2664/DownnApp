package com.shivam.downn.ui.screens.profile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.shivam.downn.data.network.NetworkResult

@Composable
fun PublicProfileRoute(
    userId: Long,
    onClose: () -> Unit,
    onFollowClick: () -> Unit = {},
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    
    // Refresh public profile when screen resumes
    DisposableEffect(lifecycleOwner, userId) {
        val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
            if (event == androidx.lifecycle.Lifecycle.Event.ON_RESUME) {
                viewModel.fetchUserDetails(userId)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
    
    LaunchedEffect(userId) {
        viewModel.fetchUserDetails(userId)
    }
    
    val viewedProfileResult by viewModel.viewedProfile.collectAsState()

    when (viewedProfileResult) {
        is NetworkResult.Loading, null -> {
            Scaffold(containerColor = Color(0xFF0F172A)) {
                Box(modifier = Modifier.fillMaxSize().padding(it), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
        }
        is NetworkResult.Error -> {
            Scaffold(containerColor = Color(0xFF0F172A)) {
                Box(modifier = Modifier.fillMaxSize().padding(it), contentAlignment = Alignment.Center) {
                    Text((viewedProfileResult as NetworkResult.Error).message ?: "Error", color = Color.White)
                }
            }
        }
        is NetworkResult.Success -> {
            val profileData = (viewedProfileResult as NetworkResult.Success).data
            if (profileData != null) {
                ProfileContent(
                    isOwnProfile = false,
                    outerPadding = PaddingValues(0.dp),
                    onClose = onClose,
                    onFollowClick = onFollowClick,
                    activeProfile = profileData // Pass fetched data
                )
            }
        }
    }
}
