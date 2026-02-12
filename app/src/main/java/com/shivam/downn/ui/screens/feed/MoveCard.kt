package com.shivam.downn.ui.screens.feed

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.shivam.downn.data.models.SocialType
import androidx.compose.ui.graphics.Brush
import androidx.compose.material.icons.filled.Verified

@Composable
fun MoveCard(
    userName: String="",
    userAvatar: String="",
    moveTitle: String="",
    description: String="",
    category: String="",
    categoryEmoji: String="",
    timeAgo: String="",
    distance: String="",
    participantCount: Int=0,
    maxParticipants: Int?=0,
    participantAvatars: List<String>? =emptyList(),
    socialType: SocialType = SocialType.PERSONAL,
    isRequested: Boolean = false,
    isRejected: Boolean = false,
    isParticipant: Boolean = false,
    isOwner: Boolean = false,
    onCardClick: () -> Unit,
    onJoinClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isBusiness = socialType == SocialType.BUSINESS
    val cardBorder = if (isBusiness) {
        BorderStroke(2.dp, Brush.horizontalGradient(listOf(Color(0xFFFDBA74), Color(0xFFF97316))))
    } else {
        BorderStroke(1.dp, Color(0xFF334155))
    }

    Card(
        onClick = onCardClick,
        modifier = modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        border = cardBorder,
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1E293B)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isBusiness) 8.dp else 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header: User info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // User Avatar
                    if (userAvatar != null) {
                        AsyncImage(
                            model = userAvatar,
                            contentDescription = "User avatar",
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = userName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                            if (isBusiness) {
                                Icon(
                                    imageVector = Icons.Default.Verified,
                                    contentDescription = "Verified Business",
                                    tint = Color(0xFF3B82F6),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                        Text(
                            text = if (isBusiness) "Official Move • $timeAgo" else timeAgo,
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isBusiness) Color(0xFFFDBA74) else Color(0xFF94A3B8),
                            fontWeight = if (isBusiness) FontWeight.Medium else FontWeight.Normal
                        )
                    }
                }

                // Category Badge
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = categoryEmoji)
//                        Text(
//                            text = category,
//                            style = MaterialTheme.typography.labelMedium,
//                            color = MaterialTheme.colorScheme.onSecondaryContainer,
//                            fontWeight = FontWeight.Medium
//                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Move Title
            Text(
                text = moveTitle,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = Color.White
            )

            // Description (if exists)
            if (description != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFFCBD5E1),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Distance Badge
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = distance,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF3B82F6),
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Footer: Participants & Join Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Participant Avatars
                Row(
                    horizontalArrangement = Arrangement.spacedBy((-12).dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    participantAvatars?.take(3)?.forEach { avatar ->
                        AsyncImage(
                            model = avatar,
                            contentDescription = "Participant",
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surface),
                            contentScale = ContentScale.Crop
                        )
                    }

                    if (participantCount > 3) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "+${participantCount - 3}",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(30.dp))

                    Text(
                        text = if (maxParticipants != null) {
                            "$participantCount/$maxParticipants joined"
                        } else {
                            "$participantCount joined"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF94A3B8)
                    )
                }

                if (!isParticipant && !isOwner) {
                    val buttonText = when {
                        isRejected -> "REJECTED"
                        isRequested -> "REQUESTED"
                        else -> "I'M DOWN"
                    }
                    val isButtonEnabled = !isRequested && !isRejected

                    Button(
                        onClick = { if (isButtonEnabled) onJoinClick() },
                        modifier = Modifier.height(40.dp),
                        enabled = isButtonEnabled,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = when {
                                isRejected -> Color(0xFFEF4444).copy(alpha = 0.1f)
                                isRequested -> Color(0xFFF97316).copy(alpha = 0.2f)
                                isBusiness -> Color(0xFFF97316)
                                else -> MaterialTheme.colorScheme.primary
                            },
                            contentColor = when {
                                isRejected -> Color(0xFFEF4444)
                                isRequested -> Color(0xFFF97316)
                                else -> Color.White
                            },
                            disabledContainerColor = when {
                                isRejected -> Color(0xFFEF4444).copy(alpha = 0.1f)
                                isRequested -> Color(0xFFF97316).copy(alpha = 0.2f)
                                else -> Color(0xFF1E293B)
                            },
                            disabledContentColor = when {
                                isRejected -> Color(0xFFEF4444)
                                isRequested -> Color(0xFFF97316)
                                else -> Color(0xFF94A3B8)
                            }
                        ),
                        shape = RoundedCornerShape(20.dp),
                        border = when {
                            isRejected -> BorderStroke(1.dp, Color(0xFFEF4444).copy(alpha = 0.5f))
                            isRequested -> BorderStroke(1.dp, Color(0xFFF97316).copy(alpha = 0.5f))
                            else -> null
                        }
                    ) {
                        if (!isRequested && !isRejected) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                        }
                        Text(
                            text = buttonText,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

// ============================================
// File: presentation/components/UserAvatar.kt
// ============================================

@Composable
fun UserAvatar(
    avatarUrl: String?,
    userName: String,
    size: Int = 40,
    modifier: Modifier = Modifier
) {
    if (avatarUrl != null) {
        AsyncImage(
            model = avatarUrl,
            contentDescription = "$userName's avatar",
            modifier = modifier
                .size(size.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
    } else {
        Box(
            modifier = modifier
                .size(size.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = userName.firstOrNull()?.uppercase() ?: "?",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// ============================================
// File: presentation/components/PostCreationCard.kt
// ============================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostCreationCard(
    activityText: String,
    onActivityTextChange: (String) -> Unit,
    selectedCategory: String?,
    onCategorySelect: (String) -> Unit,
    selectedTime: String,
    onTimeSelect: () -> Unit,
    location: String,
    onLocationClick: () -> Unit,
    onPostClick: () -> Unit,
    isPosting: Boolean,
    modifier: Modifier = Modifier
) {
    val categories = listOf(
        "Travel" to "✈️",
        "Party" to "🎉",
        "Food" to "🍔",
        "Events" to "🎭",
        "Hobby" to "🎨",
        "Sports" to "⚽"
    )

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Title
            Text(
                text = "What's the move?",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            // Activity Input
            OutlinedTextField(
                value = activityText,
                onValueChange = onActivityTextChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 120.dp),
                placeholder = {
                    Text("E.g., Looking for people to explore Connaught Place tonight!")
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                shape = RoundedCornerShape(12.dp)
            )

            // Category Selection
            Text(
                text = "Category",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.take(3).forEach { (name, emoji) ->
                    CategoryChip(
                        label = name,
                        emoji = emoji,
                        isSelected = selectedCategory == name,
                        onClick = { onCategorySelect(name) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.drop(3).forEach { (name, emoji) ->
                    CategoryChip(
                        label = name,
                        emoji = emoji,
                        isSelected = selectedCategory == name,
                        onClick = { onCategorySelect(name) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Time Selection
            OutlinedCard(
                onClick = onTimeSelect,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Column {
                            Text(
                                text = "When?",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = selectedTime,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowRight,
                        contentDescription = null
                    )
                }
            }

            // Location Selection
            OutlinedCard(
                onClick = onLocationClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Column {
                            Text(
                                text = "Location",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = location,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowRight,
                        contentDescription = null
                    )
                }
            }

            // Post Button
            Button(
                onClick = onPostClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                enabled = !isPosting && activityText.isNotBlank() && selectedCategory != null,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(26.dp)
            ) {
                if (isPosting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(
                        text = "POST MOVE",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

// ============================================
// File: presentation/components/EmptyState.kt
// ============================================

@Composable
fun EmptyState(
    icon: ImageVector,
    title: String,
    description: String,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
        )

        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        if (actionText != null && onActionClick != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onActionClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(text = actionText)
            }
        }
    }
}

// ============================================
// File: presentation/components/LoadingIndicator.kt
// ============================================

@Composable
fun LoadingIndicator(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = MaterialTheme.colorScheme.primary
        )
    }
}

// ============================================
// File: presentation/components/ErrorView.kt
// ============================================

@Composable
fun ErrorView(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.error
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Oops!",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = onRetry) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Try Again")
        }
    }
}

@Composable
fun CategoryChip(
    label: String,
    emoji: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isSelected) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surface
    }

    val textColor = if (isSelected) {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    val borderColor = if (isSelected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
    }

    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick),
        color = backgroundColor,
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = borderColor
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = emoji,
                fontSize = 16.sp
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = textColor,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
            )
        }
    }
}

@Preview(showBackground = true, name = "Default Move Card")
@Composable
fun MoveCardPreview() {
    // You might need to wrap it in your app's theme for correct styling
    // MaterialTheme {
    MoveCard(
        userName = "Jane Doe",
        userAvatar = "https://example.com/avatar.jpg", // Replace with a valid image URL or leave empty
        moveTitle = "Evening Jog in the Park",
        description = "A relaxing jog to unwind after a long day. Everyone is welcome, no matter the pace!",
        category = "Running",
        categoryEmoji = "🏃‍♀️",
        timeAgo = "5m ago",
        distance = "2 km away",
        participantCount = 5,
        maxParticipants = 10,
        participantAvatars = listOf(
            "https://example.com/p1.jpg",
            "https://example.com/p2.jpg",
            "https://example.com/p3.jpg",
            "https://example.com/p4.jpg"
        ),
        onCardClick = {},
        onJoinClick = {}
    )
    // }
}

@Preview(showBackground = true, name = "Card with No Description")
@Composable
fun MoveCardNoDescriptionPreview() {
    // MaterialTheme {
    MoveCard(
        userName = "John Smith",
        userAvatar = "", // Example of no user avatar
        moveTitle = "Morning Coffee Meetup",
        description = "", // Empty description
        category = "Social",
        categoryEmoji = "☕",
        timeAgo = "1h ago",
        distance = "500m away",
        participantCount = 2,
        maxParticipants = 4,
        participantAvatars = listOf("https://example.com/p1.jpg"),
        onCardClick = {},
        onJoinClick = {}
    )
    // }
}

@Preview(showBackground = true, name = "Card with Unlimited Participants")
@Composable
fun MoveCardUnlimitedParticipantsPreview() {
    // MaterialTheme {
    MoveCard(
        userName = "Community Event",
        userAvatar = "",
        moveTitle = "Local Park Cleanup",
        description = "Let's make our park beautiful again. Gloves and bags will be provided.",
        category = "Volunteering",
        categoryEmoji = "🌍",
        timeAgo = "2d ago",
        distance = "1.5 km away",
        participantCount = 12,
        maxParticipants = null, // No limit on participants
        participantAvatars = listOf(
            "https://example.com/p1.jpg",
            "https://example.com/p2.jpg",
            "https://example.com/p3.jpg",
            "https://example.com/p4.jpg",
            "https://example.com/p5.jpg"
        ),
        onCardClick = {},
        onJoinClick = {}
    )
    // }
}



/*
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
    gradient: Brush,
    images: List<String> = emptyList(),
    onClick: (() -> Unit)? = null
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(gradient)
            .clickable(enabled = onClick != null) { onClick?.invoke() }
            .padding(20.dp)
    ) {

        */
/* Header *//*

        Row(verticalAlignment = Alignment.CenterVertically) {

            AsyncImage(
                model = avatar,
                contentDescription = userName,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .border(2.dp, androidx.compose.ui.graphics.Color.White.copy(alpha = 0.5f), CircleShape),
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(userName, color = androidx.compose.ui.graphics.Color.White, fontWeight = FontWeight.SemiBold)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AccessTime, null, tint = Color.White.copy(.8f), modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(timePosted, color = Color.White.copy(.8f), fontSize = 12.sp)
                }
            }

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(categoryColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                categoryIcon()
            }
        }

        Spacer(Modifier.height(16.dp))

        */
/* Title *//*

        Text(
            title,
            color = Color.White,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(16.dp))

        */
/* Images *//*

        if (images.isNotEmpty()) {
            ActivityImages(images)
            Spacer(Modifier.height(16.dp))
        }

        */
/* Info Bar *//*

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            InfoItem(Icons.Default.Place, distance)
            InfoItem(Icons.Default.Group, "$participantCount joined")
        }

        Spacer(Modifier.height(16.dp))

        */
/* Join Button *//*

        Button(
            onClick = {},
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.White)
        ) {
            Text("JOIN", color = Color.Black, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun ActivityImages(images: List<String>) {
    when (images.size) {
        1 -> SingleImage(images[0])
        2 -> TwoImages(images)
        else -> ThreeImages(images)
    }
}

@Composable
private fun SingleImage(url: String) {
    AsyncImage(
        model = url,
        contentDescription = null,
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(RoundedCornerShape(20.dp)),
        contentScale = ContentScale.Crop
    )
}

@Composable
private fun TwoImages(images: List<String>) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        images.take(2).forEach {
            AsyncImage(
                model = it,
                contentDescription = null,
                modifier = Modifier
                    .weight(1f)
                    .height(160.dp)
                    .clip(RoundedCornerShape(20.dp)),
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Composable
private fun ThreeImages(images: List<String>) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        SingleImage(images[0])
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            images.drop(1).take(2).forEach {
                AsyncImage(
                    model = it,
                    contentDescription = null,
                    modifier = Modifier
                        .height(76.dp)
                        .clip(RoundedCornerShape(20.dp)),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}

@Composable
private fun InfoItem(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = Color.White, modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(4.dp))
        Text(text, color = Color.White, fontSize = 13.sp)
    }
}

@Composable
fun CategoryChip(
    label: String,
    emoji: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isSelected) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surface
    }

    val textColor = if (isSelected) {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    val borderColor = if (isSelected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
    }

    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick),
        color = backgroundColor,
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = borderColor
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = emoji,
                fontSize = 16.sp
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = textColor,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
            )
        }
    }
}
*/

