package com.shivam.downn.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.shivam.downn.data.models.ReportRequest
import com.shivam.downn.data.network.NetworkResult

@Composable
fun PublicProfileRoute(
    userId: Long,
    onClose: () -> Unit,
    onFollowClick: () -> Unit = {},
    viewModel: PublicProfileViewModel = hiltViewModel()
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

    // Report dialog state
    var showReportDialog by remember { mutableStateOf(false) }
    var reportReason by remember { mutableStateOf("") }

    // Block confirmation dialog state
    var showBlockConfirmDialog by remember { mutableStateOf(false) }

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
                    onFollowClick = {
                        if (profileData.isFollowing) {
                            viewModel.unfollowUser(profileData.userId)
                        } else {
                            viewModel.followUser(profileData.userId)
                        }
                    },
                    activeProfile = profileData,
                    onBlockClick = {
                        if (profileData.isBlocked) {
                            viewModel.unblockUser(profileData.userId)
                        } else {
                            showBlockConfirmDialog = true
                        }
                    },
                    onReportClick = { showReportDialog = true },
                    isBlocked = profileData.isBlocked
                )

                // Block confirmation dialog
                if (showBlockConfirmDialog) {
                    AlertDialog(
                        onDismissRequest = { showBlockConfirmDialog = false },
                        title = { Text("Block ${profileData.name}?", color = Color.White, fontWeight = FontWeight.Bold) },
                        text = {
                            Text(
                                "They won't be able to see your activities, and you won't see theirs. They won't be notified.",
                                color = Color(0xFF94A3B8),
                                fontSize = 14.sp
                            )
                        },
                        confirmButton = {
                            Button(
                                onClick = {
                                    viewModel.blockUser(profileData.userId)
                                    showBlockConfirmDialog = false
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                            ) {
                                Text("Block")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showBlockConfirmDialog = false }) {
                                Text("Cancel", color = Color(0xFF94A3B8))
                            }
                        },
                        containerColor = Color(0xFF1E293B)
                    )
                }

                // Report dialog
                if (showReportDialog) {
                    AlertDialog(
                        onDismissRequest = { showReportDialog = false },
                        title = { Text("Report ${profileData.name}", color = Color.White, fontWeight = FontWeight.Bold) },
                        text = {
                            Column {
                                Text(
                                    "Please describe why you are reporting this user:",
                                    color = Color(0xFF94A3B8),
                                    fontSize = 14.sp
                                )
                                OutlinedTextField(
                                    value = reportReason,
                                    onValueChange = { reportReason = it },
                                    modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                                    placeholder = { Text("Reason...", color = Color(0xFF64748B)) },
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White,
                                        focusedBorderColor = Color(0xFF6366F1),
                                        unfocusedBorderColor = Color(0xFF334155)
                                    ),
                                    maxLines = 3
                                )
                            }
                        },
                        confirmButton = {
                            Button(
                                onClick = {
                                    if (reportReason.isNotBlank()) {
                                        viewModel.reportContent(
                                            ReportRequest(
                                                reason = reportReason,
                                                reportedUserId = profileData.userId
                                            )
                                        )
                                        showReportDialog = false
                                        reportReason = ""
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                                enabled = reportReason.isNotBlank()
                            ) {
                                Text("Submit Report")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = {
                                showReportDialog = false
                                reportReason = ""
                            }) {
                                Text("Cancel", color = Color(0xFF94A3B8))
                            }
                        },
                        containerColor = Color(0xFF1E293B)
                    )
                }
            }
        }
    }
}

