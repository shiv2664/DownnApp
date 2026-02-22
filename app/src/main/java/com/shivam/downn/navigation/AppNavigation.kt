package com.shivam.downn.navigation

import android.widget.Toast
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.shivam.downn.ui.screens.chat.GroupChatRoute
import com.shivam.downn.ui.screens.create_activity.StartMove
import com.shivam.downn.ui.screens.auth.LoginScreen
import com.shivam.downn.ui.screens.profile.EditProfileScreen
import com.shivam.downn.ui.screens.profile.CreateProfileScreen
import com.shivam.downn.ui.screens.create_activity.StartBusinessMove
import com.shivam.downn.ui.screens.chat.LiveBoardScreen
import com.shivam.downn.ui.screens.settings.SettingsScreen
import com.shivam.downn.ui.screens.settings.SettingsDetailScreen
import com.shivam.downn.ui.screens.profile.MyProfileViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.shivam.downn.ui.screens.profile.PublicBusinessProfileScreen
import com.shivam.downn.data.models.ProfileType
import com.shivam.downn.data.models.SocialType
import com.shivam.downn.ui.screens.activity_detail.ParticipantsRoute
import com.shivam.downn.ui.screens.activity_detail.SocialDetailRoute
import com.shivam.downn.ui.screens.explore.ExploreRoute
import com.shivam.downn.ui.screens.feed.FeedRoute
import com.shivam.downn.ui.screens.notification.NotificationsRoute
import com.shivam.downn.ui.screens.profile.BusinessProfileRoute
import com.shivam.downn.ui.screens.profile.EditBusinessProfileScreen
import com.shivam.downn.ui.screens.profile.PublicProfileRoute
import com.shivam.downn.ui.screens.profile.UserProfileRoute

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.shivam.downn.data.local.SessionManager
import com.shivam.downn.ui.screens.auth.ForgotPasswordRoute
import com.shivam.downn.ui.screens.auth.ResetPasswordRoute
import com.shivam.downn.ui.screens.chat.ChatListRoute
import com.shivam.downn.ui.screens.notification.NotificationViewModel
import com.shivam.downn.utils.SnackbarManager

@Composable
fun AppNavigation(
    startDestination: String = "login",
    sessionManager: SessionManager? = null
) {
    val navController = rememberNavController()
    val myProfileViewModel: MyProfileViewModel = hiltViewModel()
    val navigationViewModel: NavigationViewModel = hiltViewModel()
    val notificationViewModel: NotificationViewModel = hiltViewModel()
    val unreadCount by notificationViewModel.unreadCount.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Global Snackbar collection
    LaunchedEffect(Unit) {
        SnackbarManager.messages.collect { msg ->
            snackbarHostState.showSnackbar(
                message = msg.message,
                actionLabel = msg.actionLabel,
                duration = if (msg.isError) SnackbarDuration.Long
                else SnackbarDuration.Short
            )
        }
    }

    val context = LocalContext.current
    LaunchedEffect(Unit) {
        sessionManager?.logoutEvent?.collect { message ->
            if (message != null) {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }
            navController.navigate("login") {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            BottomBar(navController, unreadNotificationCount = unreadCount) { route ->
                navigationViewModel.onBottomNavClick(route)
            }
        }
    ) { outerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
        ) {
            composable("login") {
                LoginScreen(onLoginSuccess = {
                    navController.navigate(itemsDataList[0].route) {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavigateToForgotPassword = {
                    navController.navigate("forgot_password")
                })
            }

            composable("forgot_password") {
                ForgotPasswordRoute(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToResetPassword = { navController.navigate("reset_password") }
                )
            }

            composable("reset_password") {
                ResetPasswordRoute(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToLogin = {
                        navController.navigate("login") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                )
            }

            composable(
                route = itemsDataList[0].route
            ) {
                FeedRoute(
                    { socialType, socialId ->
                        navController.navigate("social_detail/$socialId")
                    }, {
                            socialType, socialId ->
                        if (socialType == SocialType.BUSINESS) {
                            navController.navigate("social_detail/$socialId")
                        } else {
                            navController.navigate("social_detail/$socialId")
                        }
                    },
                    onChatClick = {
                        navController.navigate("chat_list")
                    })
            }

            composable(
                route = itemsDataList[1].route
            ) {
                ExploreRoute(outerPadding, onSocialClick = { social ->
                    navController.navigate("social_detail/${social.id}")
                })
            }

            composable(
                route = itemsDataList[2].route
            ) {
                val activeProfile by myProfileViewModel.activeProfile.collectAsState()

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
                NotificationsRoute(
                    outerPadding = outerPadding,
                    onNotificationClick = { notification ->
                        notification.activityId?.let { id ->
                            navController.navigate("social_detail/$id")
                        }
                    }
                )
            }

            composable(
                route = itemsDataList[4].route
            ) {
                UserProfileRoute(
                    outerPadding = outerPadding,
                    onClose = { navController.navigateUp() },
                    onSettingsClick = { navController.navigate("settings") },
                    onEditClick = { navController.navigate("edit_profile") },
                    onCreateProfileClick = { navController.navigate("create_profile") },
                    onBusinessMoveClick = { moveId -> navController.navigate("social_detail/$moveId") },
                    viewModel = myProfileViewModel,
                    onEditBusinessProfileClick = { id -> navController.navigate("edit_business_profile/$id") },
                    onActivityClick = { id -> navController.navigate("social_detail/$id") }
                )
            }

            composable("edit_profile") {
                EditProfileScreen(
                    onClose = { navController.navigateUp() },
                    activeProfile = myProfileViewModel.activeProfile.collectAsState().value
                )
            }
            composable("edit_business_profile/{businessId}") { backStackEntry ->
                val businessId = backStackEntry.arguments?.getString("businessId")?.toLongOrNull() ?: -1L
                EditBusinessProfileScreen(
                    businessId = businessId,
                    onClose = { navController.navigateUp() },
                    activeProfile = myProfileViewModel.activeProfile.collectAsState().value
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
                    },
                    onNavigateToDetail = { title ->
                        navController.navigate("settings_detail/$title")
                    }
                )
            }

            composable("settings_detail/{title}") { backStackEntry ->
                val title = backStackEntry.arguments?.getString("title") ?: "Settings"
                SettingsDetailScreen(
                    title = title,
                    onClose = { navController.navigateUp() }
                )
            }

            composable("public_profile/{userId}") { backStackEntry ->
                val userId = backStackEntry.arguments?.getString("userId")?.toLongOrNull() ?: -1L
                PublicProfileRoute(
                    userId = userId,
                    onClose = { navController.navigateUp() },
                    onFollowClick = { /* Handle follow */ }
                )
            }


            composable(route = "social_detail/{socialId}") { backStackEntry ->
                val socialId = backStackEntry.arguments?.getString("socialId")?.toIntOrNull() ?: 1
                SocialDetailRoute(
                    socialId = socialId,
                    onClose = { navController.navigateUp() },
                    onOpenChat = { title -> navController.navigate("group_chat/$socialId?title=$title") },
                    onOpenLiveBoard = { title, businessName, businessAvatar, isOwner ->
                        val encodedTitle = java.net.URLEncoder.encode(title, java.nio.charset.StandardCharsets.UTF_8.toString())
                        val encodedName = java.net.URLEncoder.encode(businessName, java.nio.charset.StandardCharsets.UTF_8.toString())
                        val encodedAvatar = java.net.URLEncoder.encode(businessAvatar, java.nio.charset.StandardCharsets.UTF_8.toString())
                        navController.navigate("live_board/$socialId?title=$encodedTitle&businessName=$encodedName&businessAvatar=$encodedAvatar&isOwner=$isOwner")
                    },
                    onViewProfile = { userId, isBusiness ->
                        if (isBusiness) {
                            navController.navigate("public_business_profile/$userId")
                        } else {
                            navController.navigate("public_profile/$userId")
                        }
                    },
                    onSeeAllParticipants = { id -> navController.navigate("participants/$id") },
                    onEditActivity = { id -> navController.navigate("edit_activity/$id") }
                )
            }
            
            composable(route = "edit_activity/{socialId}") { backStackEntry ->
                val socialId = backStackEntry.arguments?.getString("socialId")?.toIntOrNull() ?: -1
                com.shivam.downn.ui.screens.create_activity.EditActivityScreen(
                    socialId = socialId,
                    onClose = { navController.navigateUp() },
                    onUpdateSuccess = { navController.navigateUp() }
                )
            }

            composable(route = "participants/{socialId}") { backStackEntry ->
                val socialId = backStackEntry.arguments?.getString("socialId")?.toIntOrNull() ?: 1
                ParticipantsRoute(
                    socialId = socialId,
                    onClose = { navController.navigateUp() },
                    onViewProfile = { userId -> navController.navigate("public_profile/$userId") }
                )
            }

            composable(route = "business_profile/{businessId}") { backStackEntry ->
                val businessId = backStackEntry.arguments?.getString("businessId")?.toLongOrNull() ?: 16L
                BusinessProfileRoute(
                    businessId = businessId,
                    onClose = { navController.navigateUp() },
                    onMoveClick = { moveId -> navController.navigate("social_detail/$moveId") },
                    onEditBusinessProfileClick = { id -> navController.navigate("edit_business_profile/$id") },
                    viewModel = myProfileViewModel
                )
            }

            composable("chat_list") {
                ChatListRoute(navController = navController)
            }

            composable(
                "group_chat/{socialId}?title={title}",
                arguments = listOf(
                    navArgument("socialId") {
                        type = NavType.StringType
                    },
                    navArgument("title") {
                        type = NavType.StringType; defaultValue = "Chat"
                    }
                )
            ) { backStackEntry ->
                val socialIdString = backStackEntry.arguments?.getString("socialId")
                val socialId = socialIdString?.toLongOrNull() ?: 1L
                val title = backStackEntry.arguments?.getString("title") ?: "Chat"

                GroupChatRoute(
                    activityId = socialId,
                    innerPadding = outerPadding,
                    socialTitle = title,
                    categoryIcon = {
                        Text("💬", fontSize = 20.sp)
                    },
                    categoryColor = Color(0xFFFBBF24).copy(alpha = 0.2f),
                    participantCount = 0, // In real app, pass this or fetch in VM
                    onClose = {
                        navController.navigateUp()
                    },
                    onViewDetails = {
                        navController.navigate("social_detail/$socialId")
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

            composable(
                "live_board/{socialId}?title={title}&businessName={businessName}&businessAvatar={businessAvatar}&isOwner={isOwner}",
                arguments = listOf(
                    navArgument("socialId") { type = NavType.IntType },
                    navArgument("title") { type = NavType.StringType; defaultValue = "" },
                    navArgument("businessName") { type = NavType.StringType; defaultValue = "" },
                    navArgument("businessAvatar") { type = NavType.StringType; defaultValue = "" },
                    navArgument("isOwner") { type = NavType.BoolType; defaultValue = false }
                )
            ) { backStackEntry ->
                val socialId = backStackEntry.arguments?.getInt("socialId") ?: 0
                val title = backStackEntry.arguments?.getString("title") ?: ""
                val businessName = backStackEntry.arguments?.getString("businessName") ?: ""
                val businessAvatar = backStackEntry.arguments?.getString("businessAvatar") ?: ""
                val isOwner = backStackEntry.arguments?.getBoolean("isOwner") ?: false

                LiveBoardScreen(
                    socialId = socialId,
                    socialTitle = title,
                    businessName = businessName,
                    businessAvatar = businessAvatar,
                    isOwner = isOwner,
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

