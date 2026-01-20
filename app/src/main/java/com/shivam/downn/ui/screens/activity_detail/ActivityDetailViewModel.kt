package com.shivam.downn.ui.screens.activity_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shivam.downn.data.models.ActivityResponse
import com.shivam.downn.data.repository.ActivityRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ActivityDetailState {
    object Loading : ActivityDetailState()
    data class Success(val activity: ActivityResponse) : ActivityDetailState()
    data class Error(val message: String) : ActivityDetailState()
}

@HiltViewModel
class ActivityDetailViewModel @Inject constructor(
    private val activityRepository: ActivityRepository
) : ViewModel() {

    private val _state = MutableStateFlow<ActivityDetailState>(ActivityDetailState.Loading)
    val state: StateFlow<ActivityDetailState> = _state

    fun loadActivityDetails(activityId: Int) {
        viewModelScope.launch {
            _state.value = ActivityDetailState.Loading
            val result = activityRepository.getActivityById(activityId)
            result.onSuccess {
                _state.value = ActivityDetailState.Success(it)
            }.onFailure {
                _state.value = ActivityDetailState.Error(it.message ?: "Unknown error")
            }
        }
    }
}
