package com.shivam.downn.ui.screens.activity_detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.shivam.downn.data.models.ParticipantResponse

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.shivam.downn.data.network.NetworkResult

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParticipantsRoute(
    socialId: Int,
    onClose: () -> Unit,
    onViewProfile: (userId: Long) -> Unit,
    viewModel: SocialDetailViewModel = hiltViewModel()
) {
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    
    // Refresh participants when screen resumes
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
    val removeState by viewModel.removeState.collectAsState()
    val currentUserId = viewModel.currentUserId
    
    ParticipantsContent(
        state = state,
        removeState = removeState,
        currentUserId = currentUserId,
        socialId = socialId,
        onClose = onClose,
        onViewProfile = onViewProfile,
        onRemoveParticipant = { sId, pId -> viewModel.removeParticipant(sId, pId) },
        onResetStates = { viewModel.resetStates() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParticipantsContent(
    state: NetworkResult<com.shivam.downn.data.models.SocialResponse>,
    removeState: NetworkResult<Unit>?,
    currentUserId: Long,
    socialId: Int,
    onClose: () -> Unit,
    onViewProfile: (userId: Long) -> Unit,
    onRemoveParticipant: (Int, Long) -> Unit,
    onResetStates: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(removeState) {
        if (removeState is NetworkResult.Success) {
            snackbarHostState.showSnackbar("Participant removed")
            onResetStates()
        } else if (removeState is NetworkResult.Error) {
            snackbarHostState.showSnackbar(removeState?.message ?: "Failed to remove participant")
            onResetStates()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    val title = when (val s = state) {
                        is NetworkResult.Success -> "Participants (${s.data?.participants?.size ?: 0})"
                        else -> "Participants"
                    }
                    Text(
                        title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF0F172A)
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color(0xFF0F172A)
    ) { innerPadding ->
        when (val currentState = state) {
            is NetworkResult.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFFA855F7))
                }
            }
            is NetworkResult.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Error: ${currentState.message}", color = Color.Red)
                }
            }
            is NetworkResult.Success -> {
                val social = currentState.data
                val participants = social?.participants ?: emptyList()
                val isOwner = social?.userId?.toLong() == currentUserId

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentPadding = PaddingValues(20.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    items(participants) { participant ->
                        ParticipantRow(
                            participant = participant,
                            showRemove = isOwner && participant.id != currentUserId,
                            onClick = { onViewProfile(participant.id) },
                            onRemove = { onRemoveParticipant(socialId, participant.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ParticipantRow(
    participant: ParticipantResponse,
    showRemove: Boolean = false,
    onClick: () -> Unit,
    onRemove: () -> Unit = {}
) {
    Surface(
        onClick = onClick,
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AsyncImage(
                model = com.shivam.downn.utils.ImageUtils.getFullImageUrl(participant.avatar),
                contentDescription = null,
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape),
                alignment = Alignment.Center
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    participant.name,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    participant.role ?: "Participant",
                    color = Color(0xFF94A3B8),
                    fontSize = 14.sp
                )
            }

            if (showRemove) {
                TextButton(
                    onClick = onRemove,
                    colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFFEF4444))
                ) {
                    Text(
                        "Remove",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}
