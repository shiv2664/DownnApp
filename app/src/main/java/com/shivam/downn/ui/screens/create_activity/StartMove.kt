package com.shivam.downn.ui.screens.create_activity

import android.widget.Toast
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TimeInput
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.*
import androidx.compose.runtime.derivedStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.shivam.downn.data.network.NetworkResult

import androidx.compose.material3.TimePickerDialog
import androidx.compose.material3.TextButton
import androidx.compose.material3.Text
import com.shivam.downn.data.models.SocialResponse
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.geometry.Offset
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.CameraPosition
import com.shivam.downn.ui.components.FancyMap
import com.google.maps.android.compose.MarkerComposable
import com.google.maps.android.compose.rememberMarkerState
import com.google.maps.android.compose.rememberCameraPositionState

data class Category(
    val id: String,
    val name: String,
    val icon: ImageVector,
    val gradient: List<Color>
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StartMove(
    outerPadding: PaddingValues,
    onClose: () -> Unit,
) {
    val viewModel: StartMoveViewModel = hiltViewModel()
    val state by viewModel.state.collectAsState()

    StartMoveContent(
        state,
        outerPadding = outerPadding,
        onClose = onClose,
        onCreateSocial = { title, description, category, location, time, lat, lng, imageUri ->
            viewModel.createSocial(
                title = title,
                description = description,
                category = category,
                city = "Denver",
                locationName = location,
                scheduledTime = time,
                maxParticipants = 10,
                latitude = lat,
                longitude = lng,
                imageUri = imageUri
            )
        },
        onResetState = { viewModel.resetState() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StartMoveContent(
    state: NetworkResult<SocialResponse?>?,
    outerPadding: PaddingValues,
    onClose: () -> Unit,
    onCreateSocial: (String, String, String, String, String, Double?, Double?, Uri?) -> Unit,
    onResetState: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedCategoryId by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var latitude by remember { mutableStateOf<Double?>(null) }
    var longitude by remember { mutableStateOf<Double?>(null) }
    var showMapPicker by remember { mutableStateOf(false) }
    
    // Default to a reasonable location if none selected (e.g., Denver)
    val mapCenter = remember(latitude, longitude) {
        if (latitude != null && longitude != null) LatLng(latitude!!, longitude!!)
        else LatLng(39.7392, -104.9903) 
    }

    val previewCameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(mapCenter, 13f)
    }

    // Sync preview camera when coordinates change
    LaunchedEffect(latitude, longitude) {
        if (latitude != null && longitude != null) {
            previewCameraPositionState.position = CameraPosition.fromLatLngZoom(
                LatLng(latitude!!, longitude!!),
                13f
            )
        }
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }

    val context = LocalContext.current

    val categories = listOf(
        Category(
            "SPORTS",
            "Sports",
            Icons.Default.SportsBasketball,
            listOf(Color(0xFF3B82F6), Color(0xFF06B6D4))
        ),
        Category(
            "TRAVEL",
            "Travel",
            Icons.Default.Flight,
            listOf(Color(0xFF3B82F6), Color(0xFF06B6D4))
        ),
        Category(
            "PARTY",
            "Party",
            Icons.Default.Celebration,
            listOf(Color(0xFFEC4899), Color(0xFFA855F7))
        ),
        Category(
            "FOOD",
            "Food",
            Icons.Default.Restaurant,
            listOf(Color(0xFFF97316), Color(0xFFEF4444))
        ),
        Category(
            "HOBBY",
            "Hobby",
            Icons.Default.Palette,
            listOf(Color(0xFF22C55E), Color(0xFF14B8A6))
        ),
    )
//    OTHER, HOBBY, PARTY, EVENTS, TRAVEL, FOOD, SPORTS

    val isFormValid = title.isNotEmpty() && description.isNotEmpty() && selectedCategoryId.isNotEmpty() && time.isNotEmpty() && location.isNotEmpty()

    LaunchedEffect(state) {
        if (state is NetworkResult.Success) {
            Toast.makeText(context, "Move Created!", Toast.LENGTH_SHORT).show()
            onClose()
            onResetState()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Start a Move",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                /*navigationIcon = {
                    IconButton(
                        onClick = onClose,
                        modifier = Modifier
                            .padding(8.dp)
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF1E293B))
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color(0xFFCBD5E1)
                        )
                    }
                },*/
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0F172A))
            )
        },
        containerColor = Color(0xFF0F172A)
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = paddingValues.calculateStartPadding(LayoutDirection.Ltr),
                    end = paddingValues.calculateStartPadding(LayoutDirection.Ltr),
                    top = paddingValues.calculateTopPadding(),
                    bottom = 20.dp
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(
                        bottom = outerPadding.calculateBottomPadding(),
                        start = 20.dp,
                        end = 20.dp
                    )
            ) {
                // Move Image section
                InputLabel("Move Photo")
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFF1E293B).copy(alpha = 0.5f))
                        .border(1.dp, Color(0xFF334155), RoundedCornerShape(20.dp))
                        .clickable { photoPickerLauncher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    if (selectedImageUri != null) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            AsyncImage(
                                model = selectedImageUri,
                                contentDescription = "Activity Image",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                            // Change overlay
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Black.copy(alpha = 0.3f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Icon(Icons.Default.Edit, contentDescription = null, tint = Color.White)
                                    Text("Change Photo", color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            }
                            // Remove button
                            IconButton(
                                onClick = { selectedImageUri = null },
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(8.dp)
                                    .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                                    .size(32.dp)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Remove", tint = Color.White, modifier = Modifier.size(16.dp))
                            }
                        }
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, tint = Color(0xFF94A3B8), modifier = Modifier.size(40.dp))
                            Text("Add Move Photo", color = Color(0xFF94A3B8), fontWeight = FontWeight.Medium)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Title Input
                InputLabel("What's happening?")
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    placeholder = {
                        Text(
                            "Let's grab coffee at that new spot! ☕",
                            color = Color(0xFF64748B)
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF1E293B).copy(alpha = 0.5f),
                        unfocusedContainerColor = Color(0xFF1E293B).copy(alpha = 0.5f),
                        focusedBorderColor = Color(0xFFA855F7),
                        unfocusedBorderColor = Color(0xFF334155),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                InputLabel("Description")
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    placeholder = {
                        Text(
                            "Add some details about the move...",
                            color = Color(0xFF64748B)
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF1E293B).copy(alpha = 0.5f),
                        unfocusedContainerColor = Color(0xFF1E293B).copy(alpha = 0.5f),
                        focusedBorderColor = Color(0xFFA855F7),
                        unfocusedBorderColor = Color(0xFF334155),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Category Selector
                InputLabel("Category")

                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    categories.chunked(2).forEach { rowItems ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            rowItems.forEach { category ->
                                val isSelected = selectedCategoryId == category.id
                                Box(modifier = Modifier.weight(1f)) {
                                    CategoryItem(
                                        category = category,
                                        isSelected = isSelected,
                                        onClick = { selectedCategoryId = category.id }
                                    )
                                }
                            }
                            // Add an empty space if the row has only one item
                            if (rowItems.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Time Input
                InputLabel("When?")
                var showTimePicker by remember { mutableStateOf(false) }
                val selectedTime = remember { mutableStateOf<Pair<Int, Int>?>(null) }

                val currentCalendar = Calendar.getInstance()
                val currentHour = currentCalendar.get(Calendar.HOUR_OF_DAY)
                val currentMinute = currentCalendar.get(Calendar.MINUTE)
                val timePickerState = rememberTimePickerState(initialHour = currentHour, initialMinute = currentMinute, is24Hour = false)

                val formattedDateTime by remember(selectedTime.value) {
                    derivedStateOf {
                        if (selectedTime.value != null) {
                            val timePair = selectedTime.value!!
                            val todayCalendar = Calendar.getInstance().apply { // This captures today's date automatically
                                set(Calendar.HOUR_OF_DAY, timePair.first)
                                set(Calendar.MINUTE, timePair.second)
                                set(Calendar.SECOND, 0) // Explicitly set seconds to 0
                                set(Calendar.MILLISECOND, 0) // Explicitly set milliseconds to 0
                            }
                            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                            sdf.format(todayCalendar.time)
                        } else {
                            ""
                        }
                    }
                }

                LaunchedEffect(formattedDateTime) {
                    time = formattedDateTime
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF1E293B).copy(alpha = 0.5f))
                        .border(1.dp, Color(0xFF334155), RoundedCornerShape(16.dp))
                        .clickable { showTimePicker = true } // Directly show time picker
                        .padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.Schedule, // Use Schedule icon
                            contentDescription = null,
                            tint = Color(0xFF94A3B8)
                        )
                        Text(
                            text = if (formattedDateTime.isNotEmpty()) formattedDateTime else "Select Time (Today)", // Updated placeholder
                            color = if (formattedDateTime.isNotEmpty()) Color.White else Color(0xFF64748B),
                            fontSize = 16.sp
                        )
                    }
                }

                if (showTimePicker) {
                    TimePickerDialog(
                        onDismissRequest = { showTimePicker = false },
                        confirmButton = {
                            TextButton(onClick = {
                                selectedTime.value =
                                    Pair(timePickerState.hour, timePickerState.minute)
                                showTimePicker = false
                            }) { Text("OK") }
                        },
                        dismissButton = {
                            TextButton(onClick = { showTimePicker = false }) { Text("Cancel") }
                        },
                        title = {Text("Pick Time")}
                    ) {
                        TimeInput(state = timePickerState)
                    }
                }


                Spacer(modifier = Modifier.height(32.dp))

                // Location Input
                InputLabel("Where?")
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    placeholder = { Text("Bear Creek Trail (Denver)", color = Color(0xFF64748B)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    leadingIcon = {
                        Icon(
                            Icons.Default.Place,
                            contentDescription = null,
                            tint = Color(0xFF94A3B8)
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF1E293B).copy(alpha = 0.5f),
                        unfocusedContainerColor = Color(0xFF1E293B).copy(alpha = 0.5f),
                        focusedBorderColor = Color(0xFFA855F7),
                        unfocusedBorderColor = Color(0xFF334155),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                // Map Preview for Location
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    InputLabel("Pin your location")
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

                if (state is NetworkResult.Error) {
                    Spacer(modifier = Modifier.height(16.dp))
                    state.message?.let {
                        Text(
                            text = it,
                            color = Color.Red,
                            fontSize = 14.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        onCreateSocial(title, description, selectedCategoryId, location, time, latitude, longitude, selectedImageUri)
                    },
                    enabled = isFormValid && state !is NetworkResult.Loading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        disabledContainerColor = Color(0xFF1E293B)
                    ),
                    contentPadding = PaddingValues()
                )
                {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .then(
                                if (isFormValid) Modifier.background(
                                    Brush.horizontalGradient(
                                        listOf(
                                            Color(0xFF9333EA),
                                            Color(0xFFDB2777)
                                        )
                                    )
                                )
                                else Modifier
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
                                "POST MOVE",
                                color = if (isFormValid) Color.White else Color(0xFF475569),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
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
    }
    }
}

@Composable
fun InputLabel(text: String) {
    Text(
        text = text,
        color = Color(0xFFCBD5E1),
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
private fun CategoryItem(category: Category, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .clip(RoundedCornerShape(16.dp))
            .then(
                if (isSelected) Modifier.background(Brush.linearGradient(category.gradient))
                else Modifier
                    .background(Color(0xFF1E293B).copy(alpha = 0.5f))
                    .border(2.dp, Color(0xFF334155), RoundedCornerShape(16.dp))
            )
            .clickable { onClick() }
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                category.icon,
                contentDescription = null,
                tint = if (isSelected) Color.White else Color(0xFFCBD5E1),
                modifier = Modifier.size(24.dp)
            )
            Text(
                category.name,
                color = if (isSelected) Color.White else Color(0xFFCBD5E1),
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0F172A)
@Composable
fun StartMovePreview() {
    MaterialTheme {
        StartMoveContent(
            state = null,
            outerPadding = PaddingValues(0.dp),
            onClose = {},
            onCreateSocial = { _, _, _, _, _, _, _, _ -> },
            onResetState = {}
        )
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationPicker(
    initialLocation: LatLng,
    onLocationSelected: (Double, Double) -> Unit,
    onDismiss: () -> Unit
) {
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(initialLocation, 15f)
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
    ) {
        FancyMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            gesturesEnabled = true
        )

        // Center Pin Placeholder (Fixed in middle of map)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 24.dp), // Adjust for pin tail offset if needed
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Place,
                contentDescription = null,
                tint = Color(0xFFF87171),
                modifier = Modifier.size(48.dp)
            )
        }

        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onDismiss,
                modifier = Modifier.background(Color.Black.copy(alpha = 0.5f), CircleShape)
            ) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
            }
            
            Text(
                "Select Location",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier
                    .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
            
            Spacer(modifier = Modifier.size(48.dp))
        }

        // Bottom Action
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(24.dp)
        ) {
            Button(
                onClick = {
                    val target = cameraPositionState.position.target
                    onLocationSelected(target.latitude, target.longitude)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFA855F7)
                )
            ) {
                Text(
                    "Confirm Location",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
