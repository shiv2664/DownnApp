package com.shivam.downn.utils

import com.shivam.downn.R
import com.shivam.downn.data.models.ProfileType

object PlaceholderUtils {

    fun getAvatarPlaceholder(profileType: ProfileType?): Int {
        return when (profileType) {
            ProfileType.BUSINESS -> R.drawable.default_avatar_business
            else -> R.drawable.placeholder
        }
    }

    fun getCoverPlaceholder(category: String?): Int {
        val catLevel = category?.lowercase() ?: ""
        return when {
            catLevel.contains("sport") || catLevel.contains("fitness") -> R.drawable.default_cover_sports
            catLevel.contains("party") || catLevel.contains("nightlife") -> R.drawable.default_cover_party
            catLevel.contains("travel") || catLevel.contains("adventure") -> R.drawable.default_cover_travel
            catLevel.contains("food") || catLevel.contains("dining") -> R.drawable.default_cover_food
            catLevel.contains("entertainment") || catLevel.contains("art") -> R.drawable.default_cover_entertainment
            catLevel.contains("network") || catLevel.contains("business") -> R.drawable.default_cover_networking
            else -> R.drawable.default_cover_generic
        }
    }

    fun getBusinessCoverPlaceholder(): Int {
        return R.drawable.default_cover_business
    }

    fun getPersonalCoverPlaceholder(): Int {
        return R.drawable.default_cover_personal
    }
}
