package com.shivam.downn.data.repository

import com.shivam.downn.data.api.SocialApi
import com.shivam.downn.data.models.SocialResponse
import com.shivam.downn.data.models.CreateSocialRequest

import com.shivam.downn.data.network.NetworkResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import javax.inject.Inject
import javax.inject.Singleton
import com.shivam.downn.data.local.db.SocialDao
import com.shivam.downn.data.models.Pageable
import com.shivam.downn.data.models.Sort
import com.shivam.downn.utils.toSocialResponse
import com.shivam.downn.utils.toCachedSocial
import kotlinx.coroutines.flow.first

@Singleton
class SocialRepository @Inject constructor(
    private val socialApi: SocialApi,
    private val appSettingsRepository: AppSettingsRepository,
    private val socialDao: SocialDao
) {
    fun createSocial(request: CreateSocialRequest, images: List<MultipartBody.Part>? = null): Flow<NetworkResult<SocialResponse?>> = flow {
        try {
            val url = appSettingsRepository.getEndpoint("activities.create")!!
            val gson = com.google.gson.Gson()
            val json = gson.toJson(request)
            val requestBody = okhttp3.RequestBody.create(
                "application/json".toMediaTypeOrNull(),
                json
            )
            val response = socialApi.createSocial(url, requestBody, images)
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
        page: Int = 0,
        size: Int = 10
    ): Flow<NetworkResult<List<SocialResponse>>> = flow {
        try {
            val urlTemplate = appSettingsRepository.getEndpoint("activities.getByCity")!!
            val url = urlTemplate.replace("{city}", city)
            val response = socialApi.getSocialsByCity(url, category, page, size)
            if (response.isSuccessful && response.body() != null) {
               emit(NetworkResult.Error("Use getSocialsPaged for pagination support"))
            } else {
                emit(NetworkResult.Error("Fetch failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(NetworkResult.Error(e.localizedMessage ?: "Unknown error"))
        }
    }
    
    fun getSocialsPaged(
        city: String,
        category: String?=null,
        page: Int = 0,
        size: Int = 10
    ): Flow<NetworkResult<com.shivam.downn.data.models.PagedResponse<SocialResponse>>> = flow {
        if (page == 0) {
            try {
                val cached = socialDao.getFeedActivities().first()
                if (cached.isNotEmpty()) {
                    val pagedCache = com.shivam.downn.data.models.PagedResponse(
                        content = cached.map { it.toSocialResponse() },
                        pageable = Pageable(Sort(true, false, true), 0, 0, cached.size, true, false),
                        last = false, totalElements = cached.size.toLong(), totalPages = 1,
                        size = cached.size, number = 0, sort = Sort(true, false, true),
                        first = true, numberOfElements = cached.size, empty = false
                    )
                    emit(NetworkResult.Success(pagedCache))
                }
            } catch (e: Exception) {
                // Ignore cache errors
            }
        }
        
        try {
            val urlTemplate = appSettingsRepository.getEndpoint("activities.getByCity")!!
            val url = urlTemplate.replace("{city}", city)
            val response = socialApi.getSocialsByCity(url, category, page, size)
            if (response.isSuccessful && response.body() != null) {
                val pagedData = response.body()!!
                if (page == 0) {
                    socialDao.clearFeed()
                    socialDao.insertActivities(pagedData.content.map { it.toCachedSocial() })
                }
                emit(NetworkResult.Success(pagedData))
            } else {
                emit(NetworkResult.Error("Fetch failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(NetworkResult.Error(e.localizedMessage ?: "Unknown error"))
        }
    }

    fun getUserSocials(
        userId: Long,
        page: Int = 0,
        size: Int = 10
    ): Flow<NetworkResult<com.shivam.downn.data.models.PagedResponse<SocialResponse>>> = flow {
        try {
            val urlTemplate = appSettingsRepository.getEndpoint("activities.getByUser")!!
            val url = urlTemplate.replace("{userId}", userId.toString())
            val response = socialApi.getUserSocials(url, page, size)
            if (response.isSuccessful && response.body() != null) {
                emit(NetworkResult.Success(response.body()!!))
            } else {
                emit(NetworkResult.Error("Fetch failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(NetworkResult.Error(e.localizedMessage ?: "Unknown error"))
        }
    }

    fun getProfileSocials(
        profileId: Long,
        page: Int = 0,
        size: Int = 10
    ): Flow<NetworkResult<com.shivam.downn.data.models.PagedResponse<SocialResponse>>> = flow {
        try {
            val urlTemplate = appSettingsRepository.getEndpoint("activities.getByProfile")!!
            val url = urlTemplate.replace("{profileId}", profileId.toString())
            val response = socialApi.getUserSocials(url, page, size)
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
            val urlTemplate = appSettingsRepository.getEndpoint("activities.getById")!!
            val url = urlTemplate.replace("{id}", socialId.toString())
            val response = socialApi.getSocialById(url)
            if (response.isSuccessful && response.body() != null) {
                emit(NetworkResult.Success(response.body()!!))
            } else {
                emit(NetworkResult.Error("Fetch failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(NetworkResult.Error(e.localizedMessage ?: "Unknown error"))
        }
    }

    fun joinSocial(socialId: Int): Flow<NetworkResult<Unit>> = flow {
        try {
            val urlTemplate = appSettingsRepository.getEndpoint("activities.join")!!
            val url = urlTemplate.replace("{id}", socialId.toString())
            val response = socialApi.joinSocial(url)
            if (response.isSuccessful) {
                emit(NetworkResult.Success(Unit))
            } else {
                emit(NetworkResult.Error("Join failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(NetworkResult.Error(e.localizedMessage ?: "Unknown error"))
        }
    }

    fun leaveSocial(socialId: Int): Flow<NetworkResult<Unit>> = flow {
        emit(NetworkResult.Loading())
        try {
            val urlTemplate = appSettingsRepository.getEndpoint("activities.leave")!!
            val url = urlTemplate.replace("{id}", socialId.toString())
            val response = socialApi.leaveSocial(url)
            if (response.isSuccessful) {
                emit(NetworkResult.Success(Unit))
            } else {
                emit(NetworkResult.Error("Leave failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(NetworkResult.Error(e.localizedMessage ?: "Unknown error"))
        }
    }

    fun removeParticipant(socialId: Int, participantId: Long): Flow<NetworkResult<Unit>> = flow {
        emit(NetworkResult.Loading())
        try {
            val urlTemplate = appSettingsRepository.getEndpoint("activities.removeParticipant")!!
            val url = urlTemplate
                .replace("{id}", socialId.toString())
                .replace("{participantId}", participantId.toString())
            val response = socialApi.removeParticipant(url)
            if (response.isSuccessful) {
                emit(NetworkResult.Success(Unit))
            } else {
                emit(NetworkResult.Error("Removal failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(NetworkResult.Error(e.localizedMessage ?: "Unknown error"))
        }
    }

    fun deleteActivity(socialId: Int): Flow<NetworkResult<Unit>> = flow {
        emit(NetworkResult.Loading())
        try {
            val urlTemplate = appSettingsRepository.getEndpoint("activities.delete")!!
            val url = urlTemplate.replace("{id}", socialId.toString())
            val response = socialApi.deleteActivity(url)
            if (response.isSuccessful) {
                emit(NetworkResult.Success(Unit))
            } else {
                emit(NetworkResult.Error("Delete failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(NetworkResult.Error(e.localizedMessage ?: "Unknown error"))
        }
    }

    fun updateActivity(socialId: Int, request: CreateSocialRequest): Flow<NetworkResult<SocialResponse?>> = flow {
        emit(NetworkResult.Loading())
        try {
            val urlTemplate = appSettingsRepository.getEndpoint("activities.update")!!
            val url = urlTemplate.replace("{id}", socialId.toString())
            val response = socialApi.updateActivity(url, request)
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
