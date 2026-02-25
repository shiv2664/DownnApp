package com.shivam.downn.data.api

import com.shivam.downn.data.models.AuthRequest
import com.shivam.downn.data.models.AuthResponse
import com.shivam.downn.data.models.GoogleLoginRequest
import com.shivam.downn.data.models.ForgotPasswordRequest
import com.shivam.downn.data.models.RegisterRequest
import com.shivam.downn.data.models.LogoutResponse
import com.shivam.downn.data.models.ResetPasswordRequest
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

    @POST
    suspend fun forgotPassword(@Url url: String, @Body request: ForgotPasswordRequest): Response<Void>

    @POST
    suspend fun resetPassword(@Url url: String, @Body request: ResetPasswordRequest): Response<Void>

    @DELETE
    suspend fun deleteAccount(@Url url: String): Response<LogoutResponse>

/*
    @POST
    suspend fun googleLogin(@Url url: String, @Body request: GoogleLoginRequest): Response<AuthResponse>
*/
}
