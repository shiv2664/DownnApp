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
    private val socialRepository: SocialRepository,
    private val navigationEventBus: com.shivam.downn.navigation.NavigationEventBus
) : ViewModel() {

    private val _state = MutableStateFlow<NetworkResult<List<SocialResponse>>>(NetworkResult.Loading())
    val state: StateFlow<NetworkResult<List<SocialResponse>>> = _state

    private var currentPage = 0
    private var isLastPage = false
    private var isLoadingMore = false
    private val pageSize = 30
    
    // Cache filter params
    private var currentCity = "Denver"
    private var currentCategory: String? = null

    init {
        fetchSocials()
        
        viewModelScope.launch {
            navigationEventBus.events.collect { route ->
                if (route == "explore") {
                    refresh()
                }
            }
        }
    }

    fun updateCity(context: android.content.Context) {
        viewModelScope.launch {
            val city = com.shivam.downn.utils.LocationUtils.getCurrentCity(context)
            city?.let {
                if (it != currentCity) {
                    currentCity = it
                    refresh()
                }
            }
        }
    }
    
    fun refresh() {
        currentPage = 0
        isLastPage = false
        isLoadingMore = false
        fetchSocials(city = currentCity, category = currentCategory, isRefresh = true)
    }

    fun fetchSocials(city: String = "Denver", category: String? = null, isRefresh: Boolean = false) {
        if (isRefresh) {
             currentPage = 0
             isLastPage = false
             _state.value = NetworkResult.Loading()
        }
        
        // Update cached params if they changed
        if (city != currentCity || category != currentCategory) {
            currentCity = city
            currentCategory = category
            if (!isRefresh) {
                 currentPage = 0
                 isLastPage = false
                 _state.value = NetworkResult.Loading()
            }
        }

        viewModelScope.launch {
            socialRepository.getSocialsPaged(currentCity, currentCategory, currentPage, pageSize).collect { result ->
                when(result) {
                   is NetworkResult.Success -> {
                       val pagedResponse = result.data
                       val newItems = pagedResponse?.content ?: emptyList()
                       isLastPage = pagedResponse?.last ?: true
                       
                       if (currentPage == 0) {
                           _state.value = NetworkResult.Success(newItems)
                       } else {
                           val currentItems = (_state.value.data ?: emptyList()) + newItems
                           _state.value = NetworkResult.Success(currentItems)
                       }
                   }
                   is NetworkResult.Error -> {
                       if (currentPage == 0) {
                           _state.value = NetworkResult.Error(result.message)
                       } else {
                           // Handle error for load more silently or via side effect
                       }
                   }
                   is NetworkResult.Loading -> {
                       if (currentPage == 0) {
                           _state.value = NetworkResult.Loading()
                       }
                   }
                }
                isLoadingMore = false
            }
        }
    }
    
    fun loadMore() {
        if (!isLastPage && !isLoadingMore && _state.value is NetworkResult.Success) {
            isLoadingMore = true
            currentPage++
            fetchSocials(currentCity, currentCategory)
        }
    }
}
