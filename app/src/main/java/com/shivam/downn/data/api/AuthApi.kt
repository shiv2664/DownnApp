package com.shivam.downn.data.api

import com.shivam.downn.data.models.AuthRequest
import com.shivam.downn.data.models.AuthResponse
import com.shivam.downn.data.models.RegisterRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @POST("api/auth/login")
    suspend fun login(@Body request: AuthRequest): Response<AuthResponse>
}
