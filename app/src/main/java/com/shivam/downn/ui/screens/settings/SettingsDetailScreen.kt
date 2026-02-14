package com.shivam.downn.ui.screens.settings

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil.compose.AsyncImage
import com.shivam.downn.data.network.NetworkResult

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsDetailScreen(
    title: String,
    onClose: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val userDetails by viewModel.userDetails.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title, color = Color.White, fontWeight = FontWeight.Bold) },
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
            when (title) {
                "Personal Information" -> {
                    when (val details = userDetails) {
                        is NetworkResult.Success -> {
                            val user = details.data
                            PersonalInfoContent(
                                viewModel = viewModel,
                                name = user?.name ?: "",
                                bio = user?.bio ?: "",
                                location = user?.location ?: "",
                                avatar = user?.avatar ?: "",
                                email = viewModel.getEmail()
                            )
                        }
                        is NetworkResult.Loading -> {
                            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(color = Color(0xFFA855F7))
                            }
                        }
                        else -> PersonalInfoContent(viewModel = viewModel, email = viewModel.getEmail())
                    }
                }
                "Security" -> SecurityContent()
                "Privacy Settings" -> PrivacyContent()
                "Notifications" -> NotificationsContent()
                "Appearance" -> AppearanceContent()
                "Language" -> LanguageContent()
                "Help Center" -> HelpCenterContent()
                "About Downn" -> AboutContent()
                else -> PlaceholderContent(title)
            }
        }
    }
}

@Composable
private fun PersonalInfoContent(
    viewModel: SettingsViewModel,
    name: String = "",
    bio: String = "",
    location: String = "",
    avatar: String = "",
    email: String = ""
) {
    val context = LocalContext.current
    var editableName by remember(name) { mutableStateOf(name) }
    var editableBio by remember(bio) { mutableStateOf(bio) }
    var editableLocation by remember(location) { mutableStateOf(location) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    val updateState by viewModel.updateState.collectAsState()

    LaunchedEffect(updateState) {
        when (val result = updateState) {
            is NetworkResult.Success -> {
                Toast.makeText(context, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
            }
            is NetworkResult.Error -> {
                Toast.makeText(context, result.message ?: "Update failed", Toast.LENGTH_SHORT).show()
            }
            else -> {}
        }
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Avatar section
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Box(contentAlignment = Alignment.BottomEnd) {
                Surface(
                    modifier = Modifier.size(120.dp),
                    shape = CircleShape,
                    color = Color.Transparent,
                    border = BorderStroke(2.dp, Brush.linearGradient(listOf(Color(0xFF9333EA), Color(0xFFDB2777))))
                ) {
                    AsyncImage(
                        model = selectedImageUri ?: com.shivam.downn.utils.ImageUtils.getFullImageUrl(avatar),
                        contentDescription = "Profile Photo",
                        modifier = Modifier.clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF9333EA))
                        .border(2.dp, Color(0xFF0F172A), CircleShape)
                        .clickable { photoPickerLauncher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.CameraAlt, contentDescription = "Edit Photo", tint = Color.White, modifier = Modifier.size(18.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Email (read-only)
        DetailTextField(label = "Email", value = email, onValueChange = {}, enabled = false)

        // Editable fields
        DetailTextField(label = "Full Name", value = editableName, onValueChange = { editableName = it })
        DetailTextField(label = "Bio", value = editableBio, onValueChange = { editableBio = it })
        DetailTextField(label = "Location", value = editableLocation, onValueChange = { editableLocation = it })
        
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            "Public profile information helps people get to know you before they join your activities.",
            color = Color(0xFF94A3B8),
            fontSize = 12.sp,
            lineHeight = 18.sp
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Button(
            onClick = { viewModel.updateUser(editableName, editableBio, editableLocation, selectedImageUri) },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFA855F7)),
            enabled = updateState !is NetworkResult.Loading
        ) {
            if (updateState is NetworkResult.Loading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else {
                Text("Save Changes", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}

@Composable
private fun SecurityContent() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        DetailItem(Icons.Default.Password, "Change Password")
        DetailItem(Icons.Default.PhonelinkLock, "Two-Factor Authentication", hasToggle = true)
        DetailItem(Icons.Default.History, "Login Activity")
        DetailItem(Icons.Default.Devices, "Recognized Devices")
    }
}

@Composable
private fun PrivacyContent() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        DetailItem(Icons.Default.Public, "Public Profile", hasToggle = true, initialValue = true)
        DetailItem(Icons.Default.Timer, "Show Activity Status", hasToggle = true, initialValue = true)
        DetailItem(Icons.Default.PersonAddDisabled, "Allow Tagging", hasToggle = true)
        DetailItem(Icons.Default.Block, "Blocked Users")
    }
}

@Composable
private fun NotificationsContent() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        DetailItem(Icons.Default.NotificationsActive, "Push Notifications", hasToggle = true, initialValue = true)
        // DetailItem(Icons.Default.Email, "Email Notifications", hasToggle = true) // Hidden for MVP
        // DetailItem(Icons.Default.Sms, "SMS Notifications", hasToggle = true) // Hidden for MVP
        // DetailItem(Icons.Default.Campaign, "Marketing Updates", hasToggle = true) // Hidden for MVP
    }
}

@Composable
private fun AppearanceContent() {
    var selectedTheme by remember { mutableStateOf("System") }
    
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Theme", color = Color(0xFF94A3B8), fontSize = 12.sp, fontWeight = FontWeight.Bold)
        
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color(0xFF1E293B).copy(alpha = 0.5f),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                listOf("Light", "Dark", "System").forEach { theme ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedTheme = theme }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        RadioButton(
                            selected = selectedTheme == theme,
                            onClick = { selectedTheme = theme },
                            colors = RadioButtonDefaults.colors(selectedColor = Color(0xFFA855F7), unselectedColor = Color(0xFF475569))
                        )
                        Text(theme, color = Color.White, fontSize = 16.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun LanguageContent() {
    var selectedLanguage by remember { mutableStateOf("English (US)") }
    
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = Color(0xFF1E293B).copy(alpha = 0.5f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            listOf("English (US)", "English (UK)", "Hindi", "Spanish", "French", "German").forEach { lang ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedLanguage = lang }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    RadioButton(
                        selected = selectedLanguage == lang,
                        onClick = { selectedLanguage = lang },
                        colors = RadioButtonDefaults.colors(selectedColor = Color(0xFFA855F7), unselectedColor = Color(0xFF475569))
                    )
                    Text(lang, color = Color.White, fontSize = 16.sp)
                }
            }
        }
    }
}

@Composable
private fun HelpCenterContent() {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        DetailItem(Icons.Default.QuestionAnswer, "Frequently Asked Questions")
        DetailItem(Icons.Default.SupportAgent, "Contact Support")
        DetailItem(Icons.Default.BugReport, "Report a Bug")
        DetailItem(Icons.Default.MenuBook, "User Guide")
    }
}

@Composable
private fun AboutContent() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // App Logo Placeholder
        Box(
            modifier = Modifier
                .size(100.dp)
                .background(Brush.linearGradient(listOf(Color(0xFF9333EA), Color(0xFFDB2777))), RoundedCornerShape(24.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text("D", color = Color.White, fontSize = 48.sp, fontWeight = FontWeight.Black)
        }
        
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Downn", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text("Version 1.0.0 (Build 22)", color = Color(0xFF94A3B8), fontSize = 14.sp)
        }
        
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color(0xFF1E293B).copy(alpha = 0.5f),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                DetailItem(Icons.Default.Language, "Website")
                DetailItem(Icons.Default.Description, "Terms of Service")
                DetailItem(Icons.Default.PrivacyTip, "Privacy Policy")
                DetailItem(Icons.Default.Star, "Rate the App")
            }
        }
    }
}

@Composable
private fun DetailTextField(label: String, value: String, onValueChange: (String) -> Unit = {}, enabled: Boolean = true) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(label, color = Color(0xFF94A3B8), fontSize = 12.sp, fontWeight = FontWeight.Bold)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                disabledTextColor = Color(0xFF94A3B8),
                focusedBorderColor = Color(0xFFA855F7),
                unfocusedBorderColor = Color(0xFF334155),
                disabledBorderColor = Color(0xFF334155).copy(alpha = 0.5f),
                unfocusedContainerColor = Color(0xFF1E293B).copy(alpha = 0.5f),
                focusedContainerColor = Color(0xFF1E293B).copy(alpha = 0.5f),
                disabledContainerColor = Color(0xFF1E293B).copy(alpha = 0.3f)
            ),
            singleLine = true,
            enabled = enabled
        )
    }
}

@Composable
private fun DetailItem(
    icon: ImageVector,
    label: String,
    hasToggle: Boolean = false,
    initialValue: Boolean = false,
    onClick: () -> Unit = {}
) {
    var checked by remember { mutableStateOf(initialValue) }
    
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFF1E293B).copy(alpha = 0.5f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .clickable { if (!hasToggle) onClick() }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(icon, contentDescription = null, tint = Color(0xFFA855F7), modifier = Modifier.size(24.dp))
            Text(label, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
            
            if (hasToggle) {
                Switch(
                    checked = checked,
                    onCheckedChange = { checked = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Color(0xFFA855F7),
                        uncheckedThumbColor = Color(0xFF94A3B8),
                        uncheckedTrackColor = Color(0xFF334155)
                    )
                )
            } else {
                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color(0xFF475569), modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
private fun PlaceholderContent(title: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Content for $title coming soon!", color = Color.White)
    }
}
