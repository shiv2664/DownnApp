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

import com.shivam.downn.data.repository.SocialRepository

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val prefsManager: PrefsManager,
    private val profileRepository: ProfileRepository,
    private val socialRepository: SocialRepository, // Injected SocialRepository
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _profiles = MutableStateFlow<List<UserProfileData>>(emptyList())
    val profiles: StateFlow<List<UserProfileData>> = _profiles.asStateFlow()

    private val _activeProfile = MutableStateFlow<UserProfileData?>(null)
    val activeProfile: StateFlow<UserProfileData?> = _activeProfile.asStateFlow()

    private val _canCreateBusinessProfile = MutableStateFlow(true)
    val canCreateBusinessProfile: StateFlow<Boolean> = _canCreateBusinessProfile.asStateFlow()

    private val _createProfileState = MutableStateFlow<NetworkResult<UserProfileData>?>(null)
    val createProfileState: StateFlow<NetworkResult<UserProfileData>?> = _createProfileState.asStateFlow()

    private val _viewedProfile = MutableStateFlow<NetworkResult<UserProfileData>?>(null)
    val viewedProfile: StateFlow<NetworkResult<UserProfileData>?> = _viewedProfile.asStateFlow()

    private val _userUpdateResponse = MutableStateFlow<NetworkResult<UserDetailsResponse>?>(null)
    val userUpdateResponse: StateFlow<NetworkResult<UserDetailsResponse>?> =
        _userUpdateResponse.asStateFlow()

    // Pagination for User Activities
    private val _userActivities = MutableStateFlow<NetworkResult<List<com.shivam.downn.data.models.SocialResponse>>>(NetworkResult.Loading())
    val userActivities: StateFlow<NetworkResult<List<com.shivam.downn.data.models.SocialResponse>>> = _userActivities.asStateFlow()

    private var userActivitiesPage = 0
    private var isUserActivitiesLastPage = false
    private var isUserActivitiesLoadingMore = false
    private val pageSize = 10
    private var currentUserActivityId: Long? = null



    init {
        // Initialize with cached auth data to avoid "User Name" placeholder
        prefsManager.getAuthResponse()?.let {
            _activeProfile.value = it.toUserProfileData()
        }
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
                    val profileToActivate = profileList.find { it.id == savedId } ?: profileList.firstOrNull()
                    
                    if (profileToActivate != null) {
                        _activeProfile.value = profileToActivate
                        fetchCurrentUserDetails()
                    }
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
        // If activeProfile is null (e.g., init ran before login),
        // try to re-initialize from PrefsManager
        if (_activeProfile.value == null) {
            prefsManager.getAuthResponse()?.let {
                _activeProfile.value = it.toUserProfileData()
            }
        }

        // If profiles are empty (e.g., loadProfiles failed during init because no token),
        // reload them now that the user is authenticated
        if (_profiles.value.isEmpty() && prefsManager.getToken() != null) {
            loadProfiles()
            return // loadProfiles will call fetchCurrentUserDetails after loading
        }

        val currentProfile = _activeProfile.value ?: return
        viewModelScope.launch {
            if (currentProfile.type == ProfileType.PERSONAL) {
                val flow = profileRepository.getUserDetails(prefsManager.getUserId())
                flow.collectLatest { result ->
                    if (result is NetworkResult.Success) {
                        val updatedProfile = result.data?.toUserProfileData()
                        _activeProfile.value = updatedProfile
                        // Fetch activities for the current user
                        fetchUserActivities(updatedProfile!!.id, isRefresh = true)
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
        vibes: List<String>,
        latitude: Double? = null,
        longitude: Double? = null
    ) {
        viewModelScope.launch {
            _createProfileState.value = NetworkResult.Loading()
            val request = CreateProfileRequest(
                name = name,
                bio = bio,
                coverImage = coverImage,
                vibes = vibes.joinToString(","),
                type = ProfileType.BUSINESS,
                latitude = latitude,
                longitude = longitude
            )
            profileRepository.createProfile(request).collectLatest { result ->
                if (result is NetworkResult.Success) {
                    val newProfile = result.data!!.toUserProfileData()
                    _profiles.value += newProfile
                    _activeProfile.value = newProfile
                    prefsManager.saveActiveProfileId(newProfile.id)
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

    fun updateUser(name: String, bio: String, location: String, avatarUri: Uri?) {
        viewModelScope.launch {
            val request = UpdateUserRequest(name = name, bio = bio, location = location)
            val avatarPart = avatarUri?.let { context.createMultipartBodyPart(it, "avatar") }

            profileRepository.updateUser(request, avatarPart).collectLatest { result ->
                _userUpdateResponse.value = result
                if (result is NetworkResult.Success) {
                    val updatedUser = result.data?.toUserProfileData()
                    updatedUser?.let {
                        _activeProfile.value = it
                        loadProfiles()
                    }
                }
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
                    // Handle result
                    if (result is NetworkResult.Success) {
                        loadProfiles() // Reload profiles to get updated data
                    }
                }
        }
    }

    fun fetchUserActivities(userId: Long, isRefresh: Boolean = false) {
        if (isRefresh) {
            userActivitiesPage = 0
            isUserActivitiesLastPage = false
            _userActivities.value = NetworkResult.Loading()
        }
        
        currentUserActivityId = userId

        viewModelScope.launch {
            socialRepository.getUserSocials(userId, userActivitiesPage, pageSize).collectLatest { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        val pagedResponse = result.data
                        val newItems = pagedResponse?.content ?: emptyList()
                        isUserActivitiesLastPage = pagedResponse?.last ?: true

                        if (userActivitiesPage == 0) {
                             _userActivities.value = NetworkResult.Success(newItems)
                        } else {
                            val currentItems = (_userActivities.value.data ?: emptyList()) + newItems
                            _userActivities.value = NetworkResult.Success(currentItems)
                        }
                    }
                    is NetworkResult.Error -> {
                         if (userActivitiesPage == 0) {
                             _userActivities.value = NetworkResult.Error(result.message)
                         }
                    }
                    is NetworkResult.Loading -> {
                        if (userActivitiesPage == 0) {
                            _userActivities.value = NetworkResult.Loading()
                        }
                    }
                }
                isUserActivitiesLoadingMore = false
            }
        }
    }

    fun loadMoreUserActivities() {
        if (!isUserActivitiesLastPage && !isUserActivitiesLoadingMore && _userActivities.value is NetworkResult.Success) {
            isUserActivitiesLoadingMore = true
            userActivitiesPage++
            currentUserActivityId?.let { fetchUserActivities(it) }
        }
    }

    fun followUser(userId: Long) {
        viewModelScope.launch {
            profileRepository.followUser(userId).collectLatest { result ->
                if (result is NetworkResult.Success) {
                    val currentResult = _viewedProfile.value
                    if (currentResult is NetworkResult.Success) {
                        val currentDetails = currentResult.data!!
                        if (currentDetails.userId == userId) {
                             _viewedProfile.value = NetworkResult.Success(
                                 currentDetails.copy(
                                     isFollowing = true,
                                     followersCount = currentDetails.followersCount + 1
                                 )
                             )
                        }
                    }
                }
            }
        }
    }

    fun unfollowUser(userId: Long) {
        viewModelScope.launch {
            profileRepository.unfollowUser(userId).collectLatest { result ->
                if (result is NetworkResult.Success) {
                    val currentResult = _viewedProfile.value
                    if (currentResult is NetworkResult.Success) {
                        val currentDetails = currentResult.data!!
                        if (currentDetails.userId == userId) {
                            _viewedProfile.value = NetworkResult.Success(
                                currentDetails.copy(
                                    isFollowing = false,
                                    followersCount = kotlin.math.max(0, currentDetails.followersCount - 1)
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}