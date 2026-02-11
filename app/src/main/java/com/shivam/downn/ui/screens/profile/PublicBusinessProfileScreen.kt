package com.shivam.downn.ui.screens.profile

import androidx.compose.runtime.Composable

@Composable
fun PublicBusinessProfileScreen(
    businessId: Long,
    onClose: () -> Unit,
    onMoveClick: (Int) -> Unit
) {
    // Simply reuse BusinessProfileRoute with isOwnProfile = false
    BusinessProfileRoute(
        businessId = businessId,
        onClose = onClose,
        onMoveClick = onMoveClick,
        isOwnProfile = false,
        onEditBusinessProfileClick = {}
    )
}
