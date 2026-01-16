package com.shivam.downn.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shivam.downn.data.models.ActivityResponse
import com.shivam.downn.data.repository.ActivityRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class FeedState {
    object Loading : FeedState()
    data class Success(val activities: List<ActivityResponse>) : FeedState()
    data class Error(val message: String) : FeedState()
}

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val activityRepository: ActivityRepository
) : ViewModel() {

    private val _state = MutableStateFlow<FeedState>(FeedState.Loading)
    val state: StateFlow<FeedState> = _state as StateFlow<FeedState>

    init {
        fetchActivities()
    }

    fun fetchActivities(category: String? = "SPORTS") {
        viewModelScope.launch {
            _state.value = FeedState.Loading
            val result = activityRepository.getActivitiesByCity("Denver", category)
            result.onSuccess {
                _state.value = FeedState.Success(it)
            }.onFailure {
                _state.value = FeedState.Error(it.message ?: "Unknown error")
            }
        }
    }

    fun joinActivity(activityId: Int) {
        viewModelScope.launch {
            val result = activityRepository.joinActivity(activityId)
            result.onSuccess {
                fetchActivities()
            }.onFailure {
                // Handle failure
            }
        }
    }
}