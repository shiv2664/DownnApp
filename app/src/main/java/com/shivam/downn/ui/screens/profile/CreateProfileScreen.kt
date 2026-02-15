package com.shivam.downn.ui.screens.profile

import android.net.Uri
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
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil.compose.AsyncImage
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.CameraPosition
import com.shivam.downn.ui.components.FancyMap
import com.google.maps.android.compose.MarkerComposable
import com.google.maps.android.compose.rememberMarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.shivam.downn.ui.screens.create_activity.LocationPicker
import androidx.compose.ui.geometry.Offset
import com.shivam.downn.data.network.NetworkResult

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateProfileScreen(
    onClose: () -> Unit,
    onCreateSuccess: (String, String, String, String) -> Unit
) {

    val profileViewModel: ProfileViewModel = hiltViewModel()
    var businessName by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var selectedCoverUri by remember { mutableStateOf<Uri?>(null) }
    var latitude by remember { mutableStateOf<Double?>(null) }
    var longitude by remember { mutableStateOf<Double?>(null) }
    var showMapPicker by remember { mutableStateOf(false) }

    val mapCenter = remember(latitude, longitude) {
        if (latitude != null && longitude != null) LatLng(latitude!!, longitude!!)
        else LatLng(39.7392, -104.9903) 
    }

    val previewCameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(mapCenter, 13f)
    }

    LaunchedEffect(latitude, longitude) {
        if (latitude != null && longitude != null) {
            previewCameraPositionState.position = CameraPosition.fromLatLngZoom(
                LatLng(latitude!!, longitude!!),
                13f
            )
        }
    }
    
    val allVibes = listOf("Cozy", "Jazz", "Energy", "Chill", "Loud", "Outdoor", "Elegant", "Underground")
    var selectedVibes by remember { mutableStateOf(setOf<String>()) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
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
                title = { Text("Create Business", color = Color.White, fontWeight = FontWeight.Bold) },
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
                .padding(bottom = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Cover Photo Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(Color(0xFF1E293B))
                    .clickable { coverPickerLauncher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (selectedCoverUri != null) {
                    AsyncImage(
                        model = selectedCoverUri,
                        contentDescription = "Cover photo",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.AddAPhoto, contentDescription = null, tint = Color(0xFF94A3B8))
                        Text("Add Cover Photo", color = Color(0xFF94A3B8), fontSize = 12.sp)
                    }
                }
            }

            // Profile Photo Section (Overlapping cover)
            Box(
                modifier = Modifier
                    .offset(y = (-50).dp)
                    .padding(horizontal = 24.dp)
                    .fillMaxWidth(),
                contentAlignment = Alignment.BottomStart
            ) {
                Box(contentAlignment = Alignment.BottomEnd) {
                    Surface(
                        modifier = Modifier.size(100.dp),
                        shape = CircleShape,
                        color = Color(0xFF0F172A),
                        border = BorderStroke(4.dp, Color(0xFF0F172A))
                    ) {
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            shape = CircleShape,
                            color = Color(0xFF1E293B),
                            border = BorderStroke(2.dp, Brush.linearGradient(listOf(Color(0xFF9333EA), Color(0xFFDB2777))))
                        ) {
                            if (selectedImageUri != null) {
                                AsyncImage(
                                    model = selectedImageUri,
                                    contentDescription = "Business Logo",
                                    modifier = Modifier.clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.AddAPhoto, contentDescription = null, tint = Color(0xFF94A3B8), modifier = Modifier.size(24.dp))
                                }
                            }
                        }
                    }
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF9333EA))
                            .border(2.dp, Color(0xFF0F172A), CircleShape)
                            .clickable { photoPickerLauncher.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.AddAPhoto, contentDescription = "Add Photo", tint = Color.White, modifier = Modifier.size(14.dp))
                    }
                }
            }

            Column(modifier = Modifier.padding(horizontal = 24.dp).offset(y = (-30).dp)) {
                // Fields
                CreateField(label = "Business Name", value = businessName, placeholder = "e.g. The Daily Grind", onValueChange = { businessName = it })
                CreateField(label = "Category", value = category, placeholder = "e.g. Cafe, Nightclub", onValueChange = { category = it })
                CreateField(label = "Bio", value = bio, placeholder = "Tell the world about your vibe...", onValueChange = { bio = it }, singleLine = false)
                CreateField(label = "Location", value = location, placeholder = "e.g. San Francisco, CA", onValueChange = { location = it })

                // Map Preview for Location (Added)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Pin your location",
                        color = Color(0xFFCBD5E1),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        "Change Location",
                        color = Color(0xFFA855F7),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.clickable { showMapPicker = true }
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFF1E293B))
                        .border(1.dp, Color(0xFF334155), RoundedCornerShape(20.dp))
                        .clickable { showMapPicker = true }
                ) {
                    FancyMap(
                        modifier = Modifier.fillMaxSize(),
                        cameraPositionState = previewCameraPositionState,
                        gesturesEnabled = false
                    ) {
                        if (latitude != null && longitude != null) {
                            val markerState = rememberMarkerState(
                                key = "${latitude}_${longitude}",
                                position = LatLng(latitude!!, longitude!!)
                            )
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
                    
                    // Overlay to emphasize it's clickable
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Surface(
                            color = Color.Black.copy(alpha = 0.6f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                if (latitude == null) "Tap to select location" else "Tap to change",
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                fontSize = 12.sp
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
                
                // Vibe Selection
                Text(
                    "Select Vibe Tags",
                    color = Color(0xFFCBD5E1),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 12.dp, top = 8.dp)
                )
                
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    allVibes.forEach { vibe ->
                        val isSelected = selectedVibes.contains(vibe)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(100.dp))
                                .background(if (isSelected) Color(0xFFA855F7) else Color(0xFF1E293B))
                                .border(1.dp, if (isSelected) Color.Transparent else Color(0xFF334155), RoundedCornerShape(100.dp))
                                .clickable { 
                                    selectedVibes = if (isSelected) selectedVibes - vibe else selectedVibes + vibe
                                }
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(vibe, color = if (isSelected) Color.White else Color(0xFF94A3B8), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
                
                Button(
                    onClick = { 
                        if (businessName.isNotBlank() && category.isNotBlank()) {
                            // Don't call onCreateSuccess immediately. Wait for API result.
                            profileViewModel.createBusinessProfile(
                                businessName, category, bio, location,
                                selectedCoverUri?.toString() ?: "", 
                                selectedVibes.toList(),
                                latitude,
                                longitude
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent
                    ),
                    contentPadding = PaddingValues()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.horizontalGradient(listOf(Color(0xFF9333EA), Color(0xFFDB2777))),
                                RoundedCornerShape(16.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Create Profile", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    "By creating a business profile, you agree to our terms for professional hosts.",
                    color = Color(0xFF94A3B8),
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }
        }
    }

    if (showMapPicker) {
        LocationPicker(
            initialLocation = if (latitude != null && longitude != null) LatLng(latitude!!, longitude!!) else mapCenter,
            onLocationSelected = { lat, lng ->
                latitude = lat
                longitude = lng
                showMapPicker = false
            },
            onDismiss = { showMapPicker = false }
        )
    }
    val createProfileState by profileViewModel.createProfileState.collectAsState()
    val context = androidx.compose.ui.platform.LocalContext.current

    LaunchedEffect(createProfileState) {
        when (createProfileState) {
            is NetworkResult.Success -> {
                val profile = (createProfileState as NetworkResult.Success).data
                onCreateSuccess(businessName, category, bio, location) // Keep existing callback
                profileViewModel.resetCreateProfileState()
                onClose()
            }
            is NetworkResult.Error -> {
                android.widget.Toast.makeText(context, (createProfileState as NetworkResult.Error).message ?: "Failed to create profile", android.widget.Toast.LENGTH_LONG).show()
                profileViewModel.resetCreateProfileState()
            }
            else -> {}
        }
    }

    if (createProfileState is NetworkResult.Loading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
                .clickable(enabled = false) {},
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Color(0xFFA855F7))
        }
    }
    }
}

@Composable
private fun CreateField(
    label: String,
    value: String,
    placeholder: String,
    onValueChange: (String) -> Unit,
    singleLine: Boolean = true
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
            placeholder = { Text(placeholder, color = Color(0xFF64748B)) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            textStyle = LocalTextStyle.current.copy(color = Color.White, fontSize = 16.sp),
            singleLine = singleLine,
            maxLines = if (singleLine) 1 else 4,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color(0xFF1E293B).copy(alpha = 0.5f),
                unfocusedContainerColor = Color(0xFF1E293B).copy(alpha = 0.5f),
                focusedBorderColor = Color(0xFFA855F7),
                unfocusedBorderColor = Color(0xFF334155),
            )
        )
    }
}
