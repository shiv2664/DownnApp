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

@HiltViewModel
class StartMoveViewModel @Inject constructor(
    private val socialRepository: SocialRepository,
    private val prefsManager: PrefsManager,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _state = MutableStateFlow<NetworkResult<SocialResponse?>?>(null)
    val state: StateFlow<NetworkResult<SocialResponse?>?> = _state

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

            val activeProfileId = prefsManager.getActiveProfileId()
            val request = CreateSocialRequest(
                title = title,
                description = description,
                category = category,
                city = city,
                locationName = locationName,
                scheduledTime = scheduledTime,
                maxParticipants = maxParticipants,
                profileId = if (activeProfileId != -1L) activeProfileId else prefsManager.getUserId(),
                latitude = latitude,
                longitude = longitude
            )
            socialRepository.createSocial(request).collectLatest {
                _state.value = it
            }
        }
    }

    fun resetState() {
        _state.value = null
    }
}

