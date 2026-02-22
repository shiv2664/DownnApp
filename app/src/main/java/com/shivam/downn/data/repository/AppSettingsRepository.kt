package com.shivam.downn.data.repository

import android.util.Log
import com.shivam.downn.data.api.AppSettingsApi
import com.shivam.downn.data.local.PrefsManager
import com.shivam.downn.data.models.AppSettings
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Fetches endpoint configuration from the server and caches it in SharedPreferences via PrefsManager.
 * On app launch, call [refreshSettings] to update the cached endpoints.
 * If the network call fails, the previously cached version is used as fallback.
 */
@Singleton
class AppSettingsRepository @Inject constructor(
    private val prefsManager: PrefsManager,
    private val appSettingsApi: AppSettingsApi
) {

    companion object {
        private const val TAG = "AppSettingsRepo"
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
        return prefsManager.getAppSettings()
    }

    /**
     * Get a specific endpoint path by dotted key (e.g., "auth.login").
     * Falls back to the default v1 path if not cached.
     */
    fun getEndpoint(key: String): String? {
        val settings = getSettings()
        val parts = key.split(".")
        if (parts.size != 2) return null

        // If settings are null (not yet fetched), use hardcoded defaults
        if (settings == null) {
            return getDefaultEndpoint(key)
        }

        return when (parts[0]) {
            "auth" -> when (parts[1]) {
                "login" -> settings.endpoints.auth.login
                "register" -> settings.endpoints.auth.register
                "logout" -> settings.endpoints.auth.logout
                "deleteAccount" -> settings.endpoints.auth.deleteAccount
                "forgotPassword" -> "/api/v1/auth/forgot-password"
                "resetPassword" -> "/api/v1/auth/reset-password"
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
                "getByProfile" -> "/api/v1/activities/profile/{profileId}"
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
                "follow" -> "/api/v1/users/{userId}/follow"
                "unfollow" -> "/api/v1/users/{userId}/follow"
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

    private fun getDefaultEndpoint(key: String): String? {
        return when (key) {
            "auth.login" -> "api/v1/auth/login"
            "auth.register" -> "api/v1/auth/register"
            "auth.logout" -> "api/v1/auth/logout"
            "auth.deleteAccount" -> "api/v1/auth/delete"
            "auth.forgotPassword" -> "/api/v1/auth/forgot-password"
            "auth.resetPassword" -> "/api/v1/auth/reset-password"
            "activities.create" -> "api/v1/activities"
            "activities.getByCity" -> "api/v1/activities/city/{city}"
            "activities.getByUser" -> "api/v1/activities/user/{userId}"
            "activities.getRecent" -> "api/v1/activities/recent"
            "activities.getById" -> "api/v1/activities/{id}"
            "activities.join" -> "api/v1/activities/{id}/request-to-join"
            "activities.leave" -> "api/v1/activities/{id}/leave"
            "activities.removeParticipant" -> "api/v1/activities/{id}/participants/{participantId}"
            "activities.delete" -> "api/v1/activities/{id}"
            "activities.update" -> "api/v1/activities/{id}"
            "activities.messages" -> "api/v1/activities/{activityId}/messages"
            "activities.getByProfile" -> "/api/v1/activities/profile/{profileId}"
            "users.getDetails" -> "api/v1/users/{userId}"
            "users.getProfiles" -> "api/v1/users/profiles"
            "users.createProfile" -> "api/v1/users/profiles"
            "users.getProfileDetails" -> "api/v1/users/profiles/{profileId}"
            "users.updateUser" -> "api/v1/users/save"
            "users.updateProfile" -> "api/v1/users/profiles/{profileId}"
            "users.joinRequest" -> "api/v1/users/profiles/{profileId}/join-request"
            "users.follow" -> "/api/v1/users/{userId}/follow"
            "users.unfollow" -> "/api/v1/users/{userId}/follow"
            "notifications.getAll" -> "api/v1/notifications"
            "notifications.approve" -> "api/v1/notifications/{id}/approve"
            "notifications.reject" -> "api/v1/notifications/{id}/reject"
            "notifications.clearAll" -> "api/v1/notifications/clear-all"
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
        return prefsManager.getAppSettings() != null
    }

    /**
     * Get timestamp of last successful fetch.
     */
    fun getLastFetchedAt(): Long {
        return prefsManager.getLastFetchedAppSettingsTime()
    }

    private fun saveSettings(settings: AppSettings) {
        prefsManager.saveAppSettings(settings)
    }
}

