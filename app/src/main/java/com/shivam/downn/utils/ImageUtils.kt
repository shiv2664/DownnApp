package com.shivam.downn.utils

object ImageUtils {
    // Centralized base URL — matches the Retrofit base URL in NetworkModule.
    // For Emulator: http://10.0.2.2:8081
    // For Physical Device: Your LAN IP
    private const val BASE_URL = "http://192.168.1.8:8081"

    fun getFullImageUrl(path: String?): String {
        if (path.isNullOrEmpty()) return "https://images.unsplash.com/photo-1551818255-e6e10975bc17" // Default fallback
        if (path.startsWith("http")) return path
        if (path.startsWith("/")) return "$BASE_URL$path"
        return "$BASE_URL/$path"
    }

    /**
     * Get just the base URL — useful for constructing dynamic URLs.
     */
    fun getBaseUrl(): String = BASE_URL
}
