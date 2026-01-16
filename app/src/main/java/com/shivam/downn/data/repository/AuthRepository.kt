package com.shivam.downn.data.repository

import com.shivam.downn.data.api.AuthApi
import com.shivam.downn.data.local.PrefsManager
import com.shivam.downn.data.models.AuthRequest
import com.shivam.downn.data.models.AuthResponse
import com.shivam.downn.data.models.RegisterRequest
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val authApi: AuthApi,
    private val prefsManager: PrefsManager
) {
    suspend fun login(request: AuthRequest): Result<AuthResponse?> {
        return try {
            val response = authApi.login(request)
            if (response.isSuccessful && response.body() != null) {
                val authResponse = response.body()
                authResponse?.let {
                    prefsManager.saveAuthResponse(authResponse)
                }
                Result.success(authResponse)
            } else {
                Result.failure(Exception("Login failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(request: RegisterRequest): Result<AuthResponse?> {
        return try {
            val response = authApi.register(request)
            if (response.isSuccessful && response.body() != null) {
                val authResponse = response.body()
                authResponse?.let {
                    prefsManager.saveAuthResponse(authResponse)
                }
                Result.success(authResponse)
            } else {
                Result.failure(Exception("Registration failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun isUserLoggedIn(): Boolean {
        return prefsManager.getToken() != null
    }

    fun logout() {
        prefsManager.clear()
    }
}
