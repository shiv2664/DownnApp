package com.shivam.downn.ui.screens.explore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shivam.downn.data.models.SocialResponse
import com.shivam.downn.data.network.NetworkResult
import com.shivam.downn.data.repository.SocialRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExploreViewModel @Inject constructor(
    private val socialRepository: SocialRepository
) : ViewModel() {

    private val _state = MutableStateFlow<NetworkResult<List<SocialResponse>>>(NetworkResult.Loading())
    val state: StateFlow<NetworkResult<List<SocialResponse>>> = _state

    init {
        fetchSocials()
    }

    fun fetchSocials(city: String = "Denver", category: String? = null) {
        viewModelScope.launch {
            _state.value = NetworkResult.Loading()
            socialRepository.getSocials(city, category).collect {
                _state.value = it
            }
        }
    }
}
