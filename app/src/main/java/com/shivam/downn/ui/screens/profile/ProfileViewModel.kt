package com.shivam.downn.ui.screens.profile

import androidx.lifecycle.ViewModel
import com.shivam.downn.data.local.PrefsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import androidx.lifecycle.viewModelScope
import com.shivam.downn.data.models.CreateProfileRequest
import com.shivam.downn.data.models.ProfileResponse
import com.shivam.downn.data.models.ProfileType
import com.shivam.downn.data.network.NetworkResult
import com.shivam.downn.data.repository.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

data class UserProfileData(
    val id: Long,
    val name: String,
    val avatar: String,
    val coverImage: String = "",
    val vibes: List<String> = emptyList(),
    val type: ProfileType
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val prefsManager: PrefsManager,
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val _profiles = MutableStateFlow<List<UserProfileData>>(emptyList())
    val profiles: StateFlow<List<UserProfileData>> = _profiles.asStateFlow()

    private val _activeProfile = MutableStateFlow<UserProfileData?>(null)
    val activeProfile: StateFlow<UserProfileData?> = _activeProfile.asStateFlow()

    private val _canCreateBusinessProfile = MutableStateFlow(true)
    val canCreateBusinessProfile: StateFlow<Boolean> = _canCreateBusinessProfile.asStateFlow()

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
                    _activeProfile.value = profileList.find { it.id.toInt() == savedId } ?: profileList.firstOrNull()
                }
            }
        }
    }

    private fun ProfileResponse.toUserProfileData(): UserProfileData {
        return UserProfileData(
            id = this.id,
            name = this.name,
            avatar = this.avatar ?: "https://images.unsplash.com/photo-1566330429822-c413e4bc27a5",
            coverImage = this.coverImage ?: "",
            vibes = this.vibes?.split(",")?.filter { it.isNotBlank() } ?: emptyList(),
            type = this.type
        )
    }

    fun switchProfile(profile: UserProfileData) {
        _activeProfile.value = profile
        prefsManager.saveActiveProfileId(profile.id)
    }

    fun createBusinessProfile(name: String, category: String, bio: String, location: String, coverImage: String, vibes: List<String>) {
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
}
