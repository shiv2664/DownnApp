package com.shivam.downn.data.api

import com.shivam.downn.data.models.CreateProfileRequest
import com.shivam.downn.data.models.ProfileResponse
import com.shivam.downn.data.models.UpdateProfileRequest
import com.shivam.downn.data.models.UpdateUserRequest
import com.shivam.downn.data.models.UserDetailsResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

interface ProfileApi {
    @GET("api/users/profiles")
    suspend fun getProfiles(): Response<List<ProfileResponse>>

    @POST("api/users/profiles")
    suspend fun createProfile(@Body request: CreateProfileRequest): Response<ProfileResponse>

    @GET("api/users/profiles/{profileId}")
    suspend fun getProfileDetails(@Path("profileId") profileId: Long): Response<ProfileResponse>

    @GET("api/users/{userId}")
    suspend fun getUserDetails(@Path("userId") userId: Long): Response<UserDetailsResponse>

    @Multipart
    @PUT("api/users/save")
    suspend fun updateUser(
        @Part("user") request: UpdateUserRequest,
        @Part avatar: MultipartBody.Part?
    ): Response<UserDetailsResponse>

    @Multipart
    @PUT("api/users/profiles/{profileId}")
    suspend fun updateProfile(
        @Path("profileId") profileId: Long,
        @Part("profile") request: UpdateProfileRequest,
        @Part avatar: MultipartBody.Part?,
        @Part cover: MultipartBody.Part?
    ): Response<ProfileResponse>
}
