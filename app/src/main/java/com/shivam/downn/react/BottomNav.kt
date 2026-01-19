package com.shivam.downn.react

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

sealed class BottomNavTab(val route: String, val label: String) {
    object Home : BottomNavTab("home", "Home")
    object Explore : BottomNavTab("explore", "Explore")
    object Create : BottomNavTab("create_post", "Create")
    object Notifications : BottomNavTab("notifications", "Alerts")
    object Profile : BottomNavTab("profile", "Profile")
}

@Composable
fun BottomNav(
    activeTab: BottomNavTab = BottomNavTab.Home,
    unreadCount: Int = 0,
    onTabSelected: (BottomNavTab) -> Unit = {},
    onCreateClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF0F172A).copy(alpha = 0.95f)) // Slate-900
            .padding(bottom = 12.dp) // Safe area simulation
    ) {
        // Divider
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(0.5.dp)
                .background(Color(0xFF334155).copy(alpha = 0.5f)) // Slate-700
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Home
            NavItem(
                label = "Home",
                icon = if (activeTab == BottomNavTab.Home) Icons.Filled.Home else Icons.Outlined.Home,
                isActive = activeTab == BottomNavTab.Home,
                onClick = { onTabSelected(BottomNavTab.Home) }
            )

            // Explore
            NavItem(
                label = "Explore",
                icon = Icons.Outlined.Search,
                isActive = activeTab == BottomNavTab.Explore,
                onClick = { onTabSelected(BottomNavTab.Explore) }
            )

            // Create
            NavItem(
                label = "Create",
                icon = if (activeTab == BottomNavTab.Create) Icons.Filled.Add else Icons.Outlined.Add,
                isActive = activeTab == BottomNavTab.Create,
                onClick = { onTabSelected(BottomNavTab.Create) }
            )

            // Alerts
            NavItem(
                label = "Alerts",
                icon = if (activeTab == BottomNavTab.Notifications) Icons.Filled.Notifications else Icons.Outlined.Notifications,
                isActive = activeTab == BottomNavTab.Notifications,
                unreadCount = unreadCount,
                onClick = { onTabSelected(BottomNavTab.Notifications) }
            )

            // Profile
            NavItem(
                label = "Profile",
                icon = Icons.Outlined.Person,
                isActive = activeTab == BottomNavTab.Profile,
                onClick = { onTabSelected(BottomNavTab.Profile) }
            )
        }
    }
}

@Composable
private fun NavItem(
    label: String,
    icon: ImageVector,
    isActive: Boolean,
    unreadCount: Int = 0,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val scale by animateFloatAsState(if (isActive) 0.9f else 1f)

    Column(
        modifier = Modifier
            .scale(scale)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(contentAlignment = Alignment.TopEnd) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.size(24.dp),
                tint = if (isActive) Color(0xFFC084FC) else Color(0xFF94A3B8) // Purple-400 : Slate-400
            )
            
            if (unreadCount > 0) {
                Box(
                    modifier = Modifier
                        .offset(x = 8.dp, y = (-4).dp)
                        .size(20.dp)
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(Color(0xFFF97316), Color(0xFFEA580C)) // Orange-500 to Orange-600
                            ),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (unreadCount > 9) "9+" else unreadCount.toString(),
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = if (isActive) Color(0xFFC084FC) else Color(0xFF94A3B8)
        )
    }
}


// Add this preview code to the end of your BottomNav.kt file

@Preview(showBackground = true, name = "Bottom Nav - Home Active")
@Composable
fun BottomNavPreview() {
    // It's good practice to wrap previews in your app's theme
    // YourAppTheme {
    Column {
        // Spacer to show the floating action button's offset
        Spacer(modifier = Modifier.height(40.dp))
        BottomNav(
            activeTab = BottomNavTab.Home,
            onTabSelected = {},
            onCreateClick = {}
        )
    }
    // }
}

@Preview(showBackground = true, name = "Bottom Nav - Alerts Active with Badge")
@Composable
fun BottomNavWithBadgePreview() {
    // YourAppTheme {
    Column {
        Spacer(modifier = Modifier.height(40.dp))
        BottomNav(
            activeTab = BottomNavTab.Notifications,
            unreadCount = 3,
            onTabSelected = {},
            onCreateClick = {}
        )
    }
    // }
}

@Preview(showBackground = true, name = "Bottom Nav - Badge Overflow")
@Composable
fun BottomNavWithOverflowBadgePreview() {
    // YourAppTheme {
    Column {
        Spacer(modifier = Modifier.height(40.dp))
        BottomNav(
            activeTab = BottomNavTab.Home,
            unreadCount = 25, // More than 9 to show "9+"
            onTabSelected = {},
            onCreateClick = {}
        )
    }
    // }
}

@Preview(showBackground = true, name = "Bottom Nav - Explore Active")
@Composable
fun BottomNavExplorePreview() {
    // YourAppTheme {
    Column {
        Spacer(modifier = Modifier.height(40.dp))
        BottomNav(
            activeTab = BottomNavTab.Explore,
            onTabSelected = {},
            onCreateClick = {}
        )
    }
    // }
}
