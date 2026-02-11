package com.shivam.downn.ui.screens.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shivam.downn.data.models.NotificationResponse
import com.shivam.downn.data.network.NetworkResult
import com.shivam.downn.data.repository.NotificationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val repository: NotificationRepository
) : ViewModel() {

    private val _state = MutableStateFlow<NetworkResult<List<NotificationResponse>>>(NetworkResult.Loading())
    val state: StateFlow<NetworkResult<List<NotificationResponse>>> = _state

    private val _notificationActions = MutableStateFlow<Map<Long, String>>(emptyMap())
    val notificationActions: StateFlow<Map<Long, String>> = _notificationActions

    private val _actioningNotificationId = MutableStateFlow<Long?>(null)
    val actioningNotificationId: StateFlow<Long?> = _actioningNotificationId

    private val _actionState = MutableStateFlow<NetworkResult<Unit>?>(null)
    val actionState: StateFlow<NetworkResult<Unit>?> = _actionState

    init {
        fetchNotifications()
    }

    fun approveNotification(notificationId: Long) {
        viewModelScope.launch {
            _actioningNotificationId.value = notificationId
            repository.approveNotification(notificationId).collect { result ->
                _actionState.value = result
                if (result is NetworkResult.Success) {
                    _notificationActions.value = _notificationActions.value + (notificationId to "APPROVED")
                    fetchNotifications()
                }
                if (result !is NetworkResult.Loading) {
                    _actioningNotificationId.value = null
                }
            }
        }
    }

    fun rejectNotification(notificationId: Long) {
        viewModelScope.launch {
            _actioningNotificationId.value = notificationId
            repository.rejectNotification(notificationId).collect { result ->
                _actionState.value = result
                if (result is NetworkResult.Success) {
                    _notificationActions.value = _notificationActions.value + (notificationId to "REJECTED")
                    fetchNotifications()
                }
                if (result !is NetworkResult.Loading) {
                    _actioningNotificationId.value = null
                }
            }
        }
    }

    fun clearNotifications() {
        viewModelScope.launch {
            repository.clearAllNotifications().collect { result ->
                _actionState.value = result
                if (result is NetworkResult.Success) {
                    fetchNotifications()
                }
            }
        }
    }

    fun fetchNotifications() {
        viewModelScope.launch {
            repository.getNotifications().collect { result ->
                _state.value = result
            }
        }
    }
}
