package com.shivam.downn.navigation

import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.shivam.downn.ui.screens.activity_detail.ActivityDetailScreen
import com.shivam.downn.ui.screens.chat.GroupChat
import com.shivam.downn.ui.screens.create_activity.CreateActivity
import com.shivam.downn.ui.screens.explore.Explore
import com.shivam.downn.ui.screens.home.FeedScreen
import com.shivam.downn.ui.screens.auth.LoginScreen
import com.shivam.downn.ui.screens.notification.Notifications
import com.shivam.downn.ui.screens.profile.UserProfile
import com.shivam.downn.ui.screens.profile.EditProfileScreen
import com.shivam.downn.ui.screens.profile.PublicProfile
import com.shivam.downn.ui.screens.settings.SettingsScreen

@Composable
fun AppNavigation(startDestination: String = "login") {
    val navController = rememberNavController()
    Scaffold(bottomBar = { BottomBar(navController) }) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
        ) {
            composable("login") {
                LoginScreen(onLoginSuccess = {
                    navController.navigate(itemsDataList[0].route) {
                        popUpTo("login") { inclusive = true }
                    }
                })
            }

            composable(
                route = itemsDataList[0].route) {
                FeedScreen(innerPadding, { activityId ->
                    navController.navigate("activity_detail/$activityId")
                }, {
                    navController.navigate("activity_detail/1")
                })
            }

            composable(
                route = itemsDataList[1].route) {
                Explore(innerPadding) {}
            }

            composable(
                route = itemsDataList[2].route) {
                CreateActivity(innerPadding,{})
            }

            composable(
                route = itemsDataList[3].route) {
                Notifications(innerPadding)
            }

            composable(
                route = itemsDataList[4].route) {
                UserProfile(
                    outerPadding = innerPadding,
                    onClose = { navController.navigateUp() },
                    onSettingsClick = { navController.navigate("settings") },
                    onEditClick = { navController.navigate("edit_profile") }
                )
            }


            composable("edit_profile") {
                EditProfileScreen(
                    onClose = { navController.navigateUp() },
                    onSave = { name, bio, location ->
                        // In a real app, update ViewModel here
                        navController.navigateUp()
                    }
                )
            }

            composable("settings") {
                SettingsScreen(
                    onClose = { navController.navigateUp() },
                    onLogout = {
                        // In a real app, clear session and navigate to login
                        navController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }

            composable("public_profile/{userId}") {
                PublicProfile(
                    onClose = { navController.navigateUp() },
                    onFollowClick = { /* Handle follow */ }
                )
            }


            composable(route = "activity_detail/{activityId}") { backStackEntry ->
                val activityId = backStackEntry.arguments?.getString("activityId")?.toIntOrNull() ?: 1
                ActivityDetailScreen(
                    activityId = activityId,
                    onClose = { navController.navigateUp() },
                    onOpenChat = { navController.navigate("group_chat/$activityId") },
                    onViewProfile = { userId -> navController.navigate("public_profile/$userId") }
                )
            }

            composable("group_chat/{activityId}") {
                GroupChat(innerPadding,
                    activityTitle = "Downtown Coffee Meetup",
                    categoryIcon = {
                        // Using an emoji as a simple icon for the preview
                        Text("☕️", fontSize = 20.sp)
                    },
                    categoryColor = Color(0xFFFBBF24).copy(alpha = 0.2f),
                    participantCount = 12,
                    onClose = {
                        navController.navigateUp()
                    }
                )
            }
        }
    }
}

