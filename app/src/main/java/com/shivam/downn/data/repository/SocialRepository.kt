package com.shivam.downn.data.repository

import com.shivam.downn.data.api.SocialApi
import com.shivam.downn.data.models.SocialResponse
import com.shivam.downn.data.models.CreateSocialRequest
import com.shivam.downn.data.models.JoinResponse
import com.shivam.downn.data.network.NetworkResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SocialRepository @Inject constructor(
    private val socialApi: SocialApi
) {
    fun createSocial(request: CreateSocialRequest): Flow<NetworkResult<SocialResponse?>> = flow {
        try {
            val response = socialApi.createSocial(request)
            if (response.isSuccessful && response.body() != null) {
                emit(NetworkResult.Success(response.body()!!))
            } else {
                emit(NetworkResult.Error("Creation failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(NetworkResult.Error(e.localizedMessage ?: "Unknown error"))
        }
    }

    fun getSocials(
        city: String,
        category: String?=null,
        minPrice: Int? = null,
        maxPrice: Int? = null,
        date: String? = null
    ): Flow<NetworkResult<List<SocialResponse>>> = flow {
        try {
            val response = socialApi.getSocialsByCity(city,category)
            if (response.isSuccessful && response.body() != null) {
                emit(NetworkResult.Success(response.body()!!))
            } else {
                emit(NetworkResult.Error("Fetch failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(NetworkResult.Error(e.localizedMessage ?: "Unknown error"))
        }
    }

    fun getSocialById(socialId: Int): Flow<NetworkResult<SocialResponse>> = flow {
        try {
            val response = socialApi.getSocialById(socialId)
            if (response.isSuccessful && response.body() != null) {
                emit(NetworkResult.Success(response.body()!!))
            } else {
                emit(NetworkResult.Error("Fetch failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(NetworkResult.Error(e.localizedMessage ?: "Unknown error"))
        }
    }

    fun joinSocial(socialId: Int): Flow<NetworkResult<JoinResponse>> = flow {
        try {
            val response = socialApi.joinSocial(socialId)
            if (response.isSuccessful && response.body() != null) {
                emit(NetworkResult.Success(response.body()!!))
            } else {
                emit(NetworkResult.Error("Join failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(NetworkResult.Error(e.localizedMessage ?: "Unknown error"))
        }
    }
}
