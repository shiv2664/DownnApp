package com.shivam.downn.navigation

import com.shivam.downn.R


//data class DataScreens(val route: String, val label: String, val icon: @Composable () -> Unit)

data class DataScreens(
    val route: String,
    val label: String,
    val iconRes: Int,
    val iconResSelected: Int
)

val itemsDataList: List<DataScreens> = listOf(
    DataScreens(
        route = "home",
        label = "Home",
        iconRes = R.drawable.home_new,
        iconResSelected = R.drawable.home_new_selected
    ),
    DataScreens(
        "explore",
        " Explore",
        iconRes = R.drawable.bookmarks_new,
        iconResSelected = R.drawable.bookmarks_new_selected
    ),
    DataScreens(
        "create_post",
        " Create",
        iconRes = R.drawable.bookmarks_new,
        iconResSelected = R.drawable.bookmarks_new_selected
    ),
    DataScreens(
        "alerts",
        "Alerts",
        iconRes = R.drawable.bookmarks_new,
        iconResSelected = R.drawable.bookmarks_new_selected
    ),
    DataScreens(
        "profile",
        "Profile",
        iconRes = R.drawable.bookmarks_new,
        iconResSelected = R.drawable.bookmarks_new_selected
    )
)


//val itemsDataList: List<DataScreens> = listOf(
//    DataScreens(
//        route = "home",
//        label = "Home",
//        icon = { Icon(Icons.Filled.Home, contentDescription = "Home") }),
//    DataScreens(
//        "your_saves",
//        "Your Saves",
//        { Icon(Icons.Filled.Warning, contentDescription = "Search") })
//)