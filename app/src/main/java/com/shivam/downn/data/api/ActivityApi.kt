package com.shivam.downn.data.api

import com.shivam.downn.data.models.ActivityResponse
import com.shivam.downn.data.models.CreateActivityRequest
import com.shivam.downn.data.models.JoinResponse
import retrofit2.Response
import retrofit2.http.*

interface ActivityApi {
    @POST("api/activities")
    suspend fun createActivity(@Body request: CreateActivityRequest): Response<ActivityResponse>

    @GET("api/activities/city/{city}")
    suspend fun getActivitiesByCity(
        @Path("city") city: String,
        @Query("category") category: String? = null
    ): Response<List<ActivityResponse>>

    @GET("api/activities/recent")
    suspend fun getRecentActivities(): Response<List<ActivityResponse>>

    @GET("api/activities/{id}")
    suspend fun getActivityById(@Path("id") activityId: Int): Response<ActivityResponse>

    @POST("api/activities/{id}/join")
    suspend fun joinActivity(@Path("id") activityId: Int): Response<JoinResponse>
}
