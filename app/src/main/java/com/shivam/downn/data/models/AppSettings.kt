package com.shivam.downn.data.models

import com.google.gson.annotations.SerializedName

/**
 * Response from GET /api/v1/app-settings.
 * Contains all available API endpoint paths.
 */
data class AppSettings(
    @SerializedName("apiVersion")
    val apiVersion: String,

    @SerializedName("endpoints")
    val endpoints: Endpoints
)

data class Endpoints(
    @SerializedName("auth")
    val auth: AuthEndpoints,

    @SerializedName("activities")
    val activities: ActivityEndpoints,

    @SerializedName("users")
    val users: UserEndpoints,

    @SerializedName("notifications")
    val notifications: NotificationEndpoints
)

data class AuthEndpoints(
    val login: String,
    val register: String,
    val logout: String,
    val deleteAccount: String
)

data class ActivityEndpoints(
    val create: String,
    val getByCity: String,
    val getByUser: String,
    val getRecent: String,
    val getById: String,
    val join: String,
    val leave: String,
    val removeParticipant: String,
    val delete: String,
    val update: String,
    val messages: String
)

data class UserEndpoints(
    val getDetails: String,
    val getProfiles: String,
    val createProfile: String,
    val getProfileDetails: String,
    val updateUser: String,
    val updateProfile: String,
    val joinRequest: String
)

data class NotificationEndpoints(
    val getAll: String,
    val approve: String,
    val reject: String,
    val clearAll: String
)
