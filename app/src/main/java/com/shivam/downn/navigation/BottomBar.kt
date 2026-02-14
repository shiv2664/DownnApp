package com.shivam.downn.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.AddCircleOutline
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
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController


@Composable
fun BottomBar(
    navController: NavHostController,
    unreadNotificationCount: Int = 0,
    onItemClick: (String) -> Unit
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val bottomRoutes = remember {
        itemsDataList.map { it.route }.toSet()
    }

    val shouldShowBottomBar =
        currentDestination?.hierarchy?.any { it.route in bottomRoutes } == true

    AnimatedVisibility(
        visible = shouldShowBottomBar,
        enter = slideInVertically { it } + fadeIn(),
        exit = slideOutVertically { it } + fadeOut()
    ) {
        BottomBarContent(navController, unreadNotificationCount, onItemClick)
    }
}

@Composable
fun BottomBarContent(
    navController: NavHostController,
    unreadNotificationCount: Int = 0,
    onItemClick: (String) -> Unit
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val currentDestination = navBackStackEntry?.destination


    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF0F172A).copy(alpha = 0.95f))
            .padding(bottom = 20.dp)
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
                icon = if (currentDestination?.hierarchy?.any { it.route == itemsDataList[0].route } == true) Icons.Filled.Home else Icons.Outlined.Home,
                isActive = currentDestination?.hierarchy?.any { it.route == itemsDataList[0].route } == true
            ) {

                navController.navigate(itemsDataList[0].route) {
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
                onItemClick(itemsDataList[0].route)
            }

            BottomNavItem(
                label = "Explore",
                icon = if (currentDestination?.hierarchy?.any { it.route == itemsDataList[1].route } == true) Icons.Filled.Search else Icons.Outlined.Search,
                isActive = currentDestination?.hierarchy?.any { it.route == itemsDataList[1].route } == true
            ) {
                navController.navigate(itemsDataList[1].route) {
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
                onItemClick(itemsDataList[1].route)
            }


            BottomNavItem(
                label = "Create",
                icon = if (currentDestination?.hierarchy?.any { it.route == itemsDataList[2].route } == true) Icons.Filled.AddCircle else Icons.Outlined.AddCircleOutline,
                isActive = currentDestination?.hierarchy?.any { it.route == itemsDataList[2].route } == true,
            ) {
                navController.navigate(itemsDataList[2].route) {
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
                onItemClick(itemsDataList[2].route)
            }


            BottomNavItem(
                label = "Alerts",
                icon = if (currentDestination?.hierarchy?.any { it.route == itemsDataList[3].route } == true) Icons.Filled.Notifications else Icons.Outlined.Notifications,
                isActive = currentDestination?.hierarchy?.any { it.route == itemsDataList[3].route } == true,
                unreadCount = unreadNotificationCount
            ) {
                navController.navigate(itemsDataList[3].route) {
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
                onItemClick(itemsDataList[3].route)
            }

            BottomNavItem(
                label = "Profile",
                icon = if (currentDestination?.hierarchy?.any { it.route == itemsDataList[4].route } == true) Icons.Filled.Person else Icons.Outlined.Person,
                isActive = currentDestination?.hierarchy?.any { it.route == itemsDataList[4].route } == true
            ) {
                navController.navigate(itemsDataList[4].route) {
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
                onItemClick(itemsDataList[4].route)
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
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(if (isActive) 1.1f else 1f)

    Column(
        modifier = modifier
            .scale(scale)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }) {
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
                                tint = Color.White            )

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
    BottomBarContent(navController = rememberNavController(), onItemClick = {})
}