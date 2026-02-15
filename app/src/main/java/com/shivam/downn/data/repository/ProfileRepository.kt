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
    private val profileApi: ProfileApi,
    private val appSettingsRepository: AppSettingsRepository
) {
    fun getProfiles(): Flow<NetworkResult<List<ProfileResponse>>> = flow {
        emit(NetworkResult.Loading())
        try {
            val url = appSettingsRepository.getEndpoint("users.getProfiles")!!
            val response = profileApi.getProfiles(url)
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
            val url = appSettingsRepository.getEndpoint("users.createProfile")!!
            val response = profileApi.createProfile(url, request)
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
            val urlTemplate = appSettingsRepository.getEndpoint("users.getProfileDetails")!!
            val url = urlTemplate.replace("{profileId}", profileId.toString())
            val response = profileApi.getProfileDetails(url)
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
            val urlTemplate = appSettingsRepository.getEndpoint("users.getDetails")!!
            val url = urlTemplate.replace("{userId}", userId.toString())
            val response = profileApi.getUserDetails(url)
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
            val url = appSettingsRepository.getEndpoint("users.updateUser")!!
            val response = profileApi.updateUser(url, request, avatar)
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
            val urlTemplate = appSettingsRepository.getEndpoint("users.updateProfile")!!
            val url = urlTemplate.replace("{profileId}", profileId.toString())
            val response = profileApi.updateProfile(url, request, avatar, cover)
            if (response.isSuccessful && response.body() != null) {
                emit(NetworkResult.Success(response.body()!!))
            } else {
                emit(NetworkResult.Error("Update failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(NetworkResult.Error(e.localizedMessage ?: "Unknown error"))
        }
    }

    fun followUser(userId: Long): Flow<NetworkResult<Unit>> = flow {
        emit(NetworkResult.Loading())
        try {
            val urlTemplate = appSettingsRepository.getEndpoint("users.follow")!!
            val url = urlTemplate.replace("{userId}", userId.toString())
            val response = profileApi.followUser(url)
            if (response.isSuccessful) {
                emit(NetworkResult.Success(Unit))
            } else {
                emit(NetworkResult.Error("Follow failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(NetworkResult.Error(e.localizedMessage ?: "Unknown error"))
        }
    }

    fun unfollowUser(userId: Long): Flow<NetworkResult<Unit>> = flow {
        emit(NetworkResult.Loading())
        try {
            val urlTemplate = appSettingsRepository.getEndpoint("users.unfollow")!!
            val url = urlTemplate.replace("{userId}", userId.toString())
            val response = profileApi.unfollowUser(url)
            if (response.isSuccessful) {
                emit(NetworkResult.Success(Unit))
            } else {
                emit(NetworkResult.Error("Unfollow failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(NetworkResult.Error(e.localizedMessage ?: "Unknown error"))
        }
    }
}
