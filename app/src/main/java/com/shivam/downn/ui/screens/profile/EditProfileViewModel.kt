package com.shivam.downn.ui.screens.profile

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import androidx.lifecycle.viewModelScope
import com.shivam.downn.data.models.UpdateProfileRequest
import com.shivam.downn.data.models.UpdateUserRequest
import com.shivam.downn.data.models.UserDetailsResponse
import com.shivam.downn.data.network.NetworkResult
import com.shivam.downn.data.repository.ProfileRepository
import com.shivam.downn.utils.createMultipartBodyPart
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _userUpdateResponse = MutableStateFlow<NetworkResult<UserDetailsResponse>?>(null)
    val userUpdateResponse: StateFlow<NetworkResult<UserDetailsResponse>?> =
        _userUpdateResponse.asStateFlow()

    private val _profileUpdateSuccess = MutableStateFlow(false)
    val profileUpdateSuccess: StateFlow<Boolean> = _profileUpdateSuccess.asStateFlow()

    fun updateUser(name: String, bio: String, location: String, avatarUri: Uri?) {
        viewModelScope.launch {
            val request = UpdateUserRequest(name = name, bio = bio, location = location)
            val avatarPart = avatarUri?.let { context.createMultipartBodyPart(it, "avatar") }

            profileRepository.updateUser(request, avatarPart).collectLatest { result ->
                _userUpdateResponse.value = result
            }
        }
    }

    fun updateProfile(
        profileId: Long,
        name: String,
        bio: String,
        location: String,
        vibes: String,
        avatarUri: Uri?,
        coverUri: Uri?,
        latitude: Double? = null,
        longitude: Double? = null
    ) {
        viewModelScope.launch {
            val request =
                UpdateProfileRequest(
                    name = name,
                    bio = bio,
                    location = location,
                    vibes = vibes,
                    latitude = latitude,
                    longitude = longitude
                )
            val avatarPart = avatarUri?.let { context.createMultipartBodyPart(it, "avatar") }
            val coverPart = coverUri?.let { context.createMultipartBodyPart(it, "cover") }

            profileRepository.updateProfile(profileId, request, avatarPart, coverPart)
                .collectLatest { result ->
                    if (result is NetworkResult.Success) {
                        _profileUpdateSuccess.value = true
                    }
                }
        }
    }
}
