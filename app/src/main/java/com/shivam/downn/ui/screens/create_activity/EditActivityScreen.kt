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
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil.compose.AsyncImage
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.MarkerComposable
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import com.shivam.downn.data.network.NetworkResult
import com.shivam.downn.ui.components.FancyMap
import com.shivam.downn.utils.ImageUtils
import com.shivam.downn.utils.LocationUtils
import com.shivam.downn.utils.TimeUtils
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditActivityScreen(
    socialId: Int,
    onClose: () -> Unit,
    onUpdateSuccess: () -> Unit
) {
    val viewModel: EditActivityViewModel = hiltViewModel()
    val state by viewModel.updateState.collectAsState()
    val activityDetails by viewModel.activityDetails.collectAsState()
    
    // Load activity details on launch
    LaunchedEffect(socialId) {
        viewModel.loadActivityDetails(socialId)
    }

    val context = LocalContext.current
    
    LaunchedEffect(state) {
        if (state is NetworkResult.Success) {
            Toast.makeText(context, "Activity Updated!", Toast.LENGTH_SHORT).show()
            onUpdateSuccess()
        }
    }

    if (activityDetails is NetworkResult.Loading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Color.White)
        }
    } else if (activityDetails is NetworkResult.Error) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Error loading activity details", color = Color.White)
        }
    } else if (activityDetails is NetworkResult.Success) {
        val activity = (activityDetails as NetworkResult.Success).data!!
        
        EditActivityContent(
            activity = activity,
            state = state,
            onClose = onClose,
            onUpdateSocial = { title, description, category, city, location, time, lat, lng ->
                viewModel.updateSocial(
                    socialId = socialId,
                    title = title,
                    description = description,
                    category = category,
                    city = city,
                    locationName = location,
                    scheduledTime = time,
                    maxParticipants = activity.maxParticipants ?: 10, // Keep existing max participants for now
                    latitude = lat,
                    longitude = lng
                )
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditActivityContent(
    activity: com.shivam.downn.data.models.SocialResponse,
    state: NetworkResult<com.shivam.downn.data.models.SocialResponse?>?,
    onClose: () -> Unit,
    onUpdateSocial: (String, String, String, String, String, String, Double?, Double?) -> Unit
) {
    var title by remember { mutableStateOf(activity.title) }
    var description by remember { mutableStateOf(activity.description ?: "") }
    var selectedCategoryId by remember { mutableStateOf(activity.category) }
    var time by remember { mutableStateOf(activity.scheduledTime ?: "") }
    var latitude by remember { mutableStateOf(activity.latitude) }
    var longitude by remember { mutableStateOf(activity.longitude) }
    var detectedCity by remember { mutableStateOf(activity.city) }
    var showMapPicker by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // Default to a reasonable location if none selected (e.g., Denver)
    val mapCenter = remember(latitude, longitude) {
        if (latitude != null && longitude != null) LatLng(latitude!!, longitude!!)
        else LatLng(39.7392, -104.9903) 
    }

    val previewCameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(mapCenter, 16f)
    }

    // Sync preview camera when coordinates change
    LaunchedEffect(latitude, longitude) {
        if (latitude != null && longitude != null) {
            previewCameraPositionState.position = CameraPosition.fromLatLngZoom(
                LatLng(latitude!!, longitude!!),
                16f
            )
        }
    }

    // Categories (Duplicated from StartMove.kt for now - should verify if shared resource exists)
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

    val isFormValid = title.isNotEmpty() && description.isNotEmpty() && selectedCategoryId.isNotEmpty() && time.isNotEmpty() && detectedCity.isNotEmpty()

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Edit Activity",
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onClose) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0F172A))
                )
            },
            containerColor = Color(0xFF0F172A)
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp)
                ) {
                    // Image section (Read-only for MVP as per plan)
                     InputLabel("Move Photo (Cannot be changed)")
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0xFF1E293B).copy(alpha = 0.5f))
                            .border(1.dp, Color(0xFF334155), RoundedCornerShape(20.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        val imageUrl = activity.images.firstOrNull()
                        if (imageUrl != null) {
                            AsyncImage(
                                model = ImageUtils.getFullImageUrl(imageUrl),
                                contentDescription = "Activity Image",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(Icons.Default.Image, contentDescription = null, tint = Color(0xFF94A3B8), modifier = Modifier.size(40.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Title Input
                    InputLabel("What's happening?")
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        modifier = Modifier.fillMaxWidth(),
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
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        shape = RoundedCornerShape(16.dp),
                        singleLine = false,
                        textStyle = LocalTextStyle.current.copy(textAlign = androidx.compose.ui.text.style.TextAlign.Start),
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
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
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
                                if (rowItems.size == 1) Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Time Input
                    InputLabel("When?")
                    var showTimePicker by remember { mutableStateOf(false) }
                    val timePickerState = rememberTimePickerState(initialHour = 12, initialMinute = 0, is24Hour = false)

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF1E293B).copy(alpha = 0.5f))
                            .border(1.dp, Color(0xFF334155), RoundedCornerShape(16.dp))
                            .clickable { showTimePicker = true }
                            .padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Schedule, contentDescription = null, tint = Color(0xFF94A3B8))
                            Text(
                                text = if (time.isNotEmpty()) TimeUtils.formatScheduledTime(time) else "Select Time",
                                color = if (time.isNotEmpty()) Color.White else Color(0xFF64748B),
                                fontSize = 16.sp,
                                fontWeight = if (time.isNotEmpty()) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }

                     if (showTimePicker) {
                        TimePickerDialog(
                            onDismissRequest = { showTimePicker = false },
                            confirmButton = {
                                TextButton(onClick = {
                                    val todayCalendar = Calendar.getInstance().apply {
                                        set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                                        set(Calendar.MINUTE, timePickerState.minute)
                                        set(Calendar.SECOND, 0)
                                        set(Calendar.MILLISECOND, 0)
                                    }
                                    val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                                    time = sdf.format(todayCalendar.time)
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
                        value = detectedCity,
                        onValueChange = { },
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        leadingIcon = { Icon(Icons.Default.Place, contentDescription = null, tint = Color(0xFF94A3B8)) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFF1E293B).copy(alpha = 0.5f),
                            unfocusedContainerColor = Color(0xFF1E293B).copy(alpha = 0.5f),
                            focusedBorderColor = Color(0xFFA855F7),
                            unfocusedBorderColor = Color(0xFF334155),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )

                    // Map Preview
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
                                val markerState = rememberMarkerState(position = LatLng(latitude!!, longitude!!))
                                LaunchedEffect(latitude, longitude) {
                                    markerState.position = LatLng(latitude!!, longitude!!)
                                }
                                MarkerComposable(
                                    state = markerState,
                                    anchor = androidx.compose.ui.geometry.Offset(0.5f, 1.0f)
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
                    }

                    if (state is NetworkResult.Error) {
                        Spacer(modifier = Modifier.height(16.dp))
                        state.message?.let {
                            Text(text = it, color = Color.Red, fontSize = 14.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = {
                            onUpdateSocial(title, description, selectedCategoryId, detectedCity, detectedCity, time, latitude, longitude)
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
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .then(
                                    if (isFormValid) Modifier.background(
                                        Brush.horizontalGradient(
                                            listOf(Color(0xFF9333EA), Color(0xFFDB2777))
                                        )
                                    )
                                    else Modifier
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (state is NetworkResult.Loading) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                            } else {
                                Text("UPDATE MOVE", color = if (isFormValid) Color.White else Color(0xFF475569), fontSize = 18.sp, fontWeight = FontWeight.Bold)
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
                        scope.launch {
                            val city = LocationUtils.getCityFromCoordinates(context, lat, lng)
                            city?.let { detectedCity = it }
                        }
                        showMapPicker = false
                    },
                    onDismiss = { showMapPicker = false }
                )
            }
        }
    }
}

// Reuse helper composables if not in a shared file, otherwise import them.
// Expecting CategoryItem and internal data classes to be available or redefined.
// For now, re-defining simplified CategoryItem here to ensure self-containment if StartMove components aren't public.

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
