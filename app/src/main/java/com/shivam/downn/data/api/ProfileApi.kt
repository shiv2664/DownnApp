package com.shivam.downn.data.api

import com.shivam.downn.data.models.CreateProfileRequest
import com.shivam.downn.data.models.ProfileResponse
import com.shivam.downn.data.models.UpdateProfileRequest
import com.shivam.downn.data.models.UpdateUserRequest
import com.shivam.downn.data.models.UserDetailsResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface ProfileApi {
    @GET
    suspend fun getProfiles(@Url url: String): Response<List<ProfileResponse>>

    @POST
    suspend fun createProfile(@Url url: String, @Body request: CreateProfileRequest): Response<ProfileResponse>

    @GET
    suspend fun getProfileDetails(@Url url: String): Response<ProfileResponse>

    @GET
    suspend fun getUserDetails(@Url url: String): Response<UserDetailsResponse>

    @Multipart
    @PUT
    suspend fun updateUser(
        @Url url: String,
        @Part("user") request: UpdateUserRequest,
        @Part avatar: MultipartBody.Part?
    ): Response<UserDetailsResponse>

    @Multipart
    @PUT
    suspend fun updateProfile(
        @Url url: String,
        @Part("profile") request: UpdateProfileRequest,
        @Part avatar: MultipartBody.Part?,
        @Part cover: MultipartBody.Part?
    ): Response<ProfileResponse>

    @POST
    suspend fun followUser(@Url url: String): Response<Void>

    @DELETE
    suspend fun unfollowUser(@Url url: String): Response<Void>
}
