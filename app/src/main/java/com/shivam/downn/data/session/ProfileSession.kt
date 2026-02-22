package com.shivam.downn.data.session

import com.shivam.downn.data.local.PrefsManager
import com.shivam.downn.data.models.ProfileType
import com.shivam.downn.data.models.UserProfileData
import com.shivam.downn.data.network.NetworkResult
import com.shivam.downn.data.repository.ProfileRepository
import com.shivam.downn.utils.toUserProfileData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Singleton session-level state holder for profile data.
 * Survives ViewModel lifecycle — no re-fetching on navigation.
 * Every screen observes this; no passing ViewModels through navigation for profile state.
 */
@Singleton
class ProfileSession @Inject constructor(
    private val prefsManager: PrefsManager,
    private val profileRepository: ProfileRepository
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private val _activeProfile = MutableStateFlow<UserProfileData?>(null)
    val activeProfile: StateFlow<UserProfileData?> = _activeProfile.asStateFlow()

    private val _profiles = MutableStateFlow<List<UserProfileData>>(emptyList())
    val profiles: StateFlow<List<UserProfileData>> = _profiles.asStateFlow()

    private val _canCreateBusinessProfile = MutableStateFlow(true)
    val canCreateBusinessProfile: StateFlow<Boolean> = _canCreateBusinessProfile.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        // Restore from prefs on creation
        prefsManager.getAuthResponse()?.let {
            _activeProfile.value = it.toUserProfileData()
        }
    }

    fun switchProfile(profile: UserProfileData) {
        _activeProfile.value = profile
        prefsManager.saveActiveProfileId(profile.id)
    }

    fun refresh() {
        if (_isLoading.value) return
        loadProfiles()
    }

    fun loadProfiles() {
        if (prefsManager.getToken() == null) {
            _isLoading.value = false
            return
        }
        
        scope.launch {
            _isLoading.value = true
            profileRepository.getProfiles().collectLatest { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        val profileList = result.data?.map { it.toUserProfileData() } ?: emptyList()
                        _profiles.value = profileList

                        val businessCount = profileList.count { it.type == ProfileType.BUSINESS }
                        _canCreateBusinessProfile.value = businessCount < 3

                        // Restore or set active profile
                        val savedProfileId = prefsManager.getActiveProfileId()
                        val matchedProfile = profileList.find { it.id == savedProfileId }

                        if (matchedProfile != null) {
                            _activeProfile.value = matchedProfile
                        } else if (profileList.isNotEmpty()) {
                            val personal = profileList.find { it.type == ProfileType.PERSONAL }
                            _activeProfile.value = personal ?: profileList.first()
                            prefsManager.saveActiveProfileId(_activeProfile.value!!.id)
                        }
                    }
                    is NetworkResult.Error -> { /* keep existing data */ }
                    is NetworkResult.Loading -> { /* ignore */ }
                }
                _isLoading.value = false
            }
        }
    }

    fun fetchCurrentUserDetails() {
        val currentProfile = _activeProfile.value ?: return
        scope.launch {
            if (currentProfile.type == ProfileType.PERSONAL) {
                profileRepository.getUserDetails(currentProfile.userId).collectLatest { result ->
                    when (result) {
                        is NetworkResult.Success -> {
                            result.data?.let { response ->
                                val updatedProfile = response.toUserProfileData()
                                _activeProfile.value = updatedProfile

                                // Also update profiles list
                                val currentProfiles = _profiles.value.toMutableList()
                                val idx = currentProfiles.indexOfFirst { it.id == updatedProfile.id }
                                if (idx >= 0) {
                                    currentProfiles[idx] = updatedProfile
                                    _profiles.value = currentProfiles
                                }
                            }
                        }
                        else -> { /* ignore */ }
                    }
                }
            } else {
                profileRepository.getProfileDetails(currentProfile.id).collectLatest { result ->
                    when (result) {
                        is NetworkResult.Success -> {
                            result.data?.let { response ->
                                val updatedProfile = response.toUserProfileData()
                                _activeProfile.value = updatedProfile

                                val currentProfiles = _profiles.value.toMutableList()
                                val idx = currentProfiles.indexOfFirst { it.id == updatedProfile.id }
                                if (idx >= 0) {
                                    currentProfiles[idx] = updatedProfile
                                    _profiles.value = currentProfiles
                                }
                            }
                        }
                        else -> { /* ignore */ }
                    }
                }
            }
        }
    }

    fun clear() {
        _activeProfile.value = null
        _profiles.value = emptyList()
        _canCreateBusinessProfile.value = true
    }
}
