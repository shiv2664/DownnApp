package com.shivam.downn.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shivam.downn.data.models.AuthRequest
import com.shivam.downn.data.models.AuthResponse
import com.shivam.downn.data.models.RegisterRequest
import com.shivam.downn.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.shivam.downn.data.network.NetworkResult
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private val _authState = MutableStateFlow<NetworkResult<AuthResponse?>?>(null)
    val authState: StateFlow<NetworkResult<AuthResponse?>?> = _authState.asStateFlow()

    private val _forgotPasswordState = MutableStateFlow<NetworkResult<String?>?>(null)
    val forgotPasswordState = _forgotPasswordState.asStateFlow()

    private val _resetPasswordState = MutableStateFlow<NetworkResult<String?>?>(null)
    val resetPasswordState = _resetPasswordState.asStateFlow()

    fun login(authRequest: AuthRequest) {
        viewModelScope.launch {
            _authState.value = NetworkResult.Loading()
            repository.login(authRequest).collect {
                _authState.value = it
            }
        }
    }

    fun register(registerRequest: RegisterRequest) {
        viewModelScope.launch {
            _authState.value = NetworkResult.Loading()
            repository.register(registerRequest).collect {
                _authState.value = it
            }
        }
    }
    
    fun forgotPassword(email: String) {
        viewModelScope.launch {
            _forgotPasswordState.value = NetworkResult.Loading()
            repository.forgotPassword(email).collect {
                _forgotPasswordState.value = it
            }
        }
    }

    fun resetForgotPasswordState() {
        _forgotPasswordState.value = null
    }

    fun resetPassword(token: String, newPassword: String) {
        viewModelScope.launch {
            _resetPasswordState.value = NetworkResult.Loading()
            repository.resetPassword(token, newPassword).collect {
                _resetPasswordState.value = it
            }
        }
    }

    fun resetResetPasswordState() {
        _resetPasswordState.value = null
    }
}
