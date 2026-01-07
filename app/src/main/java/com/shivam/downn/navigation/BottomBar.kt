package com.shivam.downn.navigation

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController


@Composable
fun BottomBar(
    navController: NavHostController
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF0F172A).copy(alpha = 0.95f))
            .padding(bottom = 12.dp)
    ) {
        // Divider
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(0.5.dp)
                .background(Color(0xFF334155).copy(alpha = 0.5f))
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            BottomNavItem(
                label = "Home",
                icon = if (currentRoute == itemsDataList[0].route) Icons.Filled.Home else Icons.Outlined.Home,
                isActive = currentRoute == itemsDataList[0].route
            ) {
                navController.navigate(itemsDataList[0].route) {
//                    popUpTo(navController.graph.findStartDestination().id) {
//                        saveState = true
//                    }
                    launchSingleTop = true
//                    restoreState = true
                }
            }

            BottomNavItem(
                label = "Explore",
                icon = if (currentRoute == itemsDataList[1].route) Icons.Filled.Search else Icons.Outlined.Search,
                isActive = currentRoute == itemsDataList[1].route
            ) {
                navController.navigate(itemsDataList[1].route) {
                    launchSingleTop = true
                }
            }


            Box(
                modifier = Modifier
                    .offset(y = (-24).dp)
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(Color(0xFFA855F7), Color(0xFFEC4899))
                        )
                    )
                    .clickable {
                        navController.navigate(itemsDataList[2].route) {
                            launchSingleTop = true
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Create",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }


            BottomNavItem(
                label = "Alerts",
                icon = if (currentRoute == itemsDataList[3].route)
                    Icons.Filled.Notifications else Icons.Outlined.Notifications,
                isActive = currentRoute == itemsDataList[3].route,
            ) {
                navController.navigate(itemsDataList[3].route) {
                    launchSingleTop = true
                }
            }

            BottomNavItem(
                label = "Profile",
                icon = if (currentRoute == itemsDataList[4].route) Icons.Filled.Person else Icons.Outlined.Person,
                isActive = currentRoute == itemsDataList[4].route
            ) {
                navController.navigate(itemsDataList[4].route) {
                    launchSingleTop = true
                }
            }
        }
    }
}


@Composable
fun BottomNavItem(
    label: String,
    icon: ImageVector,
    isActive: Boolean,
    unreadCount: Int = 0,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(if (isActive) 1.1f else 1f)

    Column(
        modifier = Modifier
            .scale(scale)
            .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) {
                onClick()
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(contentAlignment = Alignment.TopEnd) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.size(24.dp),
                tint = if (isActive) Color(0xFFC084FC) else Color(0xFF94A3B8)
            )

            if (unreadCount > 0) {
                Box(
                    modifier = Modifier
                        .offset(x = 8.dp, y = (-4).dp)
                        .size(20.dp)
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFFF97316), Color(0xFFEA580C))
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



@Preview
@Composable
fun BottomBarPreview() {
    BottomBar(navController = rememberNavController())
}