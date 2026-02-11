package com.shivam.downn.ui.screens.create_activity

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil.compose.AsyncImage
import com.google.android.gms.maps.model.LatLng
import com.shivam.downn.ui.components.FancyMap
import com.google.maps.android.compose.MarkerComposable
import com.google.maps.android.compose.rememberMarkerState
import com.shivam.downn.data.network.NetworkResult
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StartBusinessMove(
    outerPadding: PaddingValues,
    onClose: () -> Unit
) {
    val viewModel: StartMoveViewModel = hiltViewModel()
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("EVENTS") }
    var location by remember { mutableStateOf("") }
    var registrationLink by remember { mutableStateOf("") }
    var capacity by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    
    // Default business location (Mocked - in real app would come from profile)
    var latitude by remember { mutableStateOf<Double?>(37.4219999) }
    var longitude by remember { mutableStateOf<Double?>(-122.0840575) }
    val mapCenter = remember(latitude, longitude) { LatLng(latitude ?: 37.421, longitude ?: -122.084) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> selectedImageUri = uri }

    LaunchedEffect(state) {
        if (state is NetworkResult.Success) {
            Toast.makeText(context, "Event Promoted!", Toast.LENGTH_SHORT).show()
            onClose()
            viewModel.resetState()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Promote Event",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0F172A))
            )
        },
        containerColor = Color(0xFF0F172A)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    paddingValues = PaddingValues(
                        start = paddingValues.calculateLeftPadding(
                            LayoutDirection.Ltr
                        ),
                        end = paddingValues.calculateRightPadding(LayoutDirection.Ltr),
                        top = paddingValues.calculateTopPadding(),
                        bottom = outerPadding.calculateBottomPadding()
                    )
                )
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            // Event Banner
            Text(
                "Event Banner",
                color = Color(0xFF94A3B8),
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF1E293B))
                    .border(1.dp, Color(0xFF334155), RoundedCornerShape(20.dp))
                    .clickable { photoPickerLauncher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (selectedImageUri != null) {
                    AsyncImage(
                        model = selectedImageUri,
                        contentDescription = "Event Banner",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.AddPhotoAlternate,
                            contentDescription = null,
                            tint = Color(0xFF94A3B8),
                            modifier = Modifier.size(40.dp)
                        )
                        Text("Add Event Visual", color = Color(0xFF94A3B8))
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            BusinessInputField("Event Title", title, "e.g. Friday Live Jazz Session") { title = it }
            BusinessInputField(
                "Description",
                description,
                "What can guests expect?",
                isLong = true
            ) { description = it }
            BusinessInputField("Location", location, "e.g. Main Stage / Rooftop") { location = it }

            Spacer(modifier = Modifier.height(16.dp))

            // Business Location Preview
            Text(
                "Business Location",
                color = Color(0xFF94A3B8),
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF1E293B))
                    .border(1.dp, Color(0xFF334155), RoundedCornerShape(20.dp))
            ) {
                FancyMap(
                    modifier = Modifier.fillMaxSize(),
                    initialCameraPosition = mapCenter,
                    gesturesEnabled = false
                ) {
                    MarkerComposable(
                        state = rememberMarkerState(position = mapCenter),
                        anchor = Offset(0.5f, 1.0f)
                    ) {
                        Icon(
                            Icons.Default.Storefront,
                            contentDescription = null,
                            tint = Color(0xFFF97316),
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    BusinessInputField("Capacity", capacity, "e.g. 50") { capacity = it }
                }
                Box(modifier = Modifier.weight(1f)) {
                    BusinessInputField("Category", category, "EVENTS") { category = it }
                }
            }

            BusinessInputField(
                "Registration Link (Optional)",
                registrationLink,
                "https://tickets.com/..."
            ) { registrationLink = it }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    val finalDescription =
                        if (registrationLink.isNotEmpty()) "$description\n\nRegister here: $registrationLink" else description
                    viewModel.createSocial(
                        title = title,
                        description = finalDescription,
                        category = category,
                        city = "Denver",
                        locationName = location,
                        scheduledTime = "2026-01-31T20:00:00", // Mocked for simplicity
                        maxParticipants = capacity.toIntOrNull() ?: 0,
                        latitude = latitude,
                        longitude = longitude
                    )
                },
                enabled = title.isNotBlank() && description.isNotBlank() && state !is NetworkResult.Loading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    disabledContainerColor = Color(0xFF1E293B)
                ),
                contentPadding = PaddingValues()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            if (title.isNotBlank() && description.isNotBlank())
                                Brush.horizontalGradient(
                                    listOf(
                                        Color(0xFF9333EA),
                                        Color(0xFFDB2777)
                                    )
                                )
                            else Brush.linearGradient(listOf(Color.Transparent, Color.Transparent)),
                            RoundedCornerShape(16.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (state is NetworkResult.Loading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Text(
                            "PROMOTE EVENT",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BusinessInputField(
    label: String,
    value: String,
    placeholder: String,
    isLong: Boolean = false,
    onValueChange: (String) -> Unit
) {
    Column(modifier = Modifier.padding(bottom = 20.dp)) {
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
            modifier = Modifier
                .fillMaxWidth()
                .then(if (isLong) Modifier.height(120.dp) else Modifier),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color(0xFF1E293B).copy(alpha = 0.5f),
                unfocusedContainerColor = Color(0xFF1E293B).copy(alpha = 0.5f),
                focusedBorderColor = Color(0xFFA855F7),
                unfocusedBorderColor = Color(0xFF334155),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )
    }
}
