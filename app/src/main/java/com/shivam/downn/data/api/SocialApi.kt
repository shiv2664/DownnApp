package com.shivam.downn.data.api

import com.shivam.downn.data.models.SocialResponse
import com.shivam.downn.data.models.CreateSocialRequest
import com.shivam.downn.data.models.JoinResponse
import retrofit2.Response
import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.Part
import retrofit2.http.*

interface SocialApi {
    @POST("api/activities")
    suspend fun createSocial(@Body request: CreateSocialRequest): Response<SocialResponse>

    @GET("api/activities/city/{city}")
    suspend fun getSocialsByCity(
        @Path("city") city: String,
        @Query("category") category: String?=null
    ): Response<List<SocialResponse>>

    @GET("api/activities/recent")
    suspend fun getRecentSocials(): Response<List<SocialResponse>>

    @GET("api/activities/{id}")
    suspend fun getSocialById(@Path("id") socialId: Int): Response<SocialResponse>

    @POST("api/activities/{id}/request-to-join")
    suspend fun joinSocial(@Path("id") socialId: Int): Response<JoinResponse>

    @DELETE("api/activities/{id}/leave")
    suspend fun leaveSocial(@Path("id") socialId: Int): Response<Unit>

    @DELETE("api/activities/{id}/participants/{participantId}")
    suspend fun removeParticipant(
        @Path("id") socialId: Int,
        @Path("participantId") participantId: Long
    ): Response<Unit>
}

