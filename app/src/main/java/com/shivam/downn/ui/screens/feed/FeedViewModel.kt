package com.shivam.downn.ui.screens.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shivam.downn.data.local.PrefsManager
import com.shivam.downn.data.models.SocialResponse
import com.shivam.downn.data.repository.SocialRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.shivam.downn.data.network.NetworkResult


@HiltViewModel
class FeedViewModel @Inject constructor(
    private val socialRepository: SocialRepository,
    private val prefsManager: PrefsManager
) : ViewModel() {

    private val _state =
        MutableStateFlow<NetworkResult<List<SocialResponse>>>(NetworkResult.Loading())
    val state: StateFlow<NetworkResult<List<SocialResponse>>> = _state

    init {
        fetchSocials()
    }

    fun fetchSocials(
        city: String = "Denver",
        category: String? = null,
        minPrice: Int? = null,
        maxPrice: Int? = null,
        date: String? = null
    ) {
        viewModelScope.launch {
            _state.value = NetworkResult.Loading()
            socialRepository.getSocials(city, category, minPrice, maxPrice, date).collect {
                _state.value = it
            }
        }
    }

    fun joinSocial(socialId: Int) {
        viewModelScope.launch {
            socialRepository.joinSocial(socialId).collect { result ->
                if (result is NetworkResult.Success) {
                    fetchSocials()
                }
            }
        }
    }
}