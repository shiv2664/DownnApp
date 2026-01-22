package com.shivam.downn.ui.screens.create_activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shivam.downn.data.local.PrefsManager
import com.shivam.downn.data.models.SocialResponse
import com.shivam.downn.data.models.CreateSocialRequest
import com.shivam.downn.data.repository.SocialRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.shivam.downn.data.network.NetworkResult


@HiltViewModel
class StartMoveViewModel @Inject constructor(
    private val socialRepository: SocialRepository,
    private val  prefsManager: PrefsManager
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
        maxParticipants: Int
    ) {
        viewModelScope.launch {
            _state.value = NetworkResult.Loading()
            val request = CreateSocialRequest(
                title = title,
                description = description,
                category = category,
                city = city,
                locationName = locationName,
                scheduledTime = scheduledTime,
                maxParticipants = maxParticipants,
                profileId=prefsManager.getUserId()

            )
            socialRepository.createSocial(request).collect {
                _state.value = it
            }
        }
    }

    fun resetState() {
        _state.value = null
    }
}
