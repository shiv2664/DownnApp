package com.shivam.downn.navigation

import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.shivam.downn.react.ActivityDetailScreen
import com.shivam.downn.ui.screens.chat.GroupChat
import com.shivam.downn.ui.screens.create_activity.CreateActivity
import com.shivam.downn.ui.screens.explore.Explore
import com.shivam.downn.ui.screens.home.FeedScreen
import com.shivam.downn.ui.screens.auth.LoginScreen
import com.shivam.downn.ui.screens.notification.Notifications
import com.shivam.downn.ui.screens.profile.UserProfile

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
                UserProfile(innerPadding,{}, {}, {})
            }


            composable(route = "activity_detail/{activityId}") { backStackEntry ->
                val activityId = backStackEntry.arguments?.getString("activityId")?.toIntOrNull() ?: 1
                ActivityDetailScreen(
                    activityId = activityId,
                    innerPadding = innerPadding,
                    onClose = { navController.navigateUp() },
                    onOpenChat = { navController.navigate("group_chat") }
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
                    onClose = {
                        navController.navigateUp()
                    }
                )
            }
        }
    }
}

