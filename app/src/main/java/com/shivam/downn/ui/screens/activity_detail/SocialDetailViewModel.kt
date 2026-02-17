package com.shivam.downn.ui.screens.activity_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shivam.downn.data.models.SocialResponse
import com.shivam.downn.data.network.NetworkResult
import com.shivam.downn.data.repository.SocialRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SocialDetailViewModel @Inject constructor(
    private val repository: SocialRepository,
    private val profileRepository: com.shivam.downn.data.repository.ProfileRepository,
    private val prefsManager: com.shivam.downn.data.local.PrefsManager
) : ViewModel() {

    val currentUserId = prefsManager.getUserId()

    private val _state = MutableStateFlow<NetworkResult<SocialResponse>>(NetworkResult.Loading())
    val state: StateFlow<NetworkResult<SocialResponse>> = _state

    private val _isBusinessProfile = MutableStateFlow(false)
    val isBusinessProfile: StateFlow<Boolean> = _isBusinessProfile

    fun loadSocialDetails(socialId: Int) {
        viewModelScope.launch {
            repository.getSocialById(socialId).collect { result ->
                _state.value = result
            }
        }
    }

    private val _joinState = MutableStateFlow<NetworkResult<Unit>?>(null)
    val joinState: StateFlow<NetworkResult<Unit>?> = _joinState

    private val _leaveState = MutableStateFlow<NetworkResult<Unit>?>(null)
    val leaveState: StateFlow<NetworkResult<Unit>?> = _leaveState

    private val _removeState = MutableStateFlow<NetworkResult<Unit>?>(null)
    val removeState: StateFlow<NetworkResult<Unit>?> = _removeState

    init {
        checkCurrentProfileType()
    }

    fun joinSocial(socialId: Int) {
        viewModelScope.launch {
            repository.joinSocial(socialId).collect { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        _joinState.value = NetworkResult.Success(Unit)
                        // Local update for immediate visual feedback
                        val currentState = _state.value
                        if (currentState is NetworkResult.Success) {
                            currentState.data?.let { social ->
                                currentUserId?.let { userId ->
                                    val requested = social.requestedUserIds ?: mutableSetOf()
                                    requested.add(userId)
                                    // Re-emit state to trigger UI update with the new set
                                    _state.value = NetworkResult.Success(social.copy(requestedUserIds = requested))
                                }
                            }
                        }
                        loadSocialDetails(socialId)
                    }
                    is NetworkResult.Error -> _joinState.value = NetworkResult.Error(result.message)
                    is NetworkResult.Loading -> _joinState.value = NetworkResult.Loading()
                }
            }
        }
    }

    fun leaveSocial(socialId: Int) {
        viewModelScope.launch {
            repository.leaveSocial(socialId).collect { result ->
                if (result is NetworkResult.Success) {
                    _leaveState.value = result
                    loadSocialDetails(socialId)
                } else {
                    _leaveState.value = result
                }
            }
        }
    }

    fun removeParticipant(socialId: Int, participantId: Long) {
        viewModelScope.launch {
            repository.removeParticipant(socialId, participantId).collect { result ->
                if (result is NetworkResult.Success) {
                    _removeState.value = result
                    loadSocialDetails(socialId)
                } else {
                    _removeState.value = result
                }
            }
        }
    }
    
    fun resetStates() {
        _joinState.value = null
        _leaveState.value = null
        _removeState.value = null
        _deleteState.value = null
    }

    private val _deleteState = MutableStateFlow<NetworkResult<Unit>?>(null)
    val deleteState: StateFlow<NetworkResult<Unit>?> = _deleteState

    fun deleteActivity(socialId: Int) {
        viewModelScope.launch {
            repository.deleteActivity(socialId).collect { result ->
                _deleteState.value = result
            }
        }
    }




    private fun checkCurrentProfileType() {
        val activeProfileId = prefsManager.getActiveProfileId()
        val userId = prefsManager.getUserId()

        viewModelScope.launch {
            if (activeProfileId != -1L) {
                profileRepository.getProfileDetails(activeProfileId).collect { result ->
                    if (result is NetworkResult.Success) {
                        _isBusinessProfile.value = result.data?.type == com.shivam.downn.data.models.ProfileType.BUSINESS
                    }
                }
            } else {
                profileRepository.getUserDetails(userId).collect { result ->
                     if (result is NetworkResult.Success) {
                        _isBusinessProfile.value = result.data?.type == com.shivam.downn.data.models.ProfileType.BUSINESS
                    }
                }
            }
        }
    }
}
