package com.shivam.downn.data.api

import com.shivam.downn.data.models.NotificationResponse
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface NotificationApi {
    @GET("api/notifications")
    suspend fun getNotifications(): Response<List<NotificationResponse>>

    @PUT("api/notifications/{id}/approve")
    suspend fun approveNotification(@Path("id") id: Long): Response<Unit>


    @PUT("api/notifications/{id}/reject")
    suspend fun rejectNotification(@Path("id") id: Long): Response<Unit>

    @DELETE("api/notifications/clear-all")
    suspend fun clearAllNotifications(): Response<Unit>
}
