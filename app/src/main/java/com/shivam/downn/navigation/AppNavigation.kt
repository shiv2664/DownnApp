package com.shivam.downn.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.shivam.downn.ui.screens.feedscreen.FeedScreen
import com.shivam.downn.ui.screens.profile.Profile

@Composable
fun AppNavigation() {

    val navController = rememberNavController()
    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                itemsDataList.forEach { screen ->
                    val isSelected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
                    NavigationBarItem(
                        icon = {
                            Icon(
                                painter = painterResource( if (isSelected) screen.iconResSelected else screen.iconRes),
                                contentDescription = "",
                            )
                        },
                        label = { Text(text = screen.label) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }

                    )
                }
            }
        }) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = itemsDataList[0].route,
            modifier = Modifier.padding(innerPadding)
        ) {

            composable(route = itemsDataList[0].route) {
                FeedScreen(innerPadding)
            }

            composable(route = itemsDataList[1].route) {
                Profile()
            }

        }
    }

}