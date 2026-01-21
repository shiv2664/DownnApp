package com.shivam.downn.ui.screens.activity_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shivam.downn.data.models.SocialResponse
import com.shivam.downn.data.repository.SocialRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.shivam.downn.data.network.NetworkResult


@HiltViewModel
class SocialDetailViewModel @Inject constructor(
    private val socialRepository: SocialRepository
) : ViewModel() {

    private val _state = MutableStateFlow<NetworkResult<SocialResponse>>(NetworkResult.Loading())
    val state: StateFlow<NetworkResult<SocialResponse>> = _state

    fun loadSocialDetails(socialId: Int) {
        viewModelScope.launch {
            _state.value = NetworkResult.Loading()
            socialRepository.getSocialById(socialId).collect {
                _state.value = it
            }
        }
    }
}
