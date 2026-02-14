package com.shivam.downn.data.repository

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import com.shivam.downn.data.api.AppSettingsApi
import com.shivam.downn.data.models.AppSettings
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Fetches endpoint configuration from the server and caches it in SharedPreferences.
 * On app launch, call [refreshSettings] to update the cached endpoints.
 * If the network call fails, the previously cached version is used as fallback.
 */
@Singleton
class AppSettingsRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val appSettingsApi: AppSettingsApi
) {
    private val prefs: SharedPreferences = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
    private val gson = Gson()

    companion object {
        private const val TAG = "AppSettingsRepo"
        private const val KEY_SETTINGS = "cached_settings"
        private const val KEY_LAST_FETCHED = "last_fetched_at"
    }

    /**
     * Fetch latest settings from the server and cache them.
     * Returns true if successful, false if it fell back to cache.
     */
    suspend fun refreshSettings(): Boolean {
        return try {
            val response = appSettingsApi.getAppSettings()
            if (response.isSuccessful && response.body() != null) {
                val settings = response.body()!!
                saveSettings(settings)
                Log.d(TAG, "Settings refreshed: API v${settings.apiVersion}")
                true
            } else {
                Log.w(TAG, "Failed to fetch settings: ${response.code()}")
                false
            }
        } catch (e: Exception) {
            Log.w(TAG, "Network error fetching settings, using cache", e)
            false
        }
    }

    /**
     * Get the cached settings. Returns null if never fetched.
     */
    fun getSettings(): AppSettings? {
        val json = prefs.getString(KEY_SETTINGS, null) ?: return null
        return try {
            gson.fromJson(json, AppSettings::class.java)
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing cached settings", e)
            null
        }
    }

    /**
     * Get a specific endpoint path by dotted key (e.g., "auth.login").
     * Falls back to the default v1 path if not cached.
     */
    fun getEndpoint(key: String): String? {
        val settings = getSettings() ?: return null
        val parts = key.split(".")
        if (parts.size != 2) return null

        return when (parts[0]) {
            "auth" -> when (parts[1]) {
                "login" -> settings.endpoints.auth.login
                "register" -> settings.endpoints.auth.register
                "logout" -> settings.endpoints.auth.logout
                "deleteAccount" -> settings.endpoints.auth.deleteAccount
                else -> null
            }
            "activities" -> when (parts[1]) {
                "create" -> settings.endpoints.activities.create
                "getByCity" -> settings.endpoints.activities.getByCity
                "getByUser" -> settings.endpoints.activities.getByUser
                "getRecent" -> settings.endpoints.activities.getRecent
                "getById" -> settings.endpoints.activities.getById
                "join" -> settings.endpoints.activities.join
                "leave" -> settings.endpoints.activities.leave
                "removeParticipant" -> settings.endpoints.activities.removeParticipant
                "delete" -> settings.endpoints.activities.delete
                "update" -> settings.endpoints.activities.update
                "messages" -> settings.endpoints.activities.messages
                else -> null
            }
            "users" -> when (parts[1]) {
                "getDetails" -> settings.endpoints.users.getDetails
                "getProfiles" -> settings.endpoints.users.getProfiles
                "createProfile" -> settings.endpoints.users.createProfile
                "getProfileDetails" -> settings.endpoints.users.getProfileDetails
                "updateUser" -> settings.endpoints.users.updateUser
                "updateProfile" -> settings.endpoints.users.updateProfile
                "joinRequest" -> settings.endpoints.users.joinRequest
                else -> null
            }
            "notifications" -> when (parts[1]) {
                "getAll" -> settings.endpoints.notifications.getAll
                "approve" -> settings.endpoints.notifications.approve
                "reject" -> settings.endpoints.notifications.reject
                "clearAll" -> settings.endpoints.notifications.clearAll
                else -> null
            }
            else -> null
        }
    }

    /**
     * Get the API version from cached settings.
     */
    fun getApiVersion(): String {
        return getSettings()?.apiVersion ?: "v1"
    }

    /**
     * Check if settings have ever been fetched.
     */
    fun hasCachedSettings(): Boolean {
        return prefs.contains(KEY_SETTINGS)
    }

    /**
     * Get timestamp of last successful fetch.
     */
    fun getLastFetchedAt(): Long {
        return prefs.getLong(KEY_LAST_FETCHED, 0)
    }

    private fun saveSettings(settings: AppSettings) {
        prefs.edit().apply {
            putString(KEY_SETTINGS, gson.toJson(settings))
            putLong(KEY_LAST_FETCHED, System.currentTimeMillis())
            apply()
        }
    }
}

