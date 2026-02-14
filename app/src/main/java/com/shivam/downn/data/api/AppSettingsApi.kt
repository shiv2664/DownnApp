package com.shivam.downn.data.api

import retrofit2.Response
import retrofit2.http.GET

/**
 * The only hardcoded API path in the app (besides the base URL).
 * Called on app launch to discover all other endpoint paths.
 */
interface AppSettingsApi {
    @GET("api/v1/app-settings")
    suspend fun getAppSettings(): Response<com.shivam.downn.data.models.AppSettings>
}
