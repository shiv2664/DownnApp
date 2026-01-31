package com.shivam.downn.ui.screens.profile

import androidx.compose.runtime.Composable

@Composable
fun PublicBusinessProfileScreen(
    businessId: Long,
    onClose: () -> Unit,
    onMoveClick: (Int) -> Unit
) {
    // Simply reuse BusinessProfileScreen with isOwnProfile = false
    BusinessProfileScreen(
        businessId = businessId,
        onClose = onClose,
        onMoveClick = onMoveClick,
        isOwnProfile = false
    )
}
