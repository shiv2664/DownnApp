package com.shivam.downn.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.shivam.downn.data.network.NetworkResult
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onClose: () -> Unit,
    onLogout: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val logoutState by viewModel.logoutState.collectAsState()

    LaunchedEffect(logoutState) {
        if (logoutState is NetworkResult.Success) {
            val message = (logoutState as NetworkResult.Success<String?>).data ?: "Logged out successfully"
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            onLogout()
            viewModel.resetLogoutState()
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(
                        onClick = onClose,
                        modifier = Modifier
                            .padding(8.dp)
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White.copy(alpha = 0.1f))
                    ) {
                        Icon(Icons.Default.ChevronLeft, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0F172A))
            )
        },
        containerColor = Color(0xFF0F172A)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Account Section
            SettingsSection(title = "Account") {
                SettingsItem(icon = Icons.Default.Person, label = "Personal Information")
                SettingsItem(icon = Icons.Default.Lock, label = "Security")
                SettingsItem(icon = Icons.Default.PrivacyTip, label = "Privacy Settings")
            }

            // Preferences Section
            SettingsSection(title = "Preferences") {
                SettingsItem(icon = Icons.Default.Notifications, label = "Notifications")
                SettingsItem(icon = Icons.Default.Palette, label = "Appearance")
                SettingsItem(icon = Icons.Default.Language, label = "Language")
            }

            // Support Section
            SettingsSection(title = "Support") {
                SettingsItem(icon = Icons.Default.Help, label = "Help Center")
                SettingsItem(icon = Icons.Default.Info, label = "About Downn")
            }

            // Logout
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled = logoutState !is NetworkResult.Loading) { 
                        viewModel.logout() 
                    },
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFF1E293B).copy(alpha = 0.5f)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(Icons.Default.Logout, contentDescription = null, tint = Color(0xFFF87171))
                    if (logoutState is NetworkResult.Loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color(0xFFF87171),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Logout", color = Color(0xFFF87171), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }

            if (logoutState is NetworkResult.Error) {
                Text(
                    text = (logoutState as NetworkResult.Error).message ?: "Logout failed",
                    color = Color.Red,
                    fontSize = 12.sp,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
            
            Spacer(modifier = Modifier.height(40.dp))
            Text(
                "Version 1.0.0 (Build 22)",
                modifier = Modifier.align(Alignment.CenterHorizontally),
                color = Color(0xFF64748B),
                fontSize = 12.sp
            )
        }
    }
}

@Composable
private fun SettingsSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column {
        Text(
            text = title,
            color = Color(0xFF94A3B8),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(start = 12.dp, bottom = 12.dp)
        )
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color(0xFF1E293B).copy(alpha = 0.5f),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                content()
            }
        }
    }
}

@Composable
private fun SettingsItem(icon: ImageVector, label: String, onClick: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(icon, contentDescription = null, tint = Color(0xFFA855F7), modifier = Modifier.size(24.dp))
        Text(label, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color(0xFF475569), modifier = Modifier.size(20.dp))
    }
}


@Composable
@Preview
fun SettingsPreview() {
    SettingsScreen(onClose = {}, onLogout = {})
}
