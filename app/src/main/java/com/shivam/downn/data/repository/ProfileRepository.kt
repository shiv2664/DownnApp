package com.shivam.downn.data.repository

import com.shivam.downn.data.api.ProfileApi
import com.shivam.downn.data.models.CreateProfileRequest
import com.shivam.downn.data.models.ProfileResponse
import com.shivam.downn.data.models.UpdateProfileRequest
import com.shivam.downn.data.models.UpdateUserRequest
import com.shivam.downn.data.models.UserDetailsResponse
import com.shivam.downn.data.network.NetworkResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
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

    fun getProfileDetails(profileId: Long): Flow<NetworkResult<ProfileResponse>> = flow {
        emit(NetworkResult.Loading())
        try {
            val response = profileApi.getProfileDetails(profileId)
            if (response.isSuccessful && response.body() != null) {
                emit(NetworkResult.Success(response.body()!!))
            } else {
                emit(NetworkResult.Error("Fetch failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(NetworkResult.Error(e.localizedMessage ?: "Unknown error"))
        }
    }

    fun getUserDetails(userId: Long): Flow<NetworkResult<UserDetailsResponse>> = flow {
        emit(NetworkResult.Loading())
        try {
            val response = profileApi.getUserDetails(userId)
            if (response.isSuccessful && response.body() != null) {
                emit(NetworkResult.Success(response.body()!!))
            } else {
                emit(NetworkResult.Error("Fetch failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(NetworkResult.Error(e.localizedMessage ?: "Unknown error"))
        }
    }

    fun updateUser(
        request: UpdateUserRequest,
        avatar: MultipartBody.Part?
    ): Flow<NetworkResult<UserDetailsResponse>> = flow {
        emit(NetworkResult.Loading())
        try {
            val response = profileApi.updateUser(request, avatar)
            if (response.isSuccessful && response.body() != null) {
                emit(NetworkResult.Success(response.body()!!))
            } else {
                emit(NetworkResult.Error("Update failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(NetworkResult.Error(e.localizedMessage ?: "Unknown error"))
        }
    }

    fun updateProfile(
        profileId: Long,
        request: UpdateProfileRequest,
        avatar: MultipartBody.Part?,
        cover: MultipartBody.Part?
    ): Flow<NetworkResult<ProfileResponse>> = flow {
        emit(NetworkResult.Loading())
        try {
            val response = profileApi.updateProfile(profileId, request, avatar, cover)
            if (response.isSuccessful && response.body() != null) {
                emit(NetworkResult.Success(response.body()!!))
            } else {
                emit(NetworkResult.Error("Update failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(NetworkResult.Error(e.localizedMessage ?: "Unknown error"))
        }
    }
}
