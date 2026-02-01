package com.shivam.downn.ui.screens.profile

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import com.shivam.downn.data.local.PrefsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import androidx.lifecycle.viewModelScope
import com.shivam.downn.data.models.CreateProfileRequest
import com.shivam.downn.data.models.ProfileType
import com.shivam.downn.data.models.UpdateProfileRequest
import com.shivam.downn.data.models.UpdateUserRequest
import com.shivam.downn.data.models.UserDetailsResponse
import com.shivam.downn.data.models.UserProfileData
import com.shivam.downn.data.network.NetworkResult
import com.shivam.downn.data.repository.ProfileRepository
import com.shivam.downn.utils.createMultipartBodyPart
import com.shivam.downn.utils.toUserProfileData
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val prefsManager: PrefsManager,
    private val profileRepository: ProfileRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _profiles = MutableStateFlow<List<UserProfileData>>(emptyList())
    val profiles: StateFlow<List<UserProfileData>> = _profiles.asStateFlow()

    private val _activeProfile = MutableStateFlow<UserProfileData?>(null)
    val activeProfile: StateFlow<UserProfileData?> = _activeProfile.asStateFlow()

    private val _canCreateBusinessProfile = MutableStateFlow(true)
    val canCreateBusinessProfile: StateFlow<Boolean> = _canCreateBusinessProfile.asStateFlow()

    private val _viewedProfile = MutableStateFlow<NetworkResult<UserProfileData>?>(null)
    val viewedProfile: StateFlow<NetworkResult<UserProfileData>?> = _viewedProfile.asStateFlow()

    private val _userUpdateResponse = MutableStateFlow<NetworkResult<UserDetailsResponse>?>(null)
    val userUpdateResponse: StateFlow<NetworkResult<UserDetailsResponse>?> =
        _userUpdateResponse.asStateFlow()


    init {
        loadProfiles()
    }

    private fun loadProfiles() {
        viewModelScope.launch {
            profileRepository.getProfiles().collectLatest { result ->
                if (result is NetworkResult.Success) {
                    val profileList = result.data?.map { it.toUserProfileData() } ?: emptyList()
                    _profiles.value = profileList

                    // Restore active profile from SharedPreferences
                    val savedId = prefsManager.getActiveProfileId()
                    _activeProfile.value =
                        profileList.find { it.id == savedId } ?: profileList.firstOrNull()
                }
            }
        }
    }

    fun fetchProfileDetails(profileId: Long) {
        viewModelScope.launch {
            profileRepository.getProfileDetails(profileId).collectLatest { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        _viewedProfile.value =
                            NetworkResult.Success(result.data!!.toUserProfileData())
                    }

                    is NetworkResult.Error -> {
                        _viewedProfile.value = NetworkResult.Error(result.message)
                    }

                    is NetworkResult.Loading -> {
                        _viewedProfile.value = NetworkResult.Loading()
                    }
                }
            }
        }
    }

    fun fetchUserDetails(userId: Long) {
        viewModelScope.launch {
            profileRepository.getUserDetails(userId).collectLatest { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        _viewedProfile.value =
                            NetworkResult.Success(result.data!!.toUserProfileData())
                    }

                    is NetworkResult.Error -> {
                        _viewedProfile.value = NetworkResult.Error(result.message)
                    }

                    is NetworkResult.Loading -> {
                        _viewedProfile.value = NetworkResult.Loading()
                    }
                }
            }
        }
    }

    fun fetchCurrentUserDetails() {
        val currentProfile = _activeProfile.value ?: return
        viewModelScope.launch {
            if (currentProfile.type == ProfileType.PERSONAL) {
                val flow = profileRepository.getUserDetails(prefsManager.getUserId())
                flow.collectLatest { result ->
                    if (result is NetworkResult.Success) {
                        val updatedProfile = result.data?.toUserProfileData()
                        _activeProfile.value = updatedProfile
                        // Also update in profiles list
                        updatedProfile?.let { it1 ->
                            _profiles.value =
                                _profiles.value.map {
                                    if (it.id == it1.id && it.type == it1.type) it1 else it
                                }
                        }

                    }
                }

            } else {
                val flow = profileRepository.getProfileDetails(currentProfile.id)
                flow.collectLatest { result ->
                    if (result is NetworkResult.Success) {
                        val updatedProfile = result.data!!.toUserProfileData()
                        _activeProfile.value = updatedProfile
                        // Also update in profiles list
                        _profiles.value = _profiles.value.map {
                            if (it.id == updatedProfile.id && it.type == updatedProfile.type) updatedProfile else it
                        }
                    }
                }
            }

        }
    }

    fun switchProfile(profile: UserProfileData) {
        _activeProfile.value = profile
        prefsManager.saveActiveProfileId(profile.id)
    }

    fun createBusinessProfile(
        name: String,
        category: String,
        bio: String,
        location: String,
        coverImage: String,
        vibes: List<String>
    ) {
        viewModelScope.launch {
            val request = CreateProfileRequest(
                name = name,
                bio = bio,
                coverImage = coverImage,
                vibes = vibes.joinToString(","),
                type = ProfileType.BUSINESS
            )
            profileRepository.createProfile(request).collectLatest { result ->
                if (result is NetworkResult.Success) {
                    val newProfile = result.data!!.toUserProfileData()
                    _profiles.value += newProfile
                    _activeProfile.value = newProfile
                    prefsManager.saveActiveProfileId(newProfile.id)
                }
            }
        }
    }

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
        vibes: List<String>,
        avatarUri: Uri?,
        coverUri: Uri?
    ) {
        viewModelScope.launch {
            val request =
                UpdateProfileRequest(name = name, bio = bio, location = location, vibes = vibes)
            val avatarPart = avatarUri?.let { context.createMultipartBodyPart(it, "avatar") }
            val coverPart = coverUri?.let { context.createMultipartBodyPart(it, "cover") }

            profileRepository.updateProfile(profileId, request, avatarPart, coverPart)
                .collectLatest { result ->
                    // Handle result
                    if (result is NetworkResult.Success) {
                        loadProfiles() // Reload profiles to get updated data
                    }
                }
        }
    }
}