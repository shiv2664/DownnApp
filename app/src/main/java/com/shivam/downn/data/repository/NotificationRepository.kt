package com.shivam.downn.data.repository

import com.shivam.downn.data.api.NotificationApi
import com.shivam.downn.data.models.NotificationResponse
import com.shivam.downn.data.network.NetworkResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationRepository @Inject constructor(
    private val notificationApi: NotificationApi,
    private val appSettingsRepository: AppSettingsRepository
) {
    fun getNotifications(): Flow<NetworkResult<List<NotificationResponse>>> = flow {
        emit(NetworkResult.Loading())
        try {
            val url = appSettingsRepository.getEndpoint("notifications.getAll")!!
            val response = notificationApi.getNotifications(url)
            if (response.isSuccessful && response.body() != null) {
                emit(NetworkResult.Success(response.body()!!))
            } else {
                emit(NetworkResult.Error("Fetch failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(NetworkResult.Error(e.localizedMessage ?: "Unknown error"))
        }
    }

    fun approveNotification(notificationId: Long): Flow<NetworkResult<Unit>> = flow {
        emit(NetworkResult.Loading())
        try {
            val urlTemplate = appSettingsRepository.getEndpoint("notifications.approve")!!
            val url = urlTemplate.replace("{id}", notificationId.toString())
            val response = notificationApi.approveNotification(url)
            if (response.isSuccessful) {
                emit(NetworkResult.Success(Unit))
            } else {
                emit(NetworkResult.Error("Approval failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(NetworkResult.Error(e.localizedMessage ?: "Unknown error"))
        }
    }

    fun rejectNotification(notificationId: Long): Flow<NetworkResult<Unit>> = flow {
        emit(NetworkResult.Loading())
        try {
            val urlTemplate = appSettingsRepository.getEndpoint("notifications.reject")!!
            val url = urlTemplate.replace("{id}", notificationId.toString())
            val response = notificationApi.rejectNotification(url)
            if (response.isSuccessful) {
                emit(NetworkResult.Success(Unit))
            } else {
                emit(NetworkResult.Error("Rejection failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(NetworkResult.Error(e.localizedMessage ?: "Unknown error"))
        }
    }

    fun clearAllNotifications(): Flow<NetworkResult<Unit>> = flow {
        emit(NetworkResult.Loading())
        try {
            val url = appSettingsRepository.getEndpoint("notifications.clearAll")!!
            val response = notificationApi.clearAllNotifications(url)
            if (response.isSuccessful) {
                emit(NetworkResult.Success(Unit))
            } else {
                emit(NetworkResult.Error("Clear failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(NetworkResult.Error(e.localizedMessage ?: "Unknown error"))
        }
    }
}
