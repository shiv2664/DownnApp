package com.shivam.downn.utils

import com.shivam.downn.data.models.ProfileType

object ImageUtils {
    // Centralized base URL — matches the Retrofit base URL in NetworkModule.
    // For Emulator: http://10.0.2.2:8081
    // For Physical Device: Your LAN IP
    private const val BASE_URL = Constants.BASE_URL

    fun getFullImageUrl(path: String?): String {
        if (path.isNullOrEmpty()) return ""
        if (path.startsWith("http") || path.startsWith("https") || path.startsWith("content://") || path.startsWith("file://")) {
            return path
        }
        if (path.startsWith("/")) return "$BASE_URL$path"
        return "$BASE_URL/$path"
    }

    /**
     * Get just the base URL — useful for constructing dynamic URLs.
     */
    fun getBaseUrl(): String = BASE_URL
}
