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

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState?>(AuthState.Idle)
    val authState: StateFlow<AuthState?> = _authState as StateFlow<AuthState?>

    fun login(authRequest: AuthRequest) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = repository.login(authRequest)
            result.onSuccess {
                _authState.value = AuthState.Success(it)
            }.onFailure {
                _authState.value = AuthState.Error(it.message ?: "Unknown error")
            }
        }
    }

    fun register(registerRequest: RegisterRequest) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = repository.register(registerRequest)
            result.onSuccess {
                _authState.value = AuthState.Success(it)
            }.onFailure {
                _authState.value = AuthState.Error(it.message ?: "Unknown error")
            }
        }
    }
}
