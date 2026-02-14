package com.shivam.downn.data.api

import com.shivam.downn.data.models.SocialResponse
import com.shivam.downn.data.models.CreateSocialRequest
import com.shivam.downn.data.models.PagedResponse

import retrofit2.Response
import okhttp3.MultipartBody
import retrofit2.http.*

interface SocialApi {
    @Multipart
    @POST
    suspend fun createSocial(
        @Url url: String,
        @Part("activity") request: okhttp3.RequestBody,
        @Part images: List<MultipartBody.Part>? = null
    ): Response<SocialResponse>

    @GET
    suspend fun getSocialsByCity(
        @Url url: String,
        @Query("category") category: String? = null,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10
    ): Response<PagedResponse<SocialResponse>>

    @GET
    suspend fun getUserSocials(
        @Url url: String,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10
    ): Response<PagedResponse<SocialResponse>>

    @GET
    suspend fun getRecentSocials(@Url url: String): Response<List<SocialResponse>>

    @GET
    suspend fun getSocialById(@Url url: String): Response<SocialResponse>

    @POST
    suspend fun joinSocial(@Url url: String): Response<Unit>

    @DELETE
    suspend fun leaveSocial(@Url url: String): Response<Unit>

    @DELETE
    suspend fun removeParticipant(@Url url: String): Response<Unit>

    @DELETE
    suspend fun deleteActivity(@Url url: String): Response<Unit>

    @PUT
    suspend fun updateActivity(
        @Url url: String,
        @Body request: CreateSocialRequest
    ): Response<SocialResponse>
}
