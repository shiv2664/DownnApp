package com.shivam.downn.navigation

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Coffee
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.shivam.downn.react.ActivityDetail
import com.shivam.downn.ui.screens.chat.GroupChat
import com.shivam.downn.ui.screens.create_activity.CreateActivity
import com.shivam.downn.ui.screens.explore.Explore
import com.shivam.downn.ui.screens.home.FeedScreen
import com.shivam.downn.ui.screens.auth.LoginScreen
import com.shivam.downn.ui.screens.notification.Notifications
import com.shivam.downn.ui.screens.profile.UserProfile

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    Scaffold(bottomBar = { BottomBar(navController) }) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "login",
        ) {
            composable("login") {
                LoginScreen(onLoginSuccess = {
                    navController.navigate(itemsDataList[0].route) {
                        popUpTo("login") { inclusive = true }
                    }
                })
            }

            composable(
                route = itemsDataList[0].route, /*enterTransition = {
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
                }*/
            ) {
                FeedScreen(innerPadding, {}, {
                    navController.navigate("activity_detail")
                })
            }

            composable(
                route = itemsDataList[1].route,
               /* enterTransition = {
                    scaleIn(
                        initialScale = 0.0f,
                        transformOrigin = TransformOrigin(
                            0.4f,
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
                        transformOrigin = TransformOrigin(0.4f, 1.0f), // Shrink to right position
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
                        transformOrigin = TransformOrigin(0.4f, 1.0f),
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
                        transformOrigin = TransformOrigin(0.4f, 1.0f),
                        animationSpec = tween(
                            durationMillis = 400,
                            easing = FastOutSlowInEasing
                        )
                    ) + fadeOut(
                        animationSpec = tween(durationMillis = 300)
                    )
                }*/
            ) {
                Explore(innerPadding) {}
            }

            composable(
                route = itemsDataList[2].route,
               /* enterTransition = {
                    scaleIn(
                        initialScale = 0.0f,
                        transformOrigin = TransformOrigin(0.6f, 1.0f), // Left position (first item)
                        animationSpec = tween(
                            durationMillis = 300,
                            delayMillis = 200,
                            easing = FastOutSlowInEasing
                        )
                    ) + fadeIn(
                        animationSpec = tween(
                            durationMillis = 200,
                            delayMillis = 200
                        )
                    )
                },
                exitTransition = {
                    scaleOut(
                        targetScale = 0.0f,
                        transformOrigin = TransformOrigin(0.6f, 1.0f), // Shrink to left position
                        animationSpec = tween(
                            durationMillis = 200,
                            easing = FastOutSlowInEasing
                        )
                    ) + fadeOut(
                        animationSpec = tween(durationMillis = 300)
                    )
                },
                popEnterTransition = {
                    scaleIn(
                        initialScale = 0.0f,
                        transformOrigin = TransformOrigin(0.6f, 1.0f),
                        animationSpec = tween(
                            durationMillis = 300,
                            delayMillis = 200,
                            easing = FastOutSlowInEasing
                        )
                    ) + fadeIn(
                        animationSpec = tween(
                            durationMillis = 200,
                            delayMillis = 200
                        )
                    )
                },
                popExitTransition = {
                    scaleOut(
                        targetScale = 0.0f,
                        transformOrigin = TransformOrigin(0.6f, 1.0f),
                        animationSpec = tween(
                            durationMillis = 200,
                            easing = FastOutSlowInEasing
                        )
                    ) + fadeOut(
                        animationSpec = tween(durationMillis = 300)
                    )
                }*/
            ) {
                CreateActivity(innerPadding,{}, { _, _, _, _ -> })
            }

            composable(
                route = itemsDataList[3].route,
                /*enterTransition = {
                    scaleIn(
                        initialScale = 0.0f,
                        transformOrigin = TransformOrigin(0.8f, 1.0f), // Left position (first item)
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
                        transformOrigin = TransformOrigin(0.8f, 1.0f), // Shrink to left position
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
                }*/
            ) {
                Notifications(innerPadding)
            }

            composable(
                route = itemsDataList[4].route,
                /*enterTransition = {
                    scaleIn(
                        initialScale = 0.0f,
                        transformOrigin = TransformOrigin(
                            0.9f,
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
                        transformOrigin = TransformOrigin(0.9f, 1.0f), // Shrink to right position
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
                        transformOrigin = TransformOrigin(0.9f, 1.0f),
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
                        transformOrigin = TransformOrigin(0.9f, 1.0f),
                        animationSpec = tween(
                            durationMillis = 400,
                            easing = FastOutSlowInEasing
                        )
                    ) + fadeOut(
                        animationSpec = tween(durationMillis = 300)
                    )
                }*/
            ) {
                UserProfile(innerPadding,{}, {}, {})
            }


            composable(route = "activity_detail") {
                ActivityDetail(
                    innerPadding,
                    title = "Coffee at Blue Tokai ☕",
                    userName = "Rahul",
                    userAvatar = "",
                    distance = "0.8 km away",
                    participantCount = 3,
                    categoryIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Coffee,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    categoryColor = Color(0xFFA855F7),
                    images = listOf(
                        "https://picsum.photos/400/300",
                        "https://picsum.photos/401/300"
                    ),
                    description = "Anyone up for a quick coffee and chat this evening?",
                    onClose = {},
                    onOpenChat = {
                        navController.navigate("group_chat")
                    }
                )
            }

            composable("group_chat") {
                GroupChat(innerPadding,
                    activityTitle = "Downtown Coffee Meetup",
                    categoryIcon = {
                        // Using an emoji as a simple icon for the preview
                        Text("☕️", fontSize = 20.sp)
                    },
                    categoryColor = Color(0xFFFBBF24).copy(alpha = 0.2f),
                    participantCount = 12,
                    onClose = {})
            }
        }
    }
}

