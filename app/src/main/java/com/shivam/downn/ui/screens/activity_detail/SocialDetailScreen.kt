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
import androidx.compose.runtime.DisposableEffect
import androidx.core.view.WindowCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.shivam.downn.data.network.NetworkResult
import com.shivam.downn.data.models.SocialResponse

@Composable
fun SocialDetailRoute(
    socialId: Int,
    onClose: () -> Unit,
    onOpenChat: () -> Unit,
    onViewProfile: (userId: Long, isBusiness: Boolean) -> Unit,
    onSeeAllParticipants: (socialId: Int) -> Unit,
    viewModel: SocialDetailViewModel = hiltViewModel()
) {
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    
    // Refresh social details when screen resumes
    DisposableEffect(lifecycleOwner, socialId) {
        val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
            if (event == androidx.lifecycle.Lifecycle.Event.ON_RESUME) {
                viewModel.loadSocialDetails(socialId)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
    
    LaunchedEffect(socialId) {
        viewModel.loadSocialDetails(socialId)
    }
    
    val state by viewModel.state.collectAsState()
    val leaveState by viewModel.leaveState.collectAsState()
    val removeState by viewModel.removeState.collectAsState()
    val deleteState by viewModel.deleteState.collectAsState()
    
    SocialDetailContent(
        state = state,
        currentUserId = viewModel.currentUserId,
        leaveState = leaveState,
        removeState = removeState,
        deleteState = deleteState,
        socialId = socialId,
        onClose = onClose,
        onOpenChat = onOpenChat,
        onViewProfile = onViewProfile,
        onSeeAllParticipants = onSeeAllParticipants,
        onJoinSocial = { id -> viewModel.joinSocial(id) },
        onLeaveSocial = { id -> viewModel.leaveSocial(id) },
        onRemoveParticipant = { sId, pId -> viewModel.removeParticipant(sId, pId) },
        onDeleteActivity = { id -> viewModel.deleteActivity(id) }
    )
}

@Composable
fun SocialDetailContent(
    state: NetworkResult<SocialResponse>,
    currentUserId: Long,
    leaveState: NetworkResult<Unit>?,
    removeState: NetworkResult<Unit>?,
    deleteState: NetworkResult<Unit>? = null,
    socialId: Int,
    onClose: () -> Unit,
    onOpenChat: () -> Unit,
    onViewProfile: (userId: Long, isBusiness: Boolean) -> Unit,
    onSeeAllParticipants: (socialId: Int) -> Unit,
    onJoinSocial: (Int) -> Unit,
    onLeaveSocial: (Int) -> Unit,
    onRemoveParticipant: (Int, Long) -> Unit,
    onDeleteActivity: (Int) -> Unit = {}
) {
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color(0xFF0F172A).toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
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
                currentUserId = currentUserId,
                leaveState = leaveState,
                removeState = removeState,
                deleteState = deleteState,
                onClose = onClose,
                onOpenChat = onOpenChat,
                onViewProfile = onViewProfile,
                onSeeAllParticipants = { onSeeAllParticipants(socialId) },
                onJoinSocial = { id -> onJoinSocial(id) },
                onLeaveSocial = { id -> onLeaveSocial(id) },
                onRemoveParticipant = { sId, pId -> onRemoveParticipant(sId, pId) },
                onDeleteActivity = { id -> onDeleteActivity(id) }
            )
        }
    }
}
