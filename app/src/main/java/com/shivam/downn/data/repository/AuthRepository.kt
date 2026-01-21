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
    private val prefsManager: PrefsManager
) {
    fun login(request: AuthRequest): Flow<NetworkResult<AuthResponse?>> = flow {

        try {
            val response = authApi.login(request)
            if (response.isSuccessful && response.body() != null) {
                val authResponse = response.body()
                authResponse?.let {
                    prefsManager.saveAuthResponse(authResponse)
                }
                emit(NetworkResult.Success(response.body()))
                return@flow
            } else {
                emit(NetworkResult.Error(response.message()))
            }
        } catch (e: Exception) {
            emit(NetworkResult.Error(message = e.localizedMessage))
        }
    }

    fun register(request: RegisterRequest): Flow<NetworkResult<AuthResponse?>> = flow {
        try {
            val response = authApi.register(request)
            if (response.isSuccessful && response.body() != null) {
                val authResponse = response.body()
                authResponse?.let {
                    prefsManager.saveAuthResponse(authResponse)
                }
                emit(NetworkResult.Success(authResponse))
            } else {
                emit(NetworkResult.Error("Registration failed: ${response.message()}"))
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
            val response = authApi.logout()
            
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
}
