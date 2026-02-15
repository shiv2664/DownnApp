package com.shivam.downn.ui.screens.create_activity

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shivam.downn.data.local.PrefsManager
import com.shivam.downn.data.models.CreateSocialRequest
import com.shivam.downn.data.models.SocialResponse
import com.shivam.downn.data.network.NetworkResult
import com.shivam.downn.data.repository.SocialRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest // New import
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import javax.inject.Inject
import com.shivam.downn.utils.createMultipartBodyPart
import com.shivam.downn.data.repository.ProfileRepository
import kotlinx.coroutines.flow.first
import com.shivam.downn.data.models.ProfileResponse

@HiltViewModel
class StartMoveViewModel @Inject constructor(
    private val socialRepository: SocialRepository,
    private val profileRepository: ProfileRepository,
    private val prefsManager: PrefsManager,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _state = MutableStateFlow<NetworkResult<SocialResponse?>?>(null)
    val state: StateFlow<NetworkResult<SocialResponse?>?> = _state

    private val _profileLocation = MutableStateFlow<Pair<Double, Double>?>(null)
    val profileLocation: StateFlow<Pair<Double, Double>?> = _profileLocation

    fun fetchActiveProfileLocation() {
        viewModelScope.launch {
            val profileId = prefsManager.getActiveProfileId()
            if (profileId != -1L) {
                profileRepository.getProfiles().collect { result ->
                    if (result is NetworkResult.Success) {
                        val profile = result.data?.find { it.id == profileId }
                        if (profile != null && profile.latitude != null && profile.longitude != null) {
                            _profileLocation.value = profile.latitude to profile.longitude
                        }
                    }
                }
            }
        }
    }

    fun createSocial(
        title: String,
        description: String,
        category: String,
        city: String,
        locationName: String,
        scheduledTime: String,
        maxParticipants: Int,
        latitude: Double? = null,
        longitude: Double? = null,
        imageUri: Uri? = null
    ) {
        viewModelScope.launch {
            _state.value = NetworkResult.Loading()

            var activeProfileId = prefsManager.getActiveProfileId()

            if (activeProfileId == -1L) {
                try {
                     socialRepository.getSocialsPaged("Denver", null, 0, 1) // Dummy call to wake up if needed? No.
                     // Fetch profiles
                     profileRepository.getProfiles().collect { result ->
                         if (result is NetworkResult.Loading) return@collect
                         
                         if (result is NetworkResult.Success) {
                             val profiles = result.data
                             if (!profiles.isNullOrEmpty()) {
                                 activeProfileId = profiles.first().id
                                 prefsManager.saveActiveProfileId(activeProfileId)
                                 proceedWithCreation(title, description, category, city, locationName, scheduledTime, maxParticipants, latitude, longitude, imageUri, activeProfileId)
                             } else {
                                 _state.value = NetworkResult.Error("No profile found. Please create a profile first.")
                             }
                         } else if (result is NetworkResult.Error) {
                             // Fallback to userId if profile fetch fails, but likely to fail on backend too if mismatched
                             // But let's try the old way as last resort or show error
                             _state.value = NetworkResult.Error(result.message ?: "Failed to fetch profile")
                         }
                         throw java.util.concurrent.CancellationException("Profile fetched")
                     }
                } catch (e: java.util.concurrent.CancellationException) {
                    // ignore
                } catch (e: Exception) {
                    _state.value = NetworkResult.Error("Error: ${e.message}")
                }
            } else {
                proceedWithCreation(title, description, category, city, locationName, scheduledTime, maxParticipants, latitude, longitude, imageUri, activeProfileId)
            }
        }
    }

    private fun proceedWithCreation(
        title: String,
        description: String,
        category: String,
        city: String,
        locationName: String,
        scheduledTime: String,
        maxParticipants: Int,
        latitude: Double?,
        longitude: Double?,
        imageUri: Uri?,
        profileId: Long
    ) {
        viewModelScope.launch {
            val imageParts = mutableListOf<MultipartBody.Part>()
            imageUri?.let {
                val part = context.createMultipartBodyPart(it, "images")
                if (part != null) {
                    imageParts.add(part)
                } else {
                    _state.value = NetworkResult.Error("Error processing image")
                    return@launch
                }
            }

            val request = CreateSocialRequest(
                title = title,
                description = description,
                category = category,
                city = city,
                locationName = locationName,
                scheduledTime = scheduledTime,
                maxParticipants = maxParticipants,
                profileId = profileId,
                latitude = latitude,
                longitude = longitude
            )
            socialRepository.createSocial(request, if (imageParts.isNotEmpty()) imageParts else null).collectLatest {
                _state.value = it
            }
        }
    }

    fun resetState() {
        _state.value = null
    }
}

