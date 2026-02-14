package com.shivam.downn.data.repository

import com.shivam.downn.data.api.AuthApi
import com.shivam.downn.data.local.PrefsManager
import com.shivam.downn.data.models.AuthRequest
import com.shivam.downn.data.models.AuthResponse
import com.shivam.downn.data.models.RegisterRequest
import com.shivam.downn.data.network.NetworkResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import com.shivam.downn.data.models.LogoutResponse
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val authApi: AuthApi,
    private val prefsManager: PrefsManager,
    private val appSettingsRepository: AppSettingsRepository
) {
    fun login(request: AuthRequest): Flow<NetworkResult<AuthResponse?>> = flow {

        try {
            val url = appSettingsRepository.getEndpoint("auth.login") ?: ""
            val response = authApi.login(url, request)
            if (response.isSuccessful && response.body() != null) {
                val authResponse = response.body()
                authResponse?.let {
                    prefsManager.saveAuthResponse(authResponse)
                }
                emit(NetworkResult.Success(response.body()))
                return@flow
            } else {
                val errorMsg = parseErrorBody(response) ?: "Login failed"
                emit(NetworkResult.Error(errorMsg))
            }
        } catch (e: Exception) {
            emit(NetworkResult.Error(message = e.localizedMessage ?: "Network error"))
        }
    }

    fun register(request: RegisterRequest): Flow<NetworkResult<AuthResponse?>> = flow {
        try {
            val url = appSettingsRepository.getEndpoint("auth.register") ?: ""
            val response = authApi.register(url, request)
            if (response.isSuccessful && response.body() != null) {
                val authResponse = response.body()
                authResponse?.let {
                    prefsManager.saveAuthResponse(authResponse)
                }
                emit(NetworkResult.Success(authResponse))
            } else {
                val errorMsg = parseErrorBody(response) ?: "Registration failed"
                emit(NetworkResult.Error(errorMsg))
            }
        } catch (e: Exception) {
            emit(NetworkResult.Error(e.localizedMessage ?: "Unknown error"))
        }
    }

    fun isUserLoggedIn(): Boolean {
        return prefsManager.getToken() != null
    }

    fun logout(): Flow<NetworkResult<String?>> = flow {
        emit(NetworkResult.Loading())
        try {
            val url = appSettingsRepository.getEndpoint("auth.logout")!!
            val response = authApi.logout(url)
            
            if (response.isSuccessful) {
                prefsManager.clear()
                val message = response.body()?.message ?: "Successfully logged out"
                emit(NetworkResult.Success(message))
            } else {
                emit(NetworkResult.Error("Remote logout failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            // Local logout still happened
            prefsManager.clear()
            emit(NetworkResult.Error(e.localizedMessage ?: "Network error during logout"))
        }
    }

    fun deleteAccount(): Flow<NetworkResult<String?>> = flow {
        emit(NetworkResult.Loading())
        try {
            val url = appSettingsRepository.getEndpoint("auth.deleteAccount")!!
            val response = authApi.deleteAccount(url)
            if (response.isSuccessful) {
                prefsManager.clear()
                val message = response.body()?.message ?: "Account deleted successfully"
                emit(NetworkResult.Success(message))
            } else {
                emit(NetworkResult.Error("Failed to delete account: ${response.message()}"))
            }
        } catch (e: Exception) {
             emit(NetworkResult.Error(e.localizedMessage ?: "Network error"))
        }
    }

    /**
     * Parses the error body from a Retrofit response.
     * Spring Boot returns JSON like: {"timestamp":..., "status":401, "error":"Unauthorized", "message":"Bad credentials"}
     */
    private fun <T> parseErrorBody(response: Response<T>): String? {
        return try {
            val errorBody = response.errorBody()?.string()
            if (errorBody != null) {
                val json = org.json.JSONObject(errorBody)
                json.optString("message", null) ?: json.optString("error", null)
            } else {
                response.message().takeIf { it.isNotBlank() }
            }
        } catch (e: Exception) {
            response.message().takeIf { it.isNotBlank() }
        }
    }
}
