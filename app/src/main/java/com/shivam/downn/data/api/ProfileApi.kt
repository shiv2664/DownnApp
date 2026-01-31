package com.shivam.downn.data.api

import com.shivam.downn.data.models.CreateProfileRequest
import com.shivam.downn.data.models.ProfileResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ProfileApi {
    @GET("api/users/profiles")
    suspend fun getProfiles(): Response<List<ProfileResponse>>

    @POST("api/users/profiles")
    suspend fun createProfile(@Body request: CreateProfileRequest): Response<ProfileResponse>
}
