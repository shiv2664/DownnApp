package com.shivam.downn.ui.screens.create_activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shivam.downn.data.models.CreateSocialRequest
import com.shivam.downn.data.models.SocialResponse
import com.shivam.downn.data.network.NetworkResult
import com.shivam.downn.data.repository.SocialRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditActivityViewModel @Inject constructor(
    private val socialRepository: SocialRepository
) : ViewModel() {

    private val _updateState = MutableStateFlow<NetworkResult<SocialResponse?>?>(null)
    val updateState = _updateState.asStateFlow()
    
    private val _activityDetails = MutableStateFlow<NetworkResult<SocialResponse>?>(null)
    val activityDetails = _activityDetails.asStateFlow()

    fun loadActivityDetails(socialId: Int) {
        viewModelScope.launch {
            _activityDetails.value = NetworkResult.Loading()
            socialRepository.getSocialById(socialId).collect {
                _activityDetails.value = it
            }
        }
    }

    fun updateSocial(
        socialId: Int,
        title: String,
        description: String,
        category: String,
        city: String,
        locationName: String,
        scheduledTime: String,
        maxParticipants: Int,
        latitude: Double?,
        longitude: Double?
    ) {
        viewModelScope.launch {
            _updateState.value = NetworkResult.Loading()
            
            // We need to fetch the existing activity to get the profileId
            // Or assume the backend handles validation. The CreateSocialRequest requires profileId.
            // Let's fetch the activity first if not already loaded, OR since we loaded it in UI, we can pass profileId...
            // But wait, the `CreateSocialRequest` structure might require profileId. Let's check.
            
            // Checking CreateSocialRequest definition from SocialModels.kt...
            // data class CreateSocialRequest(..., val profileId: Long, ...)
            
            // So we need profileId. It's best to take it from the loaded activity details.
            val currentActivity = (_activityDetails.value as? NetworkResult.Success)?.data
            
            if (currentActivity == null) {
                _updateState.value = NetworkResult.Error("Activity details not loaded")
                return@launch
            }
            
            val request = CreateSocialRequest(
                title = title,
                description = description,
                category = category,
                city = city,
                locationName = locationName,
                scheduledTime = scheduledTime,
                maxParticipants = maxParticipants,
                profileId = currentActivity.profile?.id ?: 0, // Fallback, but should be there
                socialType = currentActivity.socialType,
                latitude = latitude,
                longitude = longitude,
                timezone = java.util.TimeZone.getDefault().id,
                images = null // Images not updated via this flow in MVP
            )

            socialRepository.updateActivity(socialId, request).collect { result ->
                _updateState.value = result
            }
        }
    }
}
