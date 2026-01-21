package com.shivam.downn.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shivam.downn.data.network.NetworkResult
import com.shivam.downn.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _logoutState = MutableStateFlow<NetworkResult<String?>?>(null)
    val logoutState: StateFlow<NetworkResult<String?>?> = _logoutState

    fun logout() {
        viewModelScope.launch {
            authRepository.logout().collect {
                _logoutState.value = it
            }
        }
    }

    fun resetLogoutState() {
        _logoutState.value = null
    }
}
