package com.shivam.downn.ui.screens.create_activity

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

data class Category(
    val id: String,
    val name: String,
    val icon: ImageVector,
    val gradient: List<Color>
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateActivity(
    outerPadding: PaddingValues,
    onClose: () -> Unit,
    viewModel: CreateActivityViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    CreateActivityContent(
        state = state,
        outerPadding = outerPadding,
        onClose = onClose,
        onCreateActivity = { title, description, category, location, time ->
            viewModel.createActivity(
                title = title,
                description = description,
                category = category,
                city = "Denver",
                locationName = location,
                scheduledTime = time,
                maxParticipants = 10
            )
        },
        onResetState = { viewModel.resetState() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateActivityContent(
    state: CreateActivityState,
    outerPadding: PaddingValues,
    onClose: () -> Unit,
    onCreateActivity: (String, String, String, String, String) -> Unit,
    onResetState: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedCategoryId by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }

    val context = LocalContext.current

    val categories = listOf(
        Category(
            "SPORTS",
            "Sports",
            Icons.Default.SportsBasketball,
            listOf(Color(0xFF3B82F6), Color(0xFF06B6D4))
        ),
        Category(
            "travel",
            "Travel",
            Icons.Default.Flight,
            listOf(Color(0xFF3B82F6), Color(0xFF06B6D4))
        ),
        Category(
            "party",
            "Party",
            Icons.Default.Celebration,
            listOf(Color(0xFFEC4899), Color(0xFFA855F7))
        ),
        Category(
            "food",
            "Food",
            Icons.Default.Restaurant,
            listOf(Color(0xFFF97316), Color(0xFFEF4444))
        ),
        Category(
            "hobby",
            "Hobby",
            Icons.Default.Palette,
            listOf(Color(0xFF22C55E), Color(0xFF14B8A6))
        ),
    )

    val isFormValid =
        title.isNotEmpty() && selectedCategoryId.isNotEmpty() && time.isNotEmpty() && location.isNotEmpty()

    LaunchedEffect(state) {
        if (state is CreateActivityState.Success) {
            Toast.makeText(context, "Activity Created!", Toast.LENGTH_SHORT).show()
            onClose()
            onResetState()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Create Activity",
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
                            "Add some details about the activity...",
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
                OutlinedTextField(
                    value = time,
                    onValueChange = { time = it },
                    placeholder = { Text("2026-02-01T09:00:00", color = Color(0xFF64748B)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    leadingIcon = {
                        Icon(
                            Icons.Default.CalendarToday,
                            contentDescription = null,
                            tint = Color(0xFF94A3B8)
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF1E293B).copy(alpha = 0.5f),
                        unfocusedContainerColor = Color(0xFF1E293B).copy(alpha = 0.5f),
                        focusedBorderColor = Color(0xFFA855F7),
                        unfocusedBorderColor = Color(0xFF334155),
                        unfocusedTextColor = Color.White,
                        focusedTextColor = Color.White
                    )
                )

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

                if (state is CreateActivityState.Error) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = (state as CreateActivityState.Error).message,
                        color = Color.Red,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        onCreateActivity(title, description, selectedCategoryId, location, time)
                    },
                    enabled = isFormValid && state !is CreateActivityState.Loading,
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
                        if (state is CreateActivityState.Loading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        } else {
                            Text(
                                "POST ACTIVITY",
                                color = if (isFormValid) Color.White else Color(0xFF475569),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

            }
        }
    }
}

@Composable
private fun InputLabel(text: String) {
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
fun CreateActivityPreview() {
    MaterialTheme {
        CreateActivityContent(
            state = CreateActivityState.Idle,
            outerPadding = PaddingValues(0.dp),
            onClose = {},
            onCreateActivity = { _, _, _, _, _ -> },
            onResetState = {}
        )
    }
}
