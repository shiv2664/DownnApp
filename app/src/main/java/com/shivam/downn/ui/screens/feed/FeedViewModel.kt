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
import com.shivam.downn.navigation.NavigationEventBus


@HiltViewModel
class FeedViewModel @Inject constructor(
    private val socialRepository: SocialRepository,
    private val prefsManager: PrefsManager,
    private val navigationEventBus: NavigationEventBus
) : ViewModel() {
    val currentUserId = prefsManager.getUserId()

    private val _state = MutableStateFlow<NetworkResult<List<SocialResponse>>>(NetworkResult.Loading())
    val state: StateFlow<NetworkResult<List<SocialResponse>>> = _state

    private var currentPage = 0
    private var isLastPage = false
    private var isLoadingMore = false
    private val pageSize = 10
    
    // Cache filter params to use for loadMore
    private var currentCity = "Denver"
    private var currentCategory: String? = null

    init {
        fetchSocials()
        
        viewModelScope.launch {
            navigationEventBus.events.collect { route ->
                if (route == "home") {
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

    fun fetchSocials(
        city: String = "Denver",
        category: String? = null,
        minPrice: Int? = null, // ignored for now
        maxPrice: Int? = null, // ignored for now
        date: String? = null,   // ignored for now
        isRefresh: Boolean = false
    ) {
        if (isRefresh) {
             currentPage = 0
             isLastPage = false
             _state.value = NetworkResult.Loading()
        }
        
        // Update cached params if they changed (and it's a new search, basically implied by calling this with args)
        if (city != currentCity || category != currentCategory) {
            currentCity = city
            currentCategory = category
            if (!isRefresh) { // If params changed but not explicitly refresh, treat as new search -> refresh
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
                           // Handle error for load more (maybe separate state or toast)
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

    fun joinSocial(socialId: Int) {
        viewModelScope.launch {
            socialRepository.joinSocial(socialId).collect { result ->
                if (result is NetworkResult.Success) {
                    // Local update for immediate visual feedback
                    val currentState = _state.value
                    if (currentState is NetworkResult.Success) {
                        currentState.data?.find { it.id == socialId }?.let { social ->
                            currentUserId?.let { userId ->
                                val requested = social.requestedUserIds ?: mutableSetOf()
                                requested.add(userId)
                                // Re-emit state to trigger UI update
                                _state.value = NetworkResult.Success(currentState.data!!.map { 
                                    if (it.id == socialId) it.copy(requestedUserIds = requested) else it 
                                })
                            }
                        }
                    }
                    fetchSocials()
                }
            }
        }
    }
}