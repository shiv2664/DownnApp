package com.shivam.downn.navigation

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.shivam.downn.ui.screens.MainScreen
import com.shivam.downn.ui.screens.feedscreen.FeedScreen
import com.shivam.downn.ui.screens.profile.Profile

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    Scaffold(bottomBar = { BottomBar(navController) }) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = itemsDataList[0].route,
        ) {

            composable(
                route = itemsDataList[0].route, enterTransition = {
                    scaleIn(
                        initialScale = 0.0f,
                        transformOrigin = TransformOrigin(0.2f, 1.0f), // Left position (first item)
                        animationSpec = tween(
                            durationMillis = 600,
                            delayMillis = 400, // Wait for exit animation
                            easing = FastOutSlowInEasing
                        )
                    ) + fadeIn(
                        animationSpec = tween(
                            durationMillis = 400,
                            delayMillis = 400
                        )
                    )
                },
                exitTransition = {
                    scaleOut(
                        targetScale = 0.0f,
                        transformOrigin = TransformOrigin(0.2f, 1.0f), // Shrink to left position
                        animationSpec = tween(
                            durationMillis = 400,
                            easing = FastOutSlowInEasing
                        )
                    ) + fadeOut(
                        animationSpec = tween(durationMillis = 300)
                    )
                },
                popEnterTransition = {
                    scaleIn(
                        initialScale = 0.0f,
                        transformOrigin = TransformOrigin(0.2f, 1.0f),
                        animationSpec = tween(
                            durationMillis = 600,
                            delayMillis = 400,
                            easing = FastOutSlowInEasing
                        )
                    ) + fadeIn(
                        animationSpec = tween(
                            durationMillis = 400,
                            delayMillis = 400
                        )
                    )
                },
                popExitTransition = {
                    scaleOut(
                        targetScale = 0.0f,
                        transformOrigin = TransformOrigin(0.2f, 1.0f),
                        animationSpec = tween(
                            durationMillis = 400,
                            easing = FastOutSlowInEasing
                        )
                    ) + fadeOut(
                        animationSpec = tween(durationMillis = 300)
                    )
                }
            ) {
                FeedScreen(PaddingValues(0.dp)) {
                    navController.navigate("main_screen/")
                }
            }

            composable(
                route = itemsDataList[1].route,
                enterTransition = {
                    scaleIn(
                        initialScale = 0.0f,
                        transformOrigin = TransformOrigin(
                            0.8f,
                            1.0f
                        ), // Right position (second item)
                        animationSpec = tween(
                            durationMillis = 600,
                            delayMillis = 400, // Wait for exit animation
                            easing = FastOutSlowInEasing
                        )
                    ) + fadeIn(
                        animationSpec = tween(
                            durationMillis = 400,
                            delayMillis = 400
                        )
                    )
                },
                exitTransition = {
                    scaleOut(
                        targetScale = 0.0f,
                        transformOrigin = TransformOrigin(0.8f, 1.0f), // Shrink to right position
                        animationSpec = tween(
                            durationMillis = 400,
                            easing = FastOutSlowInEasing
                        )
                    ) + fadeOut(
                        animationSpec = tween(durationMillis = 300)
                    )
                },
                popEnterTransition = {
                    scaleIn(
                        initialScale = 0.0f,
                        transformOrigin = TransformOrigin(0.8f, 1.0f),
                        animationSpec = tween(
                            durationMillis = 600,
                            delayMillis = 400,
                            easing = FastOutSlowInEasing
                        )
                    ) + fadeIn(
                        animationSpec = tween(
                            durationMillis = 400,
                            delayMillis = 400
                        )
                    )
                },
                popExitTransition = {
                    scaleOut(
                        targetScale = 0.0f,
                        transformOrigin = TransformOrigin(0.8f, 1.0f),
                        animationSpec = tween(
                            durationMillis = 400,
                            easing = FastOutSlowInEasing
                        )
                    ) + fadeOut(
                        animationSpec = tween(durationMillis = 300)
                    )
                }) {
                FeedScreen(innerPadding) {
                    navController.navigate("main_screen/")
                }
            }

            composable(
                route = itemsDataList[1].route, enterTransition = {
                    scaleIn(
                        initialScale = 0.0f,
                        transformOrigin = TransformOrigin(0.2f, 1.0f), // Left position (first item)
                        animationSpec = tween(
                            durationMillis = 600,
                            delayMillis = 400, // Wait for exit animation
                            easing = FastOutSlowInEasing
                        )
                    ) + fadeIn(
                        animationSpec = tween(
                            durationMillis = 400,
                            delayMillis = 400
                        )
                    )
                },
                exitTransition = {
                    scaleOut(
                        targetScale = 0.0f,
                        transformOrigin = TransformOrigin(0.2f, 1.0f), // Shrink to left position
                        animationSpec = tween(
                            durationMillis = 400,
                            easing = FastOutSlowInEasing
                        )
                    ) + fadeOut(
                        animationSpec = tween(durationMillis = 300)
                    )
                },
                popEnterTransition = {
                    scaleIn(
                        initialScale = 0.0f,
                        transformOrigin = TransformOrigin(0.2f, 1.0f),
                        animationSpec = tween(
                            durationMillis = 600,
                            delayMillis = 400,
                            easing = FastOutSlowInEasing
                        )
                    ) + fadeIn(
                        animationSpec = tween(
                            durationMillis = 400,
                            delayMillis = 400
                        )
                    )
                },
                popExitTransition = {
                    scaleOut(
                        targetScale = 0.0f,
                        transformOrigin = TransformOrigin(0.2f, 1.0f),
                        animationSpec = tween(
                            durationMillis = 400,
                            easing = FastOutSlowInEasing
                        )
                    ) + fadeOut(
                        animationSpec = tween(durationMillis = 300)
                    )
                }
            ) {
                FeedScreen(PaddingValues(0.dp)) {
                    navController.navigate("main_screen/")
                }
            }

            composable(
                route = itemsDataList[1].route,
                enterTransition = {
                    scaleIn(
                        initialScale = 0.0f,
                        transformOrigin = TransformOrigin(
                            0.8f,
                            1.0f
                        ), // Right position (second item)
                        animationSpec = tween(
                            durationMillis = 600,
                            delayMillis = 400, // Wait for exit animation
                            easing = FastOutSlowInEasing
                        )
                    ) + fadeIn(
                        animationSpec = tween(
                            durationMillis = 400,
                            delayMillis = 400
                        )
                    )
                },
                exitTransition = {
                    scaleOut(
                        targetScale = 0.0f,
                        transformOrigin = TransformOrigin(0.8f, 1.0f), // Shrink to right position
                        animationSpec = tween(
                            durationMillis = 400,
                            easing = FastOutSlowInEasing
                        )
                    ) + fadeOut(
                        animationSpec = tween(durationMillis = 300)
                    )
                },
                popEnterTransition = {
                    scaleIn(
                        initialScale = 0.0f,
                        transformOrigin = TransformOrigin(0.8f, 1.0f),
                        animationSpec = tween(
                            durationMillis = 600,
                            delayMillis = 400,
                            easing = FastOutSlowInEasing
                        )
                    ) + fadeIn(
                        animationSpec = tween(
                            durationMillis = 400,
                            delayMillis = 400
                        )
                    )
                },
                popExitTransition = {
                    scaleOut(
                        targetScale = 0.0f,
                        transformOrigin = TransformOrigin(0.8f, 1.0f),
                        animationSpec = tween(
                            durationMillis = 400,
                            easing = FastOutSlowInEasing
                        )
                    ) + fadeOut(
                        animationSpec = tween(durationMillis = 300)
                    )
                }) {
                Profile()
            }
        }
    }
}

@Composable
fun BottomBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = 12.dp,
                end = 12.dp,
                bottom = 16.dp
            )
    ) {
        NavigationBar(
            modifier = Modifier
                .height(70.dp)
                .clip(RoundedCornerShape(20.dp)),
            containerColor = MaterialTheme.colorScheme.primary,
            tonalElevation = 0.dp,
            windowInsets = WindowInsets(0, 0, 0, 0)
        ) {
            itemsDataList.forEach { screen ->
                val isSelected =
                    currentDestination?.hierarchy?.any { it.route == screen.route } == true

                NavigationBarItem(
                    selected = isSelected,
                    onClick = {
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = {
                        Icon(
                            modifier = Modifier.size(24.dp),
                            painter = painterResource(
                                if (isSelected) screen.iconResSelected else screen.iconRes
                            ),
                            contentDescription = null
                        )
                    },
                    label = null,
                    alwaysShowLabel = false,
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = Color.White,
                        unselectedIconColor = Color.White,
                        unselectedTextColor = Color.White,
                        indicatorColor = Color.Transparent
                    )
                )
            }
        }
    }
}


@Preview
@Composable
fun BottomBarPreview() {
    BottomBar(navController = rememberNavController())
}
