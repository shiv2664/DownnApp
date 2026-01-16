package com.shivam.downn.ui.screens.create_activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shivam.downn.data.models.CreateActivityRequest
import com.shivam.downn.data.repository.ActivityRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class CreateActivityState {
    object Idle : CreateActivityState()
    object Loading : CreateActivityState()
    object Success : CreateActivityState()
    data class Error(val message: String) : CreateActivityState()
}

@HiltViewModel
class CreateActivityViewModel @Inject constructor(
    private val activityRepository: ActivityRepository
) : ViewModel() {

    private val _state = MutableStateFlow<CreateActivityState>(CreateActivityState.Idle)
    val state: StateFlow<CreateActivityState> = _state

    fun createActivity(
        title: String,
        description: String,
        category: String,
        city: String,
        locationName: String,
        scheduledTime: String,
        maxParticipants: Int
    ) {
        viewModelScope.launch {
            _state.value = CreateActivityState.Loading
            val request = CreateActivityRequest(
                title = title,
                description = description,
                category = category,
                city = city,
                locationName = locationName,
                scheduledTime = scheduledTime,
                maxParticipants = maxParticipants
            )
            val result = activityRepository.createActivity(request)
            result.onSuccess {
                _state.value = CreateActivityState.Success
            }.onFailure {
                _state.value = CreateActivityState.Error(it.message ?: "Failed to create activity")
            }
        }
    }

    fun resetState() {
        _state.value = CreateActivityState.Idle
    }
}
