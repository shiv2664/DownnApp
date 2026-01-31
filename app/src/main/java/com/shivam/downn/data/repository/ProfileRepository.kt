package com.shivam.downn.data.repository

import com.shivam.downn.data.api.ProfileApi
import com.shivam.downn.data.models.CreateProfileRequest
import com.shivam.downn.data.models.ProfileResponse
import com.shivam.downn.data.network.NetworkResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepository @Inject constructor(
    private val profileApi: ProfileApi
) {
    fun getProfiles(): Flow<NetworkResult<List<ProfileResponse>>> = flow {
        emit(NetworkResult.Loading())
        try {
            val response = profileApi.getProfiles()
            if (response.isSuccessful && response.body() != null) {
                emit(NetworkResult.Success(response.body()!!))
            } else {
                emit(NetworkResult.Error("Fetch failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(NetworkResult.Error(e.localizedMessage ?: "Unknown error"))
        }
    }

    fun createProfile(request: CreateProfileRequest): Flow<NetworkResult<ProfileResponse>> = flow {
        emit(NetworkResult.Loading())
        try {
            val response = profileApi.createProfile(request)
            if (response.isSuccessful && response.body() != null) {
                emit(NetworkResult.Success(response.body()!!))
            } else {
                emit(NetworkResult.Error("Creation failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(NetworkResult.Error(e.localizedMessage ?: "Unknown error"))
        }
    }
}
