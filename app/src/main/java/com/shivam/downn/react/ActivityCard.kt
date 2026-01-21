/*
package com.shivam.downn.react

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SportsBasketball
import androidx.compose.material.icons.filled.Surfing
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

@Composable
fun ActivityCard(
    avatar: String,
    userName: String,
    title: String,
    timePosted: String,
    distance: String,
    categoryIcon: @Composable () -> Unit,
    categoryColor: Color,
    participantCount: Int,
    gradientColors: List<Color>,
    images: List<String> = emptyList(),
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .background(Brush.linearGradient(gradientColors))
                .padding(20.dp)
        ) {
            Column {
                // Header
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    AsyncImage(
                        model = avatar,
                        contentDescription = userName,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = userName,
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Timer,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = Color.White.copy(alpha = 0.8f)
                            )
                            Text(
                                text = timePosted,
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 14.sp
                            )
                        }
                    }
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        categoryIcon()
                    }
                }

                // Title
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Images Grid
                if (images.isNotEmpty()) {
                    ImageGrid(images = images, modifier = Modifier.padding(bottom = 16.dp))
                }

                // Info Bar
                Row(
                    modifier = Modifier.padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = Color.White.copy(alpha = 0.9f)
                        )
                        Text(
                            text = distance,
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 14.sp
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = Color.White.copy(alpha = 0.9f)
                        )
                        Text(
                            text = "$participantCount joined",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 14.sp
                        )
                    }
                }

                // Join Button
                Button(
                    onClick = { */
/* Handle Join *//*
 },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color.Black
                    ),
                    contentPadding = PaddingValues(vertical = 14.dp)
                ) {
                    Text(text = "JOIN", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun ImageGrid(images: List<String>, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(16.dp)
    when (images.size) {
        1 -> {
            AsyncImage(
                model = images[0],
                contentDescription = null,
                modifier = modifier
                    .fillMaxWidth()
                    .height(192.dp)
                    .clip(shape),
                contentScale = ContentScale.Crop
            )
        }
        2 -> {
            Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AsyncImage(
                    model = images[0],
                    contentDescription = null,
                    modifier = Modifier.weight(1f).height(160.dp).clip(shape),
                    contentScale = ContentScale.Crop
                )
                AsyncImage(
                    model = images[1],
                    contentDescription = null,
                    modifier = Modifier.weight(1f).height(160.dp).clip(shape),
                    contentScale = ContentScale.Crop
                )
            }
        }
        else -> {
            Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AsyncImage(
                    model = images[0],
                    contentDescription = null,
                    modifier = Modifier.weight(1f).height(160.dp).clip(shape),
                    contentScale = ContentScale.Crop
                )
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    AsyncImage(
                        model = images[1],
                        contentDescription = null,
                        modifier = Modifier.fillMaxWidth().height(76.dp).clip(shape),
                        contentScale = ContentScale.Crop
                    )
                    Box(modifier = Modifier.fillMaxWidth().height(76.dp)) {
                        AsyncImage(
                            model = images[2],
                            contentDescription = null,
                            modifier = Modifier.fillMaxWidth().height(76.dp).clip(shape),
                            contentScale = ContentScale.Crop
                        )
                        if (images.size > 3) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Black.copy(alpha = 0.6f), shape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "+${images.size - 3}",
                                    color = Color.White,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF1A1C1E)
@Composable
fun ActivityCardDefaultPreview() {
    // It's good practice to wrap previews in your app's theme
    // to ensure colors and fonts are applied correctly.
    // YourTheme {
    ActivityCard(
        avatar = "https://example.com/user_avatar.jpg", // Replace with a real image URL for testing
        userName = "Shivam",
        title = "Sunset Surfing Session",
        timePosted = "10 minutes ago",
        distance = "1.2 km away",
        categoryIcon = {
            Icon(
                imageVector = Icons.Default.Surfing,
                contentDescription = "Surfing",
                tint = Color(0xFF006972)
            )
        },
        categoryColor = Color(0xFF006972),
        participantCount = 5,
        gradientColors = listOf(Color(0xFF00515A), Color(0xFF00373D)),
        images = listOf(
            "https://example.com/image1.jpg",
            "https://example.com/image2.jpg",
            "https://example.com/image3.jpg",
            "https://example.com/image4.jpg" // This will result in a "+1" overlay
        )
    )
    // }
}

@Preview(showBackground = true, backgroundColor = 0xFF1A1C1E)
@Composable
fun ActivityCardSingleImagePreview() {
    // YourTheme {
    ActivityCard(
        avatar = "https://example.com/user_avatar.jpg",
        userName = "Jane Doe",
        title = "Pickup Basketball Game",
        timePosted = "1 hour ago",
        distance = "3 km away",
        categoryIcon = {
            Icon(
                imageVector = Icons.Default.SportsBasketball,
                contentDescription = "Basketball",
                tint = Color(0xFF6B5E00)
            )
        },
        categoryColor = Color(0xFF6B5E00),
        participantCount = 8,
        gradientColors = listOf(Color(0xFF8A7C00), Color(0xFF4B4300)),
        images = listOf("https://example.com/image1.jpg") // Only one image
    )
    // }
}

@Preview(showBackground = true, backgroundColor = 0xFF1A1C1E)
@Composable
fun ActivityCardNoImagePreview() {
    // YourTheme {
    ActivityCard(
        avatar = "https://example.com/user_avatar.jpg",
        userName = "John App",
        title = "Morning Coffee & Code",
        timePosted = "Just now",
        distance = "500m away",
        categoryIcon = { Text("👨‍💻", fontSize = 24.sp) }, // Using an emoji as the icon
        categoryColor = Color(0xFF5E1D67),
        participantCount = 2,
        gradientColors = listOf(Color(0xFF7A2E83), Color(0xFF430A4B)),
        images = emptyList() // No images provided
    )
    // }
}

*/
