package com.shivam.downn.ui.screens.profile

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import androidx.lifecycle.viewModelScope
import com.shivam.downn.data.models.UserProfileData
import com.shivam.downn.data.network.NetworkResult
import com.shivam.downn.data.repository.ProfileRepository
import com.shivam.downn.data.repository.SocialRepository
import com.shivam.downn.utils.toUserProfileData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PublicProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val socialRepository: SocialRepository
) : ViewModel() {

    private val _viewedProfile = MutableStateFlow<NetworkResult<UserProfileData>?>(null)
    val viewedProfile: StateFlow<NetworkResult<UserProfileData>?> = _viewedProfile.asStateFlow()

    // Pagination for User Activities
    private val _userActivities = MutableStateFlow<NetworkResult<List<com.shivam.downn.data.models.SocialResponse>>>(NetworkResult.Loading())
    val userActivities: StateFlow<NetworkResult<List<com.shivam.downn.data.models.SocialResponse>>> = _userActivities.asStateFlow()

    private var userActivitiesPage = 0
    private var isUserActivitiesLastPage = false
    private var isUserActivitiesLoadingMore = false
    private val pageSize = 10
    private var currentUserActivityId: Long? = null

    // Report state
    private val _reportState = MutableStateFlow<NetworkResult<Unit>?>(null)
    val reportState: StateFlow<NetworkResult<Unit>?> = _reportState.asStateFlow()

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

    fun blockUser(userId: Long) {
        viewModelScope.launch {
            profileRepository.blockUser(userId).collectLatest { result ->
                if (result is NetworkResult.Success) {
                    val currentResult = _viewedProfile.value
                    if (currentResult is NetworkResult.Success) {
                        val currentDetails = currentResult.data!!
                        if (currentDetails.userId == userId) {
                            _viewedProfile.value = NetworkResult.Success(
                                currentDetails.copy(
                                    isBlocked = true,
                                    isFollowing = false
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    fun unblockUser(userId: Long) {
        viewModelScope.launch {
            profileRepository.unblockUser(userId).collectLatest { result ->
                if (result is NetworkResult.Success) {
                    val currentResult = _viewedProfile.value
                    if (currentResult is NetworkResult.Success) {
                        val currentDetails = currentResult.data!!
                        if (currentDetails.userId == userId) {
                            _viewedProfile.value = NetworkResult.Success(
                                currentDetails.copy(isBlocked = false)
                            )
                        }
                    }
                }
            }
        }
    }

    fun reportContent(request: com.shivam.downn.data.models.ReportRequest) {
        viewModelScope.launch {
            profileRepository.reportContent(request).collectLatest { result ->
                _reportState.value = result
            }
        }
    }

    fun resetReportState() {
        _reportState.value = null
    }
}
