package com.shivam.downn.data.api

import com.shivam.downn.data.models.NotificationResponse
import retrofit2.Response
import retrofit2.http.*

interface NotificationApi {
    @GET
    suspend fun getNotifications(@Url url: String): Response<List<NotificationResponse>>

    @PUT
    suspend fun approveNotification(@Url url: String): Response<Unit>

    @PUT
    suspend fun rejectNotification(@Url url: String): Response<Unit>

    @DELETE
    suspend fun clearAllNotifications(@Url url: String): Response<Unit>
}
