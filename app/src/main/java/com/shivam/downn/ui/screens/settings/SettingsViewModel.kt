package com.shivam.downn.ui.screens.settings

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shivam.downn.data.models.UpdateUserRequest
import com.shivam.downn.data.models.UserDetailsResponse
import com.shivam.downn.data.network.NetworkResult
import com.shivam.downn.data.repository.AuthRepository
import com.shivam.downn.data.repository.ProfileRepository
import com.shivam.downn.data.local.PrefsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import android.content.Context
import com.shivam.downn.utils.createMultipartBodyPart
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val profileRepository: ProfileRepository,
    private val prefsManager: PrefsManager,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _logoutState = MutableStateFlow<NetworkResult<String?>?>(null)
    val logoutState: StateFlow<NetworkResult<String?>?> = _logoutState

    private val _deleteAccountState = MutableStateFlow<NetworkResult<String?>?>(null)
    val deleteAccountState: StateFlow<NetworkResult<String?>?> = _deleteAccountState

    private val _userDetails = MutableStateFlow<NetworkResult<UserDetailsResponse>?>(null)
    val userDetails: StateFlow<NetworkResult<UserDetailsResponse>?> = _userDetails

    private val _updateState = MutableStateFlow<NetworkResult<UserDetailsResponse>?>(null)
    val updateState: StateFlow<NetworkResult<UserDetailsResponse>?> = _updateState

    init {
        loadUserDetails()
    }

    fun loadUserDetails() {
        val userId = prefsManager.getUserId()
        viewModelScope.launch {
            profileRepository.getUserDetails(userId).collect { result ->
                _userDetails.value = result
            }
        }
    }

    fun getEmail(): String {
        return prefsManager.getAuthResponse()?.email ?: ""
    }

    fun updateUser(name: String, bio: String, location: String, avatarUri: Uri?) {
        viewModelScope.launch {
            _updateState.value = NetworkResult.Loading()
            val request = UpdateUserRequest(name = name, bio = bio, location = location)
            val avatarPart = avatarUri?.let { context.createMultipartBodyPart(it, "avatar") }
            profileRepository.updateUser(request, avatarPart).collect { result ->
                _updateState.value = result
                if (result is NetworkResult.Success) {
                    loadUserDetails() // Refresh data after successful save
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout().collect {
                _logoutState.value = it
            }
        }
    }

    fun deleteAccount() {
        viewModelScope.launch {
            authRepository.deleteAccount().collect {
                _deleteAccountState.value = it
            }
        }
    }

    fun resetLogoutState() {
        _logoutState.value = null
    }

    fun resetDeleteAccountState() {
        _deleteAccountState.value = null
    }
}
