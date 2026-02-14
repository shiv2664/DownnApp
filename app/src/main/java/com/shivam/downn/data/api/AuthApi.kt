package com.shivam.downn.data.api

import com.shivam.downn.data.models.AuthRequest
import com.shivam.downn.data.models.AuthResponse
import com.shivam.downn.data.models.RegisterRequest
import com.shivam.downn.data.models.LogoutResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.Url

interface AuthApi {
    @POST
    suspend fun register(@Url url: String, @Body request: RegisterRequest): Response<AuthResponse>

    @POST
    suspend fun login(@Url url: String, @Body request: AuthRequest): Response<AuthResponse>

    @POST
    suspend fun logout(@Url url: String): Response<LogoutResponse>

    @DELETE
    suspend fun deleteAccount(@Url url: String): Response<LogoutResponse>
}
