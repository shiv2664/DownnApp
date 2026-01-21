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

    fun login(authRequest: AuthRequest) {
        viewModelScope.launch {
            _authState.value = NetworkResult.Loading()
            repository.login(authRequest).collect { it ->
                _authState.value = it
            }
        }
    }

    fun register(registerRequest: RegisterRequest) {
        viewModelScope.launch {
            _authState.value = NetworkResult.Loading()
            repository.register(registerRequest).collect { it ->
                _authState.value = it
            }
        }
    }
}
