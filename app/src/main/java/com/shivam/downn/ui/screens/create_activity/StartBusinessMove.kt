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
import java.util.Locale
import java.text.SimpleDateFormat
import com.shivam.downn.utils.TimeUtils

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
    
    // Use location from profile (implied business location)
    val profileLocation by viewModel.profileLocation.collectAsState()
    var latitude by remember { mutableStateOf<Double?>(null) }
    var longitude by remember { mutableStateOf<Double?>(null) }
    var detectedCity by remember { mutableStateOf("Loading location...") }

    LaunchedEffect(Unit) {
        viewModel.fetchActiveProfileLocation()
    }

    LaunchedEffect(profileLocation) {
        profileLocation?.let { (lat, lng) ->
            latitude = lat
            longitude = lng
            val city = com.shivam.downn.utils.LocationUtils.getCityFromCoordinates(context, lat, lng)
            city?.let { detectedCity = it } ?: run { detectedCity = "Unknown City" }
        }
    }
    
    // Time Selection State
    var time by remember { mutableStateOf("") }
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
                val todayCalendar = Calendar.getInstance().apply { 
                    set(Calendar.HOUR_OF_DAY, timePair.first)
                    set(Calendar.MINUTE, timePair.second)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
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
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF1E293B).copy(alpha = 0.5f))
                    .border(1.dp, Color(0xFF334155), RoundedCornerShape(12.dp))
                    .padding(16.dp)
            ) {
                 Row(verticalAlignment = Alignment.CenterVertically) {
                     Icon(Icons.Default.LocationOn, contentDescription=null, tint=Color(0xFFA855F7))
                     Spacer(modifier = Modifier.width(8.dp))
                     Text(
                         text = detectedCity,
                         color = Color.White,
                         fontWeight = FontWeight.Medium
                     )
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

            Spacer(modifier = Modifier.height(16.dp))

            // Time Input (Business)
            Text(
                "When?",
                color = Color(0xFFCBD5E1),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF1E293B).copy(alpha = 0.5f))
                    .border(1.dp, Color(0xFF334155), RoundedCornerShape(12.dp))
                    .clickable { showTimePicker = true }
                    .padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Default.Schedule,
                        contentDescription = null,
                        tint = Color(0xFF94A3B8)
                    )
                    Text(
                        text = if (formattedDateTime.isNotEmpty()) TimeUtils.formatScheduledTime(formattedDateTime) else "Select Event Time",
                        color = if (formattedDateTime.isNotEmpty()) Color.White else Color(0xFF64748B),
                        fontSize = 16.sp,
                        fontWeight = if (formattedDateTime.isNotEmpty()) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
            
            if (showTimePicker) {
                TimePickerDialog(
                    onDismissRequest = { showTimePicker = false },
                    confirmButton = {
                        TextButton(onClick = {
                            selectedTime.value = Pair(timePickerState.hour, timePickerState.minute)
                            showTimePicker = false
                        }) { Text("OK") }
                    },
                    dismissButton = {
                        TextButton(onClick = { showTimePicker = false }) { Text("Cancel") }
                    },
                    title = { Text("Pick Event Time") }
                ) {
                    TimeInput(state = timePickerState)
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
                        city = detectedCity,
                        locationName = location,
                        scheduledTime = time.ifEmpty { "2026-01-31T20:00:00" }, // Use selected time or fallback
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
