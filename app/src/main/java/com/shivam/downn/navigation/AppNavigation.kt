package com.shivam.downn.navigation

import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.shivam.downn.ui.screens.activity_detail.SocialDetailScreen
import com.shivam.downn.ui.screens.chat.GroupChat
import com.shivam.downn.ui.screens.create_activity.StartMove
import com.shivam.downn.ui.screens.explore.Explore
import com.shivam.downn.ui.screens.feed.FeedScreen
import com.shivam.downn.ui.screens.auth.LoginScreen
import com.shivam.downn.ui.screens.notification.Notifications
import com.shivam.downn.ui.screens.profile.UserProfile
import com.shivam.downn.ui.screens.profile.EditProfileScreen
import com.shivam.downn.ui.screens.profile.PublicProfile
import com.shivam.downn.ui.screens.profile.BusinessProfileScreen
import com.shivam.downn.ui.screens.profile.CreateProfileScreen
import com.shivam.downn.ui.screens.create_activity.StartBusinessMove
import com.shivam.downn.ui.screens.chat.LiveBoardScreen
import com.shivam.downn.ui.screens.settings.SettingsScreen
import com.shivam.downn.ui.screens.profile.ProfileViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.shivam.downn.ui.screens.profile.PublicBusinessProfileScreen
import com.shivam.downn.data.models.ProfileType
import com.shivam.downn.data.models.SocialType
import com.shivam.downn.ui.screens.profile.EditBusinessProfileScreen

@Composable
fun AppNavigation(startDestination: String = "login") {
    val navController = rememberNavController()
    val sharedProfileViewModel: ProfileViewModel = hiltViewModel()

    Scaffold(bottomBar = { BottomBar(navController) }) { outerPadding ->
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
                route = itemsDataList[0].route
            ) {
                FeedScreen({ socialType, socialId ->
                    if (socialType == SocialType.BUSINESS) {
                        navController.navigate("business_profile/$socialId")
                    } else {
                        navController.navigate("social_detail/$socialId")
                    }
                }, {

                        socialType, socialId ->
                    if (socialType == SocialType.BUSINESS) {
                        navController.navigate("business_profile/$socialId")
                    } else {
                        navController.navigate("social_detail/$socialId")
                    }
                })
            }

            composable(
                route = itemsDataList[1].route
            ) {
                Explore(outerPadding) {}
            }

            composable(
                route = itemsDataList[2].route
            ) {
                val activeProfile by sharedProfileViewModel.activeProfile.collectAsState()

                if (activeProfile?.type == ProfileType.BUSINESS) {
                    StartBusinessMove(
                        outerPadding = outerPadding,
                        onClose = { navController.navigateUp() }
                    )
                } else {
                    StartMove(
                        outerPadding = outerPadding,
                        onClose = { navController.navigateUp() }
                    )
                }
            }

            composable(
                route = itemsDataList[3].route
            ) {
                Notifications(outerPadding)
            }

            composable(
                route = itemsDataList[4].route
            ) {
                UserProfile(
                    outerPadding = outerPadding,
                    onClose = { navController.navigateUp() },
                    onSettingsClick = { navController.navigate("settings") },
                    onEditClick = { navController.navigate("edit_profile") },
                    onCreateProfileClick = { navController.navigate("create_profile") },
                    onBusinessMoveClick = { moveId -> navController.navigate("social_detail/$moveId") },
                    viewModel = sharedProfileViewModel,
                    onEditBusinessProfileClick = { id -> navController.navigate("edit_business_profile/$id") }

                )
            }

            composable("edit_profile") {
                EditProfileScreen(
                    onClose = { navController.navigateUp() },
                    viewModel = sharedProfileViewModel
                )
            }
            composable("edit_business_profile/{businessId}") { backStackEntry ->
                val businessId =
                    backStackEntry.arguments?.getString("businessId")?.toLongOrNull() ?: -1L
                EditBusinessProfileScreen(
                    businessId = businessId,
                    onClose = { navController.navigateUp() },
                    viewModel = sharedProfileViewModel
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

            composable("public_profile/{userId}") { backStackEntry ->
                val userId = backStackEntry.arguments?.getString("userId")?.toLongOrNull() ?: -1L
                PublicProfile(
                    userId = userId,
                    onClose = { navController.navigateUp() },
                    onFollowClick = { /* Handle follow */ },
                    viewModel = sharedProfileViewModel
                )
            }


            composable(route = "social_detail/{socialId}") { backStackEntry ->
                val socialId = backStackEntry.arguments?.getString("socialId")?.toIntOrNull() ?: 1
                SocialDetailScreen(
                    socialId = socialId,
                    onClose = { navController.navigateUp() },
                    onOpenChat = { navController.navigate("group_chat/$socialId") },
                    onViewProfile = { userId -> navController.navigate("public_profile/$userId") }
                )
            }

            composable(route = "business_profile/{businessId}") { backStackEntry ->
                val businessId =
                    backStackEntry.arguments?.getString("businessId")?.toLongOrNull() ?: 16L
                BusinessProfileScreen(
                    businessId = businessId,
                    onClose = { navController.navigateUp() },
                    onMoveClick = { moveId -> navController.navigate("social_detail/$moveId") },
                    onEditBusinessProfileClick = { id -> navController.navigate("edit_business_profile/$id") },
                    viewModel = sharedProfileViewModel
                )
            }

            composable("group_chat/{socialId}") { backStackEntry ->
                val socialId = backStackEntry.arguments?.getString("socialId")?.toIntOrNull() ?: 1
                GroupChat(
                    outerPadding,
                    socialTitle = "Downtown Coffee Meetup",
                    categoryIcon = {
                        Text("☕️", fontSize = 20.sp)
                    },
                    categoryColor = Color(0xFFFBBF24).copy(alpha = 0.2f),
                    participantCount = 12,
                    onClose = {
                        navController.navigateUp()
                    }
                )
            }

            composable("public_business_profile/{businessId}") { backStackEntry ->
                val businessId =
                    backStackEntry.arguments?.getString("businessId")?.toLongOrNull() ?: 16L
                PublicBusinessProfileScreen(
                    businessId = businessId,
                    onClose = { navController.navigateUp() },
                    onMoveClick = { moveId -> navController.navigate("social_detail/$moveId") }
                )
            }

            composable("live_board/{socialId}") { backStackEntry ->
                val socialId = backStackEntry.arguments?.getString("socialId")?.toIntOrNull() ?: 1
                // Mock data for demo
                LiveBoardScreen(
                    socialId = socialId,
                    socialTitle = if (socialId == 16) "Live Jazz Night 🎷" else "Friday Night Fever 🕺",
                    businessName = if (socialId == 16) "The Daily Grind" else "Club Social",
                    businessAvatar = "",
                    onClose = { navController.navigateUp() }
                )
            }

            composable("create_profile") {
                CreateProfileScreen(
                    onClose = { navController.navigateUp() },
                    onCreateSuccess = { name, category, bio, loc ->
//                        profileViewModel.createBusinessProfile(name, category, bio, loc)
                        navController.navigateUp()
                    }
                )
            }
        }
    }
}

