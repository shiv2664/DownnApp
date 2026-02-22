package com.shivam.downn.ui.screens.profile

import androidx.lifecycle.ViewModel
import com.shivam.downn.data.local.PrefsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import androidx.lifecycle.viewModelScope
import com.shivam.downn.data.models.CreateProfileRequest
import com.shivam.downn.data.models.ProfileType
import com.shivam.downn.data.models.UserProfileData
import com.shivam.downn.data.network.NetworkResult
import com.shivam.downn.data.repository.ProfileRepository
import com.shivam.downn.utils.toUserProfileData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

import android.content.Context
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import com.shivam.downn.utils.createMultipartBodyPart

@HiltViewModel
class CreateProfileViewModel @Inject constructor(
    private val prefsManager: PrefsManager,
    private val profileRepository: ProfileRepository,
    private val profileSession: com.shivam.downn.data.session.ProfileSession,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _createProfileState = MutableStateFlow<NetworkResult<UserProfileData>?>(null)
    val createProfileState: StateFlow<NetworkResult<UserProfileData>?> = _createProfileState.asStateFlow()

    fun createBusinessProfile(
        name: String,
        category: String,
        bio: String,
        location: String,
        avatarImage: String,
        coverImage: String,
        vibes: List<String>,
        latitude: Double? = null,
        longitude: Double? = null
    ) {
        viewModelScope.launch {
            _createProfileState.value = NetworkResult.Loading()
            
            val avatarUri = if (avatarImage.startsWith("content://") || avatarImage.startsWith("file://")) {
                Uri.parse(avatarImage)
            } else null
            
            val coverUri = if (coverImage.startsWith("content://") || coverImage.startsWith("file://")) {
                Uri.parse(coverImage)
            } else null
            
            val request = CreateProfileRequest(
                name = name,
                bio = bio,
                avatar = if (avatarUri == null && avatarImage.isNotEmpty()) avatarImage else null,
                coverImage = if (coverUri == null && coverImage.isNotEmpty()) coverImage else null,
                vibes = vibes.joinToString(","),
                type = ProfileType.BUSINESS,
                latitude = latitude,
                longitude = longitude,
                category = category
            )
            
            val avatarPart = avatarUri?.let { context.createMultipartBodyPart(it, "avatar") }
            val coverPart = coverUri?.let { context.createMultipartBodyPart(it, "cover") }
            
            profileRepository.createProfile(request, avatarPart, coverPart).collectLatest { result ->
                if (result is NetworkResult.Success) {
                    val newProfile = result.data!!.toUserProfileData()
                    prefsManager.saveActiveProfileId(newProfile.id)
                    profileSession.refresh() // Refresh global profile list
                    _createProfileState.value = NetworkResult.Success(newProfile)
                } else if (result is NetworkResult.Error) {
                    _createProfileState.value = NetworkResult.Error(result.message)
                }
            }
        }
    }

    fun resetCreateProfileState() {
        _createProfileState.value = null
    }
}
