package com.shivam.downn.ui.screens.profile

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

@Composable
fun PublicProfile(
    onClose: () -> Unit,
    onFollowClick: () -> Unit = {}
) {
    ProfileContent(
        isOwnProfile = false,
        outerPadding = PaddingValues(0.dp),
        onClose = onClose,
        onFollowClick = onFollowClick
    )
}
