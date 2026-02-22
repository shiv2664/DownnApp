package com.shivam.downn.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import com.shivam.downn.data.models.ProfileType
import com.shivam.downn.data.models.SocialResponse
import com.shivam.downn.data.models.UserProfileData
import com.shivam.downn.data.network.NetworkResult
import com.shivam.downn.data.repository.SocialRepository
import com.shivam.downn.data.session.ProfileSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyProfileViewModel @Inject constructor(
    private val socialRepository: SocialRepository,
    val profileSession: ProfileSession
) : ViewModel() {

    // Delegate profile state to ProfileSession (singleton)
    val activeProfile: StateFlow<UserProfileData?> = profileSession.activeProfile
    val profiles: StateFlow<List<UserProfileData>> = profileSession.profiles
    val canCreateBusinessProfile: StateFlow<Boolean> = profileSession.canCreateBusinessProfile

    // Pagination for User Activities (screen-specific state)
    private val _userActivities = MutableStateFlow<NetworkResult<List<SocialResponse>>>(NetworkResult.Loading())
    val userActivities: StateFlow<NetworkResult<List<SocialResponse>>> = _userActivities.asStateFlow()

    private var userActivitiesPage = 0
    private var isUserActivitiesLastPage = false
    var isUserActivitiesLoadingMore = false
    private val pageSize = 10

    var currentUserActivityId: Long = -1L

    init {
        loadProfiles()
    }

    fun loadProfiles() {
        profileSession.loadProfiles()
    }

    fun switchProfile(profile: UserProfileData) {
        profileSession.switchProfile(profile)
        // Reload activities for the newly selected profile
        if (profile.type == ProfileType.PERSONAL) {
            fetchUserActivities(profile.id, isRefresh = true)
        } else {
            fetchProfileActivities(profile.id, isRefresh = true)
        }
    }

    fun fetchCurrentUserDetails() {
        profileSession.fetchCurrentUserDetails()
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

    fun fetchProfileActivities(profileId: Long, isRefresh: Boolean = false) {
        if (isRefresh) {
            userActivitiesPage = 0
            isUserActivitiesLastPage = false
            _userActivities.value = NetworkResult.Loading()
        }

        currentUserActivityId = profileId

        viewModelScope.launch {
            socialRepository.getProfileSocials(profileId, userActivitiesPage, pageSize).collectLatest { result ->
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
        if (isUserActivitiesLoadingMore || isUserActivitiesLastPage) return
        isUserActivitiesLoadingMore = true
        userActivitiesPage++
        val currentProfile = activeProfile.value ?: return
        if (currentProfile.type == ProfileType.PERSONAL) {
            fetchUserActivities(currentUserActivityId)
        } else {
            fetchProfileActivities(currentUserActivityId)
        }
    }
}
