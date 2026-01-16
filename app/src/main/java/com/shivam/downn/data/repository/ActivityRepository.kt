package com.shivam.downn.data.repository

import com.shivam.downn.data.api.ActivityApi
import com.shivam.downn.data.models.ActivityResponse
import com.shivam.downn.data.models.CreateActivityRequest
import com.shivam.downn.data.models.JoinResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ActivityRepository @Inject constructor(
    private val activityApi: ActivityApi
) {
    suspend fun createActivity(request: CreateActivityRequest): Result<ActivityResponse> {
        return try {
            val response = activityApi.createActivity(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Creation failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getActivitiesByCity(city: String, category: String? = null): Result<List<ActivityResponse>> {
        return try {
            val response = activityApi.getActivitiesByCity(city, category)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Fetch failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun joinActivity(activityId: Int): Result<JoinResponse> {
        return try {
            val response = activityApi.joinActivity(activityId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Join failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
