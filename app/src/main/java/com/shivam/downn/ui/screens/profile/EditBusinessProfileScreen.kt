package com.shivam.downn.ui.screens.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.google.android.gms.maps.model.LatLng
import androidx.compose.material.icons.filled.Place
import com.shivam.downn.ui.components.FancyMap
import com.shivam.downn.ui.screens.create_activity.LocationPicker
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.MarkerComposable
import com.google.maps.android.compose.rememberMarkerState
import androidx.compose.ui.geometry.Offset

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditBusinessProfileScreen(
    businessId: Long,
    onClose: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val activeProfile by viewModel.activeProfile.collectAsState()

    // Initialize state from activeProfile
    var name by remember { mutableStateOf(activeProfile?.name ?: "") }
    var bio by remember { mutableStateOf(activeProfile?.bio ?: "") }
    var location by remember { mutableStateOf(activeProfile?.location ?: "") }
    var vibes by remember { mutableStateOf(activeProfile?.vibes?.joinToString(", ") ?: "") }
    
    var latitude by remember { mutableStateOf(activeProfile?.latitude) }
    var longitude by remember { mutableStateOf(activeProfile?.longitude) }
    var showMapPicker by remember { mutableStateOf(false) }

    var selectedAvatarUri by remember { mutableStateOf<Uri?>(null) }
    var selectedCoverUri by remember { mutableStateOf<Uri?>(null) }

    // Update state when activeProfile changes (e.g. after loading)
    LaunchedEffect(activeProfile) {
        activeProfile?.let {
            name = it.name
            bio = it.bio ?: ""
            location = it.location ?: ""
            vibes = it.vibes?.joinToString(", ") ?: ""
            latitude = it.latitude
            longitude = it.longitude
        }
    }

    val avatarPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedAvatarUri = uri
    }

    val coverPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedCoverUri = uri
    }

    Box(modifier = Modifier.fillMaxSize()) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Business Profile", color = Color.White, fontWeight = FontWeight.Bold) },
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
                actions = {
                    TextButton(onClick = {
                        viewModel.updateProfile(
                            businessId, 
                            name, 
                            bio, 
                            location, 
                            vibes, 
                            selectedAvatarUri, 
                            selectedCoverUri,
                            latitude,
                            longitude
                        )
                        onClose()
                    }) {
                        Text("Save", color = Color(0xFFA855F7), fontWeight = FontWeight.Bold, fontSize = 16.sp)
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
        ) {
            // Header with Cover and Avatar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
            ) {
                // Cover Image
                AsyncImage(
                    model = selectedCoverUri ?: "https://images.unsplash.com/photo-1495474472287-4d71bcdd2085?w=800",
                    contentDescription = "Cover Photo",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentScale = ContentScale.Crop
                )
                // Edit Cover Button
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.5f))
                        .clickable { coverPickerLauncher.launch("image/*") }
                        .padding(8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Cover Photo", tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Edit Cover", color = Color.White, fontSize = 12.sp)
                    }
                }

                // Avatar Image
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 24.dp),
                    contentAlignment = Alignment.BottomEnd
                ) {
                    Surface(
                        modifier = Modifier.size(100.dp),
                        shape = CircleShape,
                        color = Color.Transparent,
                        border = androidx.compose.foundation.BorderStroke(3.dp, Color(0xFF0F172A))
                    ) {
                        AsyncImage(
                            model = selectedAvatarUri ?: "https://images.unsplash.com/photo-1509042239860-f550ce710b93?w=400",
                            contentDescription = "Profile Photo",
                            modifier = Modifier.clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF9333EA))
                            .border(2.dp, Color(0xFF0F172A), CircleShape)
                            .clickable { avatarPickerLauncher.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.CameraAlt, contentDescription = "Edit Photo", tint = Color.White, modifier = Modifier.size(16.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Fields
            Column(Modifier.padding(horizontal = 24.dp)) {
                EditField(label = "Business Name", value = name, onValueChange = { name = it })
                EditField(label = "Bio", value = bio, onValueChange = { bio = it }, singleLine = false, maxLines = 5)
                EditField(label = "Location", value = location, onValueChange = { location = it })
                
                // Map Preview
                val mapCenter = LatLng(39.7392, -104.9903) // Default to Denver
                val cameraPositionState = rememberCameraPositionState {
                    position = CameraPosition.fromLatLngZoom(
                        if (latitude != null && longitude != null) LatLng(latitude!!, longitude!!) else mapCenter,
                        15f
                    )
                }
                
                // Update camera when location changes
                LaunchedEffect(latitude, longitude) {
                    if (latitude != null && longitude != null) {
                        cameraPositionState.position = CameraPosition.fromLatLngZoom(LatLng(latitude!!, longitude!!), 15f)
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF1E293B))
                ) {
                    FancyMap(
                        cameraPositionState = cameraPositionState,
                        modifier = Modifier.fillMaxSize(),
                        onMapClick = { showMapPicker = true }
                    ) {
                        if (latitude != null && longitude != null) {
                            val markerState = rememberMarkerState(position = LatLng(latitude!!, longitude!!))
                            
                            LaunchedEffect(latitude, longitude) {
                                markerState.position = LatLng(latitude!!, longitude!!)
                            }

                            MarkerComposable(
                                state = markerState,
                                anchor = Offset(0.5f, 1.0f)
                            ) {
                                Icon(
                                    Icons.Default.Place,
                                    contentDescription = null,
                                    tint = Color(0xFFF87171),
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                    }
                    
                    // Overlay to indicate clickability
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.1f))
                            .clickable { showMapPicker = true },
                        contentAlignment = Alignment.Center
                    ) {
                         if (latitude == null || longitude == null) {
                             Text(
                                 "Tap to set location on map",
                                 color = Color.White,
                                 fontWeight = FontWeight.Bold,
                                 modifier = Modifier
                                     .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                                     .padding(8.dp)
                             )
                         }
                    }

                    // Change Location Button
                    Button(
                        onClick = { showMapPicker = true },
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9333EA)),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Icon(Icons.Default.Place, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Change Location", color = Color.White, fontSize = 12.sp)
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))

                EditField(label = "Vibes (comma-separated)", value = vibes, onValueChange = { vibes = it })
            }
        }
    }
    
    if (showMapPicker) {
        LocationPicker(
            initialLocation = if (latitude != null && longitude != null) LatLng(latitude!!, longitude!!) else LatLng(39.7392, -104.9903),
            onLocationSelected = { lat, lng ->
                latitude = lat
                longitude = lng
                showMapPicker = false
            },
            onDismiss = { showMapPicker = false }
        )
    }
    }
}

// Re-using EditField from EditProfileScreen.kt
@Composable
private fun EditField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    singleLine: Boolean = true,
    maxLines: Int = if (singleLine) 1 else 4
) {
    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp)) {
        Text(
            label,
            color = Color(0xFFCBD5E1),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            textStyle = LocalTextStyle.current.copy(color = Color.White, fontSize = 16.sp),
            singleLine = singleLine,
            maxLines = maxLines,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color(0xFF1E293B).copy(alpha = 0.5f),
                unfocusedContainerColor = Color(0xFF1E293B).copy(alpha = 0.5f),
                focusedBorderColor = Color(0xFFA855F7),
                unfocusedBorderColor = Color(0xFF334155),
            )
        )
    }
}


@Preview(showBackground = true, backgroundColor = 0xFF0F172A)
@Composable
fun EditBusinessProfileScreenPreview() {
    MaterialTheme {
        EditBusinessProfileScreen(businessId = 1L, onClose = {})
    }
}
