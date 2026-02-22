package com.shivam.downn.utils

import android.content.Context
import android.content.Intent

/**
 * Utility for sharing activity deep links.
 */
object ShareUtils {

    private const val BASE_URL = "https://downn.app"

    fun shareActivity(context: Context, activityId: Int, activityTitle: String) {
        val deepLink = "$BASE_URL/activity/$activityId"
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "Check out this activity on Downn!")
            putExtra(
                Intent.EXTRA_TEXT,
                "Hey! Check out \"$activityTitle\" on Downn 🎉\n$deepLink"
            )
        }
        context.startActivity(Intent.createChooser(shareIntent, "Share Activity"))
    }

    fun getActivityDeepLink(activityId: Int): String {
        return "$BASE_URL/activity/$activityId"
    }
}
